package com.example.echoling.definition.quiz;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;


@Service
public class QuizService {

    public String getRandomDefinition(String word, String sourceLang) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");
            httpPost.setHeader("Content-Type", "application/json; charset = UTF-8");
            httpPost.setHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"));

            StringEntity entity = new StringEntity(createResponseBody(word, sourceLang), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);

            String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(responseString);
            responseString = new JSONObject(responseString).getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                    .getString("content");
            System.out.println(responseString);
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return "Translation error: Invalid response format";
        }
    }
    private String createResponseBody(String word, String sourceLang) {
        JSONObject jsonBody = new JSONObject();

        // Use a valid model name
        jsonBody.put("model", "gpt-4o-mini");

        // Create the messages array
        JSONArray messages = new JSONArray();

        // System message
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Translate the following word using only " + sourceLang + ".\n" +
                "Provide 6 different definitions for the word '" + word + "'," +
                " each offering detailed explanations for better understanding, don't reveal the word in definition sentences. In example sentences write" +
                " {word} and immediately after the proper word.\n" +
                "Write each definition and example on separate lines with {start} at the beginning of the definition and {end} at the end of the example.\n" +
                "Do not write 'Example:' or 'Definition:' labels.\n" +
                "Write everything as plain text without any markdown syntax.\n" +
                "Ensure that each definition has its own example right after it.\n" +
                "Use only " + sourceLang + " in your responses.\n\n" +


                "Example:\n\n{start}\n" +
                "The word can describe how something functions or operates, particularly machinery or systems.\n" +
                "The engine {word} smoothly after the repair.\n" +
                "{end}\n\n" +

                "{start}\n" +
                "The word can also refer to organizing or managing an event or activity.\n" +
                "They {word} a successful campaign last year.\n" +
                "{end}\n\n"

        );



        // Add messages to the array
        messages.put(systemMessage);

        // Add messages to the body
        jsonBody.put("messages", messages);

        // Optionally add other parameters
        jsonBody.put("temperature", 0.5);
        jsonBody.put("max_tokens", 1600); // Adjust as needed
        jsonBody.put("frequency_penalty", 0);
        jsonBody.put("presence_penalty", 0);

        // Convert the JSON object to a string
        return jsonBody.toString();
    }

}


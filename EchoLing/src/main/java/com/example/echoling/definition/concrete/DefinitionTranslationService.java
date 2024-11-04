package com.example.echoling.definition.concrete;


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
public class DefinitionTranslationService {

    public String getDefinition(String word, String sourceLang) {

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
        systemMessage.put("content",
                "Translate the following word using only " + sourceLang + ".\n" +
                        "Use simple words.\n" +
                        "Provide at least 6 different definitions, each using different simple words and offering detailed explanations for better understanding.\n" +
                        "Write each definition and example on separate lines with {start} at the beginning of the definition and {end} at the end of the example.\n" +
                        "Do not write 'Example:' or 'Definition:' labels.\n" +
                        "Write everything as plain text without any markdown syntax.\n" +
                        "Ensure that each definition has its own example right after it.\n" +
                        "If the user inputs a number, treat it as a word from " + sourceLang + ".\n" +
                        "Use only " + sourceLang + " in your responses.\n\n" +

                        "Example:\n\n{start}\n" +
                        "The word can mean to move quickly from one place to another.\n" +
                        "I run to school every day.\n" +
                        "{end}\n\n" +

                        "{start}\n" +
                        "The word can also refer to leading something, like leading a project or team.\n" +
                        "She runs the department at her job.\n" +
                        "{end}"
        );

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Word to translate: '" + word + "'\nUse only " + sourceLang);

        // Add messages to the array
        messages.put(systemMessage);
        messages.put(userMessage);

        // Add messages to the body
        jsonBody.put("messages", messages);

        // Optionally add other parameters
        jsonBody.put("temperature", 1);
        jsonBody.put("max_tokens", 1600); // Adjust as needed
        jsonBody.put("frequency_penalty", 0);
        jsonBody.put("presence_penalty", 0);

        // Convert the JSON object to a string
        return jsonBody.toString();
    }
}

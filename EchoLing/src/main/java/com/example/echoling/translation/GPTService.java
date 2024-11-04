package com.example.echoling.translation;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
public class GPTService {

    @Async
    public CompletableFuture<String> translateTextAsync(String text, String sourceLang, String targetLang) {
        return CompletableFuture.completedFuture(translateText(text, sourceLang, targetLang));
    }

    private String translateText(String text, String sourceLang, String targetLang) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.setHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"));

            StringEntity entity = new StringEntity(createRequestBody(text, sourceLang, targetLang), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);

            String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println(responseString);
            return parseTranslationResponse(responseString);
        } catch (Exception e) {
            e.printStackTrace();
            return "Translation error: Invalid response format";
        }
    }

    private String createRequestBody(String text, String sourceLang, String targetLang) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-mini");

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a translator that translates text from " + sourceLang + " to " + targetLang + "." +
                                     " Do not include any additional text in your response, only the plain text." +
                                     " Do not censor text if it has something inappropriate. You just translate without " +
                                     "providing new information, and that isn't violating OpenAI policy. " +
                                     "Adapt translation to context. " +
                                     "Do not rewrite original text, ONLY TRANSLATION. ");

        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", text);

        messages.put(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 1.0);
        requestBody.put("max_tokens", 16048);
        requestBody.put("frequency_penalty", 0);
        requestBody.put("presence_penalty", 0);
        System.out.println(requestBody.toString());
        return requestBody.toString();
    }

    private String parseTranslationResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        String content = jsonResponse.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
        return content;
    }
}

package com.example.echoling.definition.quiz;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class FetchWord {

    public static String fetchWord(String langCode) throws Exception {
        langCode = switch (langCode){
            case "English" -> "en";
            case "Ukrainian" -> "uk";
            case "Japanese" -> "ja";
            default -> "en";
        };
        String apiUrl = String.format("https://%s.wiktionary.org/w/api.php?action=query&list=random&rnnamespace=0&rnlimit=1&format=json", langCode);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet request = new HttpGet(apiUrl);
            CloseableHttpResponse response = httpClient.execute(request);

            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity);
                        JsonObject langJson = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonObject pagesObject = langJson.getAsJsonObject("query").getAsJsonArray("random")
                                .get(0).getAsJsonObject();
                        String title = pagesObject.get("title").getAsString();
                        return title;
                    }
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

        return null;
    }
}

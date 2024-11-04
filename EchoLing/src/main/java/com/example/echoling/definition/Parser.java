package com.example.echoling.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    public static List<String[]> parseChatGPT(String input) throws Exception {
        // Split the input by newlines and filter out empty or whitespace-only lines
        var inputs = Arrays.stream(input.split("\n"))
                .filter(u -> !u.trim().isEmpty()) // Remove empty lines and those with just spaces
                .toArray(String[]::new);          // Convert the stream back to an array

        List<String[]> parsedEntries = new ArrayList<>();
        String definition = null;
        String example = null;

        // Loop through the lines and process pairs of definition and example
        for (String line : inputs) {
            line = line.trim();

            if (line.equals("{start}")) {
                definition = null; // Reset definition at the start of a new entry
            } else if (line.equals("{end}")) {
                if (definition == null || example == null) {
                    throw new Exception("Missing definition or example before {end} marker");
                }
                parsedEntries.add(new String[]{definition, example});
                definition = null; // Reset for the next entry
                example = null;
            } else if (definition == null) {
                definition = line; // The first non-marker line is the definition
            } else {
                example = line; // The second non-marker line is the example
            }
        }

        return parsedEntries; // Return the list of definition-example pairs
    }

    // Method to convert the result into an HTML string
    public static String toHtml(List<String[]> parsedResult, String word) {
        StringBuilder htmlBuilder = new StringBuilder();
        int entryId = 0; // Initialize a counter for unique IDs

        // Iterate through each definition-example pair and convert it to HTML
        htmlBuilder.append("<div class=\"definition-example-container\">\n");
        for (String[] entry : parsedResult) {
            entryId++; // Increment the counter for each entry
            htmlBuilder.append("\t<form action=\"/add-entry\" method=\"post\" class=\"entry-form\" data-entry-id=\"")
                    .append(entryId).append("\">\n"); // Add data-entry-id attribute
            htmlBuilder.append("\t\t<div class=\"definition-example-item\">\n");
            htmlBuilder.append("\t\t\t<p><strong>Definition:</strong> ")
                    .append(entry[0]).append("</p>\n");
            htmlBuilder.append("\t\t\t<p><strong>Example:</strong> ")
                    .append(entry[1]).append("</p>\n");
            htmlBuilder.append("\t\t\t<input type=\"hidden\" name=\"word\" value=\"")
                    .append(word).append("\">\n"); // Hidden field for word
            htmlBuilder.append("\t\t\t<input type=\"hidden\" name=\"definition\" value=\"")
                    .append(entry[0].replace("{word}", word)).append("\">\n"); // Hidden field for definition
            htmlBuilder.append("\t\t\t<input type=\"hidden\" name=\"example\" value=\"")
                    .append(entry[1].replace("{word}", word));
            htmlBuilder.append("\">\n<input type=\"hidden\" th:name=\"_csrf\" th:value=\"${_csrf.token}\" />\n"); // Hidden field for example
            htmlBuilder.append("\t\t\t<button type=\"submit\" class=\"rainbow-button-vocabulary-submission\">Add to the vocabulary</button>\n");
            htmlBuilder.append("\t\t</div>\n");
            htmlBuilder.append("\t</form>\n"); // Close form
        }
        htmlBuilder.append("</div>");

        return htmlBuilder.toString();
    }

}

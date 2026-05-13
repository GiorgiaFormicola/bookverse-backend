package giorgiaformicola.capstone.clients;

import giorgiaformicola.capstone.exceptions.GoogleBooksException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
public class GoogleBooksClient {
    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksClient(RestClientFactory factory, @Value("${google.apiKey}") String apiKey) {
        this.restClient = factory.create("https://www.googleapis.com/books/v1/volumes");
        this.apiKey = apiKey;

    }

    public JsonNode searchBooks(String query, int page, String language) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("q", query)
                                .queryParam("langRestrict", language)
                                .queryParam("maxResults", 20)
                                .queryParam("printType", "books")
                                .queryParam("startIndex", page * 20)
                                .queryParam("key", apiKey)
                                .build())
                        .retrieve()
                        .body(JsonNode.class);
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(10000 * attempt);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new GoogleBooksException();
    }
}

package giorgiaformicola.capstone.clients;

import giorgiaformicola.capstone.exceptions.GoogleBooksException;
import giorgiaformicola.capstone.payloads.books.GoogleBooksSearchResultDTO;
import giorgiaformicola.capstone.payloads.books.GoogleItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleBooksClient {
    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksClient(RestClientFactory factory, @Value("${google.apiKey}") String apiKey) {
        this.restClient = factory.create("https://www.googleapis.com/books/v1/volumes");
        this.apiKey = apiKey;

    }

    public GoogleBooksSearchResultDTO searchBooks(String query, String language) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("q", query)
                                .queryParam("langRestrict", language)
                                .queryParam("maxResults", 40)
                                .queryParam("printType", "books")
                                /*.queryParam("startIndex", page * 20)*/
                                .queryParam("key", apiKey)
                                .build())
                        .retrieve()
                        .body(GoogleBooksSearchResultDTO.class);
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

    public GoogleItemDTO searchBookByGoogleId(String bookId) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/" + bookId)
                                .queryParam("key", apiKey)
                                .build())
                        .retrieve()
                        .body(GoogleItemDTO.class);
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

package giorgiaformicola.capstone.clients;

import giorgiaformicola.capstone.exceptions.GoogleBooksException;
import giorgiaformicola.capstone.payloads.books.GoogleBooksSearchResultDTO;
import giorgiaformicola.capstone.payloads.books.GoogleItemDTO;
import giorgiaformicola.capstone.payloads.books.SearchFieldsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Component
public class GoogleBooksClient {
    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksClient(RestClientFactory factory, @Value("${google.apiKey}") String apiKey) {
        this.restClient = factory.create("https://www.googleapis.com/books/v1/volumes");
        this.apiKey = apiKey;

    }

    /*public GoogleBooksSearchResultDTO searchBooks(String query) {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("q", query)
                                .queryParam("maxResults", 40)
                                .queryParam("printType", "books")
                                *//*.queryParam("startIndex", page * 20)*//*
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
    }*/

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

    public GoogleBooksSearchResultDTO searchBooks(SearchFieldsDTO searchFields) {
        String query = buildQuery(searchFields.title(), searchFields.author(), searchFields.publisher(), searchFields.category(), searchFields.isbn());
        String encodedQuery = UriUtils.encodeQuery(query, StandardCharsets.UTF_8);
        System.out.println(encodedQuery);

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", encodedQuery)
                        .queryParam("maxResults", 40)
                        .queryParam("printType", "books")
                        /*.queryParam("startIndex", page * 20)*/
                        .queryParam("orderBy", "relevance")
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .body(GoogleBooksSearchResultDTO.class);
    }

    private String buildQuery(String title,
                              String author,
                              String publisher,
                              String category,
                              String isbn) {

        StringBuilder query = new StringBuilder();

        if (StringUtils.hasText(title)) {
            query.append("intitle:").append(title).append("+");
        }

        if (StringUtils.hasText(author)) {
            query.append("inauthor:").append(author).append("+");
        }

        if (StringUtils.hasText(publisher)) {
            query.append("inpublisher:").append(publisher).append("+");
        }

        if (StringUtils.hasText(category)) {
            query.append("subject:").append(category).append("+");
        }

        if (StringUtils.hasText(isbn)) {
            query.append("isbn:").append(isbn).append("+");
        }

        if (!query.isEmpty() && query.charAt(query.length() - 1) == '+') {
            query.deleteCharAt(query.length() - 1);
        }

        return query.toString();
    }
}

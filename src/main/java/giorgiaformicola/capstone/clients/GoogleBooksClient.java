package giorgiaformicola.capstone.clients;

import giorgiaformicola.capstone.exceptions.GoogleBooksSearchException;
import giorgiaformicola.capstone.payloads.books.GoogleBooksSearchResultDTO;
import giorgiaformicola.capstone.payloads.books.GoogleItemDTO;
import giorgiaformicola.capstone.payloads.books.SearchFieldsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class GoogleBooksClient {
    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksClient(RestClientFactory factory, @Value("${google.apiKey}") String apiKey) {
        this.restClient = factory.create("https://www.googleapis.com/books/v1/volumes");
        this.apiKey = apiKey;

    }

    public GoogleBooksSearchResultDTO searchBooks(SearchFieldsDTO searchFields) {
        String query = buildQuery(searchFields.title(), searchFields.author(), searchFields.publisher(), searchFields.category(), searchFields.isbn());
        /*String encodedQuery = UriUtils.encodeQuery(query, StandardCharsets.UTF_8);*/
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("q", query)
                                .queryParam("maxResults", 40)
                                .queryParam("printType", "books")
                                /*.queryParam("startIndex", page * 20)*/
                                .queryParam("orderBy", "relevance")
                                .queryParam("key", apiKey)
                                .build())
                        .retrieve()
                        .onStatus(status -> status.isError(), (req, res) -> {
                            System.out.println("Google error status: " + res.getStatusCode());
                            System.out.println("Body: " + new String(res.getBody().readAllBytes()));
                        })
                        .body(GoogleBooksSearchResultDTO.class);
            } catch (HttpServerErrorException | ResourceAccessException e) {
                e.printStackTrace();

                System.out.println(e.getMessage());

                if (attempt == maxAttempts) {
                    throw new GoogleBooksSearchException("Google Books unavailable after retries", e);
                }
                try {
                    Thread.sleep(10000 * attempt);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Google Books client error (non retryable)");
                System.err.println("Status: " + e.getStatusCode());
                System.err.println("Body: " + e.getResponseBodyAsString());
                throw new GoogleBooksSearchException("Google Books request invalid", e);
            } catch (Exception e) {
                System.err.println("Unexpected error while calling Google Books");
                e.printStackTrace();

                throw new GoogleBooksSearchException("Unexpected error", e);
            }
        }
        throw new GoogleBooksSearchException("Unexpected fallback error");
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
            } catch (HttpServerErrorException | ResourceAccessException e) {
                e.printStackTrace();

                System.out.println(e.getMessage());

                if (attempt == maxAttempts) {
                    throw new GoogleBooksSearchException("Google Books unavailable after retries", e);
                }
                try {
                    Thread.sleep(10000 * attempt);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Google Books client error (non retryable)");
                System.err.println("Status: " + e.getStatusCode());
                System.err.println("Body: " + e.getResponseBodyAsString());
                throw new GoogleBooksSearchException("Google Books request invalid", e);
            } catch (Exception e) {
                System.err.println("Unexpected error while calling Google Books");
                e.printStackTrace();

                throw new GoogleBooksSearchException("Unexpected error", e);
            }
        }
        throw new GoogleBooksSearchException("Unexpected fallback error");
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

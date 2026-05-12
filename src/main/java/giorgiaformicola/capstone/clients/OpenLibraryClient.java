package giorgiaformicola.capstone.clients;

import giorgiaformicola.capstone.payloads.BooksSearchResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenLibraryClient {
    private final RestClient restClient;

    public OpenLibraryClient(RestClientFactory factory) {
        this.restClient = factory.create("https://openlibrary.org");
    }

    public BooksSearchResponseDTO search(String query, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("q", query)
                        .queryParam("limit", 20)
                        .queryParam("offset", page * 20)
                        .build())
                .retrieve()
                .body(BooksSearchResponseDTO.class);
    }
}

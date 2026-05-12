package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.payloads.BooksSearchResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;

    public BooksService(OpenLibraryClient openLibraryClient) {
        this.openLibraryClient = openLibraryClient;
    }

    public BooksSearchResponseDTO search(String query, int page) {
        return openLibraryClient.search(query, page);
    }
}

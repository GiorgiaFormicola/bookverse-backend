package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.payloads.BookDetailsDTO;
import giorgiaformicola.capstone.payloads.WorksSearchResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;

    public BooksService(OpenLibraryClient openLibraryClient) {
        this.openLibraryClient = openLibraryClient;
    }

    public WorksSearchResponseDTO searchWorksFromAPI(String query, int page) {
        return openLibraryClient.searchWorks(query, page);
    }

    public BookDetailsDTO getBookFromAPI(String editionId) {
        return openLibraryClient.searchBook(editionId);
    }
}

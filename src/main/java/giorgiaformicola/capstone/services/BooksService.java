package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.clients.GoogleBooksClient;
import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.payloads.OpenLibraryBookDetailsDTO;
import giorgiaformicola.capstone.payloads.OpenLibraryWorksSearchResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;
    private final GoogleBooksClient googleBooksClient;

    public BooksService(OpenLibraryClient openLibraryClient, GoogleBooksClient googleBooksClient) {

        this.openLibraryClient = openLibraryClient;
        this.googleBooksClient = googleBooksClient;
    }

    public OpenLibraryWorksSearchResponseDTO searchWorksFromOpenLibrary(String query, int page) {
        return openLibraryClient.searchWorks(query, page);
    }

    public OpenLibraryBookDetailsDTO getBookFromOpenLibrary(String editionId) {
        return openLibraryClient.searchBook(editionId);
    }

    public JsonNode searchBooksFromGoogle(String query, int page, String language) {
        if (query.isBlank()) throw new BadRequestException("You must provide a valid query string");
        if (page < 0) throw new BadRequestException("Page number must be a positive number");
        return googleBooksClient.searchBooks(query, page, language);
    }


}

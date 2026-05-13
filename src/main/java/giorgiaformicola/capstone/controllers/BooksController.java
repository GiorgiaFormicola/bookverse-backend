package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.services.BooksService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    /*@GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public OpenLibraryWorksSearchResponseDTO searchFromAPI(@RequestParam String query,
                                                           @RequestParam(defaultValue = "0") int page) {
        return booksService.searchWorksFromAPI(query, page);
    }

    @GetMapping("/search/{editionId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public OpenLibraryBookDetailsDTO getBookDetailsFromAPI(@PathVariable String editionId) {
        return booksService.getBookFromAPI(editionId);
    }*/

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public JsonNode searchFromAPI(@RequestParam String query,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "IT") String language
    ) {
        return booksService.searchBooksFromGoogle(query, page, language);
    }


}

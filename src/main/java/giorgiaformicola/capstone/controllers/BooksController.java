package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.payloads.BookDetailsDTO;
import giorgiaformicola.capstone.payloads.WorksSearchResponseDTO;
import giorgiaformicola.capstone.services.BooksService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public WorksSearchResponseDTO searchFromAPI(@RequestParam String query,
                                                @RequestParam(defaultValue = "0") int page) {
        return booksService.searchWorksFromAPI(query, page);
    }

    @GetMapping("/search/{editionId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public BookDetailsDTO getBookDetailsFromAPI(@PathVariable String editionId) {
        return booksService.getBookFromAPI(editionId);
    }
}

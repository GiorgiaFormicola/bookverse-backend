package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.payloads.BooksSearchResponseDTO;
import giorgiaformicola.capstone.services.BooksService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public BooksSearchResponseDTO search(@RequestParam String query,
                                         @RequestParam(defaultValue = "0") int page) {
        return booksService.search(query, page);
    }
}

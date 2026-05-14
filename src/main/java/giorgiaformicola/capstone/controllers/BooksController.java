package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.GoogleItemDTO;
import giorgiaformicola.capstone.services.BooksService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //ENDPOINT PER CERCARE LIBRI DA GOOGLE
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<GoogleItemDTO> searchFromAPI(@RequestParam String query,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "it") String language
    ) {
        return booksService.searchBooksFromGoogle(query, page, language);
    }

    /*@GetMapping("/search/{googleId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public BookDetailDTO searchBookByIdFromAPI(@PathVariable String googleId
    ) {
        return booksService.searchBookByIdFromGoogle(googleId);
    }*/

    //ENDPOINT PER OTTENERE DETTAGLIO LIBRO O DA DB O DA GOOGLE
    @GetMapping("/{googleId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public BookDetailDTO getBookDetails(@PathVariable String googleId) {
        return booksService.getBookDetailsByGoogleId(googleId);
    }

    //ENDPOINT PER AGGIUNGERE LIBRO COME ADMIN ? FORSE NON SERVE
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Book saveNewBook(@RequestBody @Validated BookDetailDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return booksService.save(body);
    }


}

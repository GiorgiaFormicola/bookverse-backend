package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.books.*;
import giorgiaformicola.capstone.services.BooksService;
import giorgiaformicola.capstone.specifications.BooksSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<BookDetailDTO> searchBooks(@AuthenticationPrincipal User currentAuthenticatedUser,
                                           @RequestParam(required = false) String title,
                                           @RequestParam(required = false) String author,
                                           @RequestParam(required = false) String publisher,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(required = false) String isbn,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(defaultValue = "asc") String order) {
        SearchFieldsDTO searchFields = new SearchFieldsDTO(title, author, publisher, category, isbn);
        return booksService.searchBooks(currentAuthenticatedUser.getId(), searchFields, sortBy, order);
    }

    @GetMapping("/search/{googleId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public BookDetailDTO getBookDetailsFromGoogleOrDb(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId) {
        return booksService.getBookDetailsByGoogleId(currentAuthenticatedUser.getId(), googleId);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<Book> getBooksFromDb(@RequestParam(required = false) String title,
                                     @RequestParam(required = false) String googleId,
                                     @RequestParam(required = false) String author,
                                     @RequestParam(required = false) String publisher,
                                     @RequestParam(required = false) String isbn10,
                                     @RequestParam(required = false) String isbn13,
                                     @RequestParam(required = false) String category,
                                     @RequestParam(required = false) Boolean missingTitle,
                                     @RequestParam(required = false) Boolean missingAuthor,
                                     @RequestParam(required = false) Boolean missingPublisher,
                                     @RequestParam(required = false) Boolean missingIsbn10,
                                     @RequestParam(required = false) Boolean missingIsbn13,
                                     @RequestParam(required = false) Boolean missingCategory,
                                     @RequestParam(required = false) Boolean missingPublishedDate,
                                     @RequestParam(required = false) Boolean missingDescription,
                                     @RequestParam(required = false) Boolean missingPages,
                                     @RequestParam(required = false) Boolean missingCoverURL,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size,
                                     @RequestParam(defaultValue = "title") String sortBy,
                                     @RequestParam(defaultValue = "asc") String order) {

        Specification<Book> specification = BooksSpecification.filter(
                title,
                googleId,
                author,
                publisher,
                isbn10,
                isbn13,
                category,
                missingTitle,
                missingAuthor,
                missingPublisher,
                missingIsbn10,
                missingIsbn13,
                missingCategory,
                missingPublishedDate,
                missingDescription,
                missingPages,
                missingCoverURL
        );

        return booksService.findAll(specification, page, size, sortBy, order);
    }

    @GetMapping("/{googleId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Book getBookByGoogleId(@PathVariable String googleId) {
        return booksService.getByGoogleId(googleId);
    }

    @PutMapping("/{googleId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Book updateBookInfo(@PathVariable String googleId, @RequestBody @Validated BookInfoDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return booksService.findByIdAndUpdateBookInfo(googleId, body);
    }

    @PatchMapping("/{googleId}/cover")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Book updateBookCover(@PathVariable String googleId, @RequestParam("book_cover") MultipartFile file) {
        return booksService.findByIdAndUpdateBookCover(googleId, file);
    }

    @PatchMapping("/{googleId}/authors")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Book updateBookAuthors(@PathVariable String googleId, @RequestBody @Validated AuthorsDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return booksService.findByIdAndUpdateBookAuthors(googleId, body);
    }

    @PatchMapping("/{googleId}/categories")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Book updateBookCategories(@PathVariable String googleId, @RequestBody @Validated CategoriesDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return booksService.findByIdAndUpdateBookCategories(googleId, body);
    }

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

    @DeleteMapping("/{googleId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable String googleId) {
        booksService.findByIdAndDelete(googleId);
    }

    @GetMapping("/{googleId}/stats")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public BookStatsDTO getBookStats(@PathVariable String googleId) {
        return booksService.getBookStats(googleId);
    }
}

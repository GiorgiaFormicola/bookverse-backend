package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.enums.StatusType;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.BookStatusDTO;
import giorgiaformicola.capstone.payloads.books.BookVisibilityDTO;
import giorgiaformicola.capstone.payloads.books.UserLibraryBookDTO;
import giorgiaformicola.capstone.services.UserBooksService;
import giorgiaformicola.capstone.specifications.UserBooksSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/books")
public class UserBooksController {
    private final UserBooksService userBooksService;

    public UserBooksController(UserBooksService userBooksService) {
        this.userBooksService = userBooksService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<UserLibraryBookDTO> getMyBooks(@AuthenticationPrincipal User currentAuthenticatedUser,
                                               @RequestParam(required = false) String title,
                                               @RequestParam(required = false) String author,
                                               @RequestParam(required = false) String publisher,
                                               @RequestParam(required = false) String category,
                                               @RequestParam(required = false) String isbn,
                                               @RequestParam(required = false) Boolean isPublic,
                                               @RequestParam(required = false) StatusType status,
                                               @RequestParam(required = false) Boolean reviewed,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size,
                                               @RequestParam(defaultValue = "title") String sortBy,
                                               @RequestParam(defaultValue = "asc") String order
    ) {
        Specification<UserBook> specification = UserBooksSpecification.filter(
                currentAuthenticatedUser.getId(),
                title,
                author,
                publisher,
                isbn,
                isbn,
                category,
                isPublic,
                status,
                reviewed
        );
        return userBooksService.findUserBooks(currentAuthenticatedUser.getId(), specification, page, size, sortBy, order);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBook addBookToMyLibrary(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated BookDetailDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return userBooksService.saveBookToUserLibrary(currentAuthenticatedUser.getId(), body);
    }

    @PatchMapping("/{googleId}/visibility")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserBook updateMyBookVisibility(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId, @RequestBody @Validated BookVisibilityDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return userBooksService.updateBookVisibilityFromUserLibrary(currentAuthenticatedUser.getId(), googleId, body);
    }

    @PatchMapping("/{googleId}/status")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserBook updateMyBookStatus(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId, @RequestBody @Validated BookStatusDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return userBooksService.updateBookStatusFromUserLibrary(currentAuthenticatedUser.getId(), googleId, body);
    }

    @DeleteMapping("/{googleId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookMyLibrary(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId) {
        userBooksService.deleteBookFromUserLibrary(currentAuthenticatedUser.getId(), googleId);
    }
}

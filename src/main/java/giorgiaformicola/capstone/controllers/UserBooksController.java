package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.UserLibraryBookDTO;
import giorgiaformicola.capstone.services.UserBooksService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("me/books")
public class UserBooksController {
    private final UserBooksService userBooksService;

    public UserBooksController(UserBooksService userBooksService) {
        this.userBooksService = userBooksService;
    }

    //TODO: migliora paginazione
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<UserLibraryBookDTO> getMyBooks(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return userBooksService.findUserBooks(currentAuthenticatedUser.getId());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserBook addBookToMyLibrary(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated BookDetailDTO body) {
        return userBooksService.saveBookToUserLibrary(currentAuthenticatedUser.getId(), body);
    }


}

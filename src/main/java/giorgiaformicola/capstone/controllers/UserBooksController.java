package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.BookStatusDTO;
import giorgiaformicola.capstone.payloads.books.BookVisibilityDTO;
import giorgiaformicola.capstone.payloads.books.UserLibraryBookDTO;
import giorgiaformicola.capstone.services.UserBooksService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
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

    //ENDPOINT PER OTTENERE I LIBRI NELLA LA MIA LIBRERIA
    //TODO: migliora paginazione
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<UserLibraryBookDTO> getMyBooks(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return userBooksService.findUserBooks(currentAuthenticatedUser.getId());
    }

    /*@GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<String> getMyBooksIds(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return userBooksService.findUserBooksIds(currentAuthenticatedUser.getId());
    }*/

    //ENDPOINT PER SALVARE LIBRO NEL DB SE NON ESISTE GIA' ED POI AGGIUNGERLO ALLA LIBRERIA UTENTE
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

    //ENDPOINT PER AGGIORNARE VISIBILITA' LIBRO DENTRO LIBRERIA UTENTE
    @PatchMapping("/{googleId}/visibility")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserBook updateMyBookVisibility(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId, @RequestBody @Validated BookVisibilityDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return userBooksService.updateBookVisibilityFromUserLibrary(currentAuthenticatedUser.getId(), googleId, body);
    }

    //ENDPOINT PER AGGIORNARE STATO LIBRO DENTRO LIBRERIA UTENTE
    @PatchMapping("/{googleId}/status")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserBook updateMyBookStatus(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId, @RequestBody @Validated BookStatusDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return userBooksService.updateBookStatusFromUserLibrary(currentAuthenticatedUser.getId(), googleId, body);
    }

    //ENDPOINT PER ELIMINARE LIBRO DA LIBRERIA UTENTE
    @DeleteMapping("/{googleId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookMyLibrary(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable String googleId) {
        userBooksService.deleteBookFromUserLibrary(currentAuthenticatedUser.getId(), googleId);
    }


}

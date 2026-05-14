package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.users.*;
import giorgiaformicola.capstone.services.UsersService;
import giorgiaformicola.capstone.specifications.UsersSpecification;
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
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    //ENDPOINT PER OTTENERE MIO PROFILLO
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public UserProfileDTO getMyProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return this.usersService.getUserProfileById(currentAuthenticatedUser.getId());
    }

    //ENDPOINT PER AGGIORNARE MIO USERNAME, BIO E DISPLAY NAME
    @PutMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated ProfileUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdateProfile(currentAuthenticatedUser.getId(), body);
    }

    //ENDPOINT PER AGGIORNARE MIA IMMAGINE
    @PatchMapping("/me/picture")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyProfilePicture(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestParam("profile_picture") MultipartFile file) {
        return this.usersService.findByIdAndUpdateProfilePicture(currentAuthenticatedUser.getId(), file);
    }

    //ENDPOINT PER AGGIORNARE MIA EMAIL
    //TODO: verify email
    @PatchMapping("/me/email")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyEmail(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated EmailUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdateEmail(currentAuthenticatedUser.getId(), body);
    }

    //ENDPOINT PER AGGIORNARE MIA PASSWORD
    @PatchMapping("/me/password")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyPassword(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated PasswordUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdatePassword(currentAuthenticatedUser.getId(), body);
    }

    //ENDPOINT PER ELIMINARE MIO PROFILO
    //TODO: handle deleting related records in the DB
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
    }

    //ENDPOINT PER VEDERE PROFILO DI UN ALTRO UTENTE COME ADMIN
    //TODO:add USER authority only if following
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User getUserById(@PathVariable UUID userId) {
        return this.usersService.findById(userId);
    }

    //ENDPOINT PER VEDERE TUTTI GLI UTENTI COME ADMIN
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<User> getUsers(@RequestParam(required = false) String username,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String role,
                               @RequestParam(required = false) Boolean active,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "username") String sortBy,
                               @RequestParam(defaultValue = "asc") String order) {
        Specification<User> specification = UsersSpecification.filter(
                username,
                email,
                role,
                active
        );
        return this.usersService.findAll(specification, page, size, sortBy, order);
    }

    //ENDPOINT PER CAMBIARE RUOLO UTENTE COME ADMIN
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User updateUserRole(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable UUID userId, @RequestBody @Validated UserRoleDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdateRole(currentAuthenticatedUser.getId(), userId, body);
    }

    //ENDPOINT PER CAMBIARE STATO UTENTE COME ADMIN
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User updateUserStatus(@AuthenticationPrincipal User currentAuthenticatedUser, @PathVariable UUID userId, @RequestBody @Validated UserStatusDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdateStatus(currentAuthenticatedUser.getId(), userId, body);
    }

    //ENDPOINT PER ELIMINARE UTENTE COME ADMIN
    //TODO: handle deleting related records in the DB
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable UUID userId) {
        this.usersService.findByIdAndDelete(userId);
    }

}

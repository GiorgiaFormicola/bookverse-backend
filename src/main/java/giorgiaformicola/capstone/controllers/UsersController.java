package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.EmailUpdateDTO;
import giorgiaformicola.capstone.payloads.PasswordUpdateDTO;
import giorgiaformicola.capstone.payloads.ProfileUpdateDTO;
import giorgiaformicola.capstone.payloads.RoleDTO;
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

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User getMyProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return this.usersService.findById(currentAuthenticatedUser.getId());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated ProfileUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdateProfile(currentAuthenticatedUser.getId(), body);
    }

    @PatchMapping("/me/picture")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyProfilePicture(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestParam("profile_picture") MultipartFile file) {
        return this.usersService.findByIdAndUpdateProfilePicture(currentAuthenticatedUser.getId(), file);
    }

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

    @PatchMapping("/me/password")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateMyPassword(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated PasswordUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdatePassword(currentAuthenticatedUser.getId(), body);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
    }


    //TODO:add USER authority only if following
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User getUserById(@PathVariable UUID userId) {
        return this.usersService.findById(userId);
    }

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

    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public User updateUserRole(@PathVariable UUID userId, @RequestBody @Validated RoleDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.usersService.findByIdAndUpdateRole(userId, body);
    }

}

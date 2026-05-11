package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.services.UsersService;
import giorgiaformicola.capstone.specifications.UsersSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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


}

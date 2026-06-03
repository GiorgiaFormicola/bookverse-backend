package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRoleDTO(
        @NotBlank(message = "Role is required")
        @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be one of the following values: USER, ADMIN")
        String role
) {
}

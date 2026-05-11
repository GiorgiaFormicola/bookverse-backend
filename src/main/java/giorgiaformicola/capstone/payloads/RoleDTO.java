package giorgiaformicola.capstone.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RoleDTO(
        @NotBlank(message = "Role is mandatory")
        @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be one of the following values: USER, ADMIN")
        String role
) {
}

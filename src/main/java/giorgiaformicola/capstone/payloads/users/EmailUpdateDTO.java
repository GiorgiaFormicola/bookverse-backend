package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailUpdateDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must follow a valid email format")
        String email
) {
}

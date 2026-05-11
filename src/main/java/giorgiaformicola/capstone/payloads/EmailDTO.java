package giorgiaformicola.capstone.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email must follow a valid email format")
        String email
) {
}

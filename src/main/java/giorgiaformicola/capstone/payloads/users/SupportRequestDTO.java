package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupportRequestDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Provide a valid email")
        String email
) {
}

package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordUpdateDTO(
        @NotBlank(message = "Old password is mandatory")
        @Size(min = 8, message = "Old password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Old password must follow a valid password format")
        String currentPassword,

        @NotBlank(message = "New password is mandatory")
        @Size(min = 8, message = "New password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "New password must follow a valid password format")
        String newPassword
) {
}

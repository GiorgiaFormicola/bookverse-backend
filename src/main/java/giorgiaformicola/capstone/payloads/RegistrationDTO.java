package giorgiaformicola.capstone.payloads;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegistrationDTO(
        @NotBlank(message = "Username is mandatory and it can't contain only blank spaces")
        @Size(min = 2, max = 30, message = "Username must contain minimum 2 characters and maximum 30 characters")
        @Pattern(regexp = "^(?!.*\\.\\.)(?!.*\\.$)[a-z0-9_][a-z0-9_.]{1,29}$", message = "Username must follow a valid username format")
        String username,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email must follow a valid email format")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must follow a valid password format")
        String password,

        @NotBlank(message = "Display name is mandatory")
        @Size(min = 2, max = 50, message = "Display name must contain minimum 2 characters and maximum 50 characters")
        String displayName,

        @NotNull(message = "Birthdate is mandatory")
        @Past(message = "Birthdate must be a date in the past")
        LocalDate birthdate
) {
}

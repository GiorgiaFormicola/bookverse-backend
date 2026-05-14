package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateDTO(
        @NotBlank(message = "Username is mandatory and it can't contain only blank spaces")
        @Size(min = 2, max = 30, message = "Username must contain minimum 2 characters and maximum 30 characters")
        @Pattern(regexp = "^(?!.*\\.\\.)(?!.*\\.$)[a-z0-9_][a-z0-9_.]{1,29}$", message = "Username must follow a valid username format")
        String username,

        @NotBlank(message = "Display name is mandatory")
        @Size(min = 2, max = 50, message = "Display name must contain minimum 2 characters and maximum 50 characters")
        String displayName,

        @NotNull(message = "Bio is mandatory")
        @Size(max = 500, message = "Bio must contain maximum 500 characters")
        @Pattern(regexp = "^(?!\\s+$).*", message = "Bio can't contain only blank spaces")
        String bio
) {
}

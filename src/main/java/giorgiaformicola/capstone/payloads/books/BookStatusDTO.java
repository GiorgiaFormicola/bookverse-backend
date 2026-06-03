package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookStatusDTO(
        @NotNull(message = "Book status is required")
        @Pattern(regexp = "^(TO_READ|READING|READ)$", message = "You must provide a valid book status")
        String status
) {
}

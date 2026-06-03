package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotNull;

public record BookVisibilityDTO(
        @NotNull(message = "Book visibility is required")
        boolean isPublic
) {
}

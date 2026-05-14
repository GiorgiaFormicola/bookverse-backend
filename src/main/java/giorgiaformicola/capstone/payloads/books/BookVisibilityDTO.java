package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotNull;

public record BookVisibilityDTO(
        @NotNull(message = "The book visibility is mandatory")
        boolean isPublic
) {
}

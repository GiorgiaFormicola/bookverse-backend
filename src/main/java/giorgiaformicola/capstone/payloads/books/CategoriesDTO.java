package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoriesDTO(
        @NotNull(message = "Categories list is required")
        List<String> categories
) {
}

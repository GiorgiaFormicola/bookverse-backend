package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookDetailDTO(
        @NotBlank(message = "Google id is required")
        String googleId,

        String title,

        @NotNull(message = "Authors list is required")
        List<String> authors,

        String publisher,
        String publishedDate,
        String description,
        String isbn10,
        String isbn13,
        Long pages,

        @NotNull(message = "Categories list is required")
        List<String> categories,

        String coverURL
) {
}

package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookDetailDTO(
        @NotBlank(message = "Google id is mandatory")
        String googleId,
        String title,
        @NotNull(message = "Authors list is mandatory")
        List<String> authors,
        String publisher,
        String publishedDate,
        String description,
        String isbn10,
        String isbn13,
        Long pages,
        @NotNull(message = "Categories list is mandatory")
        List<String> categories,
        String coverURL
) {
}

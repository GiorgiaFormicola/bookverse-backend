package giorgiaformicola.capstone.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NewBookDTO(
        @NotBlank(message = "Google id is mandatory")
        String googleId,
        @NotBlank(message = "Title is mandatory")
        String title,
        @NotNull(message = "Authors is mandatory")
        List<String> authors,
        /*@NotBlank(message = "Publisher is mandatory")
        String publisher,
        @NotBlank(message = "Published date is mandatory")
        String publishedDate,
        @NotBlank(message = "Description is mandatory")
        String description,
        @NotBlank(message = "Isbn10 code is mandatory")
        String isbn10,
        @NotBlank(message = "Isbn13 code is mandatory")
        String isbn13,
        @NotBlank(message = "Isbn13 code is mandatory")
        String pages,
        */
        @NotNull(message = "Categories list is mandatory")
        List<String> categories,
        @NotBlank(message = "Cover url is mandatory")
        String coverURL
) {
}

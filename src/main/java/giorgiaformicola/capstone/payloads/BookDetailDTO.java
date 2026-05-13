package giorgiaformicola.capstone.payloads;

import java.util.List;

public record BookDetailDTO(
        String googleId,
        String title,
        List<String> authors,
        String publisher,
        String publishedDate,
        String description,
        String isbn10,
        String isbn13,
        Long pages,
        List<String> categories,
        String coverURL
) {
}

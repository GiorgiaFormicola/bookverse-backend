package giorgiaformicola.capstone.payloads.books;

public record BookInfoDTO(
        String title,
        String publisher,
        String publishedDate,
        String description,
        String isbn10,
        String isbn13,
        Long pages
) {
}

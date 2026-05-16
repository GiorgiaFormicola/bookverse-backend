package giorgiaformicola.capstone.payloads.books;

import giorgiaformicola.capstone.exceptions.ValidationException;

public record SearchFieldsDTO(String title, String author, String publisher, String category, String isbn) {
    public SearchFieldsDTO {
        if (title == null && author == null && publisher == null && category == null && isbn == null)
            throw new ValidationException("You must provide at least one search parameter (title or others)");

        if (isBlank(title) && isBlank(author) && isBlank(publisher) && isBlank(category) && isBlank(isbn))
            throw new ValidationException("You must provide at least one search parameter (title or others)");
    }

    private boolean isBlank(String field) {
        return field == null || field.isBlank();
    }
}

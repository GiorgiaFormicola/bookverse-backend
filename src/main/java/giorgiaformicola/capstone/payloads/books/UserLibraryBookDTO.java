package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonProperty;
import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.enums.StatusType;

import java.util.UUID;

public record UserLibraryBookDTO(
        Book info,
        UUID id,

        @JsonProperty("public")
        boolean isPublic,

        StatusType status
) {
}

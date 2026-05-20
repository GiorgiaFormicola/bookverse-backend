package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonProperty;
import giorgiaformicola.capstone.enums.StatusType;


public record LibraryBookDTO(
        String googleId,
        @JsonProperty("public")
        boolean isPublic,
        StatusType status
) {
}

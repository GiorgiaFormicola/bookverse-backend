package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryWorkCardDTO(
        @JsonProperty("key")
        String workKey,

        String title,

        @JsonProperty("author_name")
        List<String> authors,

        @JsonProperty("cover_edition_key")
        String editionKey,

        @JsonProperty("cover_i")
        String coverURL
) {
}

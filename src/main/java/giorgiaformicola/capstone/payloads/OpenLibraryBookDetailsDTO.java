package giorgiaformicola.capstone.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryBookDetailsDTO(
        List<String> publishers,

        @JsonProperty("description")
        JsonNode description,

        List<String> subjects,

        @JsonProperty("publish_date")
        String publishDate,

        @JsonProperty("number_of_pages")
        String pages,

        @JsonProperty("isbn_10")
        List<String> isbn10,

        @JsonProperty("isbn_13")
        List<String> isbn13
) {
}

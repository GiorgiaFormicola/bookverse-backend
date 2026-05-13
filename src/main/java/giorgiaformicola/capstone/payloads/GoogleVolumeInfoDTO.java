package giorgiaformicola.capstone.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import java.util.List;

public record GoogleVolumeInfoDTO(
        String title,
        List<String> authors,
        String publisher,
        String publishedDate,
        String description,
        @JsonProperty("industryIdentifiers")
        List<GoogleVolumeIdentifierDTO> identifiers,
        @JsonProperty("pageCount")
        String pages,
        List<String> categories,
        @JsonProperty("imageLinks")
        JsonNode coverURLS
) {
}

package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleVolumeInfoDTO(
        String title,
        List<String> authors,
        String publisher,
        String publishedDate,
        String description,

        @JsonProperty("industryIdentifiers")
        List<GoogleVolumeIdentifierDTO> identifiers,

        @JsonProperty("pageCount")
        Long pages,
        
        List<String> categories,

        @JsonProperty("imageLinks")
        GoogleVolumeImageLinksDTO coverURLS
) {
}

package giorgiaformicola.capstone.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryWorksSearchResponseDTO(
        @JsonProperty("numFound")
        long totalItems,

        @JsonProperty("start")
        long start,

        @JsonProperty("offset")
        long offset,

        @JsonProperty("q")
        String query,

        @JsonProperty("docs")
        List<OpenLibraryWorkCardDTO> books
) {
}

package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksSearchResultDTO(
        @JsonProperty("totalItems")
        long totalItems,
        @JsonProperty("items")
        List<GoogleItemDTO> items
) {

}

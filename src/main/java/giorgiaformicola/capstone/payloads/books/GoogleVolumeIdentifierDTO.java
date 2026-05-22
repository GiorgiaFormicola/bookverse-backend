package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleVolumeIdentifierDTO(
        String type,
        String identifier
) {
}

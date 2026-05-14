package giorgiaformicola.capstone.payloads.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleVolumeImageLinksDTO(
        String extraLarge,
        String large,
        String medium,
        String small,
        String thumbnail,
        String smallThumbnail
) {
}

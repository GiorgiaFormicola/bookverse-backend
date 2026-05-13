package giorgiaformicola.capstone.tools;

import giorgiaformicola.capstone.payloads.BookDetailDTO;
import giorgiaformicola.capstone.payloads.GoogleItemDTO;
import giorgiaformicola.capstone.payloads.GoogleVolumeIdentifierDTO;
import giorgiaformicola.capstone.payloads.GoogleVolumeImageLinksDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {
    private static final String defaultCover = "https://neelkanthpublishers.com/assets/bookcover_cover.png";

    private static String extractIdentifier(List<GoogleVolumeIdentifierDTO> identifiersList, String identifierType) {
        if (identifiersList == null) return null;
        return identifiersList.stream().filter(identifier -> identifier.type().equals(identifierType)).map(identifier -> identifier.identifier()).findFirst().orElse(null);
    }

    private static String extractCover(GoogleVolumeImageLinksDTO coverURLS) {
        if (coverURLS == null) return defaultCover;
        String[] covers = {
                coverURLS.extraLarge(),
                coverURLS.large(),
                coverURLS.medium(),
                coverURLS.small(),
                coverURLS.thumbnail(),
                coverURLS.smallThumbnail()
        };

        for (String cover : covers) {
            if (cover != null && !cover.isBlank()) {
                return cover;
            }
        }
        return defaultCover;
    }

    private static String cleanDescription(String description) {
        if (description == null) return null;
        return description.replaceAll("<[^>]*>", "").trim();
    }

    private static List<String> normalizeStringCollection(List<String> collection) {
        if (collection == null) return List.of();
        return collection;
    }

    public static BookDetailDTO mapFromGoogleItemDTO(GoogleItemDTO body) {
        return new BookDetailDTO(
                body.id(),
                body.volumeInfo().title(),
                normalizeStringCollection(body.volumeInfo().authors()),
                body.volumeInfo().publisher(),
                body.volumeInfo().publishedDate(),
                cleanDescription(body.volumeInfo().description()),
                extractIdentifier(body.volumeInfo().identifiers(), "ISBN_10"),
                extractIdentifier(body.volumeInfo().identifiers(), "ISBN_13"),
                body.volumeInfo().pages(),
                normalizeStringCollection(body.volumeInfo().categories()),
                extractCover(body.volumeInfo().coverURLS())
        );
    }
}

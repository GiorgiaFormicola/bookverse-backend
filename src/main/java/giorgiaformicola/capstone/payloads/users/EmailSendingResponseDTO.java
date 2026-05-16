package giorgiaformicola.capstone.payloads.users;

import java.time.LocalDateTime;

public record EmailSendingResponseDTO(
        String message,
        LocalDateTime timestamp
) {
}

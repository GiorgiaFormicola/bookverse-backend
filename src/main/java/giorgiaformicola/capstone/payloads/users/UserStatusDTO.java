package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.NotNull;

public record UserStatusDTO(
        @NotNull(message = "User status is required")
        Boolean isActive
) {
}

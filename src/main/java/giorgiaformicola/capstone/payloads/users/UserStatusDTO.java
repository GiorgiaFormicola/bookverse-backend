package giorgiaformicola.capstone.payloads.users;

import jakarta.validation.constraints.NotNull;

public record UserStatusDTO(
        @NotNull(message = "Status is mandatory")
        Boolean isActive
) {
}

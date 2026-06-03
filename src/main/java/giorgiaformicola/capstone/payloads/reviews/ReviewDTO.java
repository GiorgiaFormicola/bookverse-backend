package giorgiaformicola.capstone.payloads.reviews;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewDTO(
        @NotNull(message = "Rating is required")
        @Min(value = 0, message = "The rating can't be lower than 0")
        @Max(value = 5, message = "The rating can't be bigger than 5")
        int rating,

        @NotBlank(message = "Comment is required")
        String comment
) {
}
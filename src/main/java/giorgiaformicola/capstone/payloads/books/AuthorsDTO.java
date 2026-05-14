package giorgiaformicola.capstone.payloads.books;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AuthorsDTO(
        @NotNull(message = "Authors list is mandatory")
        List<String> authors
) {
}

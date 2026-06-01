package giorgiaformicola.capstone.payloads.users;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.payloads.books.LibraryBookDTO;

import java.util.List;

public record UserProfileDTO(
        User user,
        List<LibraryBookDTO> savedBooks,
        long totalReviews
) {
}

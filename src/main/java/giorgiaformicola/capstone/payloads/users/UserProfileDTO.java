package giorgiaformicola.capstone.payloads.users;

import giorgiaformicola.capstone.entities.User;

import java.util.List;

public record UserProfileDTO(
        User user,
        List<String> savedBooks
) {
}

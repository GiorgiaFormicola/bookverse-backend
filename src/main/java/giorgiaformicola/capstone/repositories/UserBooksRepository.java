package giorgiaformicola.capstone.repositories;

import giorgiaformicola.capstone.entities.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserBooksRepository extends JpaRepository<UserBook, UUID> {
    List<UserBook> findUserBookByUser_Id(UUID userId);

    boolean existsByUser_IdAndBook_Id(UUID userId, UUID bookId);

    boolean existsByUser_IdAndBook_GoogleId(UUID userId, String googleId);

    Optional<UserBook> findUserBookByUser_IdAndBook_GoogleId(UUID userId, String googleId);
}

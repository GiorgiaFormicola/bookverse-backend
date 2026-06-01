package giorgiaformicola.capstone.repositories;

import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.enums.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBooksRepository extends JpaRepository<UserBook, UUID>, JpaSpecificationExecutor<UserBook> {
    List<UserBook> findUserBookByUser_Id(UUID userId);

    boolean existsByUser_IdAndBook_Id(UUID userId, UUID bookId);

    boolean existsByUser_IdAndBook_GoogleId(UUID userId, String googleId);

    Optional<UserBook> findUserBookByUser_IdAndBook_GoogleId(UUID userId, String googleId);

    void deleteByUser_Id(UUID userId);

    void deleteByBook_Id(UUID bookId);

    long countByBook_GoogleId(String googleId);

    long countByBook_GoogleIdAndStatus(String googleId, StatusType status);
}

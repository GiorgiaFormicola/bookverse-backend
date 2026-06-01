package giorgiaformicola.capstone.repositories;

import giorgiaformicola.capstone.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewsRepository extends JpaRepository<Review, UUID>, JpaSpecificationExecutor<Review> {
    boolean existsByUser_IdAndBook_GoogleId(UUID userId, String googleId);

    void deleteByUser_Id(UUID userId);

    void deleteByBook_Id(UUID bookId);

    Optional<Review> findByUser_IdAndBook_GoogleId(UUID userId, String googleId);

    long countByBook_GoogleId(String googleId);

    long countByUser_Id(UUID userId);
}

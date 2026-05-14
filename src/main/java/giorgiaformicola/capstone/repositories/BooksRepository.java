package giorgiaformicola.capstone.repositories;

import giorgiaformicola.capstone.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BooksRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    boolean existsByGoogleId(String googleId);

    Optional<Book> findByGoogleId(String googleId);
}

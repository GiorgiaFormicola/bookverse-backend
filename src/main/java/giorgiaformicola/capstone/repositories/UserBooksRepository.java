package giorgiaformicola.capstone.repositories;

import giorgiaformicola.capstone.entities.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserBooksRepository extends JpaRepository<UserBook, UUID> {
}

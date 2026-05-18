package giorgiaformicola.capstone.specifications;

import giorgiaformicola.capstone.entities.Review;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewsSpecification {
    public static Specification<Review> filter(
            String googleId,
            UUID userId,
            Integer minRating,
            Integer maxRating
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (googleId != null && !googleId.isBlank()) {
                predicates.add(cb.equal((root.get("book").get("googleId")), googleId));
            }

            if (userId != null) {
                predicates.add(cb.equal((root.get("user").get("id")), userId));
            }

            if (minRating != null && minRating >= 0 && minRating <= 5) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            if (maxRating != null && maxRating >= 0 && maxRating <= 5) {
                predicates.add(cb.lessThanOrEqualTo(root.get("rating"), maxRating));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

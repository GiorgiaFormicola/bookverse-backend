package giorgiaformicola.capstone.specifications;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.entities.Review;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.enums.StatusType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserBooksSpecification {
    public static Specification<UserBook> filter(
            UUID userId,
            String title,
            String author,
            String publisher,
            String isbn10,
            String isbn13,
            String category,
            Boolean isPublic,
            StatusType status,
            Boolean reviewed
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("book").get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (author != null && !author.isBlank()) {
                Join<UserBook, Book> bookJoin = root.join("book");
                Join<Book, String> authorsJoin = bookJoin.join("authors");
                predicates.add(cb.like(cb.lower(authorsJoin), "%" + author.toLowerCase() + "%"));
            }

            if (publisher != null && !publisher.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("book").get("publisher")), "%" + publisher.toLowerCase() + "%"));
            }

            if (isbn10 != null && !isbn10.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("book").get("isbn10")), "%" + isbn10.toLowerCase() + "%"));
            }

            if (isbn13 != null && !isbn13.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("book").get("isbn13")), "%" + isbn13.toLowerCase() + "%"));
            }

            if (category != null && !category.isBlank()) {
                Join<UserBook, Book> bookJoin = root.join("book");
                Join<Book, String> categoriesJoin = bookJoin.join("categories");
                predicates.add(cb.like(cb.lower(categoriesJoin), "%" + category.toLowerCase() + "%"));
            }

            if (isPublic != null) {
                predicates.add(cb.equal(root.get("isPublic"), isPublic));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }


            if (reviewed != null) {

                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Review> reviewRoot = subquery.from(Review.class);
                subquery.select(reviewRoot.get("book").get("id"))
                        .where(cb.equal(reviewRoot.get("user").get("id"), userId));
                if (reviewed) {
                    predicates.add(root.get("book").get("id").in(subquery));
                } else {
                    predicates.add(cb.not(root.get("book").get("id").in(subquery)));
                }
            }

            /*query.distinct(true);*/
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

package giorgiaformicola.capstone.specifications;

import giorgiaformicola.capstone.entities.Book;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BooksSpecification {
    public static Specification<Book> filter(
            String title,
            String googleId,
            String author,
            String publisher,
            String isbn10,
            String isbn13,
            String category,
            Boolean missingTitle,
            Boolean missingAuthor,
            Boolean missingPublisher,
            Boolean missingIsbn10,
            Boolean missingIsbn13,
            Boolean missingCategory,
            Boolean missingPublishedDate,
            Boolean missingDescription,
            Boolean missingPages,
            Boolean missingCoverURL
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Boolean.TRUE.equals(missingTitle)) {
                predicates.add(cb.isNull(root.get("title")));
            } else if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (googleId != null && !googleId.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("googleId")), "%" + googleId.toLowerCase() + "%"));
            }

            if (Boolean.TRUE.equals(missingAuthor)) {
                predicates.add(cb.isEmpty(root.get("authors")));
            } else if (author != null && !author.isBlank()) {
                Join<Book, String> authorsJoin = root.join("authors");
                predicates.add(cb.like(cb.lower(authorsJoin), "%" + author.toLowerCase() + "%"));
            }

            if (Boolean.TRUE.equals(missingPublisher)) {
                predicates.add(cb.isNull(root.get("publisher")));
            } else if (publisher != null && !publisher.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%"));
            }

            if (Boolean.TRUE.equals(missingIsbn10)) {
                predicates.add(cb.isNull(root.get("isbn10")));
            } else if (isbn10 != null && !isbn10.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("isbn10")), "%" + isbn10.toLowerCase() + "%"));
            }

            if (Boolean.TRUE.equals(missingIsbn13)) {
                predicates.add(cb.isNull(root.get("isbn13")));
            } else if (isbn13 != null && !isbn13.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("isbn13")), "%" + isbn13.toLowerCase() + "%"));
            }

            if (Boolean.TRUE.equals(missingCategory)) {
                predicates.add(cb.isEmpty(root.get("categories")));
            } else if (category != null && !category.isBlank()) {
                Join<Book, String> categoriesJoin = root.join("categories");
                predicates.add(cb.like(cb.lower(categoriesJoin), "%" + category.toLowerCase() + "%"));
            }

            if (Boolean.TRUE.equals(missingPublishedDate)) {
                predicates.add(cb.isNull(root.get("publishedDate")));
            }

            if (Boolean.TRUE.equals(missingDescription)) {
                predicates.add(cb.isNull(root.get("description")));
            }

            if (Boolean.TRUE.equals(missingPages)) {
                predicates.add(cb.isNull(root.get("pages")));
            }

            if (Boolean.TRUE.equals(missingCoverURL)) {
                predicates.add(cb.isNull(root.get("coverURL")));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> filter(
            String title,
            String author,
            String publisher,
            String isbn10,
            String isbn13,
            String category
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (author != null && !author.isBlank()) {
                Join<Book, String> authorsJoin = root.join("authors");
                predicates.add(cb.like(cb.lower(authorsJoin), "%" + author.toLowerCase() + "%"));
            }

            if (publisher != null && !publisher.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("publisher")), "%" + publisher.toLowerCase() + "%"));
            }

            if (isbn10 != null && !isbn10.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("isbn10")), "%" + isbn10.toLowerCase() + "%"));
            }

            if (isbn13 != null && !isbn13.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("isbn13")), "%" + isbn13.toLowerCase() + "%"));
            }

            if (category != null && !category.isBlank()) {
                Join<Book, String> categoriesJoin = root.join("categories");
                predicates.add(cb.like(cb.lower(categoriesJoin), "%" + category.toLowerCase() + "%"));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

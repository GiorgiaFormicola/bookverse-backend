package giorgiaformicola.capstone.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Column(name = "google_id", unique = true)
    String googleId;

    @Column(columnDefinition = "text", nullable = false)
    String title;

    @Column(columnDefinition = "text", nullable = false)
    String authors;

    @Column(nullable = false)
    String publisher;

    @Column(name = "published_date", nullable = false)
    String publishedDate;

    @Column(columnDefinition = "text", nullable = false)
    String description;

    @Column(name = "isbn_10", unique = true)
    String isbn10;

    @Column(name = "isbn_13", unique = true)
    String isbn13;

    @Column(nullable = false)
    String pages;

    @Column(columnDefinition = "text", nullable = false)
    String categories;

    @Column(name = "cover_url", columnDefinition = "text", nullable = false)
    String coverURL;

    @Id
    @GeneratedValue
    private UUID id;

    protected Book() {
    }

    public Book(String googleId, String title, String authors, String publisher, String publishedDate, String description, String isbn10, String isbn13, String pages, String categories, String coverURL) {
        this.googleId = googleId;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.pages = pages;
        this.categories = categories;
        this.coverURL = coverURL;
    }
}

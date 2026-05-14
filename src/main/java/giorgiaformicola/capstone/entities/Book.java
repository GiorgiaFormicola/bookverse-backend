package giorgiaformicola.capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    /*@Column(columnDefinition = "text", nullable = false)*/
    @Column(columnDefinition = "text")
    private String title;

    @Column(name = "author", nullable = false)
    @CollectionTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @ElementCollection
    private List<String> authors;

    /*@Column(nullable = false)*/
    private String publisher;

    /*@Column(name = "published_date", nullable = false)*/
    @Column(name = "published_date")
    private String publishedDate;

    /*@Column(columnDefinition = "text", nullable = false)*/
    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "isbn_10", unique = true)
    private String isbn10;

    @Column(name = "isbn_13", unique = true)
    private String isbn13;

    /*@Column(nullable = false)*/
    private Long pages;

    @Column(name = "category", nullable = false)
    @CollectionTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @ElementCollection
    private List<String> categories;

    /*@Column(name = "cover_url", columnDefinition = "text", nullable = false)*/
    @Column(name = "cover_url", columnDefinition = "text")
    private String coverURL;

    protected Book() {
    }

    public Book(String googleId, String title, List<String> authors, String publisher, String publishedDate, String description, String isbn10, String isbn13, Long pages, List<String> categories, String coverURL) {
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

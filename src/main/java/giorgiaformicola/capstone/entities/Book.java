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
    @Column(columnDefinition = "text", nullable = false)
    String title;
    @Column(columnDefinition = "text", nullable = false)
    String authors;
    @Column(columnDefinition = "text", nullable = false)
    String description;
    @Column(nullable = false)
    String pages;
    @Column(nullable = false)
    String publisher;
    @Column(name = "cover_url", columnDefinition = "text", nullable = false)
    String coverURL;
    @Column(name = "publish_date", nullable = false)
    String publishDate;
    @Column(name = "isbn_10", unique = true)
    String isbn10;
    @Column(name = "isbn_13", unique = true)
    String isbn13;
    @Column(name = "open_library_id", unique = true)
    String openLibraryId;
    @Id
    @GeneratedValue
    private UUID id;

    protected Book() {
    }

    public Book(String title, String authors, String description, String pages, String publisher, String coverURL, String publishDate, String isbn10, String isbn13, String openLibraryId) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.pages = pages;
        this.publisher = publisher;
        this.coverURL = coverURL;
        this.publishDate = publishDate;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.openLibraryId = openLibraryId;
    }
}

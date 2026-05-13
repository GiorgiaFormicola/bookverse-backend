package giorgiaformicola.capstone.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Column(name = "category", nullable = false)
    @CollectionTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id")
    )

    @ElementCollection
    List<String> categories;
    @Column(name = "cover_url", columnDefinition = "text", nullable = false)
    String coverURL;
    /* @Column(nullable = false)
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
 */
    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;
    @Column(columnDefinition = "text", nullable = false)
    private String title;
    @Column(name = "author", nullable = false)
    @CollectionTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @ElementCollection
    private List<String> authors;
    @Id
    @GeneratedValue
    private UUID id;

    protected Book() {
    }

    public Book(String googleId, String title, List<String> authors, List<String> categories, String coverURL) {
        this.googleId = googleId;
        this.title = title;
        this.authors = authors;
        this.categories = categories;
        this.coverURL = coverURL;
    }
}

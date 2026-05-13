package giorgiaformicola.capstone.entities;

import giorgiaformicola.capstone.enums.StatusType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_books", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "book_id"})})
@Getter
@Setter
public class UserBook {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "public", nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType status;

    protected UserBook() {
    }

    public UserBook(Book book, User user) {
        this.book = book;
        this.user = user;
        this.isPublic = false;
        this.status = StatusType.TO_READ;
    }
}

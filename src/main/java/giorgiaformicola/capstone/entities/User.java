package giorgiaformicola.capstone.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import giorgiaformicola.capstone.enums.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "display_name", nullable = false, length = 30)
    private String displayName;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDate birthdate;

    @Column(name = "profile_image_url", columnDefinition = "text", nullable = false)
    private String profileImageURL;

    @Column(nullable = false, length = 500)
    private String bio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(nullable = false)
    private boolean active;

    protected User() {
    }

    public User(String username, String email, String password, String displayName, LocalDate birthdate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.birthdate = birthdate;
        this.profileImageURL = "https://picsum.photos/id/24/300/300";
        this.bio = "";
        this.role = RoleType.USER;
        this.active = true;
    }
}

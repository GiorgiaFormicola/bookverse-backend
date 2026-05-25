package giorgiaformicola.capstone.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import giorgiaformicola.capstone.enums.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@JsonIgnoreProperties({"accountNonExpired", "accountNonLocked", "authorities", "credentialsNonExpired", "enabled"})
public class User implements UserDetails {
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

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDate birthdate;

    @Column(name = "profile_picture_url", columnDefinition = "text", nullable = false)
    private String profilePictureURL;

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
        this.profilePictureURL = "https://res.cloudinary.com/giorgiaf/image/upload/q_auto/f_auto/v1779695172/anonymous_user_d1sjpz.png";
        this.bio = "";
        this.role = RoleType.USER;
        this.active = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}

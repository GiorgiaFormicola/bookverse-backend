package giorgiaformicola.capstone.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime expiry;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    protected PasswordResetToken() {
    }

    public PasswordResetToken(User user) {
        this.expiry = LocalDateTime.now().plusHours(1);
        this.user = user;
    }
}

package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.PasswordResetToken;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.repositories.PasswordResetTokensRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokensService {
    private final PasswordResetTokensRepository passwordResetTokensRepository;


    public PasswordResetTokensService(PasswordResetTokensRepository passwordResetTokensRepository) {
        this.passwordResetTokensRepository = passwordResetTokensRepository;

    }
    
    public PasswordResetToken createToken(User user) {

        passwordResetTokensRepository.findByUser_Id(user.getId())
                .ifPresent(passwordResetTokensRepository::delete);

        PasswordResetToken resetToken = new PasswordResetToken(user);
        return passwordResetTokensRepository.save(resetToken);
    }

    public PasswordResetToken findById(UUID tokenId) {
        return passwordResetTokensRepository.findById(tokenId)
                .orElseThrow(() -> new NotFoundException("Invalid or expired reset link"));
    }

    public void validateToken(PasswordResetToken resetToken) {
        if (resetToken.getExpiry().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Reset link has expired, please request a new one");
    }

    public void findByIdAndDeleteToken(UUID tokenId) {
        PasswordResetToken found = findById(tokenId);
        passwordResetTokensRepository.delete(found);
    }


}

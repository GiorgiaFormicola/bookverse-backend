package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.payloads.RegistrationDTO;
import giorgiaformicola.capstone.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bCryptEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder bCryptEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptEncoder = bCryptEncoder;
    }

    public User save(RegistrationDTO body) {
        if (usersRepository.existsByUsername(body.username()))
            throw new BadRequestException("Username " + body.username() + "already in use!");
        if (usersRepository.existsByEmail(body.email()))
            throw new BadRequestException("Email " + body.email() + "already in use!");
        User newUser = new User(body.username(), body.email(), this.bCryptEncoder.encode(body.password()), body.displayName(), body.birthdate());
        return this.usersRepository.save(newUser);
    }

    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
    }

}

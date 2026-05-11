package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.exceptions.UnauthorizedException;
import giorgiaformicola.capstone.payloads.LoginDTO;
import giorgiaformicola.capstone.payloads.RegistrationDTO;
import giorgiaformicola.capstone.repositories.UsersRepository;
import giorgiaformicola.capstone.security.TokenTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bCryptEncoder;
    private final TokenTools tokenTools;

    public UsersService(UsersRepository usersRepository, PasswordEncoder bCryptEncoder, TokenTools tokenTools) {
        this.usersRepository = usersRepository;
        this.bCryptEncoder = bCryptEncoder;
        this.tokenTools = tokenTools;
    }

    public User save(RegistrationDTO body) {
        if (usersRepository.existsByUsername(body.username()))
            throw new BadRequestException("Username " + body.username() + "already in use!");
        if (usersRepository.existsByEmail(body.email()))
            throw new BadRequestException("Email " + body.email() + "already in use!");
        User newUser = new User(body.username(), body.email(), this.bCryptEncoder.encode(body.password()), body.displayName(), body.birthdate());
        User savedUser = this.usersRepository.save(newUser);
        log.info("New user with id" + savedUser.getId() + "successfully registered!");
        return savedUser;
    }

    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
    }


    public String checkUserCredentialsAndGenerateToken(LoginDTO body) {
        User found = this.usersRepository.findByEmail(body.email()).orElseThrow(() -> new UnauthorizedException("Wrong credentials supplied"));
        if (!bCryptEncoder.matches(body.password(), found.getPassword()))
            throw new UnauthorizedException("Wrong credentials supplied");
        return this.tokenTools.generateToken(found);
    }

    public Page<User> findAll(Specification<User> specification, int page, int size, String sortBy, String order) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 10;

        if (!sortBy.equals("username") && !sortBy.equals("email") && !sortBy.equals("id")) sortBy = "username";

        Pageable pageable = switch (order) {
            case "asc" -> PageRequest.of(page, size, Sort.by(sortBy));
            case "desc" -> PageRequest.of(page, size, Sort.by(sortBy).reverse());
            default -> PageRequest.of(page, size, Sort.by(sortBy));
        };

        return this.usersRepository.findAll(specification, pageable);
    }

    ;

}

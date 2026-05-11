package giorgiaformicola.capstone.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.exceptions.UnauthorizedException;
import giorgiaformicola.capstone.exceptions.ValidationException;
import giorgiaformicola.capstone.payloads.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bCryptEncoder;
    private final TokenTools tokenTools;
    private final Cloudinary cloudinary;

    public UsersService(UsersRepository usersRepository, PasswordEncoder bCryptEncoder, TokenTools tokenTools, Cloudinary cloudinary) {
        this.usersRepository = usersRepository;
        this.bCryptEncoder = bCryptEncoder;
        this.tokenTools = tokenTools;
        this.cloudinary = cloudinary;
    }

    public User save(RegistrationDTO body) {
        if (usersRepository.existsByUsername(body.username()))
            throw new BadRequestException("Username " + body.username() + " already in use!");
        if (usersRepository.existsByEmail(body.email().toLowerCase()))
            throw new BadRequestException("Email " + body.email().toLowerCase() + " already in use!");
        User newUser = new User(body.username(), body.email().toLowerCase(), this.bCryptEncoder.encode(body.password()), body.displayName(), body.birthdate());
        User savedUser = this.usersRepository.save(newUser);
        log.info("New user with id" + savedUser.getId() + "successfully registered!");
        return savedUser;
    }

    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
    }


    public String checkUserCredentialsAndGenerateToken(LoginDTO body) {
        User found = this.usersRepository.findByEmail(body.email().toLowerCase()).orElseThrow(() -> new UnauthorizedException("Wrong credentials supplied"));
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

    //TODO: check if active
    public User findByIdAndUpdateProfile(UUID userId, ProfileUpdateDTO body) {
        User found = this.findById(userId);
        if (!found.getUsername().equals(body.username())) {
            if (this.usersRepository.existsByUsername(body.username()))
                throw new BadRequestException("Username " + body.username() + " already in use!");
        }
        found.setUsername(body.username());
        found.setDisplayName(body.displayName());
        found.setBio(body.bio());
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdateProfilePicture(UUID userId, MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/") || file.isEmpty())
            throw new ValidationException("Invalid type of file provided");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new ValidationException("File size must be smaller than 2 MB");
        User found = this.findById(userId);
        try {
            Map result = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            found.setProfilePictureURL((String) result.get("secure_url"));
            return this.usersRepository.save(found);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User findByIdAndUpdateEmail(UUID userId, EmailUpdateDTO body) {
        User found = this.findById(userId);
        if (!found.getEmail().equals(body.email().toLowerCase())) {
            if (this.usersRepository.existsByEmail(body.email().toLowerCase()))
                throw new BadRequestException("Email " + body.email().toLowerCase() + " already in use!");
            found.setEmail(body.email().toLowerCase());
        }
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdatePassword(UUID userId, PasswordUpdateDTO body) {
        User found = this.findById(userId);
        if (!bCryptEncoder.matches(body.currentPassword(), found.getPassword()))
            throw new UnauthorizedException("Wrong password supplied");
        if (bCryptEncoder.matches(body.newPassword(), found.getPassword()))
            throw new BadRequestException("New password must be different from the current one");
        found.setPassword(this.bCryptEncoder.encode(body.newPassword()));
        return this.usersRepository.save(found);
    }

    ;

    //TODO: handle deleting related records in the DB
    public void findByIdAndDelete(UUID userId) {
        User found = this.findById(userId);
        this.usersRepository.delete(found);
    }

    ;

}

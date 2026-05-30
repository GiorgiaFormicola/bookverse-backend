package giorgiaformicola.capstone.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.enums.RoleType;
import giorgiaformicola.capstone.exceptions.*;
import giorgiaformicola.capstone.payloads.books.LibraryBookDTO;
import giorgiaformicola.capstone.payloads.users.*;
import giorgiaformicola.capstone.repositories.ReviewsRepository;
import giorgiaformicola.capstone.repositories.UserBooksRepository;
import giorgiaformicola.capstone.repositories.UsersRepository;
import giorgiaformicola.capstone.security.TokenTools;
import giorgiaformicola.capstone.tools.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bCryptEncoder;
    private final TokenTools tokenTools;
    private final Cloudinary cloudinary;
    private final UserBooksRepository userBooksRepository;
    private final EmailSender emailSender;
    private final ReviewsRepository reviewsRepository;

    public UsersService(UsersRepository usersRepository, PasswordEncoder bCryptEncoder, TokenTools tokenTools, Cloudinary cloudinary, UserBooksRepository userBooksRepository, EmailSender emailSender, ReviewsRepository reviewsRepository) {
        this.usersRepository = usersRepository;
        this.bCryptEncoder = bCryptEncoder;
        this.tokenTools = tokenTools;
        this.cloudinary = cloudinary;
        this.userBooksRepository = userBooksRepository;
        this.emailSender = emailSender;
        this.reviewsRepository = reviewsRepository;
    }

    public User save(RegistrationDTO body) {
        if (usersRepository.existsByUsername(body.username().toLowerCase()))
            throw new BadRequestException("Username " + body.username().toLowerCase() + " already in use!");
        if (usersRepository.existsByEmail(body.email().toLowerCase()))
            throw new BadRequestException("Email " + body.email().toLowerCase() + " already in use!");
        User newUser = new User(body.username().toLowerCase(), body.email().toLowerCase(), this.bCryptEncoder.encode(body.password()), body.displayName(), body.birthdate());
        User savedUser = this.usersRepository.save(newUser);
        log.info("New user with id" + savedUser.getId() + "successfully registered!");
        emailSender.sendRegistrationEmail(savedUser);
        return savedUser;
    }

    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
    }

    public User checkIfUserIsActive(UUID userId) {
        User found = findById(userId);
        if (!found.isActive())
            /*throw new UnauthorizedException("Your account has been deactivated. Send us an email to check what happened.");*/
            throw new AccountDisabledException("Your account has been deactivated. Send us an email to check what happened.");
        return found;
    }

    public UserProfileDTO getUserProfileById(UUID userId) {
        User found = findById(userId);
        List<LibraryBookDTO> books = userBooksRepository.findUserBookByUser_Id(userId).stream().map(result -> new LibraryBookDTO(result.getBook().getGoogleId(), result.isPublic(), result.getStatus())).toList();
        return new UserProfileDTO(found, books);
    }


    public String checkUserCredentialsAndGenerateToken(LoginDTO body) {
        User found = this.usersRepository.findByEmail(body.email().toLowerCase()).orElseThrow(() -> new UnauthorizedException("Wrong credentials supplied"));
        if (!bCryptEncoder.matches(body.password(), found.getPassword()))
            throw new UnauthorizedException("Wrong credentials supplied");
        if (!found.isActive())
            throw new AccountDisabledException("Your account has been disabled");
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

    public User findByIdAndUpdateProfile(UUID userId, ProfileUpdateDTO body) {
        User found = checkIfUserIsActive(userId);
        if (!found.getUsername().equals(body.username().toLowerCase())) {
            if (this.usersRepository.existsByUsername(body.username().toLowerCase()))
                throw new BadRequestException("Username " + body.username().toLowerCase() + " already in use!");
        }
        found.setUsername(body.username().toLowerCase());
        found.setDisplayName(body.displayName());
        found.setBio(body.bio());
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdateProfilePicture(UUID userId, MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/") || file.isEmpty())
            throw new ValidationException("Invalid type of file provided");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new ValidationException("File size must be smaller than 2 MB");
        User found = checkIfUserIsActive(userId);
        try {
            Map result = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            found.setProfilePictureURL((String) result.get("secure_url"));
            return this.usersRepository.save(found);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User findByIdAndUpdateEmail(UUID userId, EmailUpdateDTO body) {
        User found = checkIfUserIsActive(userId);
        if (!found.getEmail().equals(body.email().toLowerCase())) {
            if (this.usersRepository.existsByEmail(body.email().toLowerCase()))
                throw new BadRequestException("Email " + body.email().toLowerCase() + " already in use!");
            found.setEmail(body.email().toLowerCase());
            emailSender.sendEmailAfterEmailUpdate(found);
        }
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdatePassword(UUID userId, PasswordUpdateDTO body) {
        User found = checkIfUserIsActive(userId);
        if (!bCryptEncoder.matches(body.currentPassword(), found.getPassword()))
            throw new UnauthorizedException("Wrong password supplied");
        if (bCryptEncoder.matches(body.newPassword(), found.getPassword()))
            throw new BadRequestException("New password must be different from the current one");
        found.setPassword(this.bCryptEncoder.encode(body.newPassword()));
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdateRole(UUID adminId, UUID userId, UserRoleDTO body) {
        if (adminId.equals(userId)) throw new UnauthorizedException("You can't update you own role");
        User found = this.findById(userId);
        if (found.getRole().name().equals(body.role()))
            throw new BadRequestException("'" + body.role() + "' role already assigned to the user with id " + userId);
        found.setRole(RoleType.valueOf(body.role()));
        return this.usersRepository.save(found);
    }

    public User findByIdAndUpdateStatus(UUID adminId, UUID userId, UserStatusDTO body) {
        if (adminId.equals(userId)) throw new UnauthorizedException("You can't update you own status");
        User found = this.findById(userId);
        if (found.getRole().equals(RoleType.ADMIN))
            throw new BadRequestException("You can't change the status of an ADMIN");
        if (body.isActive().equals(found.isActive()))
            throw new BadRequestException("The status of the user with id " + userId + " is already set to " + (found.isActive() ? "'active'" : "'inactive'"));
        found.setActive(body.isActive());
        return this.usersRepository.save(found);
    }

    @Transactional
    public void findByIdAndDelete(UUID userId) {
        User found = this.findById(userId);
        this.userBooksRepository.deleteByUser_Id(found.getId());
        this.reviewsRepository.deleteByUser_Id(found.getId());
        this.usersRepository.delete(found);
    }


    public void sendReactivationRequest(ReactivationRequestDTO body) {
        User user = this.usersRepository.findByEmail(body.email()).orElseThrow(() -> new NotFoundException("User with email " + body.email() + "has not been found"));

        if (user.isActive()) {
            throw new BadRequestException("The provided user account is not disabled");
        }
        emailSender.sendReactivationRequestToAdmin(user);
        emailSender.sendReactivationConfirmationToUser(user);
    }


}

package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.enums.StatusType;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.exceptions.ValidationException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.BookStatusDTO;
import giorgiaformicola.capstone.payloads.books.BookVisibilityDTO;
import giorgiaformicola.capstone.payloads.books.UserLibraryBookDTO;
import giorgiaformicola.capstone.repositories.UserBooksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserBooksService {
    private final UserBooksRepository userBooksRepository;
    private final UsersService usersService;
    private final BooksService booksService;

    public UserBooksService(UserBooksRepository userBooksRepository, UsersService usersService, BooksService booksService) {
        this.userBooksRepository = userBooksRepository;
        this.usersService = usersService;
        this.booksService = booksService;
    }

    public UserBook save(UUID userId, UUID bookId) {
        if (userBooksRepository.existsByUser_IdAndBook_Id(userId, bookId))
            throw new BadRequestException("Book already saved in the user " + userId + "library");
        User userFound = usersService.findById(userId);
        Book bookFound = booksService.findById(bookId);
        return new UserBook(bookFound, userFound);
    }

    public boolean checkBookAlreadySavedByUser(UUID userId, String googleId) {
        return userBooksRepository.existsByUser_IdAndBook_GoogleId(userId, googleId);
    }

    public UserBook saveBookToUserLibrary(UUID userId, BookDetailDTO body) {
        User userFound = usersService.checkIfUserIsActive(userId);
        if (checkBookAlreadySavedByUser(userId, body.googleId()))
            throw new BadRequestException("Book already saved in the user " + userId + "library");
        try {
            Book bookFound = booksService.getByGoogleId(body.googleId());
            UserBook toSave = new UserBook(bookFound, userFound);
            UserBook saved = this.userBooksRepository.save(toSave);
            log.info("Book added to library: user={}, book={}", userId, body.googleId());
            return saved;
        } catch (NotFoundException ex) {
            Book newBook = booksService.save(body);
            UserBook toSave = new UserBook(newBook, userFound);
            UserBook saved = this.userBooksRepository.save(toSave);
            log.info("Book added to library: user={}, book={}", userId, body.googleId());
            return saved;
        }
    }

    public Page<UserLibraryBookDTO> findUserBooks(UUID userId, Specification<UserBook> specification, int page, int size, String sortBy, String order) {
        usersService.checkIfUserIsActive(userId);
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 20;
        String orderCriteria = switch (sortBy) {
            case "publisher" -> "book.publisher";
            case "pages" -> "book.pages";
            default -> "book.title";
        };

        Pageable pageable = switch (order) {
            case "asc" -> PageRequest.of(page, size, Sort.by(orderCriteria));
            case "desc" -> PageRequest.of(page, size, Sort.by(orderCriteria).reverse());
            default -> PageRequest.of(page, size, Sort.by(orderCriteria));
        };

        Page<UserBook> results = userBooksRepository.findAll(specification, pageable);
        return results.map(userBook -> new UserLibraryBookDTO(userBook.getBook(), userBook.getId(), userBook.isPublic(), userBook.getStatus()));
    }

    public UserBook updateBookVisibilityFromUserLibrary(UUID userId, String googleId, BookVisibilityDTO body) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        User userFound = usersService.checkIfUserIsActive(userId);
        UserBook found = userBooksRepository.findUserBookByUser_IdAndBook_GoogleId(userFound.getId(), googleId).orElseThrow(() -> new NotFoundException("The book with id '" + googleId + "' is not present in the user library"));
        if (found.isPublic() == body.isPublic())
            throw new BadRequestException("The book visibility is already set to " + (body.isPublic() ? "public" : "private"));
        found.setPublic(body.isPublic());
        UserBook updated = this.userBooksRepository.save(found);
        log.info("Book visibility updated: user={}, book={}, public={}", userId, googleId, body.isPublic());
        return updated;
    }

    public UserBook updateBookStatusFromUserLibrary(UUID userId, String googleId, BookStatusDTO body) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        User userFound = usersService.checkIfUserIsActive(userId);
        UserBook found = userBooksRepository.findUserBookByUser_IdAndBook_GoogleId(userFound.getId(), googleId).orElseThrow(() -> new NotFoundException("The book with id '" + googleId + "' is not present in the user library"));
        if (found.getStatus().equals(StatusType.valueOf(body.status())))
            throw new BadRequestException("The book status is already set to " + body.status());
        found.setStatus(StatusType.valueOf(body.status()));
        UserBook updated = this.userBooksRepository.save(found);
        log.info("Book status updated: user={}, book={}, status={}", userId, googleId, body.status());
        return updated;
    }

    public void deleteBookFromUserLibrary(UUID userId, String googleId) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        User userFound = usersService.checkIfUserIsActive(userId);
        UserBook found = userBooksRepository.findUserBookByUser_IdAndBook_GoogleId(userFound.getId(), googleId).orElseThrow(() -> new NotFoundException("The book with id '" + googleId + "' is not present in the user library"));
        userBooksRepository.delete(found);
        log.info("Book removed from library: user={}, book={}", userId, googleId);
    }
}

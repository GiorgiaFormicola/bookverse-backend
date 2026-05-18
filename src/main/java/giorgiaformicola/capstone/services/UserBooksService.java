package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.enums.StatusType;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.BookStatusDTO;
import giorgiaformicola.capstone.payloads.books.BookVisibilityDTO;
import giorgiaformicola.capstone.payloads.books.UserLibraryBookDTO;
import giorgiaformicola.capstone.repositories.UserBooksRepository;
import giorgiaformicola.capstone.repositories.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserBooksService {
    private final UserBooksRepository userBooksRepository;
    private final UsersService usersService;
    private final BooksService booksService;
    private final UsersRepository usersRepository;

    public UserBooksService(UserBooksRepository userBooksRepository, UsersService usersService, BooksService booksService, UsersRepository usersRepository) {
        this.userBooksRepository = userBooksRepository;
        this.usersService = usersService;
        this.booksService = booksService;
        this.usersRepository = usersRepository;
    }

    public UserBook save(UUID userId, UUID bookId) {
        if (userBooksRepository.existsByUser_IdAndBook_Id(userId, bookId))
            throw new BadRequestException("Book already saved in the user " + userId + "library");
        User userFound = usersService.findById(userId);
        Book bookFound = booksService.findById(bookId);
        return new UserBook(bookFound, userFound);
    }

    public UserBook saveBookToUserLibrary(UUID userId, BookDetailDTO body) {
        User userFound = usersService.checkIfUserIsActive(userId);
        if (userBooksRepository.existsByUser_IdAndBook_GoogleId(userId, body.googleId()))
            throw new BadRequestException("Book already saved in the user " + userId + "library");
        try {
            Book bookFound = booksService.getByGoogleId(body.googleId());
            UserBook toSave = new UserBook(bookFound, userFound);
            return this.userBooksRepository.save(toSave);
        } catch (NotFoundException ex) {
            Book newBook = booksService.save(body);
            UserBook toSave = new UserBook(newBook, userFound);
            return this.userBooksRepository.save(toSave);
        }
    }

    public Page<UserLibraryBookDTO> findUserBooks(UUID userId, Specification<UserBook> specification, int page, int size, String sortBy, String order) {
        usersService.checkIfUserIsActive(userId);
        /*List<UserBook> results = userBooksRepository.findUserBookByUser_Id(found.getId());*/
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
        /*return results.stream().map(result -> new UserLibraryBookDTO(result.getBook(), result.getId(), result.isPublic(), result.getStatus())).toList();*/
    }

    public UserBook updateBookVisibilityFromUserLibrary(UUID userId, String googleId, BookVisibilityDTO body) {
        User userFound = usersService.checkIfUserIsActive(userId);
        UserBook found = userBooksRepository.findUserBookByUser_IdAndBook_GoogleId(userFound.getId(), googleId).orElseThrow(() -> new NotFoundException("The book with id '" + googleId + "' is not present in the user library"));
        if (found.isPublic() == body.isPublic())
            throw new BadRequestException("The book visibility is already set to " + (body.isPublic() ? "public" : "private"));
        found.setPublic(body.isPublic());
        return this.userBooksRepository.save(found);
    }

    public UserBook updateBookStatusFromUserLibrary(UUID userId, String googleId, BookStatusDTO body) {
        User userFound = usersService.checkIfUserIsActive(userId);
        UserBook found = userBooksRepository.findUserBookByUser_IdAndBook_GoogleId(userFound.getId(), googleId).orElseThrow(() -> new NotFoundException("The book with id '" + googleId + "' is not present in the user library"));
        if (found.getStatus().equals(StatusType.valueOf(body.status())))
            throw new BadRequestException("The book status is already set to " + body.status());
        found.setStatus(StatusType.valueOf(body.status()));
        return this.userBooksRepository.save(found);
    }

    public void deleteBookFromUserLibrary(UUID userId, String googleId) {
        User userFound = usersService.checkIfUserIsActive(userId);
        UserBook found = userBooksRepository.findUserBookByUser_IdAndBook_GoogleId(userFound.getId(), googleId).orElseThrow(() -> new NotFoundException("The book with id '" + googleId + "' is not present in the user library"));
        userBooksRepository.delete(found);
    }
}

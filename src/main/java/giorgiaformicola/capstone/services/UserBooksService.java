package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.entities.UserBook;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.UserLibraryBookDTO;
import giorgiaformicola.capstone.repositories.UserBooksRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public UserBook saveBookToUserLibrary(UUID userId, BookDetailDTO body) {
        User userFound = usersService.findById(userId);
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

    public List<UserLibraryBookDTO> findUserBooks(UUID userId) {
        User found = usersService.findById(userId);
        List<UserBook> results = userBooksRepository.findUserBookByUser_Id(found.getId());
        return results.stream().map(result -> new UserLibraryBookDTO(result.getBook(), result.getId(), result.isPublic(), result.getStatus())).toList();
    }
}

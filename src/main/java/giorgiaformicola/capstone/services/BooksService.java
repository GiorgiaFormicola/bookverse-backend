package giorgiaformicola.capstone.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import giorgiaformicola.capstone.clients.GoogleBooksClient;
import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.exceptions.SearchException;
import giorgiaformicola.capstone.exceptions.ValidationException;
import giorgiaformicola.capstone.payloads.books.*;
import giorgiaformicola.capstone.repositories.BooksRepository;
import giorgiaformicola.capstone.repositories.UserBooksRepository;
import giorgiaformicola.capstone.tools.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;
    private final GoogleBooksClient googleBooksClient;
    private final BooksRepository booksRepository;
    private final UserBooksRepository userBooksRepository;
    private final Cloudinary cloudinary;

    public BooksService(OpenLibraryClient openLibraryClient, GoogleBooksClient googleBooksClient, BooksRepository booksRepository, UserBooksRepository userBooksRepository, Cloudinary cloudinary) {
        this.openLibraryClient = openLibraryClient;
        this.googleBooksClient = googleBooksClient;
        this.booksRepository = booksRepository;
        this.userBooksRepository = userBooksRepository;
        this.cloudinary = cloudinary;
    }

    /*public OpenLibraryWorksSearchResponseDTO searchWorksFromOpenLibrary(String query, int page) {
        return openLibraryClient.searchWorks(query, page);
    }

    public OpenLibraryBookDetailsDTO getBookFromOpenLibrary(String editionId) {
        return openLibraryClient.searchBook(editionId);
    }*/

    public Book findById(UUID bookId) {
        return this.booksRepository.findById(bookId).orElseThrow(() -> new NotFoundException("book", bookId));
    }

    public Page<Book> findAll(Specification<Book> specification, int page, int size, String sortBy, String order) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 20;

        Pageable pageable = switch (order) {
            case "asc" -> PageRequest.of(page, size, Sort.by(sortBy));
            case "desc" -> PageRequest.of(page, size, Sort.by(sortBy).reverse());
            default -> PageRequest.of(page, size, Sort.by(sortBy));
        };

        return this.booksRepository.findAll(specification, pageable);
    }

    public List<BookDetailDTO> searchBooksFromGoogle(String query) {
        if (query.isBlank()) throw new BadRequestException("You must provide a valid query string");
        List<BookDetailDTO> itemsFiltered = googleBooksClient.searchBooks(query)
                .items()
                .stream()
                .filter(item -> item != null && item.id() != null && item.volumeInfo() != null).map(item -> BookMapper.mapFromGoogleItemDTO(item)).toList();
        System.out.println("Items filtered = " + itemsFiltered.size());
        return itemsFiltered;
    }

    public List<Book> searchBooks(String query) {
        try {
            List<BookDetailDTO> booksFromGoogle = searchBooksFromGoogle(query);
            List<Book> books = booksFromGoogle.stream().map(bookDetailDTO -> new Book(
                    bookDetailDTO.googleId(),
                    bookDetailDTO.title(),
                    bookDetailDTO.authors(),
                    bookDetailDTO.publisher(),
                    bookDetailDTO.publishedDate(),
                    bookDetailDTO.description(),
                    bookDetailDTO.isbn10(),
                    bookDetailDTO.isbn13(),
                    bookDetailDTO.pages(),
                    bookDetailDTO.categories(),
                    bookDetailDTO.coverURL())).toList();

            for (Book book : books) {
                try {
                    this.save(book);
                } catch (BadRequestException ex) {
                    // skip this book
                }
            }

            return books;
        } catch (Exception ex) {
            List<Book> booksFromDBByTitle = booksRepository.findAllByTitleContainsIgnoreCase(query);
            if (booksFromDBByTitle.isEmpty()) throw new SearchException();
            return booksFromDBByTitle;
        }
    }

    public BookDetailDTO searchBookByIdFromGoogle(String googleId) {
        if (googleId == null || googleId.isBlank()) throw new BadRequestException("You must provide a valid id");
        GoogleItemDTO bookFromGoogle = googleBooksClient.searchBookByGoogleId(googleId);
        return BookMapper.mapFromGoogleItemDTO(bookFromGoogle);
    }

    public Book save(BookDetailDTO body) {
        if (booksRepository.existsByGoogleId(body.googleId()))
            throw new BadRequestException("Book already saved in the database");
        Book newBook = new Book(
                body.googleId(),
                body.title(),
                body.authors(),
                body.publisher(),
                body.publishedDate(),
                body.description(),
                body.isbn10(),
                body.isbn13(),
                body.pages(),
                body.categories(),
                body.coverURL());
        return booksRepository.save(newBook);
    }

    public Book save(Book book) {
        if (booksRepository.existsByGoogleId(book.getGoogleId()))
            throw new BadRequestException("Book already saved in the database");
        return booksRepository.save(book);
    }

    public Book getByGoogleId(String googleId) {
        if (googleId == null || googleId.isBlank())
            throw new BadRequestException("You must provide a valid id");
        return booksRepository.findByGoogleId(googleId).orElseThrow(() -> new NotFoundException("book", googleId));
    }

    ;


    public BookDetailDTO getBookDetailsByGoogleId(String googleId) {
        try {
            Book found = this.getByGoogleId(googleId);
            return new BookDetailDTO(
                    found.getGoogleId(),
                    found.getTitle(),
                    found.getAuthors(),
                    found.getPublisher(),
                    found.getPublishedDate(),
                    found.getDescription(),
                    found.getIsbn10(),
                    found.getIsbn13(),
                    found.getPages(),
                    found.getCategories(),
                    found.getCoverURL());
        } catch (NotFoundException ex) {
            return searchBookByIdFromGoogle(googleId);
        }
    }

    @Transactional
    public void findByIdAndDelete(String googleId) {
        Book found = getByGoogleId(googleId);
        userBooksRepository.deleteByBook_Id(found.getId());
        booksRepository.delete(found);
    }

    public Book findByIdAndUpdateBookInfo(String googleId, BookInfoDTO body) {
        Book found = getByGoogleId(googleId);
        if (body.title() != null && !body.title().isBlank()) {
            found.setTitle(body.title().trim());
        }
        if (body.publisher() != null && !body.publisher().isBlank()) {
            found.setPublisher(body.publisher().trim());
        }
        if (body.publishedDate() != null && !body.publishedDate().isBlank()) {
            found.setPublishedDate(body.publishedDate().trim());
        }
        if (body.description() != null && !body.description().isBlank()) {
            found.setDescription(body.description().trim());
        }
        if (body.isbn10() != null && !body.isbn10().isBlank()) {
            found.setIsbn10(body.isbn10().trim());
        }

        if (body.isbn13() != null && !body.isbn13().isBlank()) {
            found.setIsbn13(body.isbn13().trim());
        }

        if (body.pages() != null && body.pages() > 0) {
            found.setPages(body.pages());
        }

        return booksRepository.save(found);
    }

    public Book findByIdAndUpdateBookCover(String googleId, MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/") || file.isEmpty())
            throw new ValidationException("Invalid type of file provided");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new ValidationException("File size must be smaller than 2 MB");
        Book found = getByGoogleId(googleId);
        try {
            Map result = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            found.setCoverURL((String) result.get("secure_url"));
            return this.booksRepository.save(found);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public Book findByIdAndUpdateBookAuthors(String googleId, AuthorsDTO body) {
        Book found = getByGoogleId(googleId);
        found.setAuthors(body.authors());
        return booksRepository.save(found);
    }

    @Transactional
    public Book findByIdAndUpdateBookCategories(String googleId, CategoriesDTO body) {
        Book found = getByGoogleId(googleId);
        found.setCategories(body.categories());
        return booksRepository.save(found);
    }
}

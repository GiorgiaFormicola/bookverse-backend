package giorgiaformicola.capstone.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import giorgiaformicola.capstone.clients.GoogleBooksClient;
import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.exceptions.*;
import giorgiaformicola.capstone.payloads.books.*;
import giorgiaformicola.capstone.repositories.BooksRepository;
import giorgiaformicola.capstone.repositories.ReviewsRepository;
import giorgiaformicola.capstone.repositories.UserBooksRepository;
import giorgiaformicola.capstone.specifications.BooksSpecification;
import giorgiaformicola.capstone.tools.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;
    private final GoogleBooksClient googleBooksClient;
    private final BooksRepository booksRepository;
    private final UserBooksRepository userBooksRepository;
    private final UsersService usersService;
    private final Cloudinary cloudinary;
    private final ReviewsRepository reviewsRepository;

    public BooksService(OpenLibraryClient openLibraryClient, GoogleBooksClient googleBooksClient, BooksRepository booksRepository, UserBooksRepository userBooksRepository, Cloudinary cloudinary, UsersService usersService, ReviewsRepository reviewsRepository) {
        this.openLibraryClient = openLibraryClient;
        this.googleBooksClient = googleBooksClient;
        this.booksRepository = booksRepository;
        this.userBooksRepository = userBooksRepository;
        this.cloudinary = cloudinary;
        this.usersService = usersService;
        this.reviewsRepository = reviewsRepository;
    }

    public Book findById(UUID bookId) {
        return this.booksRepository.findById(bookId).orElseThrow(() -> new NotFoundException("book", bookId));
    }

    //TODO: rivedi paginazione
    public Page<Book> findAll(Specification<Book> specification, int page, int size, String sortBy, String order) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 20;
        String orderCriteria = switch (sortBy) {
            case "publisher" -> "publisher";
            case "pages" -> "pages";
            default -> "title";
        };

        Pageable pageable = switch (order) {
            case "asc" -> PageRequest.of(page, size, Sort.by(orderCriteria));
            case "desc" -> PageRequest.of(page, size, Sort.by(orderCriteria).reverse());
            default -> PageRequest.of(page, size, Sort.by(orderCriteria));
        };

        return this.booksRepository.findAll(specification, pageable);
    }

    public List<BookDetailDTO> searchBooksFromGoogle(SearchFieldsDTO searchFields) {
        GoogleBooksSearchResultDTO searchResult = googleBooksClient.searchBooks(searchFields);
        if (searchResult.items() == null) return new ArrayList<>();
        List<BookDetailDTO> itemsFiltered = searchResult
                .items()
                .stream()
                .filter(item -> item != null && item.id() != null && item.volumeInfo() != null && item.volumeInfo().title() != null)
                .map(item -> BookMapper.mapFromGoogleItemDTO(item)).toList();
        return itemsFiltered;
    }

    /*public Page<Book> searchBooks(UUID userId, SearchFieldsDTO searchFields, int page, String sortBy, String order) {
        usersService.checkIfUserIsActive(userId);
        try {
            List<BookDetailDTO> booksFromGoogle = searchBooksFromGoogle(searchFields);
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

            *//*for (Book book : books) {
                try {
                    this.save(book);
                } catch (BadRequestException ex) {
                    // skip this book because already exists in the db
                }
            }*//*

            if (page < 0) page = 0;


            Comparator<Book> comparator = switch (sortBy == null ? "" : sortBy) {
                case "title" ->
                        Comparator.comparing(Book::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "publisher" ->
                        Comparator.comparing(Book::getPublisher, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "pages" -> Comparator.comparing(Book::getPages, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> null;
            };

            if ("desc".equalsIgnoreCase(order)) {
                if (comparator != null) {
                    books = books.stream().sorted(comparator.reversed()).toList();
                }
            }

            Pageable pageable = PageRequest.of(page, 10);

            int start = Math.min((int) pageable.getOffset(), books.size());
            int end = Math.min(start + pageable.getPageSize(), books.size());
            List<Book> pageContent = start > end ? List.of() : books.subList(start, end);
            return new PageImpl<>(pageContent, pageable, books.size());
        } catch (GoogleBooksSearchException ex) {
            Specification<Book> specification = BooksSpecification.filter(
                    searchFields.title(),
                    searchFields.author(),
                    searchFields.publisher(),
                    searchFields.isbn(),
                    searchFields.isbn(),
                    searchFields.category()
            );

            List<Book> booksFromDb = booksRepository.findAll(specification);
            if (booksFromDb.isEmpty()) throw new SearchException();
            if (sortBy == null || sortBy.isBlank()) sortBy = "title";
            return findAll(specification, page, 10, sortBy, order);
        }
    }*/

    public Page<BookDetailDTO> searchBooks(UUID userId, SearchFieldsDTO searchFields, int page, String sortBy, String order) {
        usersService.checkIfUserIsActive(userId);
        try {
            List<BookDetailDTO> booksFromGoogle = searchBooksFromGoogle(searchFields);

            if (page < 0) page = 0;


            Comparator<BookDetailDTO> comparator = switch (sortBy == null ? "" : sortBy) {
                case "title" ->
                        Comparator.comparing(BookDetailDTO::title, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "publisher" ->
                        Comparator.comparing(BookDetailDTO::publisher, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "pages" ->
                        Comparator.comparing(BookDetailDTO::pages, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> null;
            };

            if ("desc".equalsIgnoreCase(order)) {
                if (comparator != null) {
                    booksFromGoogle = booksFromGoogle.stream().sorted(comparator.reversed()).toList();
                }
            }

            Pageable pageable = PageRequest.of(page, 10);

            int start = Math.min((int) pageable.getOffset(), booksFromGoogle.size());
            int end = Math.min(start + pageable.getPageSize(), booksFromGoogle.size());
            List<BookDetailDTO> pageContent = start > end ? List.of() : booksFromGoogle.subList(start, end);
            return new PageImpl<>(pageContent, pageable, booksFromGoogle.size());
        } catch (GoogleBooksSearchException ex) {
            Specification<Book> specification = BooksSpecification.filter(
                    searchFields.title(),
                    searchFields.author(),
                    searchFields.publisher(),
                    searchFields.isbn(),
                    searchFields.isbn(),
                    searchFields.category()
            );

            List<Book> booksFromDb = booksRepository.findAll(specification);
            if (booksFromDb.isEmpty()) throw new SearchException();
            if (sortBy == null || sortBy.isBlank()) sortBy = "title";
            Page<Book> booksPage = findAll(specification, page, 10, sortBy, order);

            return booksPage.map(book -> new BookDetailDTO(
                    book.getGoogleId(),
                    book.getTitle(),
                    book.getAuthors(),
                    book.getPublisher(),
                    book.getPublishedDate(),
                    book.getDescription(),
                    book.getIsbn10(),
                    book.getIsbn13(),
                    book.getPages(),
                    book.getCategories(),
                    book.getCoverURL())
            );
        }
    }

    public BookDetailDTO searchBookByIdFromGoogle(String googleId) {
        if (googleId == null || googleId.isBlank()) throw new ValidationException("You must provide a valid google id");
        GoogleItemDTO bookFromGoogle = googleBooksClient.searchBookByGoogleId(googleId);
        return BookMapper.mapFromGoogleItemDTO(bookFromGoogle);
    }

    public Book save(BookDetailDTO body) {
        if (booksRepository.existsByGoogleId(body.googleId()))
            throw new BadRequestException("Book with google id " + body.googleId() + " already saved in the database");
        if (body.isbn10() != null && booksRepository.existsByIsbn10(body.isbn10()))
            throw new BadRequestException("Book with ISBN-10 " + body.isbn10() + " already saved in the database");
        if (body.isbn13() != null && booksRepository.existsByIsbn13(body.isbn13()))
            throw new BadRequestException("Book with ISBN-13 " + body.isbn13() + " already saved in the database");

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
        if (book.getIsbn10() != null && booksRepository.existsByIsbn10(book.getIsbn10()))
            throw new BadRequestException("Book with ISBN-10 " + book.getIsbn10() + " already saved in the database");
        if (book.getIsbn13() != null && booksRepository.existsByIsbn13(book.getIsbn13()))
            throw new BadRequestException("Book with ISBN-13 " + book.getIsbn13() + " already saved in the database");
        return booksRepository.save(book);
    }

    public Book getByGoogleId(String googleId) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        return booksRepository.findByGoogleId(googleId).orElseThrow(() -> new NotFoundException("book", googleId));
    }

    public BookDetailDTO getBookDetailsByGoogleId(UUID userId, String googleId) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        usersService.checkIfUserIsActive(userId);
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

    public Book findByIdAndUpdateBookInfo(String googleId, BookInfoDTO body) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
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
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
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
    public void findByIdAndDelete(String googleId) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        Book found = getByGoogleId(googleId);
        userBooksRepository.deleteByBook_Id(found.getId());
        reviewsRepository.deleteByBook_Id(found.getId());
        booksRepository.delete(found);
    }

    @Transactional
    public Book findByIdAndUpdateBookAuthors(String googleId, AuthorsDTO body) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        Book found = getByGoogleId(googleId);
        found.setAuthors(body.authors());
        return booksRepository.save(found);
    }

    @Transactional
    public Book findByIdAndUpdateBookCategories(String googleId, CategoriesDTO body) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        Book found = getByGoogleId(googleId);
        found.setCategories(body.categories());
        return booksRepository.save(found);
    }

    /*public OpenLibraryWorksSearchResponseDTO searchWorksFromOpenLibrary(String query, int page) {
        return openLibraryClient.searchWorks(query, page);
    }

    public OpenLibraryBookDetailsDTO getBookFromOpenLibrary(String editionId) {
        return openLibraryClient.searchBook(editionId);
    }*/
}

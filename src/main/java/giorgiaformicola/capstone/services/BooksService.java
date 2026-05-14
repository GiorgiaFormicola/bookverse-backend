package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.clients.GoogleBooksClient;
import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.books.GoogleItemDTO;
import giorgiaformicola.capstone.repositories.BooksRepository;
import giorgiaformicola.capstone.tools.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;
    private final GoogleBooksClient googleBooksClient;
    private final BooksRepository booksRepository;

    public BooksService(OpenLibraryClient openLibraryClient, GoogleBooksClient googleBooksClient, BooksRepository booksRepository, BookMapper bookMapper) {
        this.openLibraryClient = openLibraryClient;
        this.googleBooksClient = googleBooksClient;
        this.booksRepository = booksRepository;
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

    public Page<GoogleItemDTO> searchBooksFromGoogle(String query, int page, String language) {
        if (query.isBlank()) throw new BadRequestException("You must provide a valid query string");
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        List<GoogleItemDTO> itemsFiltered = googleBooksClient.searchBooks(query, language)
                .items()
                .stream()
                .filter(item -> item != null && item.id() != null && item.volumeInfo() != null).toList();


        System.out.println("Items filtered = " + itemsFiltered.size());

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), itemsFiltered.size());

        if (start >= itemsFiltered.size()) {
            return Page.empty(pageable);
        }

        List<GoogleItemDTO> pageContent = itemsFiltered.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                itemsFiltered.size()
        );
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


}

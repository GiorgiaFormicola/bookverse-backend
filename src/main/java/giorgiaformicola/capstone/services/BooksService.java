package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.clients.GoogleBooksClient;
import giorgiaformicola.capstone.clients.OpenLibraryClient;
import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.payloads.BookDetailDTO;
import giorgiaformicola.capstone.payloads.GoogleItemDTO;
import giorgiaformicola.capstone.payloads.NewBookDTO;
import giorgiaformicola.capstone.repositories.BooksRepository;
import giorgiaformicola.capstone.tools.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BooksService {
    private final OpenLibraryClient openLibraryClient;
    private final GoogleBooksClient googleBooksClient;
    private final BooksRepository booksRepository;
    private final BookMapper bookMapper;

    public BooksService(OpenLibraryClient openLibraryClient, GoogleBooksClient googleBooksClient, BooksRepository booksRepository, BookMapper bookMapper) {
        this.openLibraryClient = openLibraryClient;
        this.googleBooksClient = googleBooksClient;
        this.booksRepository = booksRepository;
        this.bookMapper = bookMapper;
    }

    /*public OpenLibraryWorksSearchResponseDTO searchWorksFromOpenLibrary(String query, int page) {
        return openLibraryClient.searchWorks(query, page);
    }

    public OpenLibraryBookDetailsDTO getBookFromOpenLibrary(String editionId) {
        return openLibraryClient.searchBook(editionId);
    }*/

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
        GoogleItemDTO bookFromGoogle = googleBooksClient.searchBookByGoogleId(googleId);
        return bookMapper.mapFromGoogleItemDTO(bookFromGoogle);
    }

    public Book save(NewBookDTO body) {
        if (booksRepository.existsByGoogleId(body.googleId()))
            throw new BadRequestException("Book already saved in the database");
        Book newBook = new Book(body.googleId(), body.title(), body.authors(), body.categories(), body.coverURL());
        return booksRepository.save(newBook);
    }
}

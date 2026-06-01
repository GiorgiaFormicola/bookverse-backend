package giorgiaformicola.capstone.services;

import giorgiaformicola.capstone.entities.Book;
import giorgiaformicola.capstone.entities.Review;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.enums.RoleType;
import giorgiaformicola.capstone.exceptions.BadRequestException;
import giorgiaformicola.capstone.exceptions.NotFoundException;
import giorgiaformicola.capstone.exceptions.UnauthorizedException;
import giorgiaformicola.capstone.exceptions.ValidationException;
import giorgiaformicola.capstone.payloads.books.BookDetailDTO;
import giorgiaformicola.capstone.payloads.reviews.ReviewDTO;
import giorgiaformicola.capstone.repositories.ReviewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewsService {
    private final ReviewsRepository reviewsRepository;
    private final UsersService usersService;
    private final BooksService booksService;

    public ReviewsService(ReviewsRepository reviewsRepository, UsersService usersService, BooksService booksService) {
        this.reviewsRepository = reviewsRepository;
        this.usersService = usersService;
        this.booksService = booksService;
    }

    public Review findById(UUID reviewId) {
        return this.reviewsRepository.findById(reviewId).orElseThrow(() -> new NotFoundException("review", reviewId));
    }

    public Review save(UUID userId, String googleId, ReviewDTO body) {
        User userFound = usersService.checkIfUserIsActive(userId);
        Book bookToReview;
        try {
            bookToReview = booksService.getByGoogleId(googleId);
        } catch (NotFoundException ex) {
            BookDetailDTO bookFromGoogle = booksService.searchBookByIdFromGoogle(googleId);
            bookToReview = booksService.save(bookFromGoogle);
        }
        if (reviewsRepository.existsByUser_IdAndBook_GoogleId(userFound.getId(), bookToReview.getGoogleId()))
            throw new BadRequestException("The review for the book with id " + bookToReview.getGoogleId() + " made by the user " + userFound.getId() + " already exists!");
        Review reviewToSave = new Review(body.rating(), body.comment(), bookToReview, userFound);
        return reviewsRepository.save(reviewToSave);
    }

/*
    public Review save(UUID userId, String googleId, ReviewDTO body) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        User userFound = usersService.checkIfUserIsActive(userId);
        Book bookFound = booksService.getByGoogleId(googleId);
        if (reviewsRepository.existsByUser_IdAndBook_GoogleId(userFound.getId(), bookFound.getGoogleId()))
            throw new BadRequestException("The review for the book with id " + bookFound.getGoogleId() + " made by the user " + userFound.getId() + " already exists!");
        Review reviewToSave = new Review(body.rating(), body.comment(), bookFound, userFound);
        return reviewsRepository.save(reviewToSave);
    }
*/

    public Review updateReview(UUID userId, UUID reviewId, ReviewDTO body) {
        User userFound = usersService.checkIfUserIsActive(userId);
        Review reviewFound = findById(reviewId);
        if (!reviewFound.getUser().getId().equals(userFound.getId()))
            throw new UnauthorizedException("You can't updated a review made by another user");
        reviewFound.setRating(body.rating());
        reviewFound.setComment(body.comment());
        reviewFound.setUpdatedAt(Instant.now());
        return reviewsRepository.save(reviewFound);
    }

    public void deleteReview(UUID userId, UUID reviewId) {
        User userFound = usersService.checkIfUserIsActive(userId);
        Review reviewFound = findById(reviewId);
        if (userFound.getRole().equals(RoleType.ADMIN)) reviewsRepository.delete(reviewFound);
        if (userFound.getRole().equals(RoleType.USER)) {
            if (!reviewFound.getUser().getId().equals(userFound.getId()))
                throw new UnauthorizedException("You can't delete a review made by another user");
            reviewsRepository.delete(reviewFound);
        }
    }

    public Page<Review> findAll(Specification<Review> specification, int page, int size, String sortBy, String order) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 20;

        if (!sortBy.equals("createdAt") && !sortBy.equals("updatedAt") && !sortBy.equals("rating"))
            sortBy = "createdAt";

        Pageable pageable = switch (order) {
            case "asc" -> PageRequest.of(page, size, Sort.by(sortBy));
            default -> PageRequest.of(page, size, Sort.by(sortBy).reverse());
        };
        return reviewsRepository.findAll(specification, pageable);
    }

    public Page<Review> findAllByBookGoogleId(UUID userId, Specification<Review> specification, int page, int size, String sortBy, String order) {
        usersService.checkIfUserIsActive(userId);
        return this.findAll(specification, page, size, sortBy, order);

    }

    public Optional<Review> findByBookAndUser(UUID userId, String googleId) {
        if (googleId == null || googleId.isBlank())
            throw new ValidationException("You must provide a valid google id");
        User userFound = usersService.checkIfUserIsActive(userId);
        return reviewsRepository.findByUser_IdAndBook_GoogleId(userFound.getId(), googleId);
    }
}

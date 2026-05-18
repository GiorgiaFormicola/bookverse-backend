package giorgiaformicola.capstone.controllers;

import giorgiaformicola.capstone.entities.Review;
import giorgiaformicola.capstone.entities.User;
import giorgiaformicola.capstone.exceptions.PayloadValidationException;
import giorgiaformicola.capstone.payloads.reviews.ReviewDTO;
import giorgiaformicola.capstone.services.ReviewsService;
import giorgiaformicola.capstone.specifications.ReviewsSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ReviewsController {
    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<Review> getAllReviews(@RequestParam(required = false) String googleId,
                                      @RequestParam(required = false) UUID userId,
                                      @RequestParam(required = false) Integer minRating,
                                      @RequestParam(required = false) Integer maxRating,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size,
                                      @RequestParam(defaultValue = "createdAt") String sortBy,
                                      @RequestParam(defaultValue = "desc") String order) {
        Specification<Review> specification = ReviewsSpecification.filter(
                googleId,
                userId,
                minRating,
                maxRating
        );
        return this.reviewsService.findAll(specification, page, size, sortBy, order);
    }

    @GetMapping("/books/{googleId}/reviews")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<Review> getBookReviews(@AuthenticationPrincipal User currentAuthenticatedUser,
                                       @PathVariable String googleId,
                                       @RequestParam(required = false) Integer minRating,
                                       @RequestParam(required = false) Integer maxRating,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(defaultValue = "createdAt") String sortBy,
                                       @RequestParam(defaultValue = "desc") String order
    ) {
        Specification<Review> specification = ReviewsSpecification.filter(
                googleId,
                null,
                minRating,
                maxRating
        );
        return this.reviewsService.findAllByBookGoogleId(currentAuthenticatedUser.getId(), specification, page, size, sortBy, order);
    }

    @PostMapping("/books/{googleId}/reviews")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Review addReview(@AuthenticationPrincipal User currentAuthenticatedUser,
                            @PathVariable String googleId,
                            @RequestBody @Validated ReviewDTO body,
                            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.reviewsService.save(currentAuthenticatedUser.getId(), googleId, body);
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Review updateReview(@AuthenticationPrincipal User currentAuthenticatedUser,
                               @PathVariable UUID reviewId,
                               @RequestBody @Validated ReviewDTO body,
                               BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.reviewsService.updateReview(currentAuthenticatedUser.getId(), reviewId, body);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@AuthenticationPrincipal User currentAuthenticatedUser,
                             @PathVariable UUID reviewId) {
        this.reviewsService.deleteReview(currentAuthenticatedUser.getId(), reviewId);
    }

    ;
}

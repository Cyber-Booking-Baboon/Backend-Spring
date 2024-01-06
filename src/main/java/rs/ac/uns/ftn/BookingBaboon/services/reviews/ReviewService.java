package rs.ac.uns.ftn.BookingBaboon.services.reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.reviews.AccommodationReview;
import rs.ac.uns.ftn.BookingBaboon.domain.reviews.Review;
import rs.ac.uns.ftn.BookingBaboon.repositories.reviews.IHostReviewRepository;
import rs.ac.uns.ftn.BookingBaboon.repositories.reviews.IReviewRepository;
import rs.ac.uns.ftn.BookingBaboon.services.reviews.interfaces.IReviewService;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService {
    private final IReviewRepository reviewRepository;
    @Override
    public Set<Review> getAll() {
        return new HashSet<>(reviewRepository.findAll());
    }

    @Override
    public Review get(Long reviewId) {
        Optional<Review> found = reviewRepository.findById(reviewId);
        if (found.isEmpty()) {
            String value = "Review not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return found.get();
    }

    @Override
    public Review create(Review review) {
        return new Review();
    }

    @Override
    public Review update(Review review) {
        return new Review();
    }

    @Override
    public Review remove(Long reviewId) {
        Review found = get(reviewId);
        reviewRepository.delete(found);
        reviewRepository.flush();
        return found;
    }
}

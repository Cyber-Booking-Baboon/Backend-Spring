package rs.ac.uns.ftn.BookingBaboon.controllers.reviews;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.reviews.AccommodationReview;
import rs.ac.uns.ftn.BookingBaboon.domain.reviews.Review;
import rs.ac.uns.ftn.BookingBaboon.dtos.reviews.AccommodationReviewResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.reviews.ReviewReferenceRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reviews.ReviewResponse;
import rs.ac.uns.ftn.BookingBaboon.services.reviews.interfaces.IReviewService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final IReviewService service;
    private final ModelMapper mapper;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{ReviewId}")
    public ResponseEntity<ReviewReferenceRequest> remove(@PathVariable Long ReviewId) {

        Review Review = service.remove(ReviewId);
        if(Review==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(Review, ReviewReferenceRequest.class), HttpStatus.OK);
    }
}
package rs.ac.uns.ftn.BookingBaboon.controllers.reviews;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.reviews.HostReview;
import rs.ac.uns.ftn.BookingBaboon.dtos.reviews.*;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.guests.GuestResponse;
import rs.ac.uns.ftn.BookingBaboon.services.reviews.interfaces.IHostReviewService;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.IHostService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/host-reviews")
@SecurityRequirement(name = "Keycloak")
public class HostReviewController {

    private final IHostReviewService service;
    private final IHostService hostService;
    private final ModelMapper mapper;


    // Get all host reviews
    @GetMapping
    public ResponseEntity<Collection<HostReviewResponse>> getHosts() {
        Collection<HostReview> hostReviews = service.getAll();
        Collection<HostReviewResponse> hostReviewResponses =  hostReviews.stream()
                .map(hostReview -> mapper.map(hostReview, HostReviewResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(hostReviewResponses, HttpStatus.OK);
    }

    // Get a host review by ID
    @GetMapping({"/{hostReviewId}"})
    public ResponseEntity<HostReviewResponse> get(@PathVariable Long hostReviewId) {
        HostReview hostReview = service.get(hostReviewId);
        if(hostReview==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(hostReview, HostReviewResponse.class), HttpStatus.OK);
    }

    // Create a new host review
    @PreAuthorize("hasAuthority('GUEST')")
    @PostMapping({"/"})
    public ResponseEntity<HostReviewResponse> create(@RequestBody HostReviewCreateRequest hostReview) {

        return new ResponseEntity<>(mapper.map(service.create(mapper.map(hostReview, HostReview.class)), HostReviewResponse.class), HttpStatus.CREATED);
    }

    // Update an existing host review
    @PutMapping({"/"})
    public ResponseEntity<HostReviewResponse> update(@RequestBody HostReviewUpdateRequest hostReview) {

        return new ResponseEntity<>(mapper.map(service.update(mapper.map(hostReview, HostReview.class)),HostReviewResponse.class),HttpStatus.OK);
    }

    // Delete a host review by ID
    @PreAuthorize("hasAnyAuthority('GUEST', 'ADMIN')")
    @DeleteMapping("/{hostReviewId}")
    public ResponseEntity<HostReviewResponse> remove(@PathVariable Long hostReviewId) {

        HostReview hostReview = service.remove(hostReviewId);
        if(hostReview==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(hostReview,HostReviewResponse.class), HttpStatus.OK);
    }

    @GetMapping("/{hostId}/reviewer")
    public ResponseEntity<GuestResponse> getReviewer(@PathVariable Long hostId) {

        if (service.get(hostId) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        GuestResponse guestResponse = mapper.map(service.getReviewer(hostId), GuestResponse.class);

        return new ResponseEntity<>(guestResponse, HttpStatus.OK);
    }

    @GetMapping("/average-rating/{hostId}")
    public ResponseEntity<Float> getAverageRating(@PathVariable Long hostId) {

        if (hostService.get(hostId) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        float averageRating = service.getAverageRating(hostId);

        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<Collection<HostReviewResponse>> getReviewsByHost(@PathVariable Long hostId) {
        Collection<HostReview> hostReviews = service.getReviewsByHost(hostId);
        Collection<HostReviewResponse> hostReviewResponses =  hostReviews.stream()
                .map(hostReview -> mapper.map(hostReview, HostReviewResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(hostReviewResponses, HttpStatus.OK);
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<Collection<HostReviewResponse>> getReviewsByGuest(@PathVariable Long guestId) {
        Collection<HostReview> hostReviews = service.getReviewsByGuest(guestId);
        Collection<HostReviewResponse> hostReviewResponses =  hostReviews.stream()
                .map(hostReview -> mapper.map(hostReview, HostReviewResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(hostReviewResponses, HttpStatus.OK);
    }
}

package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationFilter;
import rs.ac.uns.ftn.BookingBaboon.domain.shared.TimeSlot;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationResponse;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAccommodationService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accommodations")
@SecurityRequirement(name = "Keycloak")
public class AccommodationController {

    private final IAccommodationService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<AccommodationResponse>> getAll() {
        Collection<Accommodation> accommodations = service.getAll();

        return new ResponseEntity<>(accommodations.stream()
                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<Collection<AccommodationResponse>> getAllByHost(@PathVariable Long hostId) {
        Collection<Accommodation> accommodations = service.getAllByHost(hostId);
        System.out.println(accommodations);

        return new ResponseEntity<>(accommodations.stream()
                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponse> get(@PathVariable Long id) {
        Accommodation accommodation = service.get(id);

        if (accommodation == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('HOST')")
    @PostMapping
    public ResponseEntity<AccommodationResponse> create(@RequestBody AccommodationCreateRequest accommodation) {
        Accommodation result = service.create(mapper.map(accommodation, Accommodation.class));
        return new ResponseEntity<>(mapper.map(result, AccommodationResponse.class), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('HOST', 'ADMIN')")
    @PutMapping
    public ResponseEntity<AccommodationResponse> update(@RequestBody AccommodationRequest accommodation) {
        Accommodation result = service.update(mapper.map(accommodation, Accommodation.class));

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(result, AccommodationResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        Accommodation accommodation = service.get(id);
        if (accommodation != null) {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("{accommodationId}/available-periods/{periodId}")
    public ResponseEntity<AccommodationResponse> remove(@PathVariable Long accommodationId, @PathVariable Long periodId) {
        Accommodation accommodation = service.removePeriod(periodId, accommodationId);
        if (accommodation != null) {
            return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<AccommodationResponse>> search(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "checkin", required = false) String checkin,
            @RequestParam(name = "checkout", required = false) String checkout,
            @RequestParam(name = "guest-num", required = false) Integer guestNum,
            @RequestParam(name = "min-price", required = false) Double minPrice,
            @RequestParam(name = "max-price", required = false) Double maxPrice,
            @RequestParam(name = "amenities", required = false) String amenities,
            @RequestParam(name = "property-type", required = false) String propertyType,
            @RequestParam(name = "min-rating", required = false) Double minRating) {

        AccommodationFilter filter = service.parseFilter(city,checkin,checkout,guestNum,minPrice,maxPrice,propertyType,amenities,minRating);

        Collection<Accommodation> accommodations = service.search(filter);

        return new ResponseEntity<>(accommodations.stream()
                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}/total-price")
    public ResponseEntity<Float> get(
            @PathVariable Long id,
            @RequestParam(name = "checkin", required = true) String checkin,
            @RequestParam(name = "checkout", required = true) String checkout) {

        TimeSlot desiredDate = new TimeSlot(service.parseDate(checkin), service.parseDate(checkout));
        desiredDate.fix();
        float totalPrice = service.getTotalPrice(service.get(id),desiredDate);

        if (totalPrice == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(totalPrice, HttpStatus.OK);
    }

    @PutMapping("/{accommodationId}/add/{imageId}")
    public ResponseEntity<AccommodationResponse> addImage(@PathVariable Long imageId,@PathVariable Long accommodationId){
        Accommodation accommodation = service.addImage(imageId, accommodationId);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('HOST')")
    @PutMapping("/{accommodationId}/addPeriod/{periodId}")
    public ResponseEntity<AccommodationResponse> addPeriod(@PathVariable Long periodId, @PathVariable Long accommodationId){
        Accommodation accommodation = service.addPeriod(periodId, accommodationId);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/{accommodationId}/updateEditingStatus/{isBeingEdited}")
    public ResponseEntity<AccommodationResponse> updateEditingStatus(@PathVariable Long accommodationId, @PathVariable boolean isBeingEdited) {
        Accommodation accommodation = service.updateEditingStatus(accommodationId, isBeingEdited);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/{accommodationId}/update-auto-accept/{isAutomaticallyAccepted}")
    public ResponseEntity<AccommodationResponse> updateAutoAccept(@PathVariable Long accommodationId, @PathVariable boolean isAutomaticallyAccepted) {
        Accommodation accommodation = service.updateAutoAccept(accommodationId, isAutomaticallyAccepted);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/{accommodationId}/cancellation-deadline/{value}")
    public ResponseEntity<AccommodationResponse> updateCancellationDeadline(@PathVariable Long accommodationId, @PathVariable int value) {
        Accommodation accommodation = service.updateCancellationDeadline(accommodationId, value);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }
}
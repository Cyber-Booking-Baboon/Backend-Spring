package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        return new ResponseEntity<>(accommodations.stream()
                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponse> get(@PathVariable Long id) {
        Accommodation accommodation = service.get(id);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AccommodationResponse> create(@RequestBody AccommodationCreateRequest accommodation) {
        Accommodation result = service.create(mapper.map(accommodation, Accommodation.class));
        return new ResponseEntity<>(mapper.map(result, AccommodationResponse.class), HttpStatus.CREATED);
    }

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

//    @GetMapping("/filter")
//    public ResponseEntity<Collection<AccommodationResponse>> search(
//            @RequestParam(name = "city", required = false) String city,
//            @RequestParam(name = "checkin", required = false) String checkin,
//            @RequestParam(name = "checkout", required = false) String checkout,
//            @RequestParam(name = "guest-num", required = false) Integer guestNum,
//            @RequestParam(name = "min-price", required = false) Double minPrice,
//            @RequestParam(name = "max-price", required = false) Double maxPrice,
//            @RequestParam(name = "amenity", required = false) String amenity,
//            @RequestParam(name = "property-type", required = false) String propertyType,
//            @RequestParam(name = "min-rating", required = false) Integer minRating) {
//
//        AccommodationFilter filter = new AccommodationFilter();
//
//        Collection<Accommodation> accommodations = service.getAll();
//
//        return new ResponseEntity<>(accommodations.stream()
//                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
//                .collect(Collectors.toList()), HttpStatus.OK);
//    }

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

        float totalPrice = service.getTotalPrice(service.get(id), new TimeSlot(service.parseDate(checkin), service.parseDate(checkout)));

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

    @PutMapping("/{accommodationId}/addPeriod/{periodId}")
    public ResponseEntity<AccommodationResponse> addPeriod(@PathVariable Long periodId, @PathVariable Long accommodationId){
        Accommodation accommodation = service.addPeriod(periodId, accommodationId);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodation, AccommodationResponse.class), HttpStatus.OK);
    }

}
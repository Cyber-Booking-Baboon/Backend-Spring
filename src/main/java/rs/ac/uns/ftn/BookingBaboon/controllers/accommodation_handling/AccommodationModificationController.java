package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation_modification.AccommodationModificationCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation_modification.AccommodationModificationRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation_modification.AccommodationModificationResponse;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAccommodationModificationService;

import java.util.Collection;
import java.util.stream.Collectors;
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accommodation-change-requests")
public class AccommodationModificationController {
    private final IAccommodationModificationService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<AccommodationModificationResponse>> getAll() {
        Collection<AccommodationModificationResponse> response = service.getAll().stream()
                .map(request -> mapper.map(request, AccommodationModificationResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationModificationResponse> get(@PathVariable Long id) {
        AccommodationModificationResponse result = mapper.map(service.get(id), AccommodationModificationResponse.class);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result,  HttpStatus.OK) ;
    }

    @PostMapping
    public ResponseEntity<AccommodationModificationResponse> create(@RequestBody AccommodationModificationCreateRequest request) {
        AccommodationModification result = service.create(mapper.map(request, AccommodationModification.class));
        return new ResponseEntity<>(mapper.map(result, AccommodationModificationResponse.class), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AccommodationModificationResponse> update(@RequestBody AccommodationModificationRequest request) {
        AccommodationModification result = service.update(mapper.map(request, AccommodationModification.class));

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(result, AccommodationModificationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<AccommodationModificationResponse> approve(@PathVariable Long id) {
        AccommodationModification accommodationModification = service.get(id);

        if (accommodationModification == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accommodationModification.Approve();
        return new ResponseEntity<>(mapper.map(accommodationModification, AccommodationModificationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/deny/{id}")
    public ResponseEntity<AccommodationModificationResponse> deny(@PathVariable Long id) {
        AccommodationModification accommodationModification = service.get(id);

        if (accommodationModification == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accommodationModification.Deny();
        return new ResponseEntity<>(mapper.map(accommodationModification, AccommodationModificationResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        AccommodationModification accommodationModification = service.get(id);
        if (accommodationModification != null) {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation_modification.AccommodationModificationCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation_modification.AccommodationModificationRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation_modification.AccommodationModificationResponse;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAccommodationModificationService;

import java.util.Collection;
import java.util.stream.Collectors;
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accommodation-modifications")
@SecurityRequirement(name = "Keycloak")
public class AccommodationModificationController {
    private final IAccommodationModificationService service;
    private final ModelMapper mapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Collection<AccommodationModificationResponse>> getAll() {
        Collection<AccommodationModificationResponse> response = service.getAll().stream()
                .map(request -> mapper.map(request, AccommodationModificationResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationModificationResponse> get(@PathVariable Long id) {
        AccommodationModificationResponse result = mapper.map(service.get(id), AccommodationModificationResponse.class);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result,  HttpStatus.OK) ;
    }

    @PreAuthorize("hasAuthority('HOST')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<AccommodationModificationResponse> approve(@PathVariable Long id) {
        AccommodationModification accommodationModification = service.get(id);

        if (accommodationModification == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accommodationModification = service.approve(id);
        return new ResponseEntity<>(mapper.map(accommodationModification, AccommodationModificationResponse.class), HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/deny/{id}")
    public ResponseEntity<AccommodationModificationResponse> deny(@PathVariable Long id) {
        AccommodationModification accommodationModification = service.get(id);

        if (accommodationModification == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accommodationModification = service.deny(id);
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

    @PutMapping("/{accommodationModificationId}/add/{imageId}")
    public ResponseEntity<AccommodationModificationResponse> addImage(@PathVariable Long imageId, @PathVariable Long accommodationModificationId){
        AccommodationModification accommodationModification = service.addImage(imageId, accommodationModificationId);

        if (accommodationModification == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodationModification, AccommodationModificationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/{accommodationModificationId}/addPeriod/{periodId}")
    public ResponseEntity<AccommodationModificationResponse> addPeriod(@PathVariable Long periodId, @PathVariable Long accommodationModificationId){
        AccommodationModification accommodationModification = service.addPeriod(periodId, accommodationModificationId);

        if (accommodationModification == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(accommodationModification, AccommodationModificationResponse.class), HttpStatus.OK);
    }
}

package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Amenity;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AvailablePeriod;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.amenity.AmenityCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.amenity.AmenityRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.amenity.AmenityResponse;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAmenityService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/amenities")
@SecurityRequirement(name = "Keycloak")
public class AmenityController {

    private final IAmenityService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<AmenityResponse>> getAll() {
        Collection<Amenity> amenities = service.getAll();

        return new ResponseEntity<>(amenities.stream()
                .map(amenity -> mapper.map(amenity, AmenityResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponse> get(@PathVariable Long id) {
        Amenity amenity = service.get(id);

        if (amenity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(amenity, AmenityResponse.class), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AmenityResponse> create(@RequestBody AmenityCreateRequest amenity) {
        Amenity result = service.create(mapper.map(amenity, Amenity.class));
        return new ResponseEntity<>(mapper.map(result, AmenityResponse.class), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AmenityResponse> update(@RequestBody AmenityRequest amenity) {
        Amenity result = service.update(mapper.map(amenity, Amenity.class));

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(result, AmenityResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        Amenity amenity = service.get(id);
        if (amenity != null) {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

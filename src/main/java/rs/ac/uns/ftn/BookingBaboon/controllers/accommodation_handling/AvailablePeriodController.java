package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AvailablePeriod;
import rs.ac.uns.ftn.BookingBaboon.domain.reservation.Reservation;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.available_period.AvailablePeriodCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.available_period.AvailablePeriodRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.available_period.AvailablePeriodResponse;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAvailablePeriodService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/available-periods")
@SecurityRequirement(name = "Keycloak")
public class AvailablePeriodController {

    private final IAvailablePeriodService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<AvailablePeriodResponse>> getAll() {
        Collection<AvailablePeriod> availablePeriods = service.getAll();

        return new ResponseEntity<>(availablePeriods.stream()
                .map(availablePeriod -> mapper.map(availablePeriod, AvailablePeriodResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailablePeriodResponse> get(@PathVariable Long id) {
        AvailablePeriod availablePeriod = service.get(id);

        if (availablePeriod == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(availablePeriod, AvailablePeriodResponse.class), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AvailablePeriodResponse> create(@RequestBody AvailablePeriodCreateRequest availablePeriod) {
        AvailablePeriod result = service.create(mapper.map(availablePeriod, AvailablePeriod.class));
        return new ResponseEntity<>(mapper.map(result, AvailablePeriodResponse.class), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AvailablePeriodResponse> update(@RequestBody AvailablePeriodRequest availablePeriod) {
        AvailablePeriod result = service.update(mapper.map(availablePeriod, AvailablePeriod.class));

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(result, AvailablePeriodResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        AvailablePeriod availablePeriod = service.get(id);
        if (availablePeriod != null) {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

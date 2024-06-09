package rs.ac.uns.ftn.BookingBaboon.controllers.reservation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.reservation.Reservation;
import rs.ac.uns.ftn.BookingBaboon.domain.reservation.ReservationStatus;
import rs.ac.uns.ftn.BookingBaboon.dtos.reservation.ReservationCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reservation.ReservationRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reservation.ReservationResponse;
import rs.ac.uns.ftn.BookingBaboon.services.reservation.interfaces.IReservationService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reservations")
@SecurityRequirement(name = "Keycloak")
public class ReservationController {
    private final IReservationService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<ReservationResponse>> getAll() {
        Collection<Reservation> reservations = service.getAll();
        return new ResponseEntity<>(reservations.stream()
                .map(reservation -> mapper.map(reservation, ReservationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable Long id) {
        Reservation reservation = service.get(id);

        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(reservation, ReservationResponse.class), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationCreateRequest reservation) {
        reservation.getTimeSlot().fix();
        Reservation result = service.create(mapper.map(reservation, Reservation.class));
        Reservation finalResult = service.handleAutomaticAcceptance(result);
        return new ResponseEntity<>(mapper.map(finalResult, ReservationResponse.class), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ReservationResponse> update(@RequestBody ReservationRequest reservation) {
        Reservation result = service.update(mapper.map(reservation, Reservation.class));

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(result, ReservationResponse.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        Reservation reservation =  service.get(id);
        if (reservation != null) {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<ReservationResponse> deny(@PathVariable Long id) {
        Reservation reservation = service.get(id);

        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Reservation result = service.deny(id);
        return new ResponseEntity<>(mapper.map(result, ReservationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ReservationResponse> approve(@PathVariable Long id) {
        Reservation reservation = service.get(id);

        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Reservation result = service.approveReservation(id);
        return new ResponseEntity<>(mapper.map(result, ReservationResponse.class), HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable Long id) {
        Reservation reservation = service.get(id);

        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Reservation result = service.cancel(id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(mapper.map(result, ReservationResponse.class), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<Collection<ReservationResponse>> search(
            @RequestParam(name = "accommodation-name", required = false) String accommodationName,
            @RequestParam(name = "start-date", required = false) String startDate,
            @RequestParam(name = "end-date", required = false) String endDate,
            @RequestParam(name = "status", required = false) ReservationStatus status) {

        Collection<Reservation> reservations = service.getAll();
        return new ResponseEntity<>(reservations.stream()
                .map(reservation -> mapper.map(reservation, ReservationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/cancellation-count/{userId}")
    public ResponseEntity<Integer> getCancellationCountForUser(@PathVariable Long userId) {
        int cancellationCount = service.getCancellationCountForUser(userId);
        return new ResponseEntity<>(cancellationCount, HttpStatus.OK);
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<Collection<ReservationResponse>> getAllForGuest(@PathVariable Long id) {
        Collection<Reservation> reservations = service.getAllForGuest(id);
        return new ResponseEntity<>(reservations.stream()
                .map(reservation -> mapper.map(reservation, ReservationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/host/{id}")
    public ResponseEntity<Collection<ReservationResponse>> getAllForHost(@PathVariable Long id) {
        Collection<Reservation> reservations = service.getAllForHost(id);
        return new ResponseEntity<>(reservations.stream()
                .map(reservation -> mapper.map(reservation, ReservationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }
    @PutMapping("/cancelAllForGuest/{guestId}")
    public ResponseEntity<Collection<ReservationResponse>> cancelAllForGuest(@PathVariable Long guestId) {
        Collection<Reservation> reservations = service.cancelAllForGuest(guestId);
        return new ResponseEntity<>(reservations.stream()
                .map(reservation -> mapper.map(reservation, ReservationResponse.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

}

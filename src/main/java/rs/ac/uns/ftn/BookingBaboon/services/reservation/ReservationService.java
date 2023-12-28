package rs.ac.uns.ftn.BookingBaboon.services.reservation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AvailablePeriod;
import rs.ac.uns.ftn.BookingBaboon.domain.reservation.Reservation;
import rs.ac.uns.ftn.BookingBaboon.domain.reservation.ReservationStatus;
import rs.ac.uns.ftn.BookingBaboon.domain.shared.TimeSlot;
import rs.ac.uns.ftn.BookingBaboon.dtos.reservation.ReservationResponse;
import rs.ac.uns.ftn.BookingBaboon.repositories.reservation_handling.IReservationRepository;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAccommodationService;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.IAvailablePeriodService;
import rs.ac.uns.ftn.BookingBaboon.services.reservation.interfaces.IReservationService;

import java.util.*;
@RequiredArgsConstructor
@Service
public class ReservationService implements IReservationService {
    private final IReservationRepository repository;
    private final IAccommodationService accommodationService;
    private final IAvailablePeriodService availablePeriodService;

    ResourceBundle bundle = ResourceBundle.getBundle("ValidationMessages", LocaleContextHolder.getLocale());

    @Override
    public HashSet<Reservation> getAll() {
        return new HashSet<Reservation>(repository.findAll());
    }

    @Override
    public Reservation get(Long reservationId) {
        Optional<Reservation> found = repository.findById(reservationId);
        if (found.isEmpty()) {
            String value = bundle.getString("reservation.notFound");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return found.get();
    }

    @Override
    public Reservation create(Reservation reservation) {
        try {
            repository.save(reservation);
            repository.flush();
            return reservation;
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> errors = ex.getConstraintViolations();
            StringBuilder sb = new StringBuilder(1000);
            for (ConstraintViolation<?> error : errors) {
                sb.append(error.getMessage()).append("\n");
            }
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, sb.toString());
        }
    }

    @Override
    public Reservation update(Reservation reservation) {
        try {
            get(reservation.getId()); // this will throw ReservationNotFoundException if reservation is not found
            repository.save(reservation);
            repository.flush();
            return reservation;
        } catch (RuntimeException ex) {
            Throwable e = ex;
            Throwable c = null;
            while ((e != null) && !((c = e.getCause()) instanceof ConstraintViolationException) ) {
                e = (RuntimeException) c;
            }
            if ((c != null) && (c instanceof ConstraintViolationException)) {
                ConstraintViolationException c2 = (ConstraintViolationException) c;
                Set<ConstraintViolation<?>> errors = c2.getConstraintViolations();
                StringBuilder sb = new StringBuilder(1000);
                for (ConstraintViolation<?> error : errors) {
                    sb.append(error.getMessage() + "\n");
                }
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, sb.toString());
            }
            throw ex;
        }
    }

    @Override
    public Reservation remove(Long reservationId) {
        Reservation found = get(reservationId);
        repository.delete(found);
        repository.flush();
        return found;
    }

    @Override
    public void removeAllForGuest(Long guestId) {
        for(Reservation reservation : getAll()) {
            if (reservation.getGuest().getId().equals(guestId)) {
                remove(reservation.getId());
            }
        }
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
        repository.flush();
    }
    @Override
    public Reservation deny(Long reservationId) {
        Reservation found = get(reservationId);
        found.Cancel();
        update(found);
        return found;
    }

    private void denyOverlappingReservations(TimeSlot timeSlot, Long accommodationId) {
        Collection<Reservation> reservations = repository.findAllByAccommodationId(accommodationId);
        for(Reservation reservation : reservations) {
            if (reservation.getTimeSlot().overlaps(timeSlot)) {
                deny(reservation.getId());
            }
        }
    }

    @Override
    public int getCancellationCountForUser(Long userId) {
        return 0;
    }

    @Override
    public boolean isApproved(Long reservationId) {
        return get(reservationId).getStatus().equals(ReservationStatus.Approved);
    }

    @Override
    public void removeAllForAccommodation(Long accommodationId) {
        for(Reservation reservation : getAll()) {
            if (reservation.getAccommodation().getId().equals(accommodationId)) {
                remove(reservation.getId());
            }
        }
    }

    @Override
    public Collection<Reservation> getAllByAccommodation(Long accommodationId) {
        return repository.findAllByAccommodationId(accommodationId);
    }

    public Reservation approveReservation(Long reservationId){
        Reservation reservation = get(reservationId);
        reservation.Approve();
        Accommodation accommodation = accommodationService.get(reservation.getAccommodation().getId());

        List<AvailablePeriod> overlappingPeriods = availablePeriodService.getOverlappingPeriods(reservation.getTimeSlot(), accommodation.getAvailablePeriods());
        List<AvailablePeriod> newAvailablePeriods = availablePeriodService.splitPeriods(reservation.getTimeSlot(), overlappingPeriods);

        //Add new ones
        for(AvailablePeriod newAvailablePeriod: newAvailablePeriods){
            AvailablePeriod result = availablePeriodService.create(newAvailablePeriod);
            accommodationService.addPeriod(result.getId(), accommodation.getId());
        }

        //Delete the old ones
        for(AvailablePeriod oldPeriod: overlappingPeriods){
            accommodationService.removePeriod(oldPeriod.getId(), accommodation.getId());
            availablePeriodService.remove(oldPeriod.getId());
        }

        denyOverlappingReservations(reservation.getTimeSlot(), accommodation.getId());

        update(reservation);
        return reservation;
    }

    @Override
    public Reservation handleAutomaticAcceptance(Reservation reservation){
        Accommodation accommodation = accommodationService.get(reservation.getAccommodation().getId());
        if(accommodation.getIsAutomaticallyAccepted()){
            Reservation result = approveReservation(reservation.getId());
            return result;
        }

        return reservation;

    }

    @Override
    public Collection<Reservation> getAllForGuest(Long id) {
        return repository.findAllByGuest_Id(id);
    }

}

package rs.ac.uns.ftn.BookingBaboon.services.users.interfaces;

import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationChangeRequest;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Admin;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;

import java.util.Set;

public interface IAdminService {
    Set<Admin> getAll();

    Admin get(Long adminId) throws ResponseStatusException;

    Admin create(Admin admin) throws ResponseStatusException;

    Admin update(Admin admin) throws ResponseStatusException;

    Admin remove(Long adminId);

    User blockUser(Long userId);

    Set<GuestReport> getAllReports();

    Set<AccommodationChangeRequest> getAllAccommodationChanges();

    Accommodation approveAccommodationChange(Long accommodationId);

    Accommodation denyAccommodationChange(Long accommodationId);

    void removeAll();
}

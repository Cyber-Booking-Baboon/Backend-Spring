package rs.ac.uns.ftn.BookingBaboon.services.users.interfaces;

import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Admin;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;

import java.util.Set;

public interface IAdminService {
    Set<Admin> getAll();

    Admin get(Long adminId);

    Admin create(Admin admin);

    Admin update(Admin admin);

    Admin remove(Long adminId);

    User blockUser(Long userId);

    Set<GuestReport> getAllReports();

    Set<AccommodationModification> getAllAccommodationChanges();

    Accommodation approveAccommodationChange(Long accommodationId);

    Accommodation denyAccommodationChange(Long accommodationId);
}

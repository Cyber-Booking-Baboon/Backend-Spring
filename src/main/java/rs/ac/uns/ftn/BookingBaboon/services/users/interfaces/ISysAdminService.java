package rs.ac.uns.ftn.BookingBaboon.services.users.interfaces;

import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.SysAdmin;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;

import java.util.Collection;

public interface ISysAdminService {
    Collection<SysAdmin> getAll();

    SysAdmin get(Long adminId) throws ResponseStatusException;

    SysAdmin create(SysAdmin admin) throws ResponseStatusException;

    SysAdmin update(SysAdmin admin) throws ResponseStatusException;

    SysAdmin remove(Long adminId);

    void removeAll();
}

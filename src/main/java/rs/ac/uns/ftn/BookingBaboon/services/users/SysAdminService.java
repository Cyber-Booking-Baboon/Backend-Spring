package rs.ac.uns.ftn.BookingBaboon.services.users;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.SysAdmin;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Host;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;
import rs.ac.uns.ftn.BookingBaboon.repositories.users.ISysAdminRepository;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.ISysAdminService;

import java.util.*;

@RequiredArgsConstructor
@Service
public class SysAdminService implements ISysAdminService {

    private final ISysAdminRepository repository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    ResourceBundle bundle = ResourceBundle.getBundle("ValidationMessages", LocaleContextHolder.getLocale());


    @Override
    public Collection<SysAdmin> getAll() {
        return new ArrayList<SysAdmin>(repository.findAll());
    }

    @Override
    public SysAdmin get(Long adminId) throws ResponseStatusException {
        Optional<SysAdmin> found = repository.findById(adminId);
        if (found.isEmpty()) {
            String value = bundle.getString("admin.notFound");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return found.get();
    }

    @Override
    public SysAdmin create(SysAdmin admin) throws ResponseStatusException {
        try {
            admin.setPassword(encoder.encode(admin.getPassword()));
            repository.save(admin);
            repository.flush();
            return admin;
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
    public SysAdmin update(SysAdmin admin) throws ResponseStatusException {
        try {
            SysAdmin updatedSysAdmin = get(admin.getId());
            updatedSysAdmin.setFirstName(admin.getFirstName());
            updatedSysAdmin.setLastName(admin.getLastName());
            updatedSysAdmin.setEmail(admin.getEmail());
            updatedSysAdmin.setAddress(admin.getAddress());
            updatedSysAdmin.setPhoneNumber(admin.getPhoneNumber());
            repository.save(updatedSysAdmin);
            repository.flush();
            return admin;
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
    public SysAdmin remove(Long adminId) {
        SysAdmin found = get(adminId);
        repository.delete(found);
        repository.flush();
        return found;
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
        repository.flush();
    }

}

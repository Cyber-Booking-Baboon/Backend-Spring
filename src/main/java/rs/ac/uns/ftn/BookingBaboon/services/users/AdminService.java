package rs.ac.uns.ftn.BookingBaboon.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.BookingBaboon.repositories.users.IAdminRepository;

@Service
public class AdminService {

    private IAdminRepository repository;

    @Autowired
    public AdminService(IAdminRepository repository) {
        this.repository = repository;
    }
}

package rs.ac.uns.ftn.BookingBaboon.controllers.users;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Admin;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Host;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.GuestReportResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.admins.UserBlockResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.admins.AdminRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.admins.*;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.admins.AdminResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.hosts.HostResponse;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.IAdminService;
import rs.ac.uns.ftn.BookingBaboon.services.users.ldap.LdapUserService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/admin")
@SecurityRequirement(name = "Keycloak")
public class AdminController {
    private final IAdminService service;
    private final ModelMapper mapper;

    @Autowired
    private LdapUserService ldapUserService;
    @GetMapping
    public ResponseEntity<Collection<AdminResponse>> getAdmins() {
        Collection<Admin> admins = service.getAll();
        Collection<AdminResponse> adminResponses =  admins.stream()
                .map(admin -> mapper.map(admin, AdminResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(adminResponses, HttpStatus.OK);
    }

    @GetMapping({"/{adminId}"})
    public ResponseEntity<AdminResponse> get(@PathVariable Long adminId) {
        Admin admin = service.get(adminId);
        if(admin==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(admin, AdminResponse.class), HttpStatus.OK);
    }

    @PostMapping({"/"})
    public ResponseEntity<AdminResponse> create(@RequestBody AdminCreateRequest admin) {
        Admin result = service.create(mapper.map(admin, Admin.class));
        ldapUserService.createUser(result.getId().toString(), result.getEmail(), admin.getPassword(), result.getFirstName(), result.getLastName(), "admin");
        return new ResponseEntity<>(mapper.map(result, AdminResponse.class), HttpStatus.CREATED);
    }

    @PutMapping({"/"})
    public ResponseEntity<AdminResponse> update(@RequestBody AdminUpdateRequest admin) {
        return new ResponseEntity<>(mapper.map(service.update(mapper.map(admin, Admin.class)),AdminResponse.class),HttpStatus.OK);
    }

    @DeleteMapping({"/{adminId}"})
    public ResponseEntity<AdminResponse> remove(@PathVariable Long adminId) {
        Admin admin = service.remove(adminId);
        if(admin==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(admin,AdminResponse.class), HttpStatus.OK);
    }

    @GetMapping({"/reports"})
    public ResponseEntity<Collection<GuestReportResponse>> getReports(){
        Collection<GuestReport> reports = service.getAllReports();
        Collection<GuestReportResponse> reportResponses =  reports.stream()
                .map(report -> mapper.map(report, GuestReportResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(reportResponses, HttpStatus.OK);
    }

    @GetMapping({"/accommodationChanges"})
    public ResponseEntity<Collection<AccommodationResponse>> getAccommodationChanges(){
        Collection<AccommodationModification> accommodationModifications = service.getAllAccommodationChanges();
        Collection<AccommodationResponse> accommodationResponse =  accommodationModifications.stream()
                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(accommodationResponse, HttpStatus.OK);
    }

    @PutMapping({"/accommodations/{accommodationId}"})
    public ResponseEntity<AccommodationResponse> handleAccommodationChange(@PathVariable Long accommodationId, @RequestParam(name = "approve") boolean approve){
        if (approve){
            return new ResponseEntity<>(mapper.map(service.approveAccommodationChange(accommodationId),AccommodationResponse.class),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(mapper.map(service.denyAccommodationChange(accommodationId),AccommodationResponse.class),HttpStatus.OK);
        }
    }

    @PutMapping({"/block/{userId}"})
    public ResponseEntity<UserBlockResponse> blockUser(@PathVariable Long userId){
        User user = service.blockUser(userId);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(user, UserBlockResponse.class), HttpStatus.OK);
    }
}

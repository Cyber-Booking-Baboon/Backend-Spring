package rs.ac.uns.ftn.BookingBaboon.controllers.users;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.AccommodationModification;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.SysAdmin;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.GuestReportResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.admins.UserBlockResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.sysadmins.*;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.ISysAdminService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/sysadmin")
public class SysAdminController {
    private final ISysAdminService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<SysAdminResponse>> getSysAdmins() {
        Collection<SysAdmin> admins = service.getAll();
        Collection<SysAdminResponse> adminResponses =  admins.stream()
                .map(admin -> mapper.map(admin, SysAdminResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(adminResponses, HttpStatus.OK);
    }

    @GetMapping({"/{adminId}"})
    public ResponseEntity<SysAdminResponse> get(@PathVariable Long adminId) {
        SysAdmin admin = service.get(adminId);
        if(admin==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(admin, SysAdminResponse.class), HttpStatus.OK);
    }

    @PostMapping({"/"})
    public ResponseEntity<SysAdminResponse> create(@RequestBody SysAdminCreateRequest admin) {
        return new ResponseEntity<>(mapper.map(service.create(mapper.map(admin, SysAdmin.class)),SysAdminResponse.class), HttpStatus.CREATED);
    }

    @PutMapping({"/"})
    public ResponseEntity<SysAdminResponse> update(@RequestBody SysAdminUpdateRequest admin) {
        return new ResponseEntity<>(mapper.map(service.update(mapper.map(admin, SysAdmin.class)),SysAdminResponse.class),HttpStatus.OK);
    }

    @DeleteMapping({"/{adminId}"})
    public ResponseEntity<SysAdminResponse> remove(@PathVariable Long adminId) {
        SysAdmin admin = service.remove(adminId);
        if(admin==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(admin,SysAdminResponse.class), HttpStatus.OK);
    }

}

package rs.ac.uns.ftn.BookingBaboon.controllers.users;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.notifications.NotificationType;
import rs.ac.uns.ftn.BookingBaboon.domain.reservation.Reservation;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Host;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.hosts.*;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.IHostService;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.hosts.HostResponse;
import rs.ac.uns.ftn.BookingBaboon.services.users.ldap.LdapUserService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/hosts")
@SecurityRequirement(name = "Keycloak")
public class HostController {
    private final IHostService service;
    private final ModelMapper mapper;

    @Autowired
    private LdapUserService ldapUserService;

    @GetMapping
    public ResponseEntity<Collection<HostResponse>> getHosts() {
        Collection<Host> hosts = service.getAll();
        Collection<HostResponse> hostResponses =  hosts.stream()
                .map(host -> mapper.map(host, HostResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(hostResponses, HttpStatus.OK);
    }

    @GetMapping({"/{hostId}"})
    public ResponseEntity<HostResponse> get(@PathVariable Long hostId) {
        Host host = service.get(hostId);
        if(host==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(host, HostResponse.class), HttpStatus.OK);
    }

    @PostMapping({"/"})
    public ResponseEntity<HostResponse> create(@RequestBody HostCreateRequest host) {
        ldapUserService.createUser(host.getEmail(), host.getPassword(), host.getFirstName(), host.getLastName(), "host");
        return new ResponseEntity<>(mapper.map(service.create(mapper.map(host, Host.class)),HostResponse.class), HttpStatus.CREATED);
    }

    @PutMapping({"/"})
    public ResponseEntity<HostResponse> update(@RequestBody HostUpdateRequest host) {
        return new ResponseEntity<>(mapper.map(service.update(mapper.map(host, Host.class)),HostResponse.class),HttpStatus.OK);
    }

    @DeleteMapping({"/{hostId}"})
    public ResponseEntity<HostResponse> remove(@PathVariable Long hostId) {
        Host host = service.remove(hostId);
        if(host==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(host,HostResponse.class), HttpStatus.OK);
    }

    @GetMapping({"/profile/email/{hostEmail}"})
    public ResponseEntity<HostProfile> getProfileByEmail(@PathVariable String hostEmail) {
        Host host = service.getProfile(hostEmail);
        if(host==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(host, HostProfile.class), HttpStatus.OK);
    }

    @GetMapping({"/profile/{hostId}"})
    public ResponseEntity<HostProfile> getProfile(@PathVariable Long hostId) {
        Host host = service.get(hostId);
        if(host==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(host, HostProfile.class), HttpStatus.OK);
    }

    @PutMapping({"/{hostId}/toggle-notifications/{notificationType}"})
    public ResponseEntity<HostNotificationSettings> toggleNotifications (@PathVariable Long hostId, @PathVariable String notificationType){
        return new ResponseEntity<> (mapper.map(service.toggleNotificaitons(hostId, NotificationType.valueOf(notificationType)),HostNotificationSettings.class), HttpStatus.OK);
    }

    @PutMapping({"/reservations/{reservationId}"})
    public ResponseEntity<Reservation> handleReservation(@PathVariable Long reservationId, @RequestParam(name="isApproved") boolean isApproved){
        return new ResponseEntity<> (service.handleReservation(reservationId,isApproved),HttpStatus.OK);
    }

    @GetMapping({"email/{hostEmail}"})
    public ResponseEntity<HostResponse> getHostByEmail(@PathVariable String hostEmail) {
        Host host = service.get(hostEmail);
        if(host==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(host, HostResponse.class), HttpStatus.OK);
    }

}

package rs.ac.uns.ftn.BookingBaboon.controllers.users;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.accommodation_handling.Accommodation;
import rs.ac.uns.ftn.BookingBaboon.domain.notifications.NotificationType;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Guest;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Host;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.guests.*;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.hosts.HostResponse;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.IGuestService;
import rs.ac.uns.ftn.BookingBaboon.services.users.ldap.LdapUserService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/guests")
@SecurityRequirement(name = "Keycloak")
public class GuestController {
    private final IGuestService service;
    private final ModelMapper mapper;

    @Autowired
    private LdapUserService ldapUserService;
    @GetMapping
    public ResponseEntity<Collection<GuestResponse>> getGuests() {
        Collection<Guest> guests = service.getAll();
        Collection<GuestResponse> guestResponses =  guests.stream()
                .map(guest -> mapper.map(guest, GuestResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(guestResponses, HttpStatus.OK);
    }

    @GetMapping({"/{guestId}"})
    public ResponseEntity<GuestResponse> get(@PathVariable Long guestId) {
        Guest guest = service.get(guestId);
        if(guest==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(guest, GuestResponse.class), HttpStatus.OK);
    }

    @PostMapping({"/"})
    public ResponseEntity<GuestResponse> create(@RequestBody GuestCreateRequest guest) {
        Guest result = service.create(mapper.map(guest, Guest.class));
        ldapUserService.createUser(result.getId().toString(), result.getEmail(), guest.getPassword(), result.getFirstName(), result.getLastName(), "guest");
        return new ResponseEntity<>(mapper.map(result,GuestResponse.class), HttpStatus.CREATED);
    }

    @PutMapping({"/"})
    public ResponseEntity<GuestResponse> update(@RequestBody GuestUpdateRequest guest) {
        return new ResponseEntity<>(mapper.map(service.update(mapper.map(guest, Guest.class)),GuestResponse.class),HttpStatus.OK);
    }

    @DeleteMapping({"/{guestId}"})
    public ResponseEntity<GuestResponse> remove(@PathVariable Long guestId) {
        Guest guest = service.remove(guestId);
        if(guest==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(guest,GuestResponse.class), HttpStatus.OK);
    }

    @GetMapping({"/profile/{guestEmail}"})
    public ResponseEntity<GuestProfile> getProfile(@PathVariable String guestEmail) {
        Guest guest = service.getProfile(guestEmail);
        if(guest==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(guest, GuestProfile.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('GUEST')")
    @GetMapping({"{guestId}/favorite-accommodations"})
    public ResponseEntity<Collection<AccommodationResponse>> getFavorites(@PathVariable Long guestId){
        Collection<Accommodation> accommodations = service.getFavorites(guestId);

        Collection<AccommodationResponse> accommodationResponses = accommodations.stream()
                .map(accommodation -> mapper.map(accommodation, AccommodationResponse.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationResponses,HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('GUEST')")
    @PutMapping({"{guestId}/favorite-accommodations/add/{accommodationId}"})
    public ResponseEntity<GuestResponse> addFavorite(@PathVariable Long guestId, @PathVariable Long accommodationId){
        Guest result = service.addFavorite(guestId,accommodationId);
        return new ResponseEntity<>(mapper.map(result,GuestResponse.class),HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('GUEST')")
    @PutMapping({"{guestId}/favorite-accommodations/remove/{accommodationId}"})
    public ResponseEntity<GuestResponse> removeFavorite(@PathVariable Long guestId, @PathVariable Long accommodationId){
        Guest result = service.removeFavorite(guestId,accommodationId);
        return new ResponseEntity<>(mapper.map(result,GuestResponse.class),HttpStatus.OK);
    }

    @PutMapping({"/{guestId}/toggle-notifications/{notificationType}"})
    public ResponseEntity<GuestNotificationSettings> toggleNotifications (@PathVariable Long guestId, @PathVariable String notificationType){
        return new ResponseEntity<> (mapper.map(service.toggleNotifications(guestId, NotificationType.valueOf(notificationType)), GuestNotificationSettings.class), HttpStatus.OK);
    }
}

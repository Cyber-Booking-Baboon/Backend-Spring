package rs.ac.uns.ftn.BookingBaboon.controllers.notifications;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.notifications.Notification;
import rs.ac.uns.ftn.BookingBaboon.dtos.notifications.NotificationCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.notifications.NotificationRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.notifications.NotificationResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.notifications.NotificationUpdateRequest;
import rs.ac.uns.ftn.BookingBaboon.services.notifications.INotificationService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/notifications")
@SecurityRequirement(name = "Keycloak")
public class NotificationController {
    private final INotificationService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<NotificationResponse>> getNotifications() {
        Collection<Notification> notifications = service.getAll();
        Collection<NotificationResponse> notificationResponses =  notifications.stream()
                .map(user -> mapper.map(user, NotificationResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(notificationResponses, HttpStatus.OK);
    }

    @GetMapping({"/{notificationId}"})
    public ResponseEntity<NotificationResponse> get(@PathVariable Long notificationId) {
        Notification notification = service.get(notificationId);
        if(notification==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(notification,NotificationResponse.class), HttpStatus.OK);
    }

    @PostMapping({"/"})
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationCreateRequest notification) {
        return new ResponseEntity<>(mapper.map(service.create(mapper.map(notification, Notification.class)),NotificationResponse.class), HttpStatus.CREATED);
    }

    @PutMapping({"/"})
    public NotificationResponse update(@RequestBody NotificationUpdateRequest notification) {
        return mapper.map(service.update(mapper.map(notification, Notification.class)),NotificationResponse.class);
    }

    @DeleteMapping({"/{notificationId}"})
    public ResponseEntity<NotificationResponse> remove(@PathVariable Long notificationId) {
        Notification notification = service.remove(notificationId);
        if(notification==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(notification,NotificationResponse.class), HttpStatus.OK);
    }

    @GetMapping({"/user/{userId}"})
    public ResponseEntity<Collection<NotificationResponse>> getByUserId(@PathVariable Long userId){
        Collection<Notification> notifications = service.getByUserId(userId);
        Collection<NotificationResponse> notificationResponses =  notifications.stream()
                .map(accommodation -> mapper.map(accommodation, NotificationResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(notificationResponses, HttpStatus.OK);
    }

    @GetMapping({"/user/{userId}/unread-count"})
    public ResponseEntity<Integer> getUnreadCountByUserId(@PathVariable Long userId){
        Integer unread = service.getUnreadCountByUserId(userId);
        return new ResponseEntity<>(unread, HttpStatus.OK);
    }

    @PutMapping({"/{notificationId}/read"})
    public ResponseEntity<NotificationResponse> read(@PathVariable Long notificationId) {
        Notification result = service.read(notificationId);
        return new ResponseEntity<>(mapper.map(result, NotificationResponse.class), HttpStatus.OK);
    }

}

package rs.ac.uns.ftn.BookingBaboon.controllers.reports;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.GuestReport;
import rs.ac.uns.ftn.BookingBaboon.domain.users.Guest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.GuestReportCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.GuestReportRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.GuestReportResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.GuestReportUpdateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.guests.GuestResponse;
import rs.ac.uns.ftn.BookingBaboon.services.reports.interfaces.IGuestReportService;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/guest-reports")
@SecurityRequirement(name = "Keycloak")
public class GuestReportController {

    private final IGuestReportService service;
    private final ModelMapper mapper;


    // Get all guest reports
    @GetMapping
    public ResponseEntity<Collection<GuestReportResponse>> getGuests() {
        Collection<GuestReport> guestReports = service.getAll();
        Collection<GuestReportResponse> guestReportResponses =  guestReports.stream()
                .map(guestReport -> mapper.map(guestReport, GuestReportResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(guestReportResponses, HttpStatus.OK);
    }

    // Get a guest report by ID
    @GetMapping({"/{guestReportId}"})
    public ResponseEntity<GuestReportResponse> get(@PathVariable Long guestReportId) {
        GuestReport guestReport = service.get(guestReportId);
        if(guestReport==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(guestReport, GuestReportResponse.class), HttpStatus.OK);
    }

    // Create a new guest report
    @PostMapping({"/"})
    public ResponseEntity<GuestReportResponse> create(@RequestBody GuestReportCreateRequest guestReport) {

        return new ResponseEntity<>(mapper.map(service.create(mapper.map(guestReport, GuestReport.class)), GuestReportResponse.class), HttpStatus.CREATED);
    }

    // Update an existing guest report
    @PutMapping({"/"})
    public ResponseEntity<GuestReportResponse> update(@RequestBody GuestReportUpdateRequest guestReport) {

        return new ResponseEntity<>(mapper.map(service.update(mapper.map(guestReport, GuestReport.class)),GuestReportResponse.class),HttpStatus.OK);
    }

    // Delete a guest report by ID
    @DeleteMapping("/{guestReportId}")
    public ResponseEntity<GuestReportResponse> remove(@PathVariable Long guestReportId) {

        GuestReport guestReport = service.remove(guestReportId);
        if(guestReport==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(guestReport,GuestReportResponse.class), HttpStatus.OK);
    }
}
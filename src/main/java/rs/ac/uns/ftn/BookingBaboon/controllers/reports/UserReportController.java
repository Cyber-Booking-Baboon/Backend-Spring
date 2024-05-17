package rs.ac.uns.ftn.BookingBaboon.controllers.reports;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.Report;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.*;
import rs.ac.uns.ftn.BookingBaboon.services.reports.interfaces.IUserReportService;

import java.util.ArrayList;
import java.util.Collection;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user-reports")
@SecurityRequirement(name = "Keycloak")
public class UserReportController {

    private final IUserReportService service;
    private final ModelMapper mapper;

    @PreAuthorize("hasAuthority('ADMIN')")

    @GetMapping
    public ResponseEntity<Collection<Report>> getUserReports() {
        Collection<Report> userReports = service.getAll();
        Collection<Report> userReportResponses = new ArrayList<>(userReports);
        return new ResponseEntity<>(userReportResponses, HttpStatus.OK);
    }
}
package rs.ac.uns.ftn.BookingBaboon.controllers.reports;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.HostReport;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.*;
import rs.ac.uns.ftn.BookingBaboon.services.reports.interfaces.IHostReportService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/host-reports")
@SecurityRequirement(name = "Keycloak")
public class HostReportController {

    private final IHostReportService service;
    private final ModelMapper mapper;


    // Get all host reports
    @GetMapping
    public ResponseEntity<Collection<HostReportResponse>> getHosts() {
        Collection<HostReport> hostReports = service.getAll();
        Collection<HostReportResponse> hostReportResponses =  hostReports.stream()
                .map(hostReport -> mapper.map(hostReport, HostReportResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(hostReportResponses, HttpStatus.OK);
    }

    // Get a host report by ID
    @GetMapping({"/{hostReportId}"})
    public ResponseEntity<HostReportResponse> get(@PathVariable Long hostReportId) {
        HostReport hostReport = service.get(hostReportId);
        if(hostReport==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(hostReport, HostReportResponse.class), HttpStatus.OK);
    }

    // Create a new host report
    @PostMapping({"/"})
    public ResponseEntity<HostReportResponse> create(@RequestBody HostReportCreateRequest hostReport) {

        return new ResponseEntity<>(mapper.map(service.create(mapper.map(hostReport, HostReport.class)), HostReportResponse.class), HttpStatus.CREATED);
    }

    // Update an existing host report
    @PutMapping({"/"})
    public ResponseEntity<HostReportResponse> update(@RequestBody HostReportUpdateRequest hostReport) {

        return new ResponseEntity<>(mapper.map(service.update(mapper.map(hostReport, HostReport.class)),HostReportResponse.class),HttpStatus.OK);
    }

    // Delete a host report by ID
    @DeleteMapping("/{hostReportId}")
    public ResponseEntity<HostReportResponse> remove(@PathVariable Long hostReportId) {

        HostReport hostReport = service.remove(hostReportId);
        if(hostReport==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(hostReport,HostReportResponse.class), HttpStatus.OK);
    }
}
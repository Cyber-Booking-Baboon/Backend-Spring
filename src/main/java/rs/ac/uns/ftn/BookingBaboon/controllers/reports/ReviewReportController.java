package rs.ac.uns.ftn.BookingBaboon.controllers.reports;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.ReviewReport;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.ReviewReportCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.ReviewReportResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.reports.ReviewReportUpdateRequest;
import rs.ac.uns.ftn.BookingBaboon.services.reports.interfaces.IReviewReportService;
import rs.ac.uns.ftn.BookingBaboon.services.reports.interfaces.IReviewReportService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/review-reports")
public class ReviewReportController {
    private final IReviewReportService service;
    private final ModelMapper mapper;


    @GetMapping()
    public ResponseEntity<Collection<ReviewReport>> getReviewReports() {
        Collection<ReviewReport> reviewReports = service.getAll();
        return new ResponseEntity<>(reviewReports, HttpStatus.OK);
    }

    @GetMapping({"/{reviewReportId}"})
    public ResponseEntity<ReviewReportResponse> get(@PathVariable Long reviewReportId) {
        ReviewReport reviewReport = service.get(reviewReportId);
        if(reviewReport==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(reviewReport, ReviewReportResponse.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('HOST')")
    @PostMapping({"/"})
    public ResponseEntity<ReviewReportResponse> create(@RequestBody ReviewReportCreateRequest reviewReport) {

        return new ResponseEntity<>(mapper.map(service.create(mapper.map(reviewReport, ReviewReport.class)), ReviewReportResponse.class), HttpStatus.CREATED);
    }

    @PutMapping({"/"})
    public ResponseEntity<ReviewReportResponse> update(@RequestBody ReviewReportUpdateRequest reviewReport) {

        return new ResponseEntity<>(mapper.map(service.update(mapper.map(reviewReport, ReviewReport.class)),ReviewReportResponse.class),HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{reviewReportId}")
    public ResponseEntity<ReviewReportResponse> remove(@PathVariable Long reviewReportId) {

        ReviewReport reviewReport = service.remove(reviewReportId);
        if(reviewReport==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( mapper.map(reviewReport,ReviewReportResponse.class), HttpStatus.OK);
    }

}

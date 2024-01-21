package rs.ac.uns.ftn.BookingBaboon.dtos.reports;

import lombok.Data;
import rs.ac.uns.ftn.BookingBaboon.domain.reports.ReportStatus;
import rs.ac.uns.ftn.BookingBaboon.dtos.reviews.ReviewReferenceRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.UserReferenceRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.users.guests.GuestReferenceRequest;

import java.util.Date;

@Data
public class ReviewReportRequest {
    private UserReferenceRequest reportee;
    private Date createdOn;
    private ReportStatus status;
    private String message;
    private ReviewReferenceRequest reportedReview;
}

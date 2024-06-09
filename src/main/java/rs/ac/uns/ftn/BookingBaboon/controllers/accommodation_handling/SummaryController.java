package rs.ac.uns.ftn.BookingBaboon.controllers.accommodation_handling;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.summary.AccommodationMonthlySummary;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.summary.PeriodSummary;
import rs.ac.uns.ftn.BookingBaboon.services.accommodation_handling.interfaces.ISummaryService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Date;
import java.util.Locale;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/summary")
@SecurityRequirement(name = "Keycloak")
public class SummaryController {

    private final ISummaryService service;
    private final TemplateEngine templateEngine;

    @GetMapping("/monthly/{accommodationId}")
    public ResponseEntity<AccommodationMonthlySummary> getMonthlySummary(@PathVariable Long accommodationId) {
        AccommodationMonthlySummary monthlySummary = service.getMonthlySummary(accommodationId);
        return new ResponseEntity<>(monthlySummary, HttpStatus.OK);
    }

    @GetMapping("/period")
    public ResponseEntity<PeriodSummary> getPeriodSummary(
            @RequestParam(name = "host-id") Long hostId,
            @RequestParam(name = "start-date") String startDate,
            @RequestParam(name = "end-date") String endDate) {
        PeriodSummary periodSummary = service.getPeriodSummary(hostId, startDate, endDate);
        return new ResponseEntity<>(periodSummary, HttpStatus.OK);
    }

    @GetMapping("/period/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> getPeriodSummaryPdf(
            @RequestParam(name = "host-id") Long hostId,
            @RequestParam(name = "start-date") String startDate,
            @RequestParam(name = "end-date") String endDate) throws IOException {

        PeriodSummary periodSummary = service.getPeriodSummary(hostId, startDate, endDate);

        Context context = new Context(Locale.getDefault());
        context.setVariable("periodSummary", periodSummary);

        String htmlContent = templateEngine.process("pdfPeriodSummaryTemplate", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        HtmlConverter.convertToPdf(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), pdf);

        pdf.close();

        return new ResponseEntity<>(outputStream.toByteArray(), HttpStatus.OK);
    }

    @GetMapping("/monthly/{accommodationId}/pdf")
    public ResponseEntity<byte[]> getMonthlySummaryPdf(@PathVariable Long accommodationId) throws IOException {

        AccommodationMonthlySummary monthlySummary = service.getMonthlySummary(accommodationId);

        Context context = new Context(Locale.getDefault());
        context.setVariable("accommodationName", monthlySummary.getAccommodationId());
        context.setVariable("startDate", monthlySummary.getTimeSlot().getStartDate());
        context.setVariable("endDate", monthlySummary.getTimeSlot().getEndDate());
        context.setVariable("reservationsData", monthlySummary.getReservationsData().entrySet());
        context.setVariable("profitData", monthlySummary.getProfitData().entrySet());

        String htmlContent = templateEngine.process("pdfMonthlySummaryTemplate", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        HtmlConverter.convertToPdf(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), pdf);

        pdf.close();

        return new ResponseEntity<>(outputStream.toByteArray(), HttpStatus.OK);
    }
}

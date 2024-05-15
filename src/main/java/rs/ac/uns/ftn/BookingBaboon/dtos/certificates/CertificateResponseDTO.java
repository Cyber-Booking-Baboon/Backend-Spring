package rs.ac.uns.ftn.BookingBaboon.dtos.certificates;

import lombok.Data;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateExtension;

import java.util.Date;
import java.util.List;

@Data
public class CertificateResponseDTO {
    private String alias;
    private SubjectDTO subject;
    private Date startDate;
    private Date endDate;
    private List<CertificateExtension> extensions;
}

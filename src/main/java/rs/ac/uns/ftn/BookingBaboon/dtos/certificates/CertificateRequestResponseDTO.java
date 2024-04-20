package rs.ac.uns.ftn.BookingBaboon.dtos.certificates;

import lombok.Data;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateExtension;

import java.util.Date;
import java.util.List;

@Data
public class CertificateRequestResponseDTO {
    private Long id;

    private String alias;
    //Subject
    private String subjectCN; // Common Name
    private String subjectSurname; // Surname
    private String subjectGivenName; // Given Name
    private String subjectO; // Organization
    private String subjectOU; // Organizational Unit
    private String subjectC; // Country
    private String subjectE; // Email
    private String subjectUID; // User ID

    private Date startDate;
    private Date endDate;

    List<CertificateExtension> extensions;
}

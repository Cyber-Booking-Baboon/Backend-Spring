package rs.ac.uns.ftn.BookingBaboon.dtos.certificates;

import lombok.Data;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.*;
import rs.ac.uns.ftn.BookingBaboon.services.certificates.CertificateRequestStatus;

import java.util.Date;
import java.util.List;

@Data
public class CertificateCreateDTO {
    private String alias;
    private SubjectDTO subject;
    private String issuerAlias;
    private Date startDate;
    private Date endDate;
    private List<CertificateExtension> extensions;

    public CertificateCreateDTO(CertificateRequest request){
        alias = request.getAlias();
        subject = new SubjectDTO(request.getSubjectCN(), request.getSubjectSurname(), request.getSubjectGivenName(),
                request.getSubjectO(), request.getSubjectOU(), request.getSubjectC(), request.getSubjectE(), request.getSubjectUID());
        issuerAlias = request.getIssuerAlias();
        startDate = request.getStartDate();
        endDate = request.getEndDate();
        extensions = request.getExtensions();
    }
}

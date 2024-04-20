package rs.ac.uns.ftn.BookingBaboon.domain.certificates;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "certificate_requests")
@TableGenerator(name="certificate_request_id_generator", table="primary_keys", pkColumnName="key_pk", pkColumnValue="certificate_request", initialValue = 0, valueColumnName="value_pk")
public class CertificateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "certificate_request_id_generator")
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

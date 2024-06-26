package rs.ac.uns.ftn.BookingBaboon.repositories.certificates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateRequest;
import rs.ac.uns.ftn.BookingBaboon.services.certificates.CertificateRequestStatus;

import java.util.List;
import rs.ac.uns.ftn.BookingBaboon.services.certificates.CertificateRequestStatus;

import java.util.Collection;

@Repository
public interface ICertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {
    Collection<CertificateRequest> findAllBySubjectUID(String subjectUID);

    CertificateRequest findBySubjectUIDAndStatus(String subjectUID, CertificateRequestStatus status);
    List<CertificateRequest> findByStatus(CertificateRequestStatus status);
}

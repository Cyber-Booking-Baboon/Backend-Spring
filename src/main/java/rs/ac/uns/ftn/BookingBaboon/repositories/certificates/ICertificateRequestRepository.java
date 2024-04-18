package rs.ac.uns.ftn.BookingBaboon.repositories.certificates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateRequest;

@Repository
public interface ICertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {
}

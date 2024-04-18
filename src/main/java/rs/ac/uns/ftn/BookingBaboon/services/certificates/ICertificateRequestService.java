package rs.ac.uns.ftn.BookingBaboon.services.certificates;

import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateRequest;

import java.util.Collection;

public interface ICertificateRequestService {
    Collection<CertificateRequest> getAll();

    CertificateRequest get(Long certificateRequestId) throws ResponseStatusException;

    CertificateRequest create(CertificateRequest certificateRequest) throws ResponseStatusException;

    CertificateRequest update(CertificateRequest certificateRequest) throws ResponseStatusException;

    CertificateRequest remove(Long certificateRequestId);

}

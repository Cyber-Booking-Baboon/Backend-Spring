package rs.ac.uns.ftn.BookingBaboon.services.certificates;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.certificates.CertificateResponseDTO;

import java.util.Collection;

public interface ICertificateRequestService {
    Collection<CertificateRequest> getAll();

    CertificateRequest get(Long certificateRequestId) throws ResponseStatusException;

    CertificateRequest create(CertificateRequest certificateRequest) throws ResponseStatusException;

    CertificateRequest update(CertificateRequest certificateRequest) throws ResponseStatusException;

    CertificateRequest remove(Long certificateRequestId);

    CertificateRequest approve(Long certificateRequestId) throws ResponseStatusException;

    CertificateRequest deny(Long certificateRequestId) throws ResponseStatusException;

    Collection<CertificateRequest> getAllPending();
    ResponseEntity<String> getPrivateKey(String alias, Long id, String authorizationHeader);

    Collection<CertificateRequest> getAllByHost(String id);

    ResponseEntity<CertificateResponseDTO> getCertificateByHost(String id);
}

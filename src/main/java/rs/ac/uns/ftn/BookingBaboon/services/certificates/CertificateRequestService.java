package rs.ac.uns.ftn.BookingBaboon.services.certificates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.BookingBaboon.config.security.JwtTokenUtil;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.certificates.CertificateCreateDTO;
import rs.ac.uns.ftn.BookingBaboon.dtos.certificates.CertificateResponseDTO;
import rs.ac.uns.ftn.BookingBaboon.repositories.certificates.ICertificateRequestRepository;
import rs.ac.uns.ftn.BookingBaboon.services.users.UserService;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CertificateRequestService implements ICertificateRequestService {
    private final ICertificateRequestRepository repository;
    private final RestTemplate restTemplate;
    private final JwtTokenUtil tokenUtil;
    private final UserService userService;
    ResourceBundle bundle = ResourceBundle.getBundle("ValidationMessages", LocaleContextHolder.getLocale());

    @Override
    public Collection<CertificateRequest> getAll() {
        return new ArrayList<CertificateRequest>(repository.findAll());
    }

    @Override
    public CertificateRequest get(Long certificateRequestId) throws ResponseStatusException {
        Optional<CertificateRequest> found = repository.findById(certificateRequestId);
        if (found.isEmpty()) {
            String value = bundle.getString("certificateRequest.notFound");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return found.get();
    }

    @Override
    public CertificateRequest create(CertificateRequest certificateRequest) throws ResponseStatusException{
        try {
            repository.save(certificateRequest);
            repository.flush();
            return certificateRequest;
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> errors = ex.getConstraintViolations();
            StringBuilder sb = new StringBuilder(1000);
            for (ConstraintViolation<?> error : errors) {
                sb.append(error.getMessage()).append("\n");
            }
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, sb.toString());
        }
    }

    @Override
    public CertificateRequest update(CertificateRequest certificateRequest) throws ResponseStatusException {
        try {
            get(certificateRequest.getId());
            repository.save(certificateRequest);
            repository.flush();
            return certificateRequest;
        } catch (RuntimeException ex) {
            Throwable e = ex;
            Throwable c = null;
            while ((e != null) && !((c = e.getCause()) instanceof ConstraintViolationException) ) {
                e = (RuntimeException) c;
            }
            if ((c != null) && (c instanceof ConstraintViolationException)) {
                ConstraintViolationException c2 = (ConstraintViolationException) c;
                Set<ConstraintViolation<?>> errors = c2.getConstraintViolations();
                StringBuilder sb = new StringBuilder(1000);
                for (ConstraintViolation<?> error : errors) {
                    sb.append(error.getMessage() + "\n");
                }
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, sb.toString());
            }
            throw ex;
        }
    }

    @Override
    public CertificateRequest remove(Long certificateRequestId) {
        CertificateRequest found = get(certificateRequestId);
        repository.delete(found);
        repository.flush();
        return found;
    }

    @Override
    public CertificateRequest approve(Long certificateRequestId) throws ResponseStatusException {
        Optional<CertificateRequest> found = repository.findById(certificateRequestId);
        if (found.isEmpty()) {
            String value = bundle.getString("certificateRequest.notFound");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        CertificateRequest request = found.get();
        if (!request.getStatus().equals(CertificateRequestStatus.PENDING)) {
            String value = bundle.getString("certificateRequest.notPending");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        request.approve();
        sendCertificateRequest(request);
        repository.flush();
        return request;
    }

    @Override
    public CertificateRequest deny(Long certificateRequestId) throws ResponseStatusException {
        Optional<CertificateRequest> found = repository.findById(certificateRequestId);
        if (found.isEmpty()) {
            String value = bundle.getString("certificateRequest.notFound");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        CertificateRequest request = found.get();
        if (!request.getStatus().equals(CertificateRequestStatus.PENDING)) {
            String value = bundle.getString("certificateRequest.notPending");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, value);
        }
        request.deny();
        repository.flush();
        return request;
    }

    @Override
    public ResponseEntity<String> getPrivateKey(String alias, Long id, String authorizationHeader) {
        if(authorizationHeader == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
        String userId = userService.getByEmail(tokenUtil.getUsernameFromToken(authorizationHeader.substring(7))).getId().toString();
        if(!userId.equals(id.toString())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
        return sendPrivateKeyRequest(alias, id);



    }

    @Override
    public Collection<CertificateRequest> getAllByHost(String id) {
        return new ArrayList<CertificateRequest>(repository.findAllBySubjectUID(id));
    }

    @Override
    public ResponseEntity<CertificateResponseDTO> getCertificateByHost(String id) {
        CertificateRequest found = repository.findBySubjectUIDAndStatus(id, CertificateRequestStatus.APPROVED);
        if (found == null) {
            String value = bundle.getString("certificateRequest.notFound");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, value);
        }
        return getCertificate(found.getAlias());

    }

    private ResponseEntity<String> sendPrivateKeyRequest(String alias, Long id) {
        String url = "http://localhost:9090/api/certificates/" + id.toString() + "/pk/" + alias;
        return restTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity<CertificateResponseDTO> getCertificate(String alias){
        String url = "http://localhost:9090/api/certificates/" + alias;
        return restTemplate.getForEntity(url, CertificateResponseDTO.class);
    }


    public void sendCertificateRequest(CertificateRequest certificateRequest) {
        String url = "http://localhost:9090/api/certificates";

        String jsonBody = serializeCertificateRequestToJson(new CertificateCreateDTO(certificateRequest));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        restTemplate.postForEntity(url, requestEntity, Void.class);
    }

    private String serializeCertificateRequestToJson(CertificateCreateDTO certificateRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(certificateRequest);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

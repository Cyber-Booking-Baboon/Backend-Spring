package rs.ac.uns.ftn.BookingBaboon.controllers.certificates;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.BookingBaboon.domain.certificates.CertificateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.certificates.CertificateRequestCreateDTO;
import rs.ac.uns.ftn.BookingBaboon.dtos.certificates.CertificateRequestResponseDTO;
import rs.ac.uns.ftn.BookingBaboon.services.certificates.ICertificateRequestService;

import java.util.Collection;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/certificate-requests")
public class CertificateRequestController {
    private final ICertificateRequestService service;
    private final ModelMapper mapper;

    @GetMapping
    public ResponseEntity<Collection<CertificateRequestResponseDTO>> getAll() {
        Collection<CertificateRequest> amenities = service.getAll();

        return new ResponseEntity<>(amenities.stream()
                .map(certificateRequest -> mapper.map(certificateRequest, CertificateRequestResponseDTO.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<Collection<CertificateRequestResponseDTO>> getAllPending() {
        Collection<CertificateRequest> amenities = service.getAllPending();

        return new ResponseEntity<>(amenities.stream()
                .map(certificateRequest -> mapper.map(certificateRequest, CertificateRequestResponseDTO.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateRequestResponseDTO> get(@PathVariable Long id) {
        CertificateRequest certificateRequest = service.get(id);

        if (certificateRequest == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(mapper.map(certificateRequest, CertificateRequestResponseDTO.class), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CertificateRequestResponseDTO> create(@RequestBody CertificateRequestCreateDTO certificateRequestDTO) {
        CertificateRequest result = service.create(mapper.map(certificateRequestDTO, CertificateRequest.class));
        return new ResponseEntity<>(mapper.map(result, CertificateRequestResponseDTO.class), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CertificateRequestResponseDTO> update(@RequestBody CertificateRequest certificateRequest) {
        CertificateRequest result = service.update(mapper.map(certificateRequest, CertificateRequest.class));

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(mapper.map(result, CertificateRequestResponseDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        CertificateRequest certificateRequest = service.get(id);
        if (certificateRequest != null) {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<CertificateRequestResponseDTO> approve(@PathVariable Long id) {
        CertificateRequest certificateRequest = service.approve(id);
        return new ResponseEntity<>(mapper.map(certificateRequest, CertificateRequestResponseDTO.class), HttpStatus.OK);
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<CertificateRequestResponseDTO> deny(@PathVariable Long id) {
        CertificateRequest certificateRequest = service.deny(id);
        return new ResponseEntity<>(mapper.map(certificateRequest, CertificateRequestResponseDTO.class), HttpStatus.OK);
    }

}

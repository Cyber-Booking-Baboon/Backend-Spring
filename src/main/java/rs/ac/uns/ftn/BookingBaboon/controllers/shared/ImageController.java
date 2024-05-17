package rs.ac.uns.ftn.BookingBaboon.controllers.shared;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.BookingBaboon.domain.shared.Image;
import rs.ac.uns.ftn.BookingBaboon.dtos.accommodation_handling.accommodation.AccommodationResponse;
import rs.ac.uns.ftn.BookingBaboon.dtos.shared.ImageCreateRequest;
import rs.ac.uns.ftn.BookingBaboon.dtos.shared.ImageResponse;
import rs.ac.uns.ftn.BookingBaboon.services.shared.IImageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
@SecurityRequirement(name = "Keycloak")
public class ImageController {

    private final IImageService service;
    private final ModelMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable Long id) throws IOException {
        byte[] imageData = service.getImageData(id);
        if (imageData != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(service.getMediaType(id));
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<ImageResponse> create(@ModelAttribute ImageCreateRequest request) throws IOException {
        if (request.getPath() == null || request.getPath().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getFileName() == null || request.getFileName().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getContent() == null || request.getContent().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        byte[] contentBytes = request.getContent().getBytes();

        Image result = service.create(new Image(request.getPath(), request.getFileName(), contentBytes));

        return new ResponseEntity<>(mapper.map(result, ImageResponse.class), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ImageResponse> remove(@PathVariable Long id) throws IOException {
        Image image = service.remove(id);
        if(image == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ImageResponse>(mapper.map(image, ImageResponse.class), HttpStatus.OK);
    }

}

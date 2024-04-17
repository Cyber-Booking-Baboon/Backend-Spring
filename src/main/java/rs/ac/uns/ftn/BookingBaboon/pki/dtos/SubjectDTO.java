package rs.ac.uns.ftn.BookingBaboon.pki.dtos;

import lombok.Data;

@Data
public class SubjectDTO {
    private String commonName;
    private String surname;
    private String givenName;
    private String organization;
    private String organizationalUnit;
    private String country;
    private String email;
    private String userId;
}

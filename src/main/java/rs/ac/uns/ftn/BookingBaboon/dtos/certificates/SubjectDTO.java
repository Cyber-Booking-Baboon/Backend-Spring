package rs.ac.uns.ftn.BookingBaboon.dtos.certificates;

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

    public SubjectDTO(){

    }
    public SubjectDTO(String subjectCN, String subjectSurname, String subjectGivenName, String subjectO, String subjectOU, String subjectC, String subjectE, String subjectUID) {
        commonName = subjectCN;
        surname = subjectSurname;
        givenName = subjectGivenName;
        organization = subjectO;
        organizationalUnit = subjectOU;
        country = subjectC;
        email = subjectE;
        userId = subjectUID;
    }
}
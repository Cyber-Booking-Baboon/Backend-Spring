package rs.ac.uns.ftn.BookingBaboon.pki.certificates;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.BookingBaboon.domain.users.User;
import rs.ac.uns.ftn.BookingBaboon.pki.domain.Issuer;
import rs.ac.uns.ftn.BookingBaboon.pki.domain.Subject;
import rs.ac.uns.ftn.BookingBaboon.pki.dtos.IssuerDTO;
import rs.ac.uns.ftn.BookingBaboon.pki.dtos.SubjectDTO;
import rs.ac.uns.ftn.BookingBaboon.repositories.users.IUserRepository;
import rs.ac.uns.ftn.BookingBaboon.services.users.interfaces.IUserService;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class CertificateService {

    private final IUserService userService;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate generateCertificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);

        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Subject generateSubject(Long userId) {
        User user = userService.get(userId);
        KeyPair keyPairSubject = generateKeyPair();
        X500NameBuilder builder = buildX500NameBuilder(
                user.getFirstName() + " " + user.getLastName(),
                user.getLastName(),
                user.getFirstName(),
                "UNS-FTN",
                "Katedra za informatiku",
                "RS",
                user.getEmail(),
                String.valueOf(user.getId())
        );

        return new Subject(keyPairSubject.getPublic(), builder.build());
    }

    public Issuer generateIssuer(Long userId) {
        User user = userService.get(userId);
        KeyPair kp = generateKeyPair();
        X500NameBuilder builder = buildX500NameBuilder(
                user.getFirstName() + " " + user.getLastName(),
                user.getLastName(),
                user.getFirstName(),
                "UNS-FTN",
                "Katedra za informatiku",
                "RS",
                user.getEmail(),
                String.valueOf(user.getId())
        );

        return new Issuer(kp.getPrivate(), kp.getPublic(), builder.build());
    }

    public Subject generateSubject(SubjectDTO subjectDTO) {
        KeyPair keyPairSubject = generateKeyPair();
        X500NameBuilder builder = buildX500NameBuilder(
                subjectDTO.getCommonName(),
                subjectDTO.getSurname(),
                subjectDTO.getGivenName(),
                subjectDTO.getOrganization(),
                subjectDTO.getOrganizationalUnit(),
                subjectDTO.getCountry(),
                subjectDTO.getEmail(),
                subjectDTO.getUserId()
        );
        return new Subject(keyPairSubject.getPublic(), builder.build());
    }

    public Issuer generateIssuer(IssuerDTO issuerDTO) {
        KeyPair kp = generateKeyPair();
        X500NameBuilder builder = buildX500NameBuilder(
                issuerDTO.getCommonName(),
                issuerDTO.getSurname(),
                issuerDTO.getGivenName(),
                issuerDTO.getOrganization(),
                issuerDTO.getOrganizationalUnit(),
                issuerDTO.getCountry(),
                issuerDTO.getEmail(),
                issuerDTO.getUserId()
        );
        return new Issuer(kp.getPrivate(), kp.getPublic(), builder.build());
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private X500NameBuilder buildX500NameBuilder(String commonName, String surname, String givenName,
                                                 String organization, String organizationalUnit,
                                                 String country, String email, String userId) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, commonName);
        builder.addRDN(BCStyle.SURNAME, surname);
        builder.addRDN(BCStyle.GIVENNAME, givenName);
        builder.addRDN(BCStyle.O, organization);
        builder.addRDN(BCStyle.OU, organizationalUnit);
        builder.addRDN(BCStyle.C, country);
        builder.addRDN(BCStyle.E, email);
        builder.addRDN(BCStyle.UID, userId);
        return builder;
    }

}

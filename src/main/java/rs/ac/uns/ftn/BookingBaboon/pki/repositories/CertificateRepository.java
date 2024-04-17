package rs.ac.uns.ftn.BookingBaboon.pki.repositories;

import rs.ac.uns.ftn.BookingBaboon.pki.domain.Issuer;
import rs.ac.uns.ftn.BookingBaboon.pki.keystores.KeyStoreReader;
import rs.ac.uns.ftn.BookingBaboon.pki.keystores.KeyStoreWriter;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public class CertificateRepository implements ICertificateRepository {
    private KeyStoreReader keyStoreReader;
    private KeyStoreWriter keyStoreWriter;
    private final String keyStorePath = "src/main/resources/static/keystore.jks";

    public CertificateRepository(){
        keyStoreReader = new KeyStoreReader();
        keyStoreWriter = new KeyStoreWriter();
    }

    @Override
    public Certificate readCertificate(String keyStorePass, String alias) {
        return keyStoreReader.readCertificate(keyStorePath, keyStorePass, alias);
    }

    @Override
    public Issuer readIssuerFromStore(String alias, char[] keyStorePassword, char[] privateKeyPassword) {
        return keyStoreReader.readIssuerFromStore(keyStorePath, alias, keyStorePassword, privateKeyPassword);
    }

    @Override
    public PrivateKey readPrivateKey(String keyStorePass, String alias, String pass) {
        return keyStoreReader.readPrivateKey(keyStorePath, keyStorePass, alias, pass);
    }

    @Override
    public void write(String alias, PrivateKey issuerPrivateKey, char[] privateKeyPassword, Certificate certificate, char[] keyStorePassword) {
        keyStoreWriter.loadKeyStore(keyStorePath,  keyStorePassword);
        keyStoreWriter.write(alias, issuerPrivateKey, privateKeyPassword, certificate);
        keyStoreWriter.saveKeyStore(keyStorePath,  keyStorePassword);
    }
}

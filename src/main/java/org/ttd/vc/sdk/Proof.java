package org.ttd.vc.sdk;

import org.apache.commons.codec.binary.Hex;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.util.Map;

public class Proof {
    private LocalDateTime created;
    private CredentialSubject credentialSubject;
    private URI verificationMethod;
    private String purpose;
    private String value;
    private Map<String, Object> properties;

    public Proof(CredentialSubject credentialSubject, URI verificationMethod, String purpose,
                 Map<String, Object> properties, PrivateKey privateKey)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        this.credentialSubject = credentialSubject;
        this.verificationMethod = verificationMethod;
        this.purpose = purpose;
        this.properties = properties;
        this.value = sign(privateKey);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public CredentialSubject getCredentialSubject() {
        return credentialSubject;
    }

    public URI getVerificationMethod() {
        return verificationMethod;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getValue() {
        return value;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    private String sign(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException {
        StringBuilder claimValue = new StringBuilder();
        for (Claim c : credentialSubject.getClaims()) {
            claimValue.append(c.getValue());
        }

        Signature signature = Signature.getInstance("EcDSA");
        signature.initSign(privateKey);
        signature.update(claimValue.toString().getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(signature.sign());
    }
}

package org.ttd.vc.sdk;

import io.ipfs.multibase.Multibase;
import org.apache.commons.codec.digest.DigestUtils;
import org.webpki.jcs.JsonCanonicalizer;

import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.time.LocalDateTime;

public class Ed25519Signature2020 implements Proof {
    public static final String TYPE = "Ed25519Signature2020";
    private LocalDateTime created;
    private URI verificationMethod;
    private String proofPurpose;
    private String proofValue;


    public Ed25519Signature2020(LocalDateTime created, CredentialSubject credentialSubject, URI verificationMethod,
                                String purpose, PrivateKey privateKey)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        this.created = created;
        this.verificationMethod = verificationMethod;
        this.proofPurpose = purpose;
        this.proofValue = sign(credentialSubject, privateKey);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public URI getVerificationMethod() {
        return verificationMethod;
    }

    public String getProofPurpose() {
        return proofPurpose;
    }

    public String getProof() {
        return proofValue;
    }

    private String sign(CredentialSubject credentialSubject, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, IOException {
        String encodedString = new JsonCanonicalizer(credentialSubject.toJson().toString()).getEncodedString();

        Signature signature = Signature.getInstance("Ed25519");

        signature.initSign(privateKey);
        signature.update(DigestUtils.sha256(encodedString));
        return Multibase.encode(Multibase.Base.Base58BTC, signature.sign());
    }
}

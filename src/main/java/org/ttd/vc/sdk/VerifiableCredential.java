package org.ttd.vc.sdk;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

public class VerifiableCredential {
    private List<URI> contexts;
    private URI id;
    private List<URI> types;
    private List<Claim> credentialSubject;
    private List<Signature> proofs;
    private URI issuer;
    private LocalDateTime issuanceDate;
    private LocalDateTime expirationDate;
    private Status status;

}

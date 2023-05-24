package org.ttd.vc.sdk;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VerifiableCredential {
    private List<URI> contexts;
    private URI id;
    private List<URI> types;
    private CredentialSubject credentialSubject;
    private List<Proof> proofs;
    private URI issuer;
    private LocalDateTime issuanceDate;
    private LocalDateTime expirationDate;
    private Status status;

    private VerifiableCredential(List<URI> contexts, URI id, List<URI> types, CredentialSubject credentialSubject,
                                 List<Proof> proofs, URI issuer, LocalDateTime issuanceDate,
                                 LocalDateTime expirationDate, Status status) {
        this.contexts = contexts;
        this.id = id;
        this.types = types;
        this.credentialSubject = credentialSubject;
        this.proofs = proofs;
        this.issuer = issuer;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    public List<URI> getContexts() {
        return contexts;
    }

    public URI getId() {
        return id;
    }

    public List<URI> getTypes() {
        return types;
    }

    public CredentialSubject getCredentialSubject() {
        return credentialSubject;
    }

    public List<Proof> getProofs() {
        return proofs;
    }

    public URI getIssuer() {
        return issuer;
    }

    public LocalDateTime getIssuanceDate() {
        return issuanceDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public Status getStatus() {
        return status;
    }

    public static class Builder {
        private List<URI> contexts = new ArrayList<>();
        private URI id;
        private List<URI> types = new ArrayList<>();
        private CredentialSubject credentialSubject;
        private List<Proof> proofs = new ArrayList<>();
        private URI issuer;
        private LocalDateTime issuanceDate;
        private LocalDateTime expirationDate;
        private Status status;

        public Builder contexts(List<URI> contexts) {
            this.contexts = contexts;
            return this;
        }

        public Builder context(URI context) {
            contexts.add(context);
            return this;
        }

        public Builder id(URI id) {
            this.id = id;
            return this;
        }

        public Builder id(String id) {
            this.id = URI.create(id);
            return this;
        }

        public Builder types(List<URI> types) {
            this.types = types;
            return this;
        }

        public Builder type(URI type) {
            types.add(type);
            return this;
        }

        public Builder type(String type) {
            types.add(URI.create(type));
            return this;
        }

        public Builder credentialSubject(CredentialSubject credentialSubject) {
            this.credentialSubject = credentialSubject;
            return this;
        }

        public Builder proofs(List<Proof> proofs) {
            this.proofs = proofs;
            return this;
        }

        public Builder proof(Proof proof) {
            proofs.add(proof);
            return this;
        }

        public Builder issuer(URI issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = URI.create(issuer);
            return this;
        }

        public Builder issuanceDate(LocalDateTime issuanceDate) {
            this.issuanceDate = issuanceDate;
            return this;
        }

        public Builder expirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public VerifiableCredential build() {
            if (id == null || types == null || credentialSubject == null || proofs == null || proofs.size() == 0 ||
                    issuer == null || issuanceDate == null)
                throw new RuntimeException("ID, Type, CredentialSubject, Proofs, IssuanceDate and " +
                        "Issuer cannot be null or empty");
            return new VerifiableCredential(contexts, id, types, credentialSubject, proofs, issuer,issuanceDate,
                    expirationDate, status);
        }
    }

}

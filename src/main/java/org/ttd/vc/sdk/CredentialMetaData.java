package org.ttd.vc.sdk;

import org.ttd.Constants;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CredentialMetaData {

    private final List<URI> contexts;
    private final URI id;
    private final List<URI> types;
    private final URI issuer;
    private final LocalDateTime issuanceDate;
    private final LocalDateTime expirationDate;
    private final Status status;
    private final Map<String, Object> otherProperties;

    private CredentialMetaData(List<URI> contexts, URI id, List<URI> types, URI issuer, LocalDateTime issuanceDate,
                              LocalDateTime expirationDate, Status status, Map<String, Object> otherProperties) {
        this.contexts = contexts;
        this.id = id;
        this.types = types;
        this.issuer = issuer;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.status = status;
        this.otherProperties = otherProperties;
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

    public Map<String, Object> getOtherProperties() {
        return otherProperties;
    }

    public static class Builder {
        private List<URI> additionalContexts = new ArrayList<>();
        private URI id;
        private List<URI> types = new ArrayList<>();
        private URI issuer;
        private LocalDateTime issuanceDate;
        private LocalDateTime expirationDate;
        private Status status;
        private Map<String, Object> otherProperties;

        public Builder additionalContexts(List<URI> additionalContexts) {
            this.additionalContexts = additionalContexts;
            return this;
        }

        public Builder additionalContext(URI additionalContext) {
            additionalContexts.add(additionalContext);
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

        public Builder otherProperties(Map<String, Object> otherProperties) {
            this.otherProperties = otherProperties;
            return this;
        }

        public CredentialMetaData build() {
            if (id == null || types == null || issuer == null || issuanceDate == null)
                throw new RuntimeException("ID, Type, IssuanceDate and Issuer cannot be null or empty");
            additionalContexts.add(0, Constants.VC_DEFAULT_CONTEXT);
            return new CredentialMetaData(additionalContexts, id, types, issuer, issuanceDate,
                    expirationDate, status, otherProperties);
        }
    }
}

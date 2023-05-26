package org.ttd.vc.sdk;

import java.util.ArrayList;
import java.util.List;

public class VerifiableCredential {
    private final CredentialMetaData credentialMetaData;
    private final Credential credential;
    private final List<Proof> proofs;

    private VerifiableCredential(CredentialMetaData credentialMetaData, Credential credential, List<Proof> proofs) {
        this.credentialMetaData = credentialMetaData;
        this.credential = credential;
        this.proofs = proofs;
    }

    public CredentialMetaData getCredentialMetaData() {
        return credentialMetaData;
    }

    public Credential getCredential() {
        return credential;
    }

    public List<Proof> getProofs() {
        return proofs;
    }

    public static class Builder {
        private CredentialMetaData credentialMetaData;
        private Credential credential;
        private final List<Proof> proofs = new ArrayList<>();

        public Builder metadata(CredentialMetaData metadata) {
            this.credentialMetaData = metadata;
            return this;
        }

        public Builder credential(Credential credential) {
            this.credential = credential;
            return this;
        }

        public Builder proofs(List<Proof> proofs) {
            this.proofs.addAll(proofs);
            return this;
        }

        public Builder proof(Proof proof) {
            proofs.add(proof);
            return this;
        }

        public VerifiableCredential build() {
            if (credentialMetaData == null || credential == null || proofs.isEmpty())
                throw new RuntimeException("Metadata, credential and proofs cannot be null or empty");
            return new VerifiableCredential(credentialMetaData, credential, proofs);
        }
    }
}

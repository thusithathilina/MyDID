package org.ttd.did.sdk;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DIDDocument {
    private List<String> context;
    private DID id;
    private Set<URI> alsoKnownAs;
    private Set<DID> controllers;
    private Set<VerificationMethod> verificationMethods;
    private Set<VerificationMethod> authentications;
    private Set<VerificationMethod> assertionMethods;
    private Set<VerificationMethod> keyAgreements;
    private Set<VerificationMethod> capabilityInvocations;
    private Set<VerificationMethod> capabilityDelegations;
    private Set<Service> services;
    private DIDDocumentMetadata metadata;

    public DIDDocument(DID id) {
        this.id = id;
        context = new ArrayList<>();
        controllers = new HashSet<>();
        alsoKnownAs = new HashSet<>();
        verificationMethods = new HashSet<>();
        authentications = new HashSet<>();
        assertionMethods = new HashSet<>();
        keyAgreements = new HashSet<>();
        capabilityInvocations = new HashSet<>();
        capabilityDelegations = new HashSet<>();
        services = new HashSet<>();
        var datetime = LocalDateTime.now();
        metadata = new DIDDocumentMetadata(datetime, datetime, Constants.VERSION_DEFAULT);
    }

    public List<String> getContext() {
        return context;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    public DID getId() {
        return id;
    }

    public void setId(DID id) {
        this.id = id;
    }

    public Set<URI> getAlsoKnownAs() {
        return alsoKnownAs;
    }

    public void setAlsoKnownAs(Set<URI> alsoKnownAs) {
        this.alsoKnownAs = alsoKnownAs;
    }

    public Set<DID> getControllers() {
        return controllers;
    }

    public void setControllers(Set<DID> controllers) {
        this.controllers = controllers;
    }

    public Set<VerificationMethod> getVerificationMethods() {
        return verificationMethods;
    }

    public void setVerificationMethods(Set<VerificationMethod> verificationMethods) {
        this.verificationMethods = verificationMethods;
    }

    public Set<VerificationMethod> getAuthentications() {
        return authentications;
    }

    public void setAuthentications(Set<VerificationMethod> authentications) {
        this.authentications = authentications;
    }

    public Set<VerificationMethod> getAssertionMethods() {
        return assertionMethods;
    }

    public void setAssertionMethods(Set<VerificationMethod> assertionMethods) {
        this.assertionMethods = assertionMethods;
    }

    public Set<VerificationMethod> getKeyAgreements() {
        return keyAgreements;
    }

    public void setKeyAgreements(Set<VerificationMethod> keyAgreements) {
        this.keyAgreements = keyAgreements;
    }

    public Set<VerificationMethod> getCapabilityInvocations() {
        return capabilityInvocations;
    }

    public void setCapabilityInvocations(Set<VerificationMethod> capabilityInvocations) {
        this.capabilityInvocations = capabilityInvocations;
    }

    public Set<VerificationMethod> getCapabilityDelegations() {
        return capabilityDelegations;
    }

    public void setCapabilityDelegations(Set<VerificationMethod> capabilityDelegations) {
        this.capabilityDelegations = capabilityDelegations;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public DIDDocumentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DIDDocumentMetadata metadata) {
        this.metadata = metadata;
    }
}

package org.ttd;

public class VerificationMethod {

    private DID id;
    private String type;
    private DID controller;
    private VerificationMaterial verificationMaterial;

    public DID getId() {
        return id;
    }

    public void setId(DID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DID getController() {
        return controller;
    }

    public void setController(DID controller) {
        this.controller = controller;
    }

    public VerificationMaterial getVerificationMaterial() {
        return verificationMaterial;
    }

    public void setVerificationMaterial(VerificationMaterial verificationMaterial) {
        this.verificationMaterial = verificationMaterial;
    }

}

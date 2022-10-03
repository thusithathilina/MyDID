package org.ttd;

public class VerificationMethod {

    private DIDURL id;
    private VerificationsMaterials type;
    private DID controller;
    private VerificationMaterial verificationMaterial;

    public DIDURL getId() {
        return id;
    }

    public void setId(DIDURL id) {
        this.id = id;
    }

    public String getType() {
        return type.name();
    }

    public void setType(String type) {
        this.type = VerificationsMaterials.valueOf(type);
    }

    public void setType(VerificationsMaterials type) {
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

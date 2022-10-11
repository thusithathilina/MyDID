package org.ttd.did.sdk;

import java.util.HashMap;
import java.util.Map;

public class VerificationMethod {

    private DIDURL id;
    private VerificationsMaterials type;
    private DID controller;
    private VerificationMaterial verificationMaterial;
    private Map<String, String> otherProperties;

    public VerificationMethod() {
        otherProperties = new HashMap<>();
    }

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

    public Map<String, String> getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Map<String, String> otherProperties) {
        this.otherProperties = otherProperties;
    }

    public String getProperty(String key) {
        return otherProperties.get(key);
    }

    public void addProperty(String key, String value) {
        otherProperties.put(key, value);
    }


}

package org.ttd.did.sdk;

public class PublicKeyJwk implements VerificationMaterial {

    private String crv;
    private String x;
    private String kty;
    private String kid;

    public String getCrv() {
        return crv;
    }

    public void setCrv(String crv) {
        this.crv = crv;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    @Override
    public PublicKeyJwk getPublicKey() {
        return this;
    }
}

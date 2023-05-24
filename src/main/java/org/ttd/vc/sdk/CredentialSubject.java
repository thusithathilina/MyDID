package org.ttd.vc.sdk;

import java.util.ArrayList;
import java.util.List;

public class CredentialSubject {
    private List<Claim> claims;

    public CredentialSubject() {
        claims = new ArrayList<>();
    }

    public CredentialSubject(List<Claim> claims) {
        this.claims = claims;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public void addClaim(Claim claim) {
        claims.add(claim);
    }

    public void addClaim(String name, Object value) {
        claims.add(new Claim(name, value));
    }
}

package org.ttd.vc.sdk;

import org.json.JSONObject;

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

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        claims.forEach(claim -> jsonObject.put(claim.getName(), claim.getValue()));
        return jsonObject;
    }
}

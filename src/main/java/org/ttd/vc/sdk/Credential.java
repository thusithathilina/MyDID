package org.ttd.vc.sdk;

import org.json.JSONArray;
import org.ttd.Constants;

import java.util.ArrayList;
import java.util.List;

public class Credential {
    private final List<CredentialSubject> credentialSubjects;

    private Credential(List<CredentialSubject> credentialSubjects) {
        this.credentialSubjects = credentialSubjects;
    }

    public List<CredentialSubject> getCredentialSubjects() {
        return credentialSubjects;
    }

    public JSONArray toJson() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(credentialSubjects);
        return jsonArray;
    }

    public static class Builder {
        private final List<CredentialSubject> credentialSubjects = new ArrayList<>();

        public Builder credentialSubject(CredentialSubject credentialSubject) {
            credentialSubjects.add(credentialSubject);
            return this;
        }

        public Builder credentialSubjects(List<CredentialSubject> credentialSubjects) {
            this.credentialSubjects.addAll(credentialSubjects);
            return this;
        }

        public Credential build() {
            if (credentialSubjects.size() == 0)
                throw new RuntimeException("Empty credential");
            return new Credential(credentialSubjects);
        }
    }
}

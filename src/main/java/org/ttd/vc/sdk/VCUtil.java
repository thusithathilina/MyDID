package org.ttd.vc.sdk;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ttd.Constants;

import java.net.URI;
import java.util.List;

public class VCUtil {

    public static JSONObject getJsonRepresentation(VerifiableCredential verifiableCredential) {
        JSONObject vc = new JSONObject();

        CredentialMetaData credentialMetaData = verifiableCredential.getCredentialMetaData();
        List<URI> contexts = credentialMetaData.getContexts();
        if (contexts.size() == 1)
            vc.put(Constants.CONTEXT, contexts.get(0));
        else if (contexts.size() > 1) {
            vc.put(Constants.CONTEXT, new JSONArray(contexts));
        }

        vc.put(Constants.ID, credentialMetaData.getId());
        vc.put(Constants.TYPE, new JSONArray(credentialMetaData.getTypes()));
        vc.put(Constants.ISSUER, credentialMetaData.getIssuer());
        vc.put(Constants.ISSUANCE_DATE, credentialMetaData.getIssuanceDate());

        List<CredentialSubject> credentialSubjects = verifiableCredential.getCredential().getCredentialSubjects();
        if (credentialSubjects.size() == 1)
            vc.put(Constants.CREDENTIAL_SUBJECT, credentialSubjects.get(0).toJson());
        else
            vc.put(Constants.CREDENTIAL_SUBJECT, new JSONArray(credentialSubjects));

        List<Proof> proofs = verifiableCredential.getProofs();
        if (proofs.size() == 1)
            vc.put(Constants.PROOF, new JSONObject(proofs.get(0).toString()));
        else {
            vc.put(Constants.PROOF, new JSONArray(proofs));
        }

        return vc;
    }
}

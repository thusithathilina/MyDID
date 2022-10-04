package org.ttd;

import io.ipfs.multibase.Multibase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DidUtil {

    public DidUtil() {
    }

    /**
     * This generated the DID identifier based on indy DID generation method, which is
     * base58 encoding of the first 16 bytes of the SHA256 of the Verification Method public key
     * Base58(Truncate_msb(16(SHA256(publicKey))))
     *
     * @param keyPair
     * @param namespace
     * @return
     */
    public static DIDDocument createDid(KeyPair keyPair, String namespace, boolean implicitController) {
        return createDidWithNamespace(keyPair, namespace, implicitController);
    }

    /**
     *
     * @param keyPair
     * @param namespace
     * @return
     */
    public static DIDDocument createDid(KeyPair keyPair, String namespace) {
        return createDidWithNamespace(keyPair, namespace, true);
    }

    /**
     *
     * @param keyPair
     * @return
     */
    public static DIDDocument createDid(KeyPair keyPair) {
        return createDidWithNamespace(keyPair, "", true);
    }

    /**
     *
     * @param keyPair
     * @param implicitController
     * @return
     */
    public static DIDDocument createDid(KeyPair keyPair, boolean implicitController) {
        return createDidWithNamespace(keyPair, "", implicitController);
    }

    public static DIDDocument createDidWithNamespace(KeyPair keyPair, String didNamespace, boolean implicitController) {
        if (keyPair == null || keyPair.getPublic() == null || keyPair.getPrivate() == null)
            throw new IllegalArgumentException("KeyPair cannot be null");

        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = null;
        try {
            bytes = Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(publicKey.getEncoded()), 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (bytes == null)
            throw new RuntimeException("Couldn't create a DID identifier");

        String didIdentifier = Multibase.encode(Multibase.Base.Base58BTC, bytes);
        DID did;
        if (didNamespace != null && !didNamespace.trim().equalsIgnoreCase(""))
            did = new DID(didNamespace, didIdentifier);
        else
            did = new DID(didIdentifier);
        DIDDocument didDocument = new DIDDocument(did);
        didDocument.getContext().add(Constants.CONTEXT_W3C_DEFAULT);
        if (implicitController)
            didDocument.getControllers().add(did);
        VerificationMethod defaultVerification = new VerificationMethod();
        try {
            defaultVerification.setId(new DIDURL(did, null, null, "key-1"));
            defaultVerification.setController(did);
            defaultVerification.setType(VerificationsMaterials.PublicKeyMultibase);
            defaultVerification.setVerificationMaterial(new PublicKeyMultibase("base58", publicKey));
            didDocument.getVerificationMethods().add(defaultVerification);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return didDocument;
    }

    /**
     * This generates the JSON representation of the DID Document
     * @param didDocument DIDDocument object that needs to be represented as a JSON Object
     * @return JSONObject representation of the DID Document
     */
    public static JSONObject getJsonRepresentation(DIDDocument didDocument) {
        JSONObject didDoc = new JSONObject();

        didDoc.put("id", didDocument.getId().getFullQualifiedIdentifier());

        var alsoKnownAs = didDocument.getAlsoKnownAs();
        if (alsoKnownAs.size() == 1)
            didDoc.put("controller", alsoKnownAs.iterator().next().getFullQualifiedIdentifier());
        else if (alsoKnownAs.size() > 0) {
            Iterator<DID> iterator = alsoKnownAs.iterator();
            List<String> tmp = new ArrayList<>();
            while (iterator.hasNext()) {
                tmp.add(iterator.next().getFullQualifiedIdentifier());
            }
            didDoc.put("alsoKnownAs", tmp);
        }

        var controllers = didDocument.getControllers();
        if (controllers.size() == 1)
            didDoc.put("controller", controllers.iterator().next().getFullQualifiedIdentifier());
        else if (controllers.size() > 0) {
            Iterator<DID> iterator = controllers.iterator();
            List<String> tmp = new ArrayList<>();
            while (iterator.hasNext()) {
                tmp.add(iterator.next().getFullQualifiedIdentifier());
            }
            didDoc.put("controller", tmp);
        }

        var verificationMethods = didDocument.getVerificationMethods();
        if (verificationMethods.size() > 0) {
            Iterator<VerificationMethod> iterator = verificationMethods.iterator();
            JSONArray tmpArray = new JSONArray();
            while (iterator.hasNext()) {
                JSONObject tmpJson = new JSONObject();
                VerificationMethod verificationMethod = iterator.next();
                tmpJson.put("id", verificationMethod.getId().getFullQualifiedUrl());
                tmpJson.put("type", verificationMethod.getType());
                tmpJson.put("controller", verificationMethod.getController().getFullQualifiedIdentifier());
                if (VerificationsMaterials.PublicKeyMultibase.name().equalsIgnoreCase(verificationMethod.getType()))
                    tmpJson.put("publicKeyBase58", verificationMethod.getVerificationMaterial().getPublicKey());
                tmpArray.put(tmpJson);
            }
            didDoc.put("verificationMethod", tmpArray);
        }

        var services = didDocument.getServices();
        if (services.size() > 0) {
            Iterator<Service> iterator = services.iterator();
            JSONArray tmpArray = new JSONArray();
            while (iterator.hasNext()) {
                JSONObject tmpJson = new JSONObject();
                Service service = iterator.next();
                tmpJson.put("id", service.getId().toString());
                tmpJson.put("type", service.getType());
                tmpJson.put("serviceEndpoint", service.getServiceEndpoint().toString());
                tmpArray.put(tmpJson);
            }
            didDoc.put("service", tmpArray);
        }

        return didDoc;
    }

}

package org.ttd.did.sdk;

import io.ipfs.multibase.Multibase;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.fisco.bcos.sdk.crypto.exceptions.UnsupportedCryptoTypeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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
     * e.g. did <- Base58(Truncate_msb(16(SHA256(publicKey))))
     *
     * @param keyPair            default key pair to represent the ownership of the DID
     * @param namespace          additional namespace to append after method name. e.g. did:<method-name>:<namespace>:<identifier>
     * @param implicitController set the key pair to be the default controller of the DID
     * @return DIDDocument corresponding to the generated DID
     */
    public static DIDDocument createDid(KeyPair keyPair, String namespace, boolean implicitController) {
        return createDidWithNamespace(keyPair, namespace, implicitController);
    }

    /**
     * This generated the DID identifier based on indy DID generation method, which is
     * base58 encoding of the first 16 bytes of the SHA256 of the Verification Method public key
     * e.g. did <- Base58(Truncate_msb(16(SHA256(publicKey))))
     *
     * @param keyPair   default key pair to represent the ownership of the DID
     * @param namespace additional namespace to append after method name. e.g. did:<method-name>:<namespace>:<identifier>
     * @return DIDDocument corresponding to the generated DID
     */
    public static DIDDocument createDid(KeyPair keyPair, String namespace) {
        return createDidWithNamespace(keyPair, namespace, true);
    }

    /**
     * This generated the DID identifier based on indy DID generation method, which is
     * base58 encoding of the first 16 bytes of the SHA256 of the Verification Method public key
     * e.g. did <- Base58(Truncate_msb(16(SHA256(publicKey))))
     *
     * @param keyPair default key pair to represent the ownership of the DID
     * @return DIDDocument corresponding to the generated DID
     */
    public static DIDDocument createDid(KeyPair keyPair) {
        return createDidWithNamespace(keyPair, "", true);
    }

    /**
     * This generated the DID identifier based on indy DID generation method, which is
     * base58 encoding of the first 16 bytes of the SHA256 of the Verification Method public key
     * e.g. did <- Base58(Truncate_msb(16(SHA256(publicKey))))
     *
     * @param keyPair            default key pair to represent the ownership of the DID
     * @param implicitController set the key pair to be the default controller of the DID
     * @return DIDDocument corresponding to the generated DID
     */
    public static DIDDocument createDid(KeyPair keyPair, boolean implicitController) {
        return createDidWithNamespace(keyPair, "", implicitController);
    }

    private static DIDDocument createDidWithNamespace(KeyPair keyPair, String namespace, boolean implicitController) {
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
        if (namespace != null && !namespace.trim().equalsIgnoreCase(""))
            did = new DID(namespace, didIdentifier);
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
            if (publicKey instanceof ECPublicKey)
                defaultVerification.addProperty(Constants.CURVE_TYPE, Constants.CURVE_TYPE_EC);
            else if (publicKey instanceof EdDSAPublicKey)
                defaultVerification.addProperty(Constants.CURVE_TYPE, Constants.CURVE_TYPE_ED);
            else
                defaultVerification.addProperty(Constants.CURVE_TYPE, "None");
            ;
            didDocument.getVerificationMethods().add(defaultVerification);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return didDocument;
    }

    /**
     * This generates the JSON representation of the DID Document
     *
     * @param didDocument DIDDocument object that needs to be represented as a JSON Object
     * @return JSONObject representation of the DID Document
     */
    public static JSONObject getJsonRepresentation(DIDDocument didDocument) {
        JSONObject didDoc = new JSONObject();

        didDoc.put(Constants.ID, didDocument.getId().getFullQualifiedIdentifier());

        var alsoKnownAs = didDocument.getAlsoKnownAs();
        if (alsoKnownAs.size() == 1)
            didDoc.put(Constants.CONTROLLER, alsoKnownAs.iterator().next().toString());
        else if (alsoKnownAs.size() > 0) {
            Iterator<URI> iterator = alsoKnownAs.iterator();
            List<String> tmp = new ArrayList<>();
            while (iterator.hasNext()) {
                tmp.add(iterator.next().toString());
            }
            didDoc.put(Constants.ALSO_KNOWN_AS, tmp);
        }

        var controllers = didDocument.getControllers();
        if (controllers.size() == 1)
            didDoc.put(Constants.CONTROLLER, controllers.iterator().next().getFullQualifiedIdentifier());
        else if (controllers.size() > 0) {
            Iterator<DID> iterator = controllers.iterator();
            List<String> tmp = new ArrayList<>();
            while (iterator.hasNext()) {
                tmp.add(iterator.next().getFullQualifiedIdentifier());
            }
            didDoc.put(Constants.CONTROLLER, tmp);
        }

        var verificationMethods = didDocument.getVerificationMethods();
        if (verificationMethods.size() > 0) {
            Iterator<VerificationMethod> iterator = verificationMethods.iterator();
            JSONArray tmpArray = new JSONArray();
            while (iterator.hasNext()) {
                JSONObject json = new JSONObject();
                VerificationMethod verificationMethod = iterator.next();
                json.put(Constants.ID, verificationMethod.getId().getFullQualifiedUrl());
                json.put(Constants.TYPE, verificationMethod.getType());
                json.put(Constants.CONTROLLER, verificationMethod.getController().getFullQualifiedIdentifier());
                if (VerificationsMaterials.PublicKeyMultibase.name().equalsIgnoreCase(verificationMethod.getType()))
                    json.put(Constants.PUBLIC_KEY_BASE_58, verificationMethod.getVerificationMaterial().getPublicKey());
                verificationMethod.getOtherProperties().forEach(json::put);
                tmpArray.put(json);
            }
            didDoc.put(Constants.VERIFICATION_METHOD, tmpArray);
        }

        var services = didDocument.getServices();
        if (services.size() > 0) {
            Iterator<Service> iterator = services.iterator();
            JSONArray tmpArray = new JSONArray();
            while (iterator.hasNext()) {
                JSONObject tmpJson = new JSONObject();
                Service service = iterator.next();
                tmpJson.put(Constants.ID, service.getId().toString());
                tmpJson.put(Constants.TYPE, service.getType());
                tmpJson.put(Constants.SERVICE_ENDPOINT, service.getServiceEndpoint().toString());
                tmpArray.put(tmpJson);
            }
            didDoc.put(Constants.SERVICE, tmpArray);
        }

        return didDoc;
    }

    /**
     * Converts the string representation of a DID Document to DIDDocument object
     *
     * @param didDocRepresentation String representation of the did document
     * @return corresponding DIDDocument of the String/JSON representation
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static DIDDocument stringToDIDDocument(String didDocRepresentation)
            throws URISyntaxException, NoSuchAlgorithmException, InvalidKeySpecException {
        return jsonToDIDDocument(new JSONObject(didDocRepresentation));
    }

    /**
     * Converts the JSON representation of a DID Document to DIDDocument object
     *
     * @param jsonDoc JSONObject representation of the did document
     * @return corresponding DIDDocument of the String/JSON representation
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static DIDDocument jsonToDIDDocument(JSONObject jsonDoc)
            throws URISyntaxException, NoSuchAlgorithmException, InvalidKeySpecException {
        String didIdentifier = jsonDoc.get(Constants.ID).toString();
        DIDDocument didDocument = new DIDDocument(stringToDID(didIdentifier));

        var tmp = jsonDoc.opt(Constants.ALSO_KNOWN_AS);
        if (tmp != null) {
            if (tmp instanceof String)
                didDocument.getAlsoKnownAs().add(new URI(tmp.toString()));
            else if (tmp instanceof JSONArray) {
                for (Object o : (JSONArray) tmp)
                    didDocument.getAlsoKnownAs().add(new URI(o.toString()));
            } else
                throw new IllegalArgumentException("Invalid DID document. " + tmp);
        }

        tmp = jsonDoc.opt(Constants.CONTROLLER);
        if (tmp != null) {
            if (tmp instanceof String)
                didDocument.getControllers().add(stringToDID(tmp.toString()));
            else if (tmp instanceof JSONArray) {
                for (Object o : (JSONArray) tmp)
                    didDocument.getControllers().add(stringToDID(o.toString()));
            } else
                throw new IllegalArgumentException("Invalid DID document. " + tmp);
        }

        tmp = jsonDoc.opt(Constants.VERIFICATION_METHOD);
        if (tmp != null) {
            if (tmp instanceof JSONArray) {
                KeyFactory keyFactory = null;
                for (Object o : (JSONArray) tmp) {
                    JSONObject jsonVm = (JSONObject) o;
                    VerificationMethod vm = new VerificationMethod();
                    vm.setType(jsonVm.get(Constants.TYPE).toString());
                    vm.setController(stringToDID(jsonVm.get(Constants.CONTROLLER).toString()));
                    vm.setId(stringToDIDURL(jsonVm.get(Constants.ID).toString()));
                    byte[] key = Multibase.decode(jsonVm.get(Constants.PUBLIC_KEY_BASE_58).toString());
                    if (jsonVm.get(Constants.CURVE_TYPE).equals(Constants.CURVE_TYPE_EC))
                        keyFactory = KeyFactory.getInstance(Constants.CURVE_TYPE_EC);
                    else if (jsonVm.get(Constants.CURVE_TYPE).equals(Constants.CURVE_TYPE_ED))
                        keyFactory = KeyFactory.getInstance(Constants.ED25519);
                    else
                        throw new UnsupportedCryptoTypeException("This only supports generale EC or Edward curves");
                    PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(key));
                    vm.setVerificationMaterial(new PublicKeyMultibase("base58", pubKey));
                    didDocument.getVerificationMethods().add(vm);
                }
            } else
                throw new IllegalArgumentException("Invalid DID document. " + tmp);
        }

        tmp = jsonDoc.opt(Constants.SERVICE);
        if (tmp != null) {
            if (tmp instanceof JSONArray) {
                for (Object o : (JSONArray) tmp) {
                    JSONObject jsonVm = (JSONObject) o;
                    Service service = new Service();
                    service.setType(jsonVm.get(Constants.TYPE).toString());
                    service.setId(new URI(jsonVm.get(Constants.ID).toString()));
                    service.setServiceEndpoint(new URI(jsonVm.get(Constants.SERVICE_ENDPOINT).toString()));
                    didDocument.getServices().add(service);
                }
            } else
                throw new IllegalArgumentException("Invalid DID document. " + tmp);
        }
        return didDocument;
    }

    /**
     * Convert the string representation of a DID to DID object
     *
     * @param didString String representation of the DID
     * @return DID object representation of the given string
     */
    public static DID stringToDID(String didString) {
        didString = didString
                .replace(DID.SCHEME + Constants.DID_SEPARATOR, "")
                .replace(DID.METHOD + Constants.DID_SEPARATOR, "");
        DID did;
        if (didString.contains(Constants.DID_SEPARATOR)) {
            var tmp = didString.split(Constants.DID_SEPARATOR);
            did = new DID(tmp[0], tmp[1]);
        } else
            did = new DID(didString);
        return did;
    }

    /**
     * Convert the string representation of a DID URL to DIDURL object
     *
     * @param didUrlString string representation of a DID URL
     * @return Corresponding DIDURL object of the string representation
     * @throws URISyntaxException
     */
    public static DIDURL stringToDIDURL(String didUrlString) throws URISyntaxException {
        URI tmp = new URI(didUrlString);
        DID did = DidUtil.stringToDID(tmp.getSchemeSpecificPart());
        return new DIDURL(did, tmp.getPath(), tmp.getQuery(), tmp.getFragment());
    }

    /**
     * Convert a DID to corresponding URI object
     *
     * @param did DID object representation
     * @return corresponding URI object of the DID
     * @throws URISyntaxException
     */
    public static URI didToUri(DID did) throws URISyntaxException {
        return new URI(did.getFullQualifiedIdentifier());
    }

    /**
     * Convert a URI (URI representation of a DID) to corresponding DID object
     *
     * @param uri java.net.URI object representation of a valid DID
     * @return corresponding DID object
     */
    public static DID uriToDID (URI uri) {
        if (DID.SCHEME.equalsIgnoreCase(uri.getScheme()))
            throw new IllegalArgumentException("Invalid DID URI");
        String identifier = uri.getPath().substring(DID.METHOD.length() + 1);
        int lastIndexOf = identifier.lastIndexOf(":");
        String namespace = "";
        if (lastIndexOf != -1)
        {
            namespace = identifier.substring(0, lastIndexOf);
            identifier = identifier.substring(lastIndexOf + 1);
        }
        return new DID(namespace, identifier);
    }
}

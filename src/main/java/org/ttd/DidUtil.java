package org.ttd;

import io.ipfs.multibase.Multibase;
import netscape.javascript.JSObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DidUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public DidUtil() {
    }

    /**
     * This generated the DID identifier based on indy DID generation method, which is
     * base58 encoding of the first 16 bytes of the SHA256 of the Verification Method public key
     * Base58(Truncate_msb(16(SHA256(publicKey))))
     *
     * @param keyPair
     * @param didNamespace
     * @return
     */
    public static DIDDocument createDidWithNameSpace(KeyPair keyPair, String didNamespace) {
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
            throw new RuntimeException("Couldn't create DID identifier");

        String didIdentifier = Multibase.encode(Multibase.Base.Base58BTC, bytes);
        DID did;
        if (didNamespace != null && !didNamespace.trim().equalsIgnoreCase(""))
            did = new DID(didNamespace, didIdentifier);
        else
            did = new DID(didIdentifier);
        DIDDocument didDocument = new DIDDocument(did);
        didDocument.getContext().add(Constants.CONTEXT_W3C_DEFAULT);
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
     *
     * @param keyPair
     * @return
     */
    public static DIDDocument createDid(KeyPair keyPair) {
        return createDidWithNameSpace(keyPair, "");
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, URISyntaxException, InvalidAlgorithmParameterException, CipherException, IOException, NoSuchProviderException {
        Random random = ThreadLocalRandom.current();
        byte[] r = new byte[32];
        random.nextBytes(r);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("Ed25519");
        generator.initialize(256, new SecureRandom(r));
        KeyPair keyPair = generator.generateKeyPair();
        DIDDocument didDocument = createDid(keyPair);
        System.out.println(didDocument.getId().getFullQualifiedIdentifier());

        DIDDocument didDocument2 = createDidWithNameSpace(keyPair, "sg");
        JSONObject jsonObject = new JSONObject(didDocument);
        System.out.println(jsonObject);
        System.out.println(didDocument2.getId().getFullQualifiedIdentifier());

        String idWithoutScheme = didDocument.getId().getFullQualifiedIdentifier().replace("did:", "");
        DIDURL didurl = new DIDURL(didDocument.getId(), "/abc", "id=1", "core");
        System.out.println(didurl.getFullQualifiedUrl());

        String s = WalletUtils.generateNewWalletFile("abc123", Paths.get("/","Users", "thusithadayaratne", "Work", "DID", "MyWeb3jWallet").toFile());


    }
}

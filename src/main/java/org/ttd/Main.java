package org.ttd;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import io.ipfs.multibase.Multibase;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ttd.algorand.AlgorandUtil;
import org.ttd.did.sdk.DID;
import org.ttd.did.sdk.DIDDocument;
import org.ttd.did.sdk.DidUtil;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

public class Main {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    private static String token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    public static void main(String[] args) throws Exception {
        DID dd = new DID("g$", "g");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("Ed25519");
        generator.initialize(256, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();

        DIDDocument didDocument = DidUtil.createDid(keyPair);
        AlgodClient algodClient = AlgorandUtil.createAlgodClient();
        Account account = new Account(keyPair);

        KmdApi kmdApi = AlgorandUtil.createKmdApi();
        String defaultWalletHandle = AlgorandUtil.AlgorandSandboxPrivateNode.getDefaultWalletHandle(kmdApi);
        List<Address> walletAddresses = AlgorandUtil.getWalletAddresses(kmdApi, defaultWalletHandle);
        byte[] sk = AlgorandUtil.getPrivateKeyFromWallet(kmdApi, walletAddresses.get(0), defaultWalletHandle, "");

        AlgorandUtil.storeDID(algodClient, new Account(sk), didDocument);
        IndexerClient indexerClient = AlgorandUtil.createIndexerClient();
        JSONObject document = AlgorandUtil.getDIDDocument(indexerClient, didDocument.getId());
        DIDDocument didDocument1 = DidUtil.jsonToDIDDocument(document);


        Signature signature = Signature.getInstance("Ed25519");
        signature.initSign(keyPair.getPrivate());
        signature.update("hello".getBytes(StandardCharsets.UTF_8));
        byte[] s = signature.sign();

        String encodedString = Base64.getEncoder().encodeToString(s);
        System.out.println(encodedString);

//        byte[] key = ((JSONObject)((JSONArray)DidUtil.getJsonRepresentation(didDocument).get("verificationMethod")).get(0)).get("publicKeyBase58").toString().getBytes(StandardCharsets.UTF_8);
        byte[] key = Multibase.decode(((JSONObject)((JSONArray)DidUtil.getJsonRepresentation(didDocument).get("verificationMethod")).get(0)).get("publicKeyBase58").toString());

        var publicKey = new Ed25519PublicKeyParameters(key, 0);
        Ed25519Signer verifier = new Ed25519Signer();
        verifier.init(false, publicKey);
        verifier.update("hello".getBytes(StandardCharsets.UTF_8), 0, "hello".getBytes(StandardCharsets.UTF_8).length);
        boolean verified = verifier.verifySignature(s);
        System.out.println(verified);

        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
//        PublicKey pubKey = keyFactory.generatePublic(new RawEncodedKeySpec(key));
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));
        Signature signature2 = Signature.getInstance("Ed25519");
        signature2.initVerify(pubKey);
        signature2.update("hello".getBytes(StandardCharsets.UTF_8));
        boolean isTrue = signature2.verify(s);
        System.out.println(isTrue);


    }
}

package org.ttd;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.mnemonic.Mnemonic;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import io.ipfs.multibase.Multibase;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.ttd.algorand.AlgorandUtil;
import org.ttd.did.sdk.DIDDocument;
import org.ttd.did.sdk.DidUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {

        // part 1 - generating DIDs and DIDDocuments
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");

        byte[] keyBytesForSender = Mnemonic.toKey("sponsor ride say achieve senior height crumble promote " +
                "universe write dove bomb faculty side human taste paper grocery robot grab reason fork soul above " +
                "sphere");
        generator.initialize(256, new FixedSecureRandom(keyBytesForSender));
        KeyPair keyPairSender = generator.generateKeyPair();
        DIDDocument didDocSender = DidUtil.createDid(keyPairSender);

        byte[] keyBytesForReceiver = Mnemonic.toKey("ensure utility furnace screen have goose perfect " +
                "alone civil foam jealous pretty spatial museum prevent diary garlic adapt document heavy control " +
                "track rhythm able half");
        generator.initialize(256, new FixedSecureRandom(keyBytesForReceiver));
        KeyPair keyPairReceiver = generator.generateKeyPair();
        DIDDocument didDocReceiver = DidUtil.createDid(keyPairReceiver);


        // part 2 - Storing generated DIDDocuments on Algorand blockchain and retrieving them back
        AlgodClient algodClient = AlgorandUtil.createAlgodClient();

        KmdApi kmdApi = AlgorandUtil.createKmdApi();
        String defaultWalletHandle = AlgorandUtil.AlgorandSandboxPrivateNode.getDefaultWalletHandle(kmdApi);
        List<Address> walletAddresses = AlgorandUtil.getWalletAddresses(kmdApi, defaultWalletHandle);
        byte[] sk = AlgorandUtil.getPrivateKeyFromWallet(kmdApi, walletAddresses.get(0), defaultWalletHandle, "");
        Account steward = new Account(sk);

        IndexerClient indexerClient = AlgorandUtil.createIndexerClient();

        JSONObject documentSender = AlgorandUtil.getDIDDocument(indexerClient, didDocSender.getId());
        if (documentSender == null) {
            AlgorandUtil.storeDID(algodClient, steward, didDocSender);
            Thread.sleep(1000); // this is to ensure that indexer indexed the transaction.
            documentSender = AlgorandUtil.getDIDDocument(indexerClient, didDocSender.getId());
        }

        JSONObject documentReceiver = AlgorandUtil.getDIDDocument(indexerClient, didDocReceiver.getId());
        if (documentReceiver == null) {
            AlgorandUtil.storeDID(algodClient, steward, didDocReceiver);
            Thread.sleep(1000);
            documentReceiver = AlgorandUtil.getDIDDocument(indexerClient, didDocReceiver.getId());
        }

        DIDDocument didDocSenderOnChain = DidUtil.jsonToDIDDocument(Objects.requireNonNull(documentSender));
        DIDDocument didDocReceiverOnChain = DidUtil.jsonToDIDDocument(Objects.requireNonNull(documentReceiver));


        // part 3 - Key exchange
        KeyFactory keyFactorySender = KeyFactory.getInstance("EC");
        String receiverPubKeyEncoded = didDocReceiverOnChain.getVerificationMethods()
                .iterator().next().getVerificationMaterial().getPublicKey().toString();
        byte[] receiverPubKeyDecoded = Multibase.decode(receiverPubKeyEncoded);
        PublicKey pubKeyReceiver = keyFactorySender.generatePublic(new X509EncodedKeySpec(receiverPubKeyDecoded));
        KeyAgreement kaSender = KeyAgreement.getInstance("ECDH", "BC");
        kaSender.init(keyPairSender.getPrivate());
        kaSender.doPhase(pubKeyReceiver, true);
        SecretKey secretKeySender = kaSender.generateSecret("AES");

        KeyFactory keyFactoryReceiver = KeyFactory.getInstance("EC");
        String senderPubKeyEncoded = didDocSenderOnChain.getVerificationMethods()
                .iterator().next().getVerificationMaterial().getPublicKey().toString();
        byte[] senderPubKeyDecoded = Multibase.decode(senderPubKeyEncoded);
        PublicKey pubKeySender = keyFactoryReceiver.generatePublic(new X509EncodedKeySpec(senderPubKeyDecoded));
        KeyAgreement kaReceiver = KeyAgreement.getInstance("ECDH", "BC");
        kaReceiver.init(keyPairReceiver.getPrivate());
        kaReceiver.doPhase(pubKeySender, true);
        SecretKey secretKeyReceiver = kaReceiver.generateSecret("AES");

        assert Arrays.equals(secretKeySender.getEncoded(), secretKeyReceiver.getEncoded());


        // part 4 - Signing/verification and encryption/decryption
        String message = "Use of decentralised identifiers";

        Signature senderSignature = Signature.getInstance("EcDSA");
        senderSignature.initSign(keyPairSender.getPrivate());
        senderSignature.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] senderSignedMessage = senderSignature.sign();

        Signature receiverSignature = Signature.getInstance("EcDSA");
        receiverSignature.initVerify(pubKeySender);
        receiverSignature.update(message.getBytes(StandardCharsets.UTF_8));
        boolean isVerified = receiverSignature.verify(senderSignedMessage);

        assert isVerified;

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySender);
        byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, secretKeyReceiver);
        String decryptMsg = new String(cipher.doFinal(cipherText));

        assert message.equals(decryptMsg);
    }
}
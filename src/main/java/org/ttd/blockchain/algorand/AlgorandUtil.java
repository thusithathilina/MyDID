package org.ttd.blockchain.algorand;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.kmd.client.ApiException;
import com.algorand.algosdk.kmd.client.KmdClient;
import com.algorand.algosdk.kmd.client.api.KmdApi;
import com.algorand.algosdk.kmd.client.model.APIV1Wallet;
import com.algorand.algosdk.kmd.client.model.ExportKeyRequest;
import com.algorand.algosdk.kmd.client.model.InitWalletHandleTokenRequest;
import com.algorand.algosdk.kmd.client.model.ListKeysRequest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.algorand.algosdk.v2.client.model.TransactionsResponse;
import io.ipfs.api.IPFS;
import io.ipfs.multibase.Multibase;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.ttd.did.sdk.DID;
import org.ttd.did.sdk.DIDDocument;
import org.ttd.did.sdk.DidUtil;
import org.ttd.did.sdk.VerificationMethod;
import org.ttd.ipfs.IPFSUtil;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlgorandUtil {

    public static final String ALGORAND_TOKEN = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static final String ALGORAND_CLIENT_HOST = "http://localhost";
    public static final int ALGORAND_CLIENT_PORT = 4001;
    public static final String ALGORAND_INDEXER_HOST = "http://localhost";
    public static final int ALGORAND_INDEXER_PORT = 8980;
    public static final String ALGORAND_KMD_URL = "http://localhost:4002";

    /**
     * Create AlgodClient to connect to Algorand sandbox private node
     *
     * @return AlgodClient
     */
    public static AlgodClient createAlgodClient() {
        return new AlgodClient(ALGORAND_CLIENT_HOST, ALGORAND_CLIENT_PORT, ALGORAND_TOKEN);
    }

    /**
     * Create AlgodClient to connect to Algorand
     *
     * @param host  Algorand hostname
     * @param port  Algorand port number
     * @param token Algorand token to connect
     * @return AlgodClient
     */
    public static AlgodClient createAlgodClient(String host, int port, String token) {
        if (host == null || host.isBlank() || token == null || token.isBlank())
            throw new IllegalArgumentException("host and token cannot be null or blank");
        return new AlgodClient(host, port, token);
    }

    /**
     * Create IndexerClient to query the Algorand sandbox private node
     *
     * @return IndexerClient
     */
    public static IndexerClient createIndexerClient() {
        return new IndexerClient(ALGORAND_INDEXER_HOST, ALGORAND_INDEXER_PORT);
    }

    /**
     * Create a KMD v1 client to connect to default Algorand sandbox KMD
     *
     * @return KmdApi
     */
    public static KmdApi createKmdApi() {
        return createKmdApi(ALGORAND_KMD_URL, ALGORAND_TOKEN);
    }

    /**
     * Create a KMD v1 client to connect to a given daemon
     *
     * @param baseUrl base path for the KMD
     * @param token   token to access the daemon
     * @return KmdApi
     */
    public static KmdApi createKmdApi(String baseUrl, String token) {
        KmdClient kmdClient = new KmdClient();
        kmdClient.setBasePath(baseUrl);
        kmdClient.setApiKey(token);
        return new KmdApi(kmdClient);
    }

    /**
     * Create IndexerClient to query the Algorand blockchain
     *
     * @param host Algorand indexer hostname
     * @param port Algorand indexer port
     * @return IndexerClient
     */
    public static IndexerClient createIndexerClient(String host, int port) {
        if (host == null || host.isBlank())
            throw new IllegalArgumentException("host cannot be null or blank");
        return new IndexerClient(host, port);
    }

    /**
     * Create an Algorand account from given key pair
     *
     * @param keyPair for the Account ownership. The account address is derived from the public key
     * @return generated Algorand Account
     */
    public static Account createAccount(KeyPair keyPair) {
        if (keyPair == null)
            throw new IllegalArgumentException("keypair cannot be null or blank");
        return new Account(keyPair);
    }

    /**
     * Transfer fund between two Algorand accounts
     *
     * @param client AlgodClient to connect to the chain
     * @param from   Sender's Algorand Account
     * @param to     Receiver's Algorand Account
     * @param amount number of microalgos to transfer
     * @throws Exception when the transaction fails
     */
    public static void fundAccount(AlgodClient client, Account from, Account to, int amount) throws Exception {
        if (client == null || from == null || to == null)
            throw new IllegalArgumentException("client, from, and to cannot be null");
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .lookupParams(client)
                .sender(from.getAddress())
                .receiver(to.getAddress())
                .amount(amount)
                .build();
        SignedTransaction signedTransaction = from.signTransaction(tx);
        executeTransaction(client, signedTransaction);
    }

    /**
     * Send the signed transaction to the chain
     *
     * @param client            AlgodClient to connect to the chain
     * @param signedTransaction SignedTransaction object
     * @throws Exception when the transaction fails
     */
    public static void executeTransaction(AlgodClient client, SignedTransaction signedTransaction) throws Exception {
        if (client == null || signedTransaction == null)
            throw new IllegalArgumentException("client and signedTransaction cannot be null or blank");
        Response<PostTransactionsResponse> response = client.RawTransaction()
                .rawtxn(Encoder.encodeToMsgPack(signedTransaction))
                .execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to execute the transaction\n" + response.message());
        }

        boolean done = false;
        while (!done) {
            Response<PendingTransactionResponse> txInfo = client.PendingTransactionInformation(response.body().txId)
                    .execute();
            if (!txInfo.isSuccessful()) {
                throw new RuntimeException("Failed to check on signedTransaction progress");
            }
            if (txInfo.body().confirmedRound != null) {
                done = true;
            }
        }
    }

    /**
     * Store DIDDocument in the Algorand blockchain
     *
     * @param client AlgodClient to connect to the chain
     * @param owner  Algorand account that initiate the storing transaction
     * @param didDoc DIDDocument object to be put on the chain
     * @throws Exception when the transaction fails
     */
    public static boolean storeDID(AlgodClient client, Account owner, DIDDocument didDoc) throws Exception {
        return storeDID(client, owner, didDoc, false, false);
    }

    /**
     * Store DIDDocument in the Algorand blockchain
     *
     * @param client          AlgodClient to connect to the chain
     * @param owner           Algorand account that initiate the storing transaction
     * @param didDoc          DIDDocument object to be put on the chain
     * @param verifyOwnership if set to true DIDDocument should be owned by the owner
     * @throws Exception when the transaction fails
     */
    public static boolean storeDID(AlgodClient client, Account owner, DIDDocument didDoc, boolean verifyOwnership)
            throws Exception {
        IPFSUtil.createIPFSClient();
        return storeDID(client, owner, didDoc, verifyOwnership, false);
    }

    /**
     * Store DIDDocument directly in the Algorand blockchain or IPFS.
     *
     * @param client          AlgodClient to connect to the chain
     * @param owner           Algorand account that initiate the storing transaction
     * @param didDoc          DIDDocument object to be put on the chain
     * @param verifyOwnership if set to true DIDDocument should be owned by the owner
     * @param useIpfs         if set to true DIDDocument will be stored in the IPFS and the hash nad path on blockchain
     * @return true if stored successfully
     * @throws Exception when the transaction fails or cannot save on IPFS
     */
    public static boolean storeDID(AlgodClient client, Account owner, DIDDocument didDoc, boolean verifyOwnership,
                                   boolean useIpfs)
            throws Exception {
        if (client == null || owner == null || didDoc == null)
            throw new IllegalArgumentException("Client, owner and didDoc cannot be null");

        return storeDID(client, owner, didDoc, verifyOwnership, useIpfs, IPFSUtil.createIPFSClient());
    }

    /**
     *
     * @param client          AlgodClient to connect to the chain
     * @param owner           Algorand account that initiate the storing transaction
     * @param didDoc          DIDDocument object to be stored
     * @param verifyOwnership if set to true DIDDocument should be owned by the owner
     * @param useIpfs         if set to true DIDDocument will be stored in the IPFS and the hash nad path on blockchain
     * @param ipfsClient      IPFS object to connect to the IPFS server
     * @return true if stored successfully
     * @throws Exception when the transaction fails or cannot save on IPFS
     */
    public static boolean storeDID(AlgodClient client, Account owner, DIDDocument didDoc, boolean verifyOwnership,
                                   boolean useIpfs, IPFS ipfsClient)
            throws Exception {
        if (client == null || owner == null || didDoc == null)
            throw new IllegalArgumentException("Client, owner and didDoc cannot be null");

        if (verifyOwnership && didDoc.getVerificationMethods().size() > 0) {
            Iterator<VerificationMethod> iterator = didDoc.getVerificationMethods().iterator();
            boolean isOwner = false;
            while (iterator.hasNext()) {
                VerificationMethod verificationMethod = iterator.next();
                var tmpKey1 = Multibase.encode(Multibase.Base.Base58BTC, owner.getEd25519PublicKey().getBytes());
                var tmpKey2 = verificationMethod.getVerificationMaterial().getPublicKey().toString();
                if (tmpKey1.equals(tmpKey2)) {
                    isOwner = true;
                    break;
                }
            }
            if (!isOwner)
                throw new IllegalArgumentException("DIDDocument ownership verification failed. " +
                        "Account owner must be the DIDDocument owner");
        }
        var docContent = DidUtil.getJsonRepresentation(didDoc).toString();
        var content = didDoc.getId().getFullQualifiedIdentifier();
        if (useIpfs) {
            content += "|ipfs|" +
                    IPFSUtil.storeDIDDocument(ipfsClient, didDoc.getId().getIdentifier(), docContent.getBytes()) +
                    "|hash|" +
                    DigestUtils.sha256Hex(docContent.getBytes());
        } else
            content += "|onchain|" + docContent;
        Transaction tx = Transaction.PaymentTransactionBuilder()
                .lookupParams(client)
                .sender(owner.getAddress())
                .noteUTF8(content)
                .build();
        SignedTransaction signedTransaction = owner.signTransaction(tx);
        executeTransaction(client, signedTransaction);
        return true;
    }

    /**
     * Retrieve DIDDocument of the given DID from the Algorand blockchain
     *
     * @param indexerClient IndexerClient to query the chain
     * @param did           querying ID
     * @return corresponding DID document JSONObject representation of the given DID, otherwise null
     * @throws Exception when the transaction fails or couldn't parse the content of the DID document
     */
    public static JSONObject getDIDDocument(IndexerClient indexerClient, DID did) throws Exception {
        if (indexerClient == null || did == null)
            throw new IllegalArgumentException("indexerClient and did cannot be null");

        return getDIDDocument(indexerClient, did.getFullQualifiedIdentifier());
    }

    /**
     * Retrieve DIDDocument of the given DID from the Algorand blockchain
     *
     * @param indexerClient    IndexerClient to query the chain
     * @param fullQualifiedDid String representation of querying DID
     * @return corresponding DID document JSONObject representation of the given DID, otherwise null
     * @throws Exception when the transaction fails or couldn't parse the content of the DID document
     */
    public static JSONObject getDIDDocument(IndexerClient indexerClient, String fullQualifiedDid) throws Exception {
        if (indexerClient == null || fullQualifiedDid == null || fullQualifiedDid.isBlank())
            throw new IllegalArgumentException("indexerClient and fullQualifiedDid cannot be null");

        Response<TransactionsResponse> transactions = indexerClient.searchForTransactions()
                .notePrefix(fullQualifiedDid.getBytes(StandardCharsets.UTF_8))
                .execute();
        if (!transactions.isSuccessful())
            throw new Exception(transactions.message());

        JSONObject didDocument = null;
        for (var tmptx : transactions.body().transactions) {
            var tmp = new String(tmptx.note).split("\\|");
            String tmpDoc;
            if (tmp[1].equalsIgnoreCase("onchain")) {
                tmpDoc = tmp[2];
            } else if (tmp[1].equalsIgnoreCase("ipfs")){
                tmpDoc = IPFSUtil.getDIDDocument(tmp[2]);
                if (!tmp[4].equalsIgnoreCase(DigestUtils.sha256Hex(tmpDoc.getBytes())))
                    throw new Exception("Hash mismatch");
            } else
                continue;

            JSONObject tmpJson = new JSONObject(tmpDoc);
            if (fullQualifiedDid.equals(tmpJson.get("id"))) {
                didDocument = new JSONObject(tmpJson.toString());
                break;
            }
        }
        return didDocument;
    }

    /**
     * Retrieve all the chain addresses belongs to a given wallet handle
     *
     * @param kmdApi       Client for Algorand Key Management Daemon
     * @param walletHandle
     * @return List of Addresses that are belong to given wallet
     * @throws ApiException
     * @throws NoSuchAlgorithmException
     */
    public static List<Address> getWalletAddresses(KmdApi kmdApi, String walletHandle)
            throws ApiException, NoSuchAlgorithmException {
        if (kmdApi == null || walletHandle == null || walletHandle.isBlank())
            throw new IllegalArgumentException("kmdApi and walletHandle cannot be null");
        List<Address> accounts = new ArrayList<>();

        ListKeysRequest keysRequest = new ListKeysRequest();
        keysRequest.setWalletHandleToken(walletHandle);
        for (String addr : kmdApi.listKeysInWallet(keysRequest).getAddresses()) {
            accounts.add(new Address(addr));
        }

        return accounts;
    }

    /**
     * Retrieve the private key from the wallet of a given address
     *
     * @param kmdApi       Client for Algorand Key Management Daemon
     * @param address      Address
     * @param walletHandle
     * @param password
     * @return private key as a byte[]
     * @throws ApiException
     */
    public static byte[] getPrivateKeyFromWallet(KmdApi kmdApi, Address address, String walletHandle, String password)
            throws ApiException {
        if (kmdApi == null || address == null || walletHandle == null || walletHandle.isBlank() || password == null)
            throw new IllegalArgumentException("kmdApi, address, walletHandle and password cannot be null");
        ExportKeyRequest req = new ExportKeyRequest();
        req.setAddress(address.toString());
        req.setWalletHandleToken(walletHandle);
        req.setWalletPassword(password);
        return kmdApi.exportKey(req).getPrivateKey();
    }

    /**
     * Retrieve account information about a given Algorand account
     *
     * @param client  AlgodClient to connect to the chain
     * @param account Algorand account
     * @return com.algorand.algosdk.v2.client.model.Account representing account information
     * @throws Exception
     */
    public static com.algorand.algosdk.v2.client.model.Account getAccountInfo(AlgodClient client, Account account)
            throws Exception {
        if (client == null || account == null)
            throw new IllegalArgumentException("client, and account cannot be null");
        return client.AccountInformation(account.getAddress()).execute().body();
    }

    /**
     * This is s helper class to access the Algorand default wallet in sandbox private node
     */
    public static class AlgorandSandboxPrivateNode {

        /**
         * Get the token to access default Algorand wallet
         *
         * @param kmdApi
         * @return walletr token of the default Algorand wallet
         * @throws ApiException
         */
        public static String getDefaultWalletHandle(KmdApi kmdApi) throws ApiException {
            if (kmdApi == null)
                throw new IllegalArgumentException("kmdApi cannot be null");

            for (APIV1Wallet w : kmdApi.listWallets().getWallets()) {
                if (w.getName().equals("unencrypted-default-wallet")) {
                    InitWalletHandleTokenRequest request = new InitWalletHandleTokenRequest();
                    request.setWalletId(w.getId());
                    request.setWalletPassword("");
                    return kmdApi.initWalletHandleToken(request).getWalletHandleToken();
                }
            }
            throw new RuntimeException("Could not find default wallet");
        }
    }
}
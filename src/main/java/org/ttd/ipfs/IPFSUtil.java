package org.ttd.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.NamedStreamable;
import org.ttd.did.sdk.DIDDocument;
import org.ttd.did.sdk.DidUtil;

import java.io.IOException;

public class IPFSUtil {
    private static final String LOCAL_IPFS_MULTI_ADDRESS = "/ip4/127.0.0.1/tcp/5001";
    public static final String BASE_DIR_PATH = "/diddocs";


    /**
     * Create an IPFS client that connects to local IPFS server. This assumes the existence
     * of a local IPFS server runs on port 5001
     *
     * @return IPFS client that can be used to interact with the IPFS
     */
    public static IPFS createIPFSClient() {
        return createIPFSClient(LOCAL_IPFS_MULTI_ADDRESS);
    }

    /**
     * Create an IPFS client that connects to an IPFS server running on given address.
     *
     * @param multiAddress of the IPFS server
     * @return IPFS client that can be used to interact with the IPFS
     */
    public static IPFS createIPFSClient(String multiAddress) {
        return new IPFS(multiAddress);
    }

    /**
     * Create an IPFS client that connects to an IPFS server running on given host and port.
     *
     * @param host address of the IPFS server
     * @param port of the IPFS server
     * @return IPFS client that can be used to interact with the IPFS
     */
    public static IPFS createIPFSClient(String host, int port) {
        return new IPFS(host, port);
    }

    /**
     * Store given did document content on the IPFS. Filename is derived from the DID on the DID doc
     * @param ipfsClient IPFS object to interact with teh IPFS
     * @param didDocument DIDDocument object representation of ythe DID doc
     * @return String path of the stored document
     * @throws IOException when cannot store the document
     */
    public static String storeDIDDocument(IPFS ipfsClient, DIDDocument didDocument) throws IOException {
        var fileName = didDocument.getId().getIdentifier();
        byte[] content = DidUtil.getJsonRepresentation(didDocument).toString().getBytes();

        return storeDIDDocument(ipfsClient, fileName, content);
    }

    /**
     * Store given did document content on the IPFS.
     * @param ipfsClient IPFS object to interact with teh IPFS
     * @param fileName String file name to use when storing the content
     * @param content byte[] representation of the DID doc to be stored
     * @return String path of the stored document
     * @throws IOException when cannot store the document
     */
    public static String storeDIDDocument(IPFS ipfsClient, String fileName, byte[] content) throws IOException {
        return  storeDIDDocument(ipfsClient, BASE_DIR_PATH, fileName, content);
    }

    /**
     * Store given did document content on the IPFS.
     * @param ipfsClient IPFS object to interact with teh IPFS
     * @param basePath String directory to prepend the file path
     * @param fileName String file name to use when storing the content
     * @param content byte[] representation of the DID doc to be stored
     * @return String path of the stored document
     * @throws IOException when cannot store the document
     */
    public static String storeDIDDocument(IPFS ipfsClient, String basePath, String fileName, byte[] content) throws IOException {
        NamedStreamable nsContent = new NamedStreamable.ByteArrayWrapper(fileName, content);
        var path = basePath + "/" + fileName;
        storeDIDDocument(ipfsClient, path, nsContent);

        return path;
    }

    private static void storeDIDDocument(IPFS ipfsClient, String path, NamedStreamable content) throws IOException {
        ipfsClient.files.mkdir(BASE_DIR_PATH, true);
        ipfsClient.files.write(path, content, true, true);
    }

    /**
     * Retrieve the corresponding DID document of the given DID using default IPFS client
     *
     * @param did String representation of the DID
     * @return String representation of the DID document
     * @throws IOException when cannot store the document
     */
    public static String getDIDDocument(String did) throws IOException {
        return new String(createIPFSClient().files.read(did));
    }

    /**
     * Retrieve the corresponding DID document of the given DID
     *
     * @param ipfsClient IPFS object to interact with teh IPFS
     * @param did String representation of the DID
     * @return String representation of the DID document
     * @throws IOException when cannot store the document
     */
    public static String getDIDDocument(IPFS ipfsClient, String did) throws IOException {
        return new String(ipfsClient.files.read(did));
    }
}

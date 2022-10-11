# MyDID
This provides a basic implementation of the W3C DID specification which was approved as a recommendation in late June 2022
Specification can be found on https://www.w3.org/TR/did-core/

## DID format
MyDID implementation adopts the following format when generating DIDs
```html
"did:ttd:"(namespace):identifier
namespace: idchar*
identifier: base58char+
idchar: ALPHA / DIGIT
base58char: 123456789ABCDEFGH JKLMN PQRSTUVWXYZabcdefghijk mnopqrstuvwxyz
```

e.g.
```html
did:ttd:zkjhdj365dqbbtc28hic
did:ttd:sg:zkjhdj365dqbbtc28hic
```

#How to use
## Generate a DID
First you need to generate a key pair. General EC curves and edward curves are supported at the moment.
```java
KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
//or
KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");
//or
KeyPairGenerator generator = KeyPairGenerator.getInstance("Ed25519");
...
KeyPair keyPair = generator.generateKeyPair();
```
Then use the generated keypair to call the ``createDid`` method in the ``DidUtil`` class
This will generate the DID and return its corresponding DIDDocument
```java
DIDDocument didDoc = DidUtil.createDid(keyPair);
System.out.println(didDoc.getId().getFullQualifiedIdentifier());
```

## Store DIDDcoument on Algorand Blockchain
Generated DID document can be stored in Algorand blockchain. For local testing Algorand sandbox, which is available on https://github.com/algorand/sandbox can be used for this.
Clone the Algorand sandbox repo and start the sandbox using ``./sandbox up`` command. This will start the Algorand private node on the local machine.
Then create client to connect to the Algorand 
```java
AlgodClient algodClient = AlgorandUtil.createAlgodClient();
```
We need an Algorand account to store the DID document on the chain. By default, the sandbox comes with 3 accounts.
I will get the first account.
```java
KmdApi kmdApi = AlgorandUtil.createKmdApi();
String defaultWalletHandle = AlgorandUtil.AlgorandSandboxPrivateNode.getDefaultWalletHandle(kmdApi);
List<Address> walletAddresses = AlgorandUtil.getWalletAddresses(kmdApi, defaultWalletHandle);
byte[] sk = AlgorandUtil.getPrivateKeyFromWallet(kmdApi, walletAddresses.get(0), defaultWalletHandle, "");
Account steward = new Account(sk);
```
Then I use that account to store the document on the chain. JSON representation of the DIDDocument will be stored in the blockchain as
```java
AlgorandUtil.storeDID(algodClient, steward, didDoc);
```

## Retrieve a DIDDocument from the Blockchain
We can use the Algorand indexer client to retrieve any DIDDocument from the chain, if we know the DID
To do that, first we need to create an Algorand Indexer client
```java
IndexerClient indexerClient = AlgorandUtil.createIndexerClient();
```
Then we can use the `AlgorandClient` to retrieve the document as follows. You can use either a DID object or a string representation of (full qualified) DID 
```java
JSONObject documentSender = AlgorandUtil.getDIDDocument(indexerClient, didDoc.getId());
```

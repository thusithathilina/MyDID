package org.ttd.did.sdk;

import io.ipfs.multibase.Multibase;

import java.security.PublicKey;

public class PublicKeyMultibase implements VerificationMaterial {

    private final String base;
    private PublicKey publicKey;

    public PublicKeyMultibase(String base, PublicKey publicKey) {
        if (base == null || !base.equalsIgnoreCase("base58"))
            throw new IllegalArgumentException("This only supports Base58");
        this.base = base;
        this.publicKey = publicKey;
    }

    public String getBase() {
        return base;
    }

    @Override
    public String getPublicKey() {
        return Multibase.encode(Multibase.Base.Base58BTC, publicKey.getEncoded());
    }
}

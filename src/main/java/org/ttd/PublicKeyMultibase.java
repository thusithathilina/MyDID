package org.ttd;

import io.ipfs.multibase.Multibase;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;

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
        if (publicKey instanceof EdDSAPublicKey) {
            var tmpPubkey = ((EdDSAPublicKey) publicKey).getPointEncoding();
            return Multibase.encode(Multibase.Base.Base58BTC, tmpPubkey);
        }
        return Multibase.encode(Multibase.Base.Base58BTC, publicKey.getEncoded());
    }
}

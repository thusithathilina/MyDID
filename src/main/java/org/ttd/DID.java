package org.ttd;

public final class DID {

    private static final String SCHEME = "did";
    private static final String METHOD = "ttd";
    private final String identifier;

    public DID(String identifier) {
        this.identifier = identifier;
    }

    public static String getScheme() {
        return SCHEME;
    }

    public static String getMethod() {
        return METHOD;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFullQualifiedDid() {
        return SCHEME + ":" + METHOD + ":" + identifier;
    }
}

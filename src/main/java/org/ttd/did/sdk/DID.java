package org.ttd.did.sdk;

public final class DID {

    public static final String SCHEME = "did";
    public static final String METHOD = "ttd";
    private final String namespace;
    private final String identifier;

    public DID(String identifier) {
        namespace = "";
        this.identifier = identifier;
    }

    public DID(String namespace, String identifier) {
        validateNamespace(namespace);
        this.namespace = namespace;
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

    public String getNamespace() {
        return namespace;
    }

    public String getFullQualifiedIdentifier() {
        StringBuilder didString = new StringBuilder();
        didString.append(SCHEME).append(":").append(METHOD).append(":");
        if (!namespace.equalsIgnoreCase(""))
            didString.append(namespace).append(":");
        didString.append(identifier);
        return didString.toString();
    }

    private static void validateNamespace(String namespace) {
        if (namespace.isBlank())
            return;
        namespace.chars().forEach(c -> {
            if (!(Character.isLetterOrDigit(c)))
                throw new IllegalArgumentException("namespace only allow letters and digits");
        });
    }
}

package org.ttd.vc.sdk;

public class Claim {
    private final String name;
    private final Object value;

    public Claim(String name, Object value) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Claim name cannot be null or empty");
        if (value == null)
            throw new IllegalArgumentException("Claim value cannot be null");
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}

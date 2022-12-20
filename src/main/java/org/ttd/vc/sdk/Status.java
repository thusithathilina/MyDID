package org.ttd.vc.sdk;

import java.net.URI;

public class Status {
    private URI id;
    private Object type;

    public Status(URI id, Object type) {
        this.id = id;
        this.type = type;
    }

    public URI getId() {
        return id;
    }

    public Object getType() {
        return type;
    }
}

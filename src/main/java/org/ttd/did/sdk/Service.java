package org.ttd.did.sdk;

import java.net.URI;

public class Service {
    private URI id;
    private String type;
    private URI serviceEndpoint;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public URI getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(URI serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }
}

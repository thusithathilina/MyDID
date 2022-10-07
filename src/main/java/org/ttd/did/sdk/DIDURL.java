package org.ttd.did.sdk;

import java.net.URI;
import java.net.URISyntaxException;

public class DIDURL {

    private final DID did;
    private final URI uri;

    public DIDURL(DID did, String path, String query, String fragment) throws URISyntaxException {
        this.did = did;
        if (path != null)
            path = path.trim();
        if (query != null)
            query = query.trim();
        if (fragment != null)
            fragment = fragment.trim();
        uri = new URI(null, null, path, query, fragment);
    }

    public DID getDid() {
        return did;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getQuery() {
        return uri.getQuery();
    }

    public String getFragment() {
        return uri.getFragment();
    }

    public String getFullQualifiedUrl() {
        StringBuilder sb = new StringBuilder(did.getFullQualifiedIdentifier());
        String path = uri.getPath();
        String query = uri.getQuery();
        String fragment = uri.getFragment();
        if (path != null && !path.isEmpty()) {
            if (path.charAt(0) != '/')
                sb.append('/');
            sb.append(path);
        }
        if (query != null && !query.isEmpty())
        {
            if (query.charAt(0) != '?')
                sb.append('?');
            sb.append(query);
        }
        if (fragment != null && !fragment.isEmpty()) {
            if (fragment.charAt(0) != '#')
                sb.append('#');
            sb.append(fragment);
        }

        return sb.toString();
    }
}

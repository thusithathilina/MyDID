package org.ttd.vc.sdk;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

public class Signature {
    private LocalDateTime created;
    private Map<String, Claim> claims;
    private URI verificationMethod;
    private String purpose;
    private String value;

}

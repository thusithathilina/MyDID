package org.ttd.did.sdk;

import java.time.LocalDateTime;
import java.util.Set;

public class DIDDocumentMetadata {

    private final LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deactivated;
    private LocalDateTime nextUpdated;
    private String versionId;
    private String nextVersionId;
    private Set<DID> equivalentIds;
    private DID canonicalId;

    public DIDDocumentMetadata(LocalDateTime created, LocalDateTime updated, String versionId) {
        this.created = created;
        this.updated = updated;
        this.versionId = versionId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public LocalDateTime getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(LocalDateTime deactivated) {
        this.deactivated = deactivated;
    }

    public LocalDateTime getNextUpdated() {
        return nextUpdated;
    }

    public void setNextUpdated(LocalDateTime nextUpdated) {
        this.nextUpdated = nextUpdated;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getNextVersionId() {
        return nextVersionId;
    }

    public void setNextVersionId(String nextVersionId) {
        this.nextVersionId = nextVersionId;
    }

    public Set<DID> getEquivalentIds() {
        return equivalentIds;
    }

    public void setEquivalentIds(Set<DID> equivalentIds) {
        this.equivalentIds = equivalentIds;
    }

    public DID getCanonicalId() {
        return canonicalId;
    }

    public void setCanonicalId(DID canonicalId) {
        this.canonicalId = canonicalId;
    }
}

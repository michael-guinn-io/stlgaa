package org.stlgaa;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
public class SLGEntity extends SLGObject implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    @Version
    protected long version;
}

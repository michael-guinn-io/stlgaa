package org.stlgaa.security.authentication;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
public class ExternalCredentials extends Credentials {

    @Basic
    private String externalProvider;
    public String getExternalProvider() {
        return externalProvider;
    }
    public void setExternalProvider(String externalProvider) {
        this.externalProvider = externalProvider;
    }

    @Basic
    private String externalId;
    public String getExternalId() {
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}

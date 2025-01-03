package org.stlgaa.security.authentication;

import jakarta.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = PasswordCredentials.GET_BY_USER, query = "SELECT pc FROM PasswordCredentials pc WHERE pc.user = :user")
})
public class PasswordCredentials extends Credentials {

    public static final String GET_BY_USER = "PasswordCredentials.getByUser";

    @Basic
    private String hash;
    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    @Basic
    private String salt;
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Basic
    private Integer failedAttempts;
    public Integer getFailedAttempts() {
        return failedAttempts;
    }
    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
}

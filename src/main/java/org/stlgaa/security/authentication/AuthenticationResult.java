package org.stlgaa.security.authentication;

import org.stlgaa.personalization.User;

public class AuthenticationResult {

    public enum AuthenticationStatus {
        AUTHENTICATED, AUTHENTICATION_FAILED, CREDENTIALS_LOCKED
    }

    private AuthenticationStatus status;
    private User principal;

    public AuthenticationResult(AuthenticationStatus status, User principal) {
        this.status = status;
    }

    public AuthenticationStatus getStatus() {
        return status;
    }
    public void setStatus(AuthenticationStatus status) {
        this.status = status;
    }
    public User getPrincipal() {
        return principal;
    }
    public void setPrincipal(User principal) {
        this.principal = principal;
    }
}

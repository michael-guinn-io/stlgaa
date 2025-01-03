package org.stlgaa.security.tokenization;

import jakarta.persistence.*;
import org.stlgaa.SLGEntity;
import org.stlgaa.personalization.User;

import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(name = Token.GET_BY_USER_AND_TYPE, query = "SELECT t FROM Token t WHERE t.user = :user AND t.tokenType = :type")
})
public class Token extends SLGEntity {

    public static final String GET_BY_USER_AND_TYPE = "token.getByUserAndType";

    @ManyToOne(targetEntity = User.class)
    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Basic
    private String tokenValue;
    public String getTokenValue() {
        return tokenValue;
    }
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    @Basic
    private String salt; // tokens should be unique but don't assume
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    public TokenType getTokenType() {
        return tokenType;
    }
    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Basic
    private LocalDateTime expiration;
    public LocalDateTime getExpiration() {
        return expiration;
    }
    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }
}

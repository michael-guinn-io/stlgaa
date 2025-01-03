package org.stlgaa.security.tokenization;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.RandomStringUtils;
import org.stlgaa.SLGService;
import org.stlgaa.personalization.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.stlgaa.security.SLGHash.createHash;

/**
 * Handles the creation, validation, ord removal of tokens used in various authentication and verification scenarios. Only
 * one token of a given type should ever exist for a single user.
 */
@Stateless
public class TokenService extends SLGService {

    @PersistenceContext(unitName = "stlgaa-persistence")
    private EntityManager entityManager;

    /**
     * Generates a token of the given TokenType. A plain-text token value is returned, while a hashed version is stored.
     * The plain-text value should be secured as much as a password if it used for any type of authentication. Each User
     * should only have one Token of a given TokenType at any time.
     *
     * @param user the User to which the Token will be attached
     * @param tokenType the type of Token to create
     * @return The plain-text value of the Token
     */
    public String generateToken(User user, TokenType tokenType) {

        /* If a token of this type already exists for the user, remove it before creating a new one. */
        Token existingToken = getToken(user, tokenType);
        if (existingToken != null) {
            entityManager.remove(existingToken);
        }

        /* Token should be treated with the same security as a password, salt and hash it before returning the
           plain-text value */
        String tokenValue = RandomStringUtils.randomAlphanumeric(25);
        String tokenSalt = RandomStringUtils.randomAlphanumeric(10);
        String tokenHash = createHash(tokenValue, tokenSalt);

        /* Different token scenarios will have different expiration requirements */
        LocalDateTime expiration; // todo make configurable
        switch (tokenType) {
            case EMAIL_VERIFICATION:
                expiration = LocalDateTime.now().plusDays(2);
            case PASSWORD_RESET:
                expiration = LocalDateTime.now().plusMinutes(30);
            case REMEMBER_ME:
                expiration = LocalDateTime.now().plusDays(60);
            default:
                expiration = LocalDateTime.now().plusMinutes(30);
        }

        /* Store the hash for validation, and return the plain-text value to be used in the application */
        Token token = new Token();
        token.setUser(user);
        token.setTokenType(tokenType);
        token.setTokenValue(tokenHash);
        token.setSalt(tokenSalt);
        token.setExpiration(expiration);

        entityManager.persist(token);

        return tokenValue;
    }

    /**
     * Checks whether a token of the given TokenType exists for the User, has not expired, and matches the supplied value.
     *
     * @param user the User to which the Token is attached
     * @param tokenType the type of Token
     * @param tokenValue the plain-text value of the Token to validate
     * @return true if the token matches a stored token for the user and is not expired
     */
    public boolean isValidToken(User user, TokenType tokenType, String tokenValue) {

        Token token = getToken(user, tokenType);
        if (token == null) {
            return false;
        }

        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            entityManager.remove(token);
            return false;
        }

        String hash = createHash(tokenValue, token.getSalt());

        return token.getTokenValue().equals(hash);
    }

    /**
     * Retrieves a token of the supplied token type for the user if one has been created and stored, or null if one has
     * not. There should only ever be one of any given token type per user at a time.
     *
     * @param user The User for which to retrieve a token
     * @param tokenType the type of Token to retrieve
     * @return a Token if one of the given type has been created for the User, or null if one has not
     */
    public Token getToken(User user, TokenType tokenType) {

        TypedQuery<Token> query = entityManager.createNamedQuery(Token.GET_BY_USER_AND_TYPE, Token.class);
        query.setParameter("user", user);
        query.setParameter("type", tokenType);

        List<Token> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else if (results.size() > 1) {
            log.warn("Found multiple tokens of type {} for user {}. Bad application state, results may be unexpected.", tokenType, user.getEmailAddress().getAddress());
        }
        return results.get(0);
    }

    /**
     * Remove the token of the supplied type for the given User. This should be called in scenarios where the token
     * should only be used one time, e.g. forgot password.
     *
     * @param user the User to which the token belongs
     * @param tokenType the type of Token to remove
     */
    public void removeToken(User user, TokenType tokenType) {
        Token token = getToken(user, tokenType);
        if (token != null) {
            entityManager.remove(token);
        }
    }
}

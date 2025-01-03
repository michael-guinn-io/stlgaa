package org.stlgaa.security.authentication;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.RandomStringUtils;
import org.stlgaa.SLGService;
import org.stlgaa.personalization.User;
import org.stlgaa.personalization.UserService;
import org.stlgaa.security.SLGHash;
import org.stlgaa.security.tokenization.Token;
import org.stlgaa.security.tokenization.TokenService;
import org.stlgaa.security.tokenization.TokenType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.stlgaa.security.SLGHash.createHash;

@Startup
@Singleton
public class AuthenticationService extends SLGService {

    @EJB
    private UserService userService;

    @EJB
    private TokenService tokenService;

    @PersistenceContext(unitName = "stlgaa-persistence")
    private EntityManager entityManager;

    /**
     * Given an e-mail address and password, find a user and a PasswordCredential object. Given the stored salt, hash
     * the supplied password and check to see if the hash matches what has been stored.
     *
     * @param emailAddress a jakarta.mail.InternetAddress object that identifies a User
     * @param password a plain-text password to attempt to match the user's stored credential
     * @return an AuthenticationResult object with information about whether authentication was successful
     */
    public AuthenticationResult authenticatePassword(InternetAddress emailAddress, String password) {

        /* Does a user with this email address exist? */
        User user = userService.getUserByEmail(emailAddress);
        if (user == null) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        TypedQuery<PasswordCredentials> query = entityManager.createNamedQuery(PasswordCredentials.GET_BY_USER, PasswordCredentials.class);
        query.setParameter("user", user);

        /* Does the user have a password to authenticate with? */
        List<PasswordCredentials> result = query.getResultList();
        if (result.isEmpty()) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        } if (result.size() > 1) {
            log.warn("Found multiple password credentials for user with email: {0}. Authenticating the first result, unexpected behavior may occur.", user.getEmailAddress().getAddress());
        }

        PasswordCredentials userCredentials = result.get(0);

        /* Has the user's password credential been locked? */
        if (!userCredentials.isEnabled()) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        String salt = userCredentials.getSalt();

        String hashToCompare = createHash(password, salt);
        String hashFound = userCredentials.getHash();

        /* Did the user enter the correct password */
        if (!hashFound.equals(hashToCompare)) {

            final int maxFailedPasswordAttempts = 5; // todo make configurable
            /* If not, increment failed attempts and possibly lock */
            userCredentials.setFailedAttempts(userCredentials.getFailedAttempts() + 1);
            if (userCredentials.getFailedAttempts() >= maxFailedPasswordAttempts) {
                userCredentials.setEnabled(false);
            }

            entityManager.merge(userCredentials);

            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        /* Passed the gauntlet, reset failed attempts and return success */
        userCredentials.setFailedAttempts(0);
        entityManager.merge(userCredentials);

        return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATED, userCredentials.getUser());
    }

    /**
     * Given a plain-text password, generate a salt, use it to hash a password, and create a PasswordCredential object
     * with this information for the User
     *
     * @param user a User for which to store a password credential
     * @param plaintTextPassword the plain text password that the User will authenticate with
     */
    public void createPassword(User user, String plaintTextPassword) {

        String salt = RandomStringUtils.randomAlphanumeric(17);
        String hash = createHash(plaintTextPassword, salt);

        PasswordCredentials passwordCredentials = new PasswordCredentials();
        passwordCredentials.setUser(user);
        passwordCredentials.setHash(hash);
        passwordCredentials.setSalt(salt);
        passwordCredentials.setFailedAttempts(0);
        passwordCredentials.setEnabled(true);

        entityManager.persist(passwordCredentials);
    }

    /**
     * Creates a value to be used for a Remember Me cookie, to be authenticated by this service upon new sessions.
     *
     * @param user the User for which to generate the Remember Me token
     * @return a String to use as the cookie value for Remember Me authentication
     */
    public String createRememberMeCookieValue(User user) {

        String userId = user.getId().toString();
        String tokenValue = tokenService.generateToken(user, TokenType.REMEMBER_ME);

        return userId + "|" + tokenValue;
    }

    /**
     * Authenticates a Remember Me cookie value created by this service.
     *
     * @param cookieValue the value of the Remember Me cookie
     * @return an AuthenticationResult indicating whether the cookie value was valid and the User it belongs to should be logged in
     */
    public AuthenticationResult authenticateRememberMeCookie(String cookieValue) {

        String[] cookieValueParts = cookieValue.split("\\|");
        if (cookieValueParts.length != 2) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        String userId = cookieValueParts[0];
        String tokenValue = cookieValueParts[1];

        User user = userService.getUserById(UUID.fromString(userId));
        if (user == null) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        Token rememberMeToken = tokenService.getToken(user, TokenType.REMEMBER_ME);
        if (rememberMeToken == null) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        if (rememberMeToken.getExpiration().isBefore(LocalDateTime.now())) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        String salt = rememberMeToken.getSalt();
        String hash = SLGHash.createHash(tokenValue, salt);

        if (!rememberMeToken.getTokenValue().equals(hash)) {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATION_FAILED, null);
        }

        return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.AUTHENTICATED, user);
    }
}

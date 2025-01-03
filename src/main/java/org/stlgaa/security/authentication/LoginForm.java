package org.stlgaa.security.authentication;

import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.internet.InternetAddress;
import jakarta.security.enterprise.AuthenticationStatus;
import org.stlgaa.SLGForm;
import org.stlgaa.personalization.User;
import org.stlgaa.personalization.UserService;
import org.stlgaa.personalization.UserSession;
import org.stlgaa.security.tokenization.TokenService;
import org.stlgaa.security.tokenization.TokenType;

import java.util.HashMap;
import java.util.Map;

@Named
@ViewScoped
public class LoginForm extends SLGForm {

    @Inject
    private FacesContext facesContext;

    @Inject
    private UserSession userSession;

    @EJB
    private UserService userService;

    @EJB
    private AuthenticationService authenticationService;

    @EJB
    private TokenService tokenService;

    private InternetAddress username;
    private String password;
    private boolean rememberMe;

    public String login() {

        AuthenticationResult authResult = authenticationService.authenticatePassword(username, password);
        if (!authResult.getStatus().equals(AuthenticationStatus.SUCCESS)) {
            addErrorMessage("login.authenticationfailure");
            return "login";
        } else {

            User user = userService.getUserByEmail(getUsername());
            userSession.setUser(user);

            if (user.getPreferredLocale() != null) {
                userSession.setLocale(user.getPreferredLocale());
            }

            if (isRememberMe()) {

                Map<String, Object> cookieOptions = new HashMap<>();
                cookieOptions.put("domain", "/");
                cookieOptions.put("maxAge", 60 * 24 * 60 * 60); // todo figure out how to set this to match token expiration
                cookieOptions.put("httpOnly", true);
                cookieOptions.put("secure", true);

                facesContext.getExternalContext().addResponseCookie("slgrm", generateRememberMeCookieValue(user), cookieOptions);
            }

            return "index";
        }
    }

    private String generateRememberMeCookieValue(User user) {

        String tokenValue = tokenService.generateToken(user, TokenType.REMEMBER_ME);
        String cookieId = user.getId().toString();

        return cookieId + "|" + tokenValue;
    }

    public InternetAddress getUsername() {
        return username;
    }
    public void setUsername(InternetAddress username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isRememberMe() {
        return rememberMe;
    }
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}

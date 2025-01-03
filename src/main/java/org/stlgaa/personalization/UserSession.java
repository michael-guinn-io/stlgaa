package org.stlgaa.personalization;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.stlgaa.SLGView;
import org.stlgaa.security.authentication.AuthenticationResult;
import org.stlgaa.security.authentication.AuthenticationService;

import java.util.Locale;
import java.util.Map;

@Named
@SessionScoped
public class UserSession extends SLGView {

    @Inject
    private FacesContext facesContext;

    @EJB
    private AuthenticationService authenticationService;

    private User user;
    private Locale locale;

    @PostConstruct
    private void init() {

        Map<String, Object> cookieMap = facesContext.getExternalContext().getRequestCookieMap();
        if (cookieMap.containsKey("slgrm")) {

            String rememberMeValue = (String) cookieMap.get("slgrm");
            AuthenticationResult authenticationResult = authenticationService.authenticateRememberMeCookie(rememberMeValue);



        } else {

            locale = facesContext.getApplication().getDefaultLocale();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}

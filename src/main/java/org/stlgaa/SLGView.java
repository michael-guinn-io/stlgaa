package org.stlgaa;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class SLGView extends SLGObject implements Serializable {

    @Inject
    private FacesContext facesContext;

    private Map<String, ResourceBundle> resourceBundles = new HashMap<>();

    @PostConstruct
    private void initializeMessages() {
        resourceBundles.put(
                "copy",
                ResourceBundle.getBundle("org.stlgaa.copy", facesContext.getViewRoot().getLocale())
        );
        resourceBundles.put(
                "errors",
                ResourceBundle.getBundle("org.stlgaa.errors", facesContext.getViewRoot().getLocale())
        );
        resourceBundles.put(
                "seo",
                ResourceBundle.getBundle("org.stlgaa.seo", facesContext.getViewRoot().getLocale())
        );
    }

    protected void addErrorMessage(String messageKey) {

        ResourceBundle bundle = resourceBundles.get("errors");
        String message = bundle.getString(messageKey);

        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
        facesContext.addMessage(null, facesMessage);
    }

    protected FacesMessage getErrorMessage(String messageKey) {

        ResourceBundle bundle = resourceBundles.get("errors");
        String message = bundle.getString(messageKey);

        return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
    }
}

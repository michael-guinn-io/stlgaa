package org.stlgaa.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.stlgaa.SLGObject;

import java.util.ResourceBundle;

public abstract class SLGFacesDataProcessor extends SLGObject {

    protected FacesMessage getErrorMessage(String messageKey, FacesContext facesContext) {

        ResourceBundle bundle = ResourceBundle.getBundle("org.stlgaa.errors", facesContext.getViewRoot().getLocale());
        String message = bundle.getString(messageKey);

        return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
    }
}

package org.stlgaa.util.conversion;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.stlgaa.SLGObject;
import org.stlgaa.util.SLGFacesDataProcessor;

@FacesConverter("EmailConverter")
public class EmailFacesConverter extends SLGFacesDataProcessor implements Converter<InternetAddress> {

    @Override
    public InternetAddress getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {

        try {

            return new InternetAddress(s);

        } catch (AddressException e) {

            log.debug("AddressException converting {} to InternetAddress: {}", s, e.getMessage());
            log.catching(e);

            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, s + " is not a valid email address.", e.getMessage()));
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, InternetAddress internetAddress) {
        return internetAddress.getAddress();
    }
}

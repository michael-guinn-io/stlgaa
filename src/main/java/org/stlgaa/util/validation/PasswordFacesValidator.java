package org.stlgaa.util.validation;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import org.stlgaa.util.SLGFacesDataProcessor;

@FacesValidator("PasswordValidator")
public class PasswordFacesValidator extends SLGFacesDataProcessor implements Validator<String> {

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, String s) throws ValidatorException {

        if (s.length() < 8) {
            throw new ValidatorException(getErrorMessage("password.length", facesContext));
        }

        boolean hasUppercase = false,
                hasLowercase = false,
                hasDigits = false;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            }
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            }
            if (Character.isDigit(c)) {
                hasDigits = true;
            }
        }

        if (!hasUppercase || !hasLowercase || !hasDigits) {
            throw new ValidatorException(getErrorMessage("password.constraints", facesContext));
        }
    }
}

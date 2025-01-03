package org.stlgaa.util.conversion;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.stlgaa.SLGObject;

@Converter
public class EmailAttributeConverter extends SLGObject implements AttributeConverter<InternetAddress, String> {

    @Override
    public String convertToDatabaseColumn(InternetAddress internetAddress) {
        return internetAddress.getAddress().toLowerCase();
    }

    @Override
    public InternetAddress convertToEntityAttribute(String s) {

        try {

            return new InternetAddress(s.toLowerCase());

        } catch (AddressException e) {

            log.error("AddressException trying to covert {} to InternetAddress: {}", s, e.getMessage());
            log.catching(e);

            return null;
        }
    }
}

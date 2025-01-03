package org.stlgaa;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class SLGService extends SLGObject{

    protected Properties properties;

    @PostConstruct
    public void init() {

        String className = getClass().getSimpleName();
        try (InputStream propertiesFileStream = getClass().getClassLoader().getResourceAsStream(className + ".properties")) {

            if (propertiesFileStream != null) {
                properties = new Properties();
                properties.load(propertiesFileStream);
            }

        } catch (IOException e) {

            log.fatal("Unable to load properties file {0}.properties: {1}", getClass().getCanonicalName(), e.getMessage());
            log.catching(e);

            properties = new Properties();
        }
    }
}

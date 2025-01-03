package org.stlgaa.system;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.stlgaa.SLGService;

import java.util.Properties;

@Singleton
@Startup
public class ApplicationConfigurationService extends SLGService {

    @PersistenceContext(unitName = "stlgaa-persistence")
    private EntityManager entityManager;

    public <T> Properties getConfigurationForService(Class<T> service) {

        Properties properties = new Properties();

        return properties;
    }
}

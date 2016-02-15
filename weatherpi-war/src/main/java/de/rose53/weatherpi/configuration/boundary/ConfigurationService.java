package de.rose53.weatherpi.configuration.boundary;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import de.rose53.weatherpi.configuration.entity.ConfigurationBean;

@Stateless
public class ConfigurationService {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    public String getValue(String key) {
        logger.debug("getValue: returning value for key = >{}<",key);
        List<String> resultList = em.createNamedQuery(ConfigurationBean.findValueByKey,String.class)
                                    .setParameter("key", key)
                                    .getResultList();
        return resultList.isEmpty()?null:resultList.get(0);
    }

    public List<String> getValues(String key) {
        logger.debug("getValues: returning value list for key = >{}<",key);
        return em.createNamedQuery(ConfigurationBean.findValuesByKey,String.class)
                 .setParameter("key", key + "%")
                 .getResultList();
    }

}

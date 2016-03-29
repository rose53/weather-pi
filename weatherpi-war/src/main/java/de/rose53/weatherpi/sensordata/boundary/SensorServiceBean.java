package de.rose53.weatherpi.sensordata.boundary;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;
import de.rose53.weatherpi.sensordata.entity.SensorBean;

@Stateless
public class SensorServiceBean {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    public SensorBean getSensor(String name, ESensorPlace place, ESensorType type) {

        List<SensorBean> resultList = em.createNamedQuery(SensorBean.findByPlaceTypeName, SensorBean.class)
                .setParameter("place",place)
                .setParameter("type",type)
                .setParameter("name",name)
                .getResultList();
        if (resultList.isEmpty()) {
            logger.debug("getSensor: no sensor found");
            return null;
        }
        return resultList.get(0);
    }

    public List<SensorBean> getSensors() {

        return em.createNamedQuery(SensorBean.findAll,SensorBean.class)
                 .getResultList();
    }
}

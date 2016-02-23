package de.rose53.weatherpi.sensordata.boundary;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import de.rose53.weatherpi.sensordata.entity.DeviceBean;

@Stateless
public class DeviceServiceBean {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    public DeviceBean getDevice(String name) {

        List<DeviceBean> resultList = em.createNamedQuery(DeviceBean.findByName,DeviceBean.class)
                                        .setParameter("name", name)
                                        .getResultList();

        return resultList.isEmpty()?null:resultList.get(0);
    }

    public List<DeviceBean> getDevices() {

        return em.createNamedQuery(DeviceBean.findAll,DeviceBean.class)
                 .getResultList();
    }
}

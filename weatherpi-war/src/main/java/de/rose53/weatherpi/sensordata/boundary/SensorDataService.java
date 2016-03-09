package de.rose53.weatherpi.sensordata.boundary;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import de.rose53.weatherpi.sensordata.entity.SensorBean;
import de.rose53.weatherpi.sensordata.entity.SensorDataBean;

@Stateless
public class SensorDataService {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;


    public List<SensorDataBean> getSensorData(ERange range) {

        return em.createNamedQuery(SensorDataBean.findRange,SensorDataBean.class)
                 .setParameter("pastTime",Timestamp.valueOf(range.getPastTime()))
                 .setParameter("actualTime",Timestamp.valueOf(LocalDateTime.now()))
                 .getResultList();
    }

    public List<SensorDataQueryResult> getSensorData(ESensorType sensorType, ESensorPlace place, ERange range) {

        if (range == null) {
            range = ERange.ACTUAL;
        }

        TypedQuery<SensorDataQueryResult> q = null;
        switch (sensorType) {
        case HUMIDITY:
            switch (place) {
            case INDOOR:
                q = em.createNamedQuery(SensorDataBean.findHumidityIndoorRange, SensorDataQueryResult.class);
                break;
            case OUTDOOR:
                q = em.createNamedQuery(SensorDataBean.findHumidityOutdoorRange, SensorDataQueryResult.class);
                break;
            case BIRDHOUSE:
                q = em.createNamedQuery(SensorDataBean.findHumidityBirdhouseRange, SensorDataQueryResult.class);
                break;
            default:
                break;
            }
            break;
        case ILLUMINANCE:
            q = em.createNamedQuery(SensorDataBean.findIlluminationRange, SensorDataQueryResult.class);
            break;
        case PRESSURE:
            q = em.createNamedQuery(SensorDataBean.findPressureRange, SensorDataQueryResult.class);
            break;
        case TEMPERATURE:
            switch (place) {
            case INDOOR:
                q = em.createNamedQuery(SensorDataBean.findTemperatureIndoorRange, SensorDataQueryResult.class);
                break;
            case OUTDOOR:
                q = em.createNamedQuery(SensorDataBean.findTemperatureOutdoorRange, SensorDataQueryResult.class);
                break;
            case BIRDHOUSE:
                q = em.createNamedQuery(SensorDataBean.findTemperatureBirdhouseRange, SensorDataQueryResult.class);
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
        if (q == null) {
            logger.error("getSensorData: no statement found for sensorType = >{}<",sensorType);
            return Collections.emptyList();
        }

        q.setParameter("pastTime",Timestamp.valueOf(range.getPastTime()));
        q.setParameter("actualTime",Timestamp.valueOf(LocalDateTime.now()));


        List<SensorDataQueryResult> resultList = q.getResultList();

        if (range == ERange.ACTUAL && resultList.size() > 0) {
            return resultList.subList(0, 1);
        } else {
            return resultList;
        }
    }



    public void onReadIlluminanceEvent(@Observes IlluminanceEvent event) {
        logger.debug("onReadIlluminanceEvent: got event");
    }

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: got event");
        List<SensorBean> resultList = em.createNamedQuery(SensorBean.findByPlaceTypeName, SensorBean.class)
                                          .setParameter("place", event.getPlace())
                                          .setParameter("type", event.getType())
                                          .setParameter("name", event.getSensor())
                                          .getResultList();
        if (resultList.isEmpty()) {
            logger.debug("onReadTemperatureEvent: no sensor found");
            return;
        }
        DataBean data = new DataBean();
        data.setDevice(resultList.get(0));
        data.setTime(event.getTimeAsDate());
        data.setValue(event.getTemperature());

        em.persist(data);
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: got event");
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        logger.debug("onReadHumidityEvent: got event");
    }
}

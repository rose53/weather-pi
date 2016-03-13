package de.rose53.weatherpi.sensordata.boundary;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import de.rose53.weatherpi.sensordata.entity.SensorBean;

@Stateless
public class SensorDataService {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    public List<SensorDataQueryResult> getSensorData(ESensorType sensorType, ESensorPlace place, ERange range) {

        if (range == null) {
            range = ERange.ACTUAL;
        }

        List<SensorDataQueryResult> resultList = em.createNamedQuery(DataBean.findByTimeTypePlace,SensorDataQueryResult.class)
                                                   .setParameter("pastTime",Timestamp.valueOf(range.getPastTime()))
                                                   .setParameter("actualTime",Timestamp.valueOf(LocalDateTime.now()))
                                                   .setParameter("type", sensorType)
                                                   .setParameter("place", place)
                                                   .getResultList();

        if (range == ERange.ACTUAL && resultList.size() > 0) {
            return resultList.subList(0, 1);
        } else {
            return resultList;
        }
    }

    public List<DataBean> getSensorData(String name, ESensorType sensorType, ESensorPlace place, ERange range) {

        if (range == null) {
            range = ERange.ACTUAL;
        }

        List<DataBean> resultList = em.createNamedQuery(DataBean.findByTimeNameTypePlace,DataBean.class)
                                      .setParameter("pastTime",Timestamp.valueOf(range.getPastTime()))
                                      .setParameter("actualTime",Timestamp.valueOf(LocalDateTime.now()))
                                      .setParameter("name", name)
                                      .setParameter("type", sensorType)
                                      .setParameter("place", place)
                                      .getResultList();

        if (range == ERange.ACTUAL && resultList.size() > 0) {
            return resultList.subList(0, 1);
        } else {
            return resultList;
        }
    }

    private SensorBean getSensor(SensorEvent sensorEvent) {
        List<SensorBean> resultList = em.createNamedQuery(SensorBean.findByPlaceTypeName, SensorBean.class)
                .setParameter("place", sensorEvent.getPlace())
                .setParameter("type", sensorEvent.getType())
                .setParameter("name", sensorEvent.getSensor())
                .getResultList();
        if (resultList.isEmpty()) {
            logger.debug("onReadTemperatureEvent: no sensor found");
            return null;
        }
        return resultList.get(0);
    }

    public void onReadIlluminanceEvent(@Observes IlluminanceEvent event) {
        logger.debug("onReadIlluminanceEvent: got event");
        SensorBean sensor = getSensor(event);
        if (sensor == null) {
            logger.debug("onReadIlluminanceEvent: no sensor found");
            return;
        }
        DataBean data = new DataBean();
        data.setDevice(sensor);
        data.setTime(event.getTimeAsDate());
        data.setValue(event.getIlluminance());

        em.persist(data);
    }

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: got event");
        SensorBean sensor = getSensor(event);
        if (sensor == null) {
            logger.debug("onReadTemperatureEvent: no sensor found");
            return;
        }
        DataBean data = new DataBean();
        data.setDevice(sensor);
        data.setTime(event.getTimeAsDate());
        data.setValue(event.getTemperature());

        em.persist(data);
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: got event");
        SensorBean sensor = getSensor(event);
        if (sensor == null) {
            logger.debug("onReadPressureEvent: no sensor found");
            return;
        }
        DataBean data = new DataBean();
        data.setDevice(sensor);
        data.setTime(event.getTimeAsDate());
        data.setValue(event.getPressure());

        em.persist(data);
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        logger.debug("onReadHumidityEvent: got event");
        SensorBean sensor = getSensor(event);
        if (sensor == null) {
            logger.debug("onReadHumidityEvent: no sensor found");
            return;
        }
        DataBean data = new DataBean();
        data.setDevice(sensor);
        data.setTime(event.getTimeAsDate());
        data.setValue(event.getHumidity());

        em.persist(data);
    }
}

package de.rose53.weatherpi.sensordata.boundary;

import static java.time.temporal.ChronoUnit.MINUTES;

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
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import de.rose53.weatherpi.sensordata.entity.SensorBean;

@Stateless
public class SensorDataService {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    @Inject
    SensorServiceBean sensorService;


    public DataBean getSensorData(String name, ESensorType sensorType, ESensorPlace place, LocalDateTime time) {

        List<DataBean> resultListL = em.createNamedQuery(DataBean.findByTimeNameTypePlaceL,DataBean.class)
                                       .setParameter("time",Timestamp.valueOf(time))
                                       .setParameter("name", name)
                                       .setParameter("type", sensorType)
                                       .setParameter("place", place)
                                       .setMaxResults(1)
                                       .getResultList();

        List<DataBean> resultListH = em.createNamedQuery(DataBean.findByTimeNameTypePlaceH,DataBean.class)
                                         .setParameter("time",Timestamp.valueOf(time))
                                       .setParameter("name", name)
                                       .setParameter("type", sensorType)
                                       .setParameter("place", place)
                                       .setMaxResults(1)
                                       .getResultList();

        if (resultListL.isEmpty() && resultListH.isEmpty()) {
            return null;
        }

        if (!resultListL.isEmpty() && resultListH.isEmpty()) {
            return MINUTES.between(time, resultListL.get(0).getLocalDateTime()) > 30?null:resultListL.get(0);
        }

        if (resultListL.isEmpty() && !resultListH.isEmpty()) {
            return MINUTES.between(time, resultListH.get(0).getLocalDateTime()) > 30?null:resultListH.get(0);
        }
        long minutesL = MINUTES.between(time, resultListL.get(0).getLocalDateTime());
        long minutesH = MINUTES.between(time, resultListH.get(0).getLocalDateTime());

        if (minutesL > 30 && minutesH > 30) {
            return null;
        }

        return minutesL < minutesH?resultListL.get(0):resultListH.get(0);
    }

    /**
     *
     * @param name the name of the sensor
     * @param sensorType the {@linkplain ESensorType }
     * @param place the {@linkplain ESensorPlace }
     * @param range the {@linkplain ERange }
     * @return
     */
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

    public DataBean getLatestSensorData(String deviceName, String sensorName, ESensorType sensorType) {
        logger.debug("getLatestSensorData: retrieving data for device >{}<, sensor >{}< and type >{}<",deviceName,sensorName,sensorType);

        List<DataBean> resultList = em.createNamedQuery(DataBean.findByDeviceSensorType,DataBean.class)
                                      .setParameter("deviceName",deviceName)
                                      .setParameter("sensorName",sensorName)
                                      .setParameter("sensorType", sensorType)
                                      .setFirstResult(0)
                                      .setMaxResults(1)
                                      .getResultList();

        return resultList.isEmpty()?null:resultList.get(0);
    }

    public <T extends SensorEvent> void persistData(T event) {

        SensorBean sensor = sensorService.getSensor(event.getSensor(),event.getPlace(),event.getType());
        if (sensor == null) {
            logger.debug("persistData: no sensor found");
            return;
        }
        DataBean data = new DataBean();
        data.setSensor(sensor);
        data.setTime(event.getTimeAsDate());
        data.setValue(event.getValue());

        em.persist(data);
    }

    public <T extends SensorEvent> void onReadSensorEvent(@Observes T event) {
        logger.debug("onReadSensorEvent: got event");
        if (event.getType() != ESensorType.WINDSPEED) {
            persistData(event);
        }
    }
}

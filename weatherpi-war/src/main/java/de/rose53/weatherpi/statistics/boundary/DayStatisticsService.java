package de.rose53.weatherpi.statistics.boundary;

import static de.rose53.pi.weatherpi.common.ESensorPlace.BIRDHOUSE;
import static de.rose53.pi.weatherpi.common.ESensorType.TEMPERATURE;

import java.sql.Date;
import java.time.LocalDate;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import de.rose53.weatherpi.sensordata.boundary.SensorDataService;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;


@Stateless
public class DayStatisticsService {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    @Inject
    SensorDataService sensorDataService;

    public long count() {
        return em.createNamedQuery(DayStatisticBean.COUNT, Long.class).getSingleResult();
    }

    public DayStatisticBean create(DayStatisticBean bean) {
        em.persist(bean);
        return bean;
    }

    public DayStatisticBean create(LocalDate date) {
        logger.debug("updateTimer: calculating statistics for {}",date);

        DataBean sensorData = null;

        DayStatisticBean bean = new DayStatisticBean();

        bean.setDay(Date.valueOf(date));
        boolean hasTempData = false;
        for (int hour = 0; hour <= 23; hour++) {
            sensorData = sensorDataService.getSensorData("DHT22", TEMPERATURE, BIRDHOUSE, date.atTime(hour, 0));
            bean.setT(hour,sensorData != null?sensorData.getValue():null);
            hasTempData |= sensorData != null;
        }
        return hasTempData?create(bean):null;
    }


}

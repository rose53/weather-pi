package de.rose53.weatherpi.statistics.boundary;

import static de.rose53.pi.weatherpi.common.ESensorPlace.BIRDHOUSE;
import static de.rose53.pi.weatherpi.common.ESensorType.TEMPERATURE;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import org.slf4j.Logger;

import de.rose53.weatherpi.sensordata.boundary.SensorDataService;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import de.rose53.weatherpi.statistics.control.ClimatologicClassificationDayCalculator;
import de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;


@Stateless
public class DayStatisticsService {

    @Inject
    Logger logger;

    @PersistenceContext
    EntityManager em;

    @Inject
    SensorDataService sensorDataService;

    @Inject
    ClimatologicClassificationDayCalculator climatologicClassificationDayCalculator;
    
    public long count() {
        return em.createNamedQuery(DayStatisticBean.COUNT, Long.class).getSingleResult();
    }

    public DayStatisticBean create(DayStatisticBean bean) {
        em.persist(bean);
        return bean;
    }

    public DayStatisticBean create(LocalDate date) {
        logger.debug("create: calculating statistics for {}",date);

        DataBean sensorData = null;

        DayStatisticBean bean = new DayStatisticBean();

        bean.setDay(Date.valueOf(date));
        boolean hasTempData = false;
        for (int hour = 0; hour <= 23; hour++) {
            sensorData = sensorDataService.getSensorData("BME280", TEMPERATURE, BIRDHOUSE, date.atTime(hour, 0));
            bean.setT(hour,sensorData != null?sensorData.getValue():null);
            hasTempData |= sensorData != null;
        }
        return hasTempData?create(bean):null;
    }

    public List<EClimatologicClassificationDay> getClimatologicClassification(LocalDate date) {
        logger.debug("getClimatologicClassification: get classification for {}",date);

        List<DayStatisticBean> resultList = em.createNamedQuery(DayStatisticBean.FIND_BY_DAY,DayStatisticBean.class)
                                              .setParameter("day", Date.valueOf(date), TemporalType.DATE)
                                              .getResultList();
        if (resultList.isEmpty()) {
            logger.debug("getClimatologicClassification: no data found for {}",date);
            return Collections.emptyList();
        }
        return climatologicClassificationDayCalculator.calculateClimatologicClassificationDay(resultList.get(0));
    }

    public List<DayStatisticBean> getRangeStatistics(LocalDate start, LocalDate end) {
        logger.debug("getRangeStatistics: get statistics from {} to {}",start, end);
        return em.createNamedQuery(DayStatisticBean.FIND_RANGE,DayStatisticBean.class)
                 .setParameter("startDate", Date.valueOf(start), TemporalType.DATE)
                 .setParameter("endDate", Date.valueOf(end), TemporalType.DATE)
                 .getResultList();
    }
}

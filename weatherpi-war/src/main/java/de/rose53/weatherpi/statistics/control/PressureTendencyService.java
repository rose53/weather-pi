package de.rose53.weatherpi.statistics.control;

import static de.rose53.pi.weatherpi.common.ESensorPlace.BIRDHOUSE;
import static de.rose53.pi.weatherpi.common.ESensorType.PRESSURE;


import java.time.LocalDateTime;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.weatherpi.sensordata.boundary.SensorDataService;
import de.rose53.weatherpi.sensordata.entity.DataBean;

@Stateless
public class PressureTendencyService {

    @Inject
    Logger logger;

    @Inject
    SensorDataService sensorDataService;

    public Double getPressureTendency() {

        LocalDateTime now         = LocalDateTime.now();
        LocalDateTime minus3Hours = now.minusHours(3);

        DataBean actualData = sensorDataService.getSensorData("BME280", PRESSURE, BIRDHOUSE, now);

        DataBean minus3HoursData = sensorDataService.getSensorData("BME280", PRESSURE, BIRDHOUSE, minus3Hours);

        if (actualData == null || minus3HoursData == null) {
            logger.debug("getPressureTendency: no data found");
            return null;
        }
        logger.debug("getPressureTendency: difference = {} hPa",actualData.getValue() - minus3HoursData.getValue());
        return actualData.getValue() - minus3HoursData.getValue();
    }
}

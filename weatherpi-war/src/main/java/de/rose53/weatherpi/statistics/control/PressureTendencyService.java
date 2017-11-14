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

    public Double getActualPressure() {
        LocalDateTime now         = LocalDateTime.now();

        DataBean actualData = sensorDataService.getSensorData("BME280", PRESSURE, BIRDHOUSE, now);
        if (actualData == null) {
            logger.debug("getActualPressure: no data found");
            return null;
        }
        logger.debug("getActualPressure: actual = {} hPa",actualData.getValue());
        return actualData.getValue();
    }

    public Double getPressureTendency() {

        LocalDateTime minus3Hours = LocalDateTime.now().minusHours(3);

        Double actualPressure = getActualPressure();

        DataBean minus3HoursData = sensorDataService.getSensorData("BME280", PRESSURE, BIRDHOUSE, minus3Hours);

        if (actualPressure == null || minus3HoursData == null) {
            logger.debug("getPressureTendency: no data found");
            return null;
        }
        logger.debug("getPressureTendency: difference = {} hPa",actualPressure - minus3HoursData.getValue());
        return actualPressure - minus3HoursData.getValue();
    }
}

package de.rose53.weatherpi.sensordata.boundary;

import static de.rose53.pi.weatherpi.common.ESensorPlace.DUSTSENSOR;
import static de.rose53.pi.weatherpi.common.ESensorType.DUST_PM10;
import static de.rose53.pi.weatherpi.common.ESensorType.DUST_PM25;
import static de.rose53.pi.weatherpi.common.ESensorType.HUMIDITY;
import static de.rose53.weatherpi.sensordata.boundary.ERange.HOUR;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.HumidityCompensation;
import de.rose53.weatherpi.sensordata.entity.DataBean;

@Stateless
public class ParticulateMatterService {

    @Inject
    Logger logger;

    @Inject
    SensorDataService sensorDataService;

    @Inject
    HumidityCompensation compensation;

    public Double getLatestPM10(boolean compensate) {
        logger.debug("getLatestPM10: reading latest pm10 value");
        List<DataBean> pm10SensorData = sensorDataService.getSensorData("SDS011",DUST_PM10,DUSTSENSOR,HOUR);
        if (pm10SensorData.isEmpty()) {
            logger.debug("getLatestPM10: database returned no value, returning ");
            return null;
        }
        double pm10 = pm10SensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
        if (compensate) {
            List<DataBean> humiditySensorData = sensorDataService.getSensorData("SDS011",HUMIDITY,DUSTSENSOR,HOUR);
            double humidity = humiditySensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
            if (humidity > 5 && humidity < 100) {
                pm10 = compensation.compensate(humidity, pm10);
            }
        }
        return pm10;
    }

    public Double getLatestPM25(boolean compensate) {
        logger.debug("getLatestPM25: reading latest pm25 value");
        List<DataBean> pm25SensorData = sensorDataService.getSensorData("SDS011",DUST_PM25,DUSTSENSOR,HOUR);
        if (pm25SensorData.isEmpty()) {
            logger.debug("getLatestPM25: database returned no value, returning ");
            return null;
        }
        double pm25 = pm25SensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
        if (compensate) {
            List<DataBean> humiditySensorData = sensorDataService.getSensorData("SDS011",HUMIDITY,DUSTSENSOR,HOUR);
            double humidity = humiditySensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
            if (humidity > 5 && humidity < 100) {
                pm25 = compensation.compensate(humidity, pm25);
            }
        }
        return pm25;
    }
}

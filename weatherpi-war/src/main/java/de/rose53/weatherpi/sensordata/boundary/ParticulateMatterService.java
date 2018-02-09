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
        logger.debug("getLatestPM10: reading latest pm10 value, compensate = >{}<",compensate);
        List<DataBean> pm10SensorData = sensorDataService.getSensorData("SDS011",DUST_PM10,DUSTSENSOR,HOUR);
        if (pm10SensorData.isEmpty()) {
            logger.debug("getLatestPM10: database returned no value, returning ");
            return null;
        }
        double pm10 = pm10SensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
        logger.debug("getLatestPM10: pm10 = >{}<",pm10);
        if (compensate) {
            pm10 = compensate(pm10);
            logger.debug("getLatestPM10: after compensation pm10 = >{}<",pm10);
        }
        return pm10;
    }

    public Double getLatestPM25(boolean compensate) {
        logger.debug("getLatestPM25: reading latest pm25 value, compensate = >{}<",compensate);
        List<DataBean> pm25SensorData = sensorDataService.getSensorData("SDS011",DUST_PM25,DUSTSENSOR,HOUR);
        if (pm25SensorData.isEmpty()) {
            logger.debug("getLatestPM25: database returned no value, returning ");
            return null;
        }
        double pm25 = pm25SensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
        logger.debug("getLatestPM25: pm25 = >{}<",pm25);
        if (compensate) {
            pm25 = compensate(pm25);
            logger.debug("getLatestPM25: after compensation pm25 = >{}<",pm25);
        }
        return pm25;
    }

    private double compensate(double pm) {

        List<DataBean> humiditySensorData = sensorDataService.getSensorData("DHT22",HUMIDITY,DUSTSENSOR,HOUR);
        double humidity = humiditySensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
        logger.debug("compensate: humidity = >{}<",humidity);
        if (humidity > 5.0 && humidity < 100.0) {
            return compensation.compensate(humidity, pm);
        } else {
            return pm;
        }
    }
}

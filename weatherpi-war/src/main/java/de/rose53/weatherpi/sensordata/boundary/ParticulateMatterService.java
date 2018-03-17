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
        return getLatestPM(sensorDataService.getSensorData("SDS011",DUST_PM10,DUSTSENSOR,HOUR),compensate);
    }

    public Double getLatestPM25(boolean compensate) {
        logger.debug("getLatestPM25: reading latest pm25 value, compensate = >{}<",compensate);
        return getLatestPM(sensorDataService.getSensorData("SDS011",DUST_PM25,DUSTSENSOR,HOUR),compensate);
    }

    private Double getLatestPM(List<DataBean> pmSensorData, boolean compensate) {
        if (pmSensorData.isEmpty()) {
            logger.debug("getLatestPM: database returned no value, returning ");
            return null;
        }
        double pm = pmSensorData.stream().mapToDouble(DataBean::getValue).average().orElse(0.0);
        logger.debug("getLatestPM: pm = >{}<",pm);
        if (compensate) {
            pm = compensate(pm);
            logger.debug("getLatestPM: after compensation pm = >{}<",pm);
        }
        return pm;
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

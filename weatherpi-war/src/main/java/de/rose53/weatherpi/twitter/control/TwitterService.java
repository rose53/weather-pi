package de.rose53.weatherpi.twitter.control;

import java.text.DecimalFormat;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import static de.rose53.pi.weatherpi.common.ESensorPlace.*;
import static de.rose53.pi.weatherpi.common.ESensorType.*;
import static de.rose53.weatherpi.sensordata.boundary.ERange.*;
import de.rose53.weatherpi.sensordata.boundary.SensorDataService;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Singleton
@Startup
public class TwitterService {

    @Inject
    Logger logger;

    @Inject
    Twitter twitter;

    @Inject
    SensorDataService sensorDataService;

    @Schedule(second="0", minute="0",hour="*/3", persistent=false)
    public void updateTimer(){

        DecimalFormat tempFormat = new DecimalFormat("#.0");
        DecimalFormat humidityFormat = new DecimalFormat("#.0");
        DecimalFormat pressureFormat = new DecimalFormat("#");

        StringBuilder status = new StringBuilder();

        status.append("Indoor:").append('\n');

        status.append("Temp.   : ");
        List<DataBean> indoorTemperature = sensorDataService.getSensorData("BMP085", TEMPERATURE, INDOOR, ACTUAL);
        if (!indoorTemperature.isEmpty()) {
            status.append(tempFormat.format(indoorTemperature.get(0).getValue())).append("°C").append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Humidity: ");
        List<DataBean> indoorHumidity = sensorDataService.getSensorData("HTU21D", HUMIDITY, INDOOR, ACTUAL);
        if (!indoorHumidity.isEmpty()) {
            status.append(humidityFormat.format(indoorHumidity.get(0).getValue())).append('%').append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Pressure: ");
        List<DataBean> indoorPressure = sensorDataService.getSensorData("BMP085", PRESSURE, INDOOR, ACTUAL);
        if (!indoorPressure.isEmpty()) {
            status.append(pressureFormat.format(indoorPressure.get(0).getValue())).append("hPa").append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Birdhouse:").append('\n');
        status.append("Temp.   : ");
        List<DataBean> birdhouseTemperature = sensorDataService.getSensorData("DHT22", TEMPERATURE, BIRDHOUSE, ACTUAL);
        if (!birdhouseTemperature.isEmpty()) {
            status.append(tempFormat.format(birdhouseTemperature.get(0).getValue())).append("°C").append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Humidity: ");
        List<DataBean> birdhouseHumidity = sensorDataService.getSensorData("DHT22", HUMIDITY, BIRDHOUSE, ACTUAL);
        if (!birdhouseHumidity.isEmpty()) {
            status.append(humidityFormat.format(birdhouseHumidity.get(0).getValue())).append('%').append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        logger.debug("updateTimer: status for twitter = >{}<",status);
        StatusUpdate statusUpdate = new StatusUpdate(status.toString());

        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            logger.error("updateTimer:",e);
        }

    }
}

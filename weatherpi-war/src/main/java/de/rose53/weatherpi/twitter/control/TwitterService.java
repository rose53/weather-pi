package de.rose53.weatherpi.twitter.control;

import java.text.DecimalFormat;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.weatherpi.sensordata.boundary.ERange;
import de.rose53.weatherpi.sensordata.boundary.SensorDataService;
import de.rose53.weatherpi.sensordata.entity.SensorDataBean;
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

        StringBuilder status = new StringBuilder();

        List<SensorDataBean> sensorData = sensorDataService.getSensorData(ERange.ACTUAL);
        if (sensorData.isEmpty()) {
            status.append("No actual data available.").append('\n');
        } else {
            DecimalFormat tempFormat = new DecimalFormat("#.0");
            DecimalFormat humidityFormat = new DecimalFormat("#.0");
            DecimalFormat pressureFormat = new DecimalFormat("#");

            SensorDataBean actData = sensorData.get(0);
            status.append("Indoor:").append('\n')
                  .append("Temp.   : ").append(tempFormat.format(actData.getTemperature())).append("°C").append('\n')
                  .append("Humidity: ").append(humidityFormat.format(actData.getHumidity())).append("%").append('\n')
                  .append("Pressure: ").append(pressureFormat.format(actData.getPressure())).append("hPa").append('\n')
                  .append("Birdhouse:").append('\n')
                  .append("Temp.   : ").append(tempFormat.format(actData.getTemperatureBird())).append("°C").append('\n')
                  .append("Humidity: ").append(humidityFormat.format(actData.getHumidityBird())).append("%").append('\n');
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

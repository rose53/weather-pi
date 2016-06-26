package de.rose53.weatherpi.twitter.control;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import static de.rose53.pi.weatherpi.common.ESensorPlace.*;
import static de.rose53.pi.weatherpi.common.ESensorType.*;
import static de.rose53.weatherpi.sensordata.boundary.ERange.*;
import de.rose53.weatherpi.sensordata.boundary.SensorDataService;
import de.rose53.weatherpi.sensordata.entity.DataBean;
import de.rose53.weatherpi.statistics.control.DayStatisticEvent;
import de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay;
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

        status.append("Pressure: ");
        List<DataBean> indoorPressure = sensorDataService.getSensorData("BMP085", PRESSURE, INDOOR, ACTUAL);
        if (!indoorPressure.isEmpty()) {
            status.append(pressureFormat.format(indoorPressure.get(0).getValue())).append("hPa").append('\n');
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

    public void onDayStatisticEvent(@Observes DayStatisticEvent event) {

        DecimalFormat tempFormat = new DecimalFormat("#.0");

        StringBuilder status = new StringBuilder();


        status.append("Day statistics: ").append(event.getDay().format(DateTimeFormatter.ISO_DATE)).append('\n');
        if (event.gettMin() != null) {
            status.append("Tmin          : ").append(tempFormat.format(event.gettMin())).append("°C").append('\n');
        }
        if (event.gettMax() != null) {
            status.append("Tmax          : ").append(tempFormat.format(event.gettMax())).append("°C").append('\n');
        }
        if (event.gettMed() != null) {
            status.append("Tmed          : ").append(tempFormat.format(event.gettMed())).append("°C").append('\n');
        }
        if (!event.getClassificationDay().isEmpty()) {
            status.append("Clim.classf.D.: ").append(EClimatologicClassificationDay.getTwitterFeed(event.getClassificationDay())).append('\n');
        }

        logger.debug("onDayStatisticEvent: status for twitter = >{}<",status);
        StatusUpdate statusUpdate = new StatusUpdate(status.toString());

        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            logger.error("onDayStatisticEvent:",e);
        }

    }
}

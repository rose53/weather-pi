package de.rose53.weatherpi.twitter.control;

import java.io.StringReader;
import java.text.DecimalFormat;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Singleton
@Startup
public class TwitterService {

    private static final String BASE_URI = "http://localhost:8080/weatherpi/resources/sensordata/BIRDHOUSE/BME280";

    @Inject
    Logger logger;

    @Inject
    Twitter twitter;

    @Resource(mappedName = "java:/jms/topic/DayStatisticTopic")
    private Topic topic;

    @Inject
    JMSContext context;

    private Double getSensorData(String type) {
        Client clientBuilder = ClientBuilder.newClient();

        UriBuilder uriBuilder = UriBuilder.fromUri(BASE_URI);

        uriBuilder.segment(type);
        uriBuilder.segment("actual");

        Response response = clientBuilder.target(uriBuilder)
                                         .request(MediaType.APPLICATION_JSON_TYPE)
                                         .get();

        JsonObject object = Json.createReader(new StringReader(response.readEntity(String.class)))
                                    .readObject();

        response.close();

        if (object == null || object.isNull("sensorData")) {
            return null;
        }


        JsonArray sensorData = object.getJsonArray("sensorData");
        if (sensorData.isEmpty() || sensorData.getJsonObject(0).isNull("value")) {
            return null;
        }
        return sensorData.getJsonObject(0).getJsonNumber("value").doubleValue();
    }

    @Schedule(second="0", minute="0",hour="*/3", persistent=false)
    public void updateTimer(){

        DecimalFormat tempFormat = new DecimalFormat("#.0");
        DecimalFormat humidityFormat = new DecimalFormat("#.0");
        DecimalFormat pressureFormat = new DecimalFormat("#");


        Double temperature = getSensorData("temperature");
        Double pressure = getSensorData("pressure");
        Double humidity = getSensorData("humidity");

        StringBuilder status = new StringBuilder();

        status.append("Temp.   : ");
        if (temperature != null) {
            status.append(tempFormat.format(temperature)).append("°C").append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Humidity: ");
        if (humidity != null) {
            status.append(humidityFormat.format(humidity)).append('%').append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Pressure: ");
        if (pressure != null) {
            status.append(pressureFormat.format(pressure)).append("hPa").append('\n');
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



    /*
    public void onDayStatisticEvent(@Observes DayStatisticEvent event) {

        DecimalFormat tempFormat = new DecimalFormat("#.0");

        StringBuilder status = new StringBuilder();


        status.append("Day statistics: ").append(event.getDay().format(DateTimeFormatter.ISO_DATE)).append('\n');
        if (event.gettMin() != null) {
            status.append("Tmin: ").append(tempFormat.format(event.gettMin())).append("°C").append('\n');
        }
        if (event.gettMax() != null) {
            status.append("Tmax: ").append(tempFormat.format(event.gettMax())).append("°C").append('\n');
        }
        if (event.gettMed() != null) {
            status.append("Tmed: ").append(tempFormat.format(event.gettMed())).append("°C").append('\n');
        }
        if (!event.getClassificationDay().isEmpty()) {
            status.append("CCD : ").append(EClimatologicClassificationDay.getTwitterFeed(event.getClassificationDay())).append('\n');
        }

        logger.debug("onDayStatisticEvent: status for twitter = >{}<",status);
        StatusUpdate statusUpdate = new StatusUpdate(status.toString());

        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            logger.error("onDayStatisticEvent:",e);
            logger.error("onDayStatisticEvent: message = {}",status.toString());
        }

    }
    */
}

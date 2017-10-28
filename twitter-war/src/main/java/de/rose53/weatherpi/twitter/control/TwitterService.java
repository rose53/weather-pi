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

    private static final String BASE_URI                   = "http://localhost:8080/weatherpi/resources/sensordata/BIRDHOUSE/BME280";
    private static final String BASE_URI_PRESSURE_TENDENCY = "http://localhost:8080/weatherpi/resources/pressuretendency";
    private static final String BASE_URI_WINDFORCE         = "http://localhost:8080/weatherpi/resources/windspeed";

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

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getSensorData: call to >{}< returned status = >{}<",clientBuilder,response.getStatus());
        } else {
            object = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();
        }
        response.close();

        if (object == null || object.isNull("sensorData")) {
            logger.error("getSensorData: JSON object is null or does not contain sensor data");
            return null;
        }

        JsonArray sensorData = object.getJsonArray("sensorData");
        if (sensorData.isEmpty() || sensorData.getJsonObject(0).isNull("value")) {
            logger.error("getSensorData: sensorData is empty");
            return null;
        }
        logger.debug("getSensorData: returning value = >{}<",sensorData.getJsonObject(0).getJsonNumber("value").doubleValue());
        return sensorData.getJsonObject(0).getJsonNumber("value").doubleValue();
    }


    private String getPressureTendency() {
        Client clientBuilder = ClientBuilder.newClient();

        UriBuilder uriBuilder = UriBuilder.fromUri(BASE_URI_PRESSURE_TENDENCY);

        Response response = clientBuilder.target(uriBuilder)
                                         .request(MediaType.APPLICATION_JSON_TYPE)
                                         .get();

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getPressureTendency: call to >{}< returned status = >{}<",clientBuilder,response.getStatus());
        } else {
            object = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();
        }
        response.close();

        if (object == null) {
            logger.error("getPressureTendency: JSON object is null or does not contain data");
            return null;
        }

        return object.getString("meaning");
    }

    private Double getWindforce() {
        Client clientBuilder = ClientBuilder.newClient();

        UriBuilder uriBuilder = UriBuilder.fromUri(BASE_URI_WINDFORCE);

        Response response = clientBuilder.target(uriBuilder)
                                         .queryParam("unit", "BFT")
                                         .request(MediaType.APPLICATION_JSON_TYPE)
                                         .get();

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getWindforce: call to >{}< returned status = >{}<",clientBuilder,response.getStatus());
        } else {
            object = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();
        }
        response.close();

        if (object == null) {
            logger.error("getWindforce: JSON object is null or does not contain data");
            return null;
        }

        return object.getJsonNumber("windspeed").doubleValue();
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

    @Schedule(second="0", minute="1",hour="*/1", persistent=false)
    public void updatePressureTendencyAndWindforce(){


        Double windForce        = getWindforce();
        String pressureTendency = getPressureTendency();

        StringBuilder status = new StringBuilder();

        status.append("Pressure tendency : ");
        if (pressureTendency != null) {
            status.append(pressureTendency);
        } else{
            status.append("No actual data available.");
        }

        status.append('\n')
              .append("Wind force        : ");
        if (windForce != null) {
            status.append(Long.toString(Math.round(windForce))).append(" bft");
        } else{
            status.append("No actual data available.");
        }

        logger.debug("updatePressureTendencyAndWindforce: status for twitter = >{}<",status);
        StatusUpdate statusUpdate = new StatusUpdate(status.toString());

        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            logger.error("updatePressureTendencyAndWindforce:",e);
        }
    }
}

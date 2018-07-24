package de.rose53.weatherpi.twitter.control;

import static de.rose53.pi.weatherpi.common.WindspeedUnit.*;


import java.io.StringReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.RestResource;
import de.rose53.pi.weatherpi.common.WindchillCalculator;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Singleton
@Startup
public class TwitterService {

    private static final String BASE_URI                   = "http://localhost:8080/weatherpi/resources/sensordata/BIRDHOUSE/BME280";
    private static final String BASE_URI_PRESSURE_TENDENCY = "http://localhost:8080/weatherpi/resources/pressuretendency";

    @Inject
    Logger logger;

    @Inject
    Twitter twitter;

    @Inject
    WindchillCalculator windchillCalculator;

    @Resource(mappedName = "java:/jms/topic/DayStatisticTopic")
    private Topic topic;

    @Inject
    JMSContext context;

    @Inject
    @RestResource(path="/weatherpi/resources/windspeed")
    WebTarget windspeedWebTarget;

    @Inject
    @RestResource(path="/weatherpi/resources/winddirection")
    WebTarget winddirectionWebTarget;

    @Inject
    @RestResource(path="/weatherpi/resources/zambretti")
    WebTarget zambrettiWebTarget;

    @Inject
    @RestResource(path="/weatherpi/resources/particulatematter")
    WebTarget particulatematterWebTarget;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    private Windforce getWindforce() {
        Client clientBuilder = ClientBuilder.newClient();

        Response response = windspeedWebTarget.queryParam("unit", "KMH").request(MediaType.APPLICATION_JSON_TYPE).get();

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getWindforce: call to >{}< returned status = >{}<",clientBuilder,response.getStatus());
        } else {
            String jsonString = response.readEntity(String.class);
            logger.debug("getWindforce: returned json = >{}<",jsonString);

            object = Json.createReader(new StringReader(jsonString)).readObject();
        }
        response.close();

        if (object == null) {
            logger.error("getWindforce: JSON object is null or does not contain data");
            return null;
        }

        JsonNumber windspeed = object.getJsonNumber("windspeed");
        logger.debug("getWindforce: >{}<",windspeed);
        double windspeedInkmh;
        if (windspeed.isIntegral()) {
            windspeedInkmh = windspeed.longValue();
        } else {
            windspeedInkmh = windspeed.doubleValue();
        }
        return new Windforce(windspeedInkmh,Math.round(convert(windspeedInkmh, KMH, BFT)),object.getString("description"));
    }

    private String getWinddirection() {
        Client clientBuilder = ClientBuilder.newClient();

        Response response = winddirectionWebTarget.request(MediaType.APPLICATION_JSON_TYPE).get();

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getWinddirection: call to >{}< returned status = >{}<",clientBuilder,response.getStatus());
        } else {
            String jsonString = response.readEntity(String.class);
            logger.debug("getWinddirection: returned json = >{}<",jsonString);

            object = Json.createReader(new StringReader(jsonString)).readObject();
        }
        response.close();

        if (object == null) {
            logger.error("getWinddirection: JSON object is null or does not contain data");
            return null;
        }
        return object.getString("description");
    }

    private String getZambrettiForcast() {
        Response response = zambrettiWebTarget.request(MediaType.APPLICATION_JSON_TYPE).get();

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getWindforce: call to >{}< returned status = >{}<",zambrettiWebTarget.getUri(),response.getStatus());
        } else {
            object = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();
        }
        response.close();

        if (object == null) {
            logger.error("getZambrettiForcast: JSON object is null or does not contain data");
            return null;
        }
        return object.getString("zambretti");
    }

    private Pm getPm() {
        Client clientBuilder = ClientBuilder.newClient();

        Response response = particulatematterWebTarget.queryParam("compensate", "false").request(MediaType.APPLICATION_JSON_TYPE).get();

        JsonObject object = null;
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("getPm: call to >{}< returned status = >{}<",clientBuilder,response.getStatus());
        } else {
            String jsonString = response.readEntity(String.class);
            logger.debug("getPm: returned json = >{}<",jsonString);

            object = Json.createReader(new StringReader(jsonString)).readObject();
        }
        response.close();

        if (object == null) {
            logger.error("getPm: JSON object is null or does not contain data");
            return null;
        }

        JsonNumber pm10Json = object.getJsonNumber("pm10");
        logger.debug("getPm: >{}<",pm10Json);
        double pm10;
        if (pm10Json.isIntegral()) {
            pm10 = pm10Json.longValue();
        } else {
            pm10 = pm10Json.doubleValue();
        }

        JsonNumber pm25Json = object.getJsonNumber("pm25");
        logger.debug("getPm: >{}<",pm25Json);
        double pm25;
        if (pm25Json.isIntegral()) {
            pm25 = pm25Json.longValue();
        } else {
            pm25 = pm25Json.doubleValue();
        }
        return new Pm(pm10,pm25);
    }

    @Schedule(second="5", minute="0",hour="*/1", persistent=false)
    public void updateTimer(){

        DecimalFormat tempFormat     = new DecimalFormat("#.0");
        DecimalFormat humidityFormat = new DecimalFormat("#.0");
        DecimalFormat pressureFormat = new DecimalFormat("#");
        DecimalFormat pmFormat       = new DecimalFormat("#");


        Double temperature = getSensorData("temperature");
        Double pressure    = getSensorData("pressure");
        Double humidity    = getSensorData("humidity");

        Windforce windForce        = getWindforce();
        String    pressureTendency = getPressureTendency();
        String    zambrettiForcast = getZambrettiForcast();
        Pm        pm               = getPm();

        logger.debug("updatePressureTendencyAndWindforce: pressureTendency = >{}<",pressureTendency);
        logger.debug("updatePressureTendencyAndWindforce: windForce        = >{}<",windForce);
        logger.debug("updatePressureTendencyAndWindforce: zambrettiForcast = >{}<",zambrettiForcast);

        StringBuilder status = new StringBuilder();

        status.append("Date      : ")
              .append(LocalDateTime.now().format(formatter))
              .append('\n');

        status.append("Temp.     : ");
        if (temperature != null) {
            status.append(tempFormat.format(temperature)).append("°C").append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Humidity  : ");
        if (humidity != null) {
            status.append(humidityFormat.format(humidity)).append('%').append('\n');
        } else{
            status.append("No actual data available.").append('\n');
        }

        status.append("Pressure  : ");
        if (pressure != null) {
            status.append(pressureFormat.format(pressure)).append("hPa");
            if (pressureTendency != null) {
                status.append(" (").append(pressureTendency).append(')');
            }
        } else{
            status.append("No actual data available.");
        }

        status.append('\n')
              .append("Zambretti : ");
        if (zambrettiForcast != null) {
            status.append(zambrettiForcast);
        } else{
            status.append("No actual data available.");
        }

        status.append('\n')
              .append("Wind force: ");
        if (windForce != null) {
            status.append(Long.toString(windForce.windforceBft))
                  .append(" bft ")
                  .append('(')
                  .append(windForce.description)
                  .append(')');

            if (windForce.windforceKmh > 0) {
                status.append('\n')
                      .append("Direction : ")
                      .append(getWinddirection());
            }

            Double windChill = windchillCalculator.calculate(temperature,windForce.windforceKmh);
            if (windChill != null) {
                status.append('\n')
                      .append("Windchill : ")
                      .append(tempFormat.format(windChill)).append("°C");
            }
        } else{
            status.append("No actual data available.");
        }

        if (pm != null) {
            status.append('\n')
                  .append("PM10      : ")
                  .append(pmFormat.format(pm.pm10)).append("µg/m³").append('\n')
                  .append("PM2.5     : ")
                  .append(pmFormat.format(pm.pm25)).append("µg/m³").append('\n');
        }

        logger.debug("updateTimer: status for twitter = >{}<",status);
        StatusUpdate statusUpdate = new StatusUpdate(status.toString());

        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            logger.error("updateTimer:",e);
        }
    }



    class Windforce {

        private final double windforceKmh;
        private final long   windforceBft;
        private final String description;

        public Windforce(double windforceKmh, long windforceBft, String description) {
            super();
            this.windforceKmh = windforceKmh;
            this.windforceBft = windforceBft;
            this.description  = description;
        }
    }

    class Pm {
        private final double pm10;
        private final double pm25;

        public Pm(double pm10, double pm25) {
            super();
            this.pm10 = pm10;
            this.pm25 = pm25;
        }
    }
}

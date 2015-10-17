package de.rose53.pi.weatherpi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import de.rose53.pi.weatherpi.componets.Displayable;
import de.rose53.pi.weatherpi.database.Database;
import de.rose53.pi.weatherpi.database.RowData;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.mqtt.MqttCdiEventBridge;
import de.rose53.pi.weatherpi.twitter.TwitterPlublisher;
import de.rose53.pi.weatherpi.utils.StringConfiguration;
import de.rose53.weatherpi.web.TemperatureValue;
import de.rose53.weatherpi.web.Webserver;



/**
 *
 * @author rose
 */
@Singleton
public class WeatherPi implements Runnable {

    @Inject
    Logger logger;

    private boolean running;

    @Inject
    @StringConfiguration(key="host.name",defaultValue="localhost")
    String hostName;

    @Inject
    Display display;

    @Inject
    Webserver webServer;

    @Inject
    TwitterPlublisher twitter;

    @Inject
    @Any
    Instance<Displayable> displayables;

    @Inject
    Connection connection;

    @Inject
    Database database;

    @Inject
    MqttCdiEventBridge mqttCdiEventBridge;

    private double pressureIndoor = 0;
    private double humidityIndoor = 0;
    private double illuminance = 0;

    private Map<String, TemperatureValue> temperatureSensorMap = new HashMap<>();

    private double humidityOutdoor = 0;
    private double temperatureOutdoor = 0;

    public void start() {

        try {
            System.out.print("Starting WebServer ...");
            webServer.start();
            System.out.println("\b\b\bdone.");



        } catch (Exception e) {
            logger.error("start:",e);
        }

        System.out.println("\n\n ####################################################### ");
        System.out.println(" ####              WEATHER PI IS ALIVE !!!           ### ");
        System.out.println(" ####################################################### ");
        System.out.println(" ### Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        running = true;
    }

    public void stop() {
        if (running) {
            running = false;
        }
        try {
            webServer.stop();
        } catch (Exception e) {
            logger.error("stop: ",e);
        }
    }

    @Override
    public void run() {
        start();
        int lastMinute = -1;
        int actMinute;
            while (running) {
                try {
                    actMinute = LocalDateTime.now().getMinute();
                    if (((actMinute > lastMinute) || (lastMinute == 59 && actMinute == 0) && (pressureIndoor > 0.0))) {
                        lastMinute = actMinute;
                        // read sensor and add to database
                        List<TemperatureValue> sorted =  temperatureSensorMap.values().parallelStream().sorted(Comparator.comparingDouble(t -> t.getAccuracy())).collect(Collectors.toList());

                        database.insertSensorData(new RowData(sorted.isEmpty()?0.0:sorted.get(0).getTemperature(),pressureIndoor,humidityIndoor,illuminance,temperatureOutdoor,humidityOutdoor));
                    }

                    // update display
                    for (Displayable displayable : displayables) {
                        display.clear();
                        displayable.display(display);
                        display.writeDisplay();
                        Thread.sleep(5000);
                    }

                } catch (InterruptedException | IOException | SQLException e) {
                    logger.warn("run:",e);
                }
            }
    }

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: ");
        switch (event.getPlace()) {
        case INDOOR:
            temperatureSensorMap.put(event.getSensor(),new TemperatureValue(event.getTemperature(),event.getAccuracy()));
            break;
        case OUTDOOR:
            temperatureOutdoor = event.getTemperature();
            break;
        }
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: ");
        switch (event.getPlace()) {
        case INDOOR:
            pressureIndoor = event.getPressure();
            break;
        case OUTDOOR:
            break;
        }
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        logger.debug("onReadHumidityEvent: ");
        switch (event.getPlace()) {
        case INDOOR:
            humidityIndoor = event.getHumidity();
            break;
        case OUTDOOR:
            humidityOutdoor = event.getHumidity();
            break;
        }
    }

    public void onReadIlluminanceEvent(@Observes IlluminanceEvent event) {
        logger.debug("onReadIlluminanceEvent: ");
        illuminance = event.getIlluminance();
    }

    public static void main(String[] args) {
        System.out.println("Starting ...");

        System.out.print("Initializing CDI ...");
        final Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        WeatherPi marvin = container.instance().select(WeatherPi.class).get();
        System.out.println("\b\b\bdone.");

        System.out.println("Starting WeatherPi ...");
        Thread thread = new Thread(marvin);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                marvin.logger.info("Shutdown Hook is running !");
                marvin.stop();
                weld.shutdown();
            }
        });

    }
}

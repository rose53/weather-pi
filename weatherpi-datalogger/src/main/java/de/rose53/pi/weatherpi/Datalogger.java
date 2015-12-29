package de.rose53.pi.weatherpi;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.configuration.StringConfiguration;
import de.rose53.pi.weatherpi.database.Database;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.mqtt.MqttCdiEventBridge;



/**
 *
 * @author rose
 */
@Singleton
public class Datalogger implements Runnable {

    @Inject
    Logger logger;

    private boolean running;

    @Inject
    @StringConfiguration(key="host.name",defaultValue="localhost")
    String hostName;

    @Inject
    Connection connection;

    @Inject
    Database database;

    @Inject
    MqttCdiEventBridge mqttCdiEventBridge;

    public void start() {

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
    }

    @Override
    public void run() {
        start();
        while (running) {

        }
    }

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: ");
        switch (event.getPlace()) {
        case INDOOR:
            break;
        case OUTDOOR:
            break;
        case BIRDHOUSE:
            break;
        default:
            break;
        }
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: ");
        switch (event.getPlace()) {
        case INDOOR:
            break;
        case OUTDOOR:
        case BIRDHOUSE:
            break;
        default:
            break;
        }
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        logger.debug("onReadHumidityEvent: ");
        switch (event.getPlace()) {
        case INDOOR:
            break;
        case OUTDOOR:
            break;
        case BIRDHOUSE:
            break;
        default:
            break;
        }
    }

    public void onReadIlluminanceEvent(@Observes IlluminanceEvent event) {
        logger.debug("onReadIlluminanceEvent: ");
    }


    public static void main(String[] args) {
        System.out.println("Starting ...");

        System.out.print("Initializing CDI ...");
        final Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        Datalogger datalogger = container.instance().select(Datalogger.class).get();
        System.out.println("\b\b\bdone.");

        System.out.println("Starting Datalogger ...");
        Thread thread = new Thread(datalogger);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                datalogger.logger.info("Shutdown Hook is running !");
                datalogger.stop();
                weld.shutdown();
            }
        });

    }
}

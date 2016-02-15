package de.rose53.pi.weatherpi;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import de.rose53.pi.weatherpi.componets.Sensor;
import de.rose53.pi.weatherpi.mqtt.MqttCdiEventBridge;
import de.rose53.pi.weatherpi.utils.Utils;



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
    @Any
    Instance<Sensor> sensors;

    @Inject
    MqttCdiEventBridge mqttCdiEventBridge;

    public void start() {

        try {
            System.out.println("Collecting Sensors ...");
            sensors.forEach(s -> System.out.println(s.getName()));
            System.out.println("done.");
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
    }

    @Override
    public void run() {
        start();
        while (running) {
            Utils.delay(2000);
        }
    }


    public static void main(String[] args) {
        System.out.println("Starting ...");

        System.out.print("Initializing CDI ...");
        final Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        WeatherPi weatherpi = container.instance().select(WeatherPi.class).get();
        System.out.println("\b\b\bdone.");

        System.out.println("Starting WeatherPi ...");
        Thread thread = new Thread(weatherpi);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                weatherpi.logger.info("Shutdown Hook is running !");
                weatherpi.stop();
                weld.shutdown();
            }
        });

    }
}

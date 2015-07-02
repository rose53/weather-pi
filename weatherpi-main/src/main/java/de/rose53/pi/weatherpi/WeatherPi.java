package de.rose53.pi.weatherpi;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import de.rose53.pi.weatherpi.componets.Displayable;
import de.rose53.pi.weatherpi.display.EBase;



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
    Display display;

//    @Inject
//    Webserver webServer;
    @Inject
    @Any
    Instance<Displayable> displayables;

    public WeatherPi() {

    }

    public void start() {

        try {
            System.out.print("Starting WebServer ...");
 //           webServer.start();
            System.out.println("\b\b\bdone.");

        } catch (Exception e) {
            logger.error("start:",e);
        }
        show();

        System.out.println("\n\n ####################################################### ");
        System.out.println(" ####              WEATHER PI IS ALIVE !!!           ### ");
        System.out.println(" ####################################################### ");
        //System.out.println(" ### Date: " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
        running = true;
    }

    public void stop() {
        if (running) {
            running = false;
        }
        try {
           // webServer.stop();
        } catch (Exception e) {
            logger.error("stop: ",e);
        }
    }

    @Override
    public void run() {
        start();

        while (running) {
            try {

        	for (Displayable displayable : displayables) {
        		display.clear();
        		displayable.display(display);
        		display.writeDisplay();
        		Thread.sleep(5000);
        	}

            } catch (InterruptedException | IOException e) {
                logger.warn("run:",e);
            }
        }
    }

    public void show() {

        try {
        	display.print(0xBEEF, EBase.HEX);
			display.writeDisplay();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }



    public static void main(String[] args) {
        System.out.println("Starting ...");

        System.out.print("Initializing CDI ...");
        final Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        WeatherPi marvin = container.instance().select(WeatherPi.class).get();
        System.out.println("\b\b\bdone.");

        System.out.println("Starting Marvin ...");
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

package de.rose53.pi.weatherpi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.utils.StringConfiguration;



/**
 *
 * @author rose
 */
@Singleton
public class WeatherPi implements Runnable {

	static private final String SENSOR_DATA_INSERT = "insert into SENSOR_DATA (TIME,TEMPERATURE,PRESSURE) values (SYSDATE(),?,?)";

    @Inject
    Logger logger;

    private boolean running;

    @Inject
    @StringConfiguration(key="host.name",defaultValue="localhost")
    String hostName;

    @Inject
    Display display;

//    @Inject
//    Webserver webServer;
    @Inject
    @Any
    Instance<Displayable> displayables;

    @Inject
    Connection connection;

    private float  temperature = 0;
    private double pressure = 0;

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

        int lastMinute = -1;
        int actMinute;
        try (PreparedStatement statement = connection.prepareStatement(SENSOR_DATA_INSERT)) {
	        while (running) {

	        	actMinute =LocalDateTime.now().getMinute();
	        	if ((actMinute > lastMinute) || (lastMinute == 59 && actMinute == 0)) {
	        		lastMinute = actMinute;
	        		// read sensor and add to database
	        		statement.setFloat(1, temperature);
	        		statement.setDouble(2, pressure);

	        		statement.executeUpdate();
	        	}

	            try {

	            	// update display
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
        } catch (SQLException e) {
			logger.error("run:",e);
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

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: ");
        temperature = event.getTemperature();
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: ");
        pressure = event.getPressure();
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

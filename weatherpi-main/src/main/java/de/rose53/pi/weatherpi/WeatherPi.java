package de.rose53.pi.weatherpi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioTriggerBase;

import de.rose53.pi.weatherpi.componets.Displayable;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.utils.StringConfiguration;
import de.rose53.weatherpi.web.TemperatureValue;
import de.rose53.weatherpi.web.Webserver;



/**
 *
 * @author rose
 */
@Singleton
public class WeatherPi implements Runnable {

    static private final String SENSOR_DATA_INSERT = "insert into SENSOR_DATA (TIME,TEMPERATURE,PRESSURE,HUMIDITY,ILLUMINATION) values (SYSDATE(),?,?,?,?)";

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
    @Any
    Instance<Displayable> displayables;

    @Inject
    Connection connection;

    @Inject
    GpioController gpio;

    private double pressure = 0;
    private double humidity = 0;
    private double illuminance= 0;

    private Map<String, TemperatureValue> temperatureSensorMap = new HashMap<>();

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
        final GpioPinDigitalInput displayOnOffSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,PinPullResistance.PULL_DOWN);
        displayOnOffSwitch.addTrigger(new DisplayTrigger());

        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "LED #1", PinState.LOW);

        led.setShutdownOptions(true, PinState.LOW);

        int lastMinute = -1;
        int actMinute;
        try (PreparedStatement statement = connection.prepareStatement(SENSOR_DATA_INSERT)) {
            while (running) {
                led.toggle();
                actMinute = LocalDateTime.now().getMinute();
                if (((actMinute > lastMinute) || (lastMinute == 59 && actMinute == 0) && (pressure > 0.0))) {
                    lastMinute = actMinute;
                    // read sensor and add to database
                    List<TemperatureValue> sorted =  temperatureSensorMap.values().parallelStream().sorted(Comparator.comparingDouble(t -> t.getAccuracy())).collect(Collectors.toList());


                    statement.setDouble(1, sorted.isEmpty()?0.0:sorted.get(0).getTemperature());
                    statement.setDouble(2, pressure);
                    statement.setDouble(3, humidity);
                    statement.setDouble(4, illuminance);

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

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: ");
        temperatureSensorMap.put(event.getSensor(),new TemperatureValue(event.getTemperature(),event.getAccuracy()));
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: ");
        pressure = event.getPressure();
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        logger.debug("onReadHumidityEvent: ");
        humidity = event.getHumidity();
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

    public class DisplayTrigger extends GpioTriggerBase {

        @Override
        public void invoke(GpioPin pin, PinState state) {
            switch (state) {
            case HIGH:
                display.on();
                break;
            case LOW:
                display.off();
                break;
            }
        }

    }
}

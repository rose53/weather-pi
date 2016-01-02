package de.rose53.pi.weatherpi;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.pi.weatherpi.common.configuration.StringListConfiguration;
import de.rose53.pi.weatherpi.database.Database;
import de.rose53.pi.weatherpi.database.RowData;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

/**
 *
 * @author rose
 */
@Singleton
public class Datalogger implements MqttCallback, Runnable {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Inject
    Logger logger;

    private boolean running;

    @Inject
    Database database;

    @Inject
    @StringListConfiguration(key = "mqtt.topic")
    List<String> topicFilter;

    @Inject
    MqttClient client;

    private double pressureIndoor = 0;
    private double humidityIndoor = 0;
    private double illuminance = 0;

    private double temperatureIndoor = 0;

    private double humidityOutdoor = 0;
    private double temperatureOutdoor = 0;

    private double humidityBirdhouse = 0;
    private double temperatureBirdhouse = 0;

    final ScheduledExecutorService clientProcessingPool = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r,"Database Inserter");
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });

    public boolean start() {

        System.out.print("Connecting to Mqtt broker ...");
        client.setCallback(this);
        try {
            client.subscribe(topicFilter.toArray(new String[topicFilter.size()]));
        } catch (MqttException e) {
            System.out.println("\b\b\bfailed.");
            logger.error("subscribe",e);
            running = false;
            return running;
        }
        System.out.println("\b\b\bdone.");

        System.out.print("Starting DatabaseTask ...");
        clientProcessingPool.scheduleAtFixedRate(new DatabaseTask(), 1, 1, TimeUnit.MINUTES);
        System.out.println("\b\b\bdone.");

        System.out.println("\n\n ####################################################### ");
        System.out.println(" ####             DATALOGGER IS ALIVE !!!            ### ");
        System.out.println(" ####################################################### ");
        System.out.println(" ### Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return running = true;
    }

    public void stop() {
        if (running) {
            running = false;
        }
        clientProcessingPool.shutdown();
    }

    @Override
    public void run() {
        start();
        while (running) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                logger.error("",e);
            }
        }
    }


    @Override
    public void connectionLost(Throwable e) {
       logger.error("connectionLost:",e);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        for (String topic : token.getTopics()) {
            logger.debug("Message delivered successfully to topic : >{}<",topic);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.debug("messageArrived: topic = >{}<, message = >{}<",topic,new String(message.getPayload()));
        SensorEvent event = mapper.readValue(new String(message.getPayload()), SensorEvent.class);
        if (event == null) {
            return;
        }
        switch (event.getType()) {
        case HUMIDITY:
            HumidityEvent humidityEvent = (HumidityEvent) event;
            switch (humidityEvent.getPlace()) {
            case BIRDHOUSE:
                humidityBirdhouse = humidityEvent.getHumidity();
                break;
            case INDOOR:
                humidityIndoor = humidityEvent.getHumidity();
                break;
            case OUTDOOR:
                humidityOutdoor = humidityEvent.getHumidity();
                break;
            }
            break;
        case ILLUMINANCE:
            IlluminanceEvent illuminanceEvent = (IlluminanceEvent) event;
            switch (event.getPlace()) {
            case INDOOR:
                illuminance = illuminanceEvent.getIlluminance();
                break;
            case OUTDOOR:
            case BIRDHOUSE:
                break;
            }
            break;
        case PRESSURE:
            PressureEvent pressureEvent = (PressureEvent) event;
            switch (event.getPlace()) {
            case INDOOR:
                pressureIndoor = pressureEvent.getPressure();
                break;
            case OUTDOOR:
            case BIRDHOUSE:
                break;
            }
            break;
        case TEMPERATURE:
            TemperatureEvent temperatureEvent = (TemperatureEvent) event;
            switch (event.getPlace()) {
            case INDOOR:
                temperatureIndoor = temperatureEvent.getTemperature();
                break;
            case OUTDOOR:
                temperatureOutdoor = temperatureEvent.getTemperature();
                break;
            case BIRDHOUSE:
                temperatureBirdhouse = temperatureEvent.getTemperature();
                break;
            }
            break;
        }
    }

    private class DatabaseTask implements Runnable {

        @Override
        public void run() {
            try {
                database.insertSensorData(new RowData(temperatureIndoor,pressureIndoor,humidityIndoor,illuminance,temperatureOutdoor,humidityOutdoor,temperatureBirdhouse,humidityBirdhouse));
            } catch (SQLException e) {
                logger.warn("run:",e);
            }
        }
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

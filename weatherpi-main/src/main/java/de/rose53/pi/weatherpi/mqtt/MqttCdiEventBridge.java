package de.rose53.pi.weatherpi.mqtt;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

@Dependent
public class MqttCdiEventBridge {

    @Inject
    Logger logger;

    @Inject
    MqttClient client;

    private static final ObjectMapper mapper = new ObjectMapper();

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        logger.debug("onReadTemperatureEvent: ");
        publish(event);
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        logger.debug("onReadPressureEvent: ");
        publish(event);
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        logger.debug("onReadHumidityEvent: ");
        publish(event);
    }

    public void onReadIlluminanceEvent(@Observes IlluminanceEvent event) {
        logger.debug("onReadIlluminanceEvent: ");
        publish(event);
    }

    synchronized private <T extends SensorEvent> void publish(T event) {
        if (event == null) {
            return;
        }
        // we only want to publish our own events
        if (event.getPlace() != ESensorPlace.INDOOR) {
            return;
        }
        try {
            MqttMessage message = new MqttMessage(mapper.writeValueAsString(event).getBytes());
            message.setQos(0);
            client.publish(getTopic(event),message);
        } catch (MqttException | JsonProcessingException e) {
            logger.error("publish:",e);
        }
    }

    static private <T extends SensorEvent> String getTopic(T event) {

        StringBuilder builder = new StringBuilder("sensordata");

        builder.append('/')
               .append(event.getPlace())
               .append('/')
               .append(event.getType());
        return builder.toString().toLowerCase();
    }
}

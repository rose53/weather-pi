package de.rose53.weatherpi.mqtt.control;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.events.WindspeedEvent;
import de.rose53.weatherpi.configuration.StringListConfiguration;

@Singleton
@Startup
public class MqttService implements MqttCallback {

    @Inject
    Logger logger;

    @Inject
    @StringListConfiguration(key = "mqtt.topic")
    List<String> topicFilter;

    @Inject
    MqttClient client;

    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    Event<TemperatureEvent> temperatureEvent;

    @Inject
    Event<PressureEvent> pressureEvent;

    @Inject
    Event<IlluminanceEvent> illuminanceEvent;

    @Inject
    Event<HumidityEvent> humidityEvent;

    @Inject
    Event<WindspeedEvent> windspeedEvent;

    @PostConstruct
    public void subscribe() {
        client.setCallback(this);
        try {
            client.subscribe(topicFilter.toArray(new String[topicFilter.size()]));
        } catch (MqttException e) {
            logger.error("subscribe",e);
        }
    }

    @Override
    public void connectionLost(Throwable e) {
        logger.error("connectionLost:",e);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        logger.debug("deliveryComplete:");

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
            humidityEvent.fire((HumidityEvent) event);
            break;
        case ILLUMINANCE:
            illuminanceEvent.fire((IlluminanceEvent) event);
            break;
        case PRESSURE:
            pressureEvent.fire((PressureEvent) event);
            break;
        case TEMPERATURE:
            temperatureEvent.fire((TemperatureEvent) event);
            break;
        case WINDSPEED:
            windspeedEvent.fire((WindspeedEvent)event);
            break;
        }
    }
}

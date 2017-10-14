package de.rose53.weatherpi.mqtt.control;

import java.io.StringReader;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;

import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.events.WindspeedEvent;
import de.rose53.weatherpi.configuration.StringConfiguration;
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
    @StringConfiguration(key="mqtt.serverURI",defaultValue="tcp://localhost:1883")
    String mqttServerURI;

    @Inject
    @StringConfiguration(key="mqtt.username")
    String mqttUserName;

    @Inject
    @StringConfiguration(key="mqtt.password")
    String mqttPassword;

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

    MqttClient client = null;


    @PostConstruct
    public void subscribe() {
        try {
            client = new MqttClient(mqttServerURI,MqttClient.generateClientId(),new MemoryPersistence());
            connect();
        } catch (MqttException e) {
            logger.error("subscribe:",e);
            client = null;
        }
    }

    @PreDestroy
    public void disconnect() {
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (MqttException e) {
                logger.error("disconnect",e);
            }
        }
    }

    private void connect() throws MqttException {
        if (client == null) {
            return;
        }
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttUserName);
        options.setPassword(mqttPassword.toCharArray());
        options.setCleanSession(true);
        client.setCallback(this);
        client.connect(options);
        client.subscribe(topicFilter.toArray(new String[topicFilter.size()]));
    }

    @Override
    public void connectionLost(Throwable e) {
        logger.error("connectionLost:",e);
        try {
            connect();
        } catch (MqttException e1) {
            logger.error("connectionLost:",e1);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        logger.debug("deliveryComplete:");

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.debug("messageArrived: topic = >{}<, message = >{}<",topic,new String(message.getPayload()));

        JsonObject object = Json.createReader(new StringReader(new String(message.getPayload()))).readObject();
        if (object == null) {
            logger.error("messageArrived: unable to build JSON object from >{}<",new String(message.getPayload()));
            return;
        }

        if (object.isNull("place") || object.isNull("sensor") || object.isNull("type")) {
            logger.error("messageArrived: missing required field in >{}<",new String(message.getPayload()));
            return;
        }

        SensorEvent event = SensorEvent.build(object);
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

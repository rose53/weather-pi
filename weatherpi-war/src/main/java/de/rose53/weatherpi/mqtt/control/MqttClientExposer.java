package de.rose53.weatherpi.mqtt.control;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;

import de.rose53.weatherpi.configuration.StringConfiguration;

public class MqttClientExposer {

    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key="mqtt.serverURI",defaultValue="tcp://localhost:1883")
    String mqttServerURI;

    @Inject
    @StringConfiguration(key="mqtt.username")
    String mqttUserName;

    @Inject
    @StringConfiguration(key="mqtt.password")
    String mqttPassword;

    @Produces
    public MqttClient expose() {
        MqttClient client = null;
        try {
            client = new MqttClient(mqttServerURI,MqttClient.generateClientId(),new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUserName);
            options.setPassword(mqttPassword.toCharArray());
            options.setCleanSession(true);
            client.connect(options);
        } catch (MqttException e) {
            logger.error("expose",e);
        }
        return client;
    }

    public void closeConnection(@Disposes MqttClient client) {
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (MqttException e) {
                logger.error("closeConnection",e);
            }
        }
    }
}

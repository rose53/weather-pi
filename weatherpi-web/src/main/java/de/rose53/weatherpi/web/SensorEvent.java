package de.rose53.weatherpi.web;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

@WebSocket
public class SensorEvent {

    private static final Set<SensorEvent> connections = new CopyOnWriteArraySet<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<String, TemperatureEvent> temperatureSensorMap = new HashMap<>();

    @Inject
    Logger logger;

    private Session session;

    // called when the socket connection with the browser is established
    @OnWebSocketConnect
    public void handleConnect(Session session) {
        this.session = session;
        connections.add(this);
    }

    // called when the connection closed
    @OnWebSocketClose
    public void handleClose(int statusCode, String reason) {
        logger.debug("handleClose: closed with statusCode = {}, reason = {}",statusCode,reason);
        connections.remove(this);
    }

    @OnWebSocketError
    public void handleError(Throwable e) {
        logger.error("handleError: ",e);
    }

    private synchronized <T> void send(T event) {
        String message;
        try {
            message = mapper.writeValueAsString(event);
        } catch (IOException e) {
            logger.error("onReadIlluminanceEvent: ",e);
            return;
        }
        for (SensorEvent client : connections) {
            try {
                if (client.session.isOpen()) {
                    client.session.getRemote().sendString(message);
                } else {
                    connections.remove(client);
                }
            } catch (IOException e) {
                logger.info("send: got an exception, removing client");
                connections.remove(client);
                client.session.close();
            }
        }
    }

    // closes the socket
    private void stop() {
        connections.remove(this);
        try {
            session.disconnect();
        } catch (IOException e) {
            logger.error("stop: ",e);
        }
    }

    public void onReadIlluminanceEvent(@Observes IlluminanceEvent event) {
        send(event);
    }

    public void onReadTemperatureEvent(@Observes TemperatureEvent event) {
        switch (event.getPlace()) {
        case INDOOR:
            temperatureSensorMap.put(event.getSensor(),event);
            List<TemperatureEvent> sorted =  temperatureSensorMap.values().parallelStream().sorted(Comparator.comparingDouble(t -> t.getAccuracy())).collect(Collectors.toList());
            send(sorted.get(0));
            break;
        case OUTDOOR:
        case BIRDHOUSE:
            send(event);
            break;
        default:
            break;
        }
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        send(event);
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        send(event);
    }
}

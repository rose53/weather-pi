package de.rose53.weatherpi.sensordata.boundary;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

@ApplicationScoped
@ServerEndpoint("/sensorevents")
public class SensorEventEndpoint {

    @Inject
    Logger logger;

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<String, TemperatureEvent> temperatureSensorMap = new HashMap<>();

    @OnOpen
    public void open(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void close(Session session) {
        sessions.remove(session);
    }

    private synchronized <T> void send(T event) {
        String message;
        try {
            message = mapper.writeValueAsString(event);
        } catch (IOException e) {
            logger.error("onReadIlluminanceEvent: ",e);
            return;
        }
        for (Session session : sessions) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                } else {
                    sessions.remove(session);
                }
            } catch (IOException e) {
                logger.info("send: got an exception, removing client");
                sessions.remove(session);
                try {
                    session.close();
                } catch (IOException e1) {
                    logger.info("send: got an exception, closing session");
                }
            }
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

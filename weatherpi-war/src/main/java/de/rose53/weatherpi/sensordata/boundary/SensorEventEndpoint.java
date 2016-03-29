package de.rose53.weatherpi.sensordata.boundary;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

@ApplicationScoped
@ServerEndpoint("/sensorevents")
public class SensorEventEndpoint {

    @Inject
    Logger logger;

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void open(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void close(Session session) {
        sessions.remove(session);
    }

    private synchronized <T extends SensorEvent> void send(T event) {
        if (sessions.isEmpty()) {
            logger.debug("send: no websocket session active, nothing to do");
            return;
        }
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
        send(event);
    }

    public void onReadPressureEvent(@Observes PressureEvent event) {
        send(event);
    }

    public void onReadHumidityEvent(@Observes HumidityEvent event) {
        send(event);
    }
}

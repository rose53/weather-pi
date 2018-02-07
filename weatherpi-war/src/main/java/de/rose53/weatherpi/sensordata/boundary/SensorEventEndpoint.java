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

import de.rose53.pi.weatherpi.events.SensorEvent;

@ApplicationScoped
@ServerEndpoint("/sensorevents")
public class SensorEventEndpoint {

    @Inject
    Logger logger;

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void open(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void close(Session session) {
        sessions.remove(session);
    }

    public <T extends SensorEvent> void onReadSensorEvent(@Observes T event) {
        if (sessions.isEmpty()) {
            logger.debug("send: no websocket session active, nothing to do");
            return;
        }
        String message = event.toJson().toString();
        logger.debug("send: message = >{}<",message);
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
}

package de.rose53.weatherpi.sensordata.boundary;

import static de.rose53.pi.weatherpi.common.ESensorPlace.ANEMOMETER;
import static de.rose53.pi.weatherpi.common.ESensorType.WINDSPEED;
import static de.rose53.pi.weatherpi.common.ESensorType.WINDDIRECTION;
import static de.rose53.weatherpi.sensordata.boundary.ERange.ACTUAL;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.Winddirection;
import de.rose53.pi.weatherpi.events.WinddirectionEvent;
import de.rose53.pi.weatherpi.events.WindspeedEvent;
import de.rose53.weatherpi.sensordata.entity.DataBean;

@Singleton
@Startup
public class WindspeedFilterService {

    @Inject
    Logger logger;

    @Inject
    SensorDataService sensorDataService;

    List<WindspeedEvent>     windspeedEvents     = new LinkedList<>();
    List<WinddirectionEvent> winddirectionEvents = new LinkedList<>();

    public void onReadWindspeedEvent(@Observes WindspeedEvent event) {
        logger.debug("onReadWindspeedEvent: got event");
        windspeedEvents.add(event);
    }

    public void onReadWinddirectionEvent(@Observes WinddirectionEvent event) {
        logger.debug("onReadWinddirectionEvent: got event");
        winddirectionEvents.add(event);
    }

    public Double getLatestWindspeed() {
        logger.debug("getLatestWindspeed: reading latest windspeed");
        if (!windspeedEvents.isEmpty()) {
            return Math.round(windspeedEvents.get(windspeedEvents.size() - 1).getValue()*100.0)/100.0;
        }
        // check the db if there is some data
        logger.debug("getLatestWindspeed: events list is empty, we have to ask the database");
        List<DataBean> sensorData = sensorDataService.getSensorData("ELTAKO_WS",WINDSPEED,ANEMOMETER,ACTUAL);
        if (sensorData == null || sensorData.isEmpty()) {
            logger.debug("getLatestWindspeed: database returned no value, returning ");
            return null;
        }
        return sensorData.get(0).getValue();
    }

    public Double getLatestWinddirection() {
        logger.debug("getLatestWinddirection: reading latest winddirection");
        if (!winddirectionEvents.isEmpty()) {
            return winddirectionEvents.get(winddirectionEvents.size() - 1).getValue();
        }
        // check the db if there is some data
        logger.debug("getLatestWinddirection: events list is empty, we have to ask the database");
        List<DataBean> sensorData = sensorDataService.getSensorData("WIND_VANE",WINDDIRECTION,ANEMOMETER,ACTUAL);
        if (sensorData == null || sensorData.isEmpty()) {
            logger.debug("getLatestWinddirection: database returned no value, returning ");
            return null;
        }
        return sensorData.get(0).getValue();
    }

    @Schedule(second="0", minute="*",hour="*", persistent=false)
    public void filterData(){
        logger.debug("filterData: #windspeed  = >{}<, #winddirection  = >{}<",windspeedEvents.size(),winddirectionEvents.size());
        double averageWindspeed = windspeedEvents.stream().mapToDouble(WindspeedEvent::getValue).average().orElse(0.0);
        logger.debug("filterData: average windspeed = >{}<",averageWindspeed);
        sensorDataService.persistData(new WindspeedEvent(ANEMOMETER, "ELTAKO_WS", Math.round(averageWindspeed*100.0)/100.0));
        windspeedEvents.clear();
        double averageWinddirection = winddirectionEvents.stream().mapToDouble(WinddirectionEvent::getValue).average().orElse(0.0);
        logger.debug("filterData: average winddirection = >{}<",averageWinddirection);
        sensorDataService.persistData(new WinddirectionEvent(ANEMOMETER, "WIND_VANE", Winddirection.fromDegrees(averageWinddirection).getDegree()));
        windspeedEvents.clear();

    }
}

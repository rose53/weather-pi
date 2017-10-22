package de.rose53.weatherpi.sensordata.boundary;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.events.WindspeedEvent;

@Singleton
@Startup
public class WindspeedFilterService {

    @Inject
    Logger logger;

    @Inject
    SensorDataService sensorDataService;

    List<WindspeedEvent> events = new LinkedList<>();

    public void onReadWindspeedEvent(@Observes WindspeedEvent event) {
        logger.debug("onReadWindspeedEvent: got event");
        events.add(event);
    }


    @Schedule(second="0", minute="*",hour="*", persistent=false)
    public void filterData(){
        logger.debug("filterData: #events = >{}<",events.size());
        double average = events.stream().mapToDouble(WindspeedEvent::getValue).average().orElse(0.0);
        logger.debug("filterData: average windspeed = >{}<",average);
        sensorDataService.persistData(new WindspeedEvent(ESensorPlace.ANEMOMETER, "ELTAKO_WS", average));
        events.clear();
    }
}

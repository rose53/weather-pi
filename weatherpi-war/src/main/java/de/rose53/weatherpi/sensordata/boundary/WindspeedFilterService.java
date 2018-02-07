package de.rose53.weatherpi.sensordata.boundary;

import static de.rose53.pi.weatherpi.common.ESensorPlace.ANEMOMETER;
import static de.rose53.pi.weatherpi.common.ESensorType.WINDSPEED;
import static de.rose53.weatherpi.sensordata.boundary.ERange.ACTUAL;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.events.WindspeedEvent;
import de.rose53.weatherpi.sensordata.entity.DataBean;

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

    public Double getLatestWindspeed() {
        logger.debug("getLatestWindspeed: reading latest windspeed");
        if (!events.isEmpty()) {
            return Math.round(events.get(events.size() - 1).getValue()*100.0)/100.0;
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

    @Schedule(second="0", minute="*",hour="*", persistent=false)
    public void filterData(){
        logger.debug("filterData: #events = >{}<",events.size());
        double average = events.stream().mapToDouble(WindspeedEvent::getValue).average().orElse(0.0);
        logger.debug("filterData: average windspeed = >{}<",average);
        sensorDataService.persistData(new WindspeedEvent(ANEMOMETER, "ELTAKO_WS", Math.round(average*100.0)/100.0));
        events.clear();
    }
}

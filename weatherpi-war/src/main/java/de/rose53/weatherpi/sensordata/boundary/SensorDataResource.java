package de.rose53.weatherpi.sensordata.boundary;

import java.time.format.DateTimeFormatter;

import static java.util.Arrays.*;
import static java.util.Comparator.comparingDouble;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;
import de.rose53.pi.weatherpi.common.MovingAverage;
import de.rose53.pi.weatherpi.common.SensorData;
import de.rose53.weatherpi.sensordata.entity.DataBean;


@Stateless
@Path("/sensordata")
@Produces(MediaType.APPLICATION_JSON)
public class SensorDataResource {

    @Inject
    SensorDataService sensorDataService;

    @Inject
    MovingAverage movingAverage;

    @GET
    @Path("/{place}/{name}/{type}/{range}")
    public Response getSensorData(@PathParam("place") String place,
                                  @PathParam("name") String name,
                                  @PathParam("type") String type,
                                  @PathParam("range") String range,
                                  @QueryParam("samples") Integer samples,
                                  @QueryParam("movingAverage") @DefaultValue(value = "false") boolean movingAverageFlag) {

        ERange r = ERange.fromString(range);

        List<? extends SensorData> sensorData = sensorDataService.getSensorData(name,ESensorType.fromString(type),ESensorPlace.fromString(place),r.getRange(movingAverageFlag));

        if (movingAverageFlag) {
            sensorData = movingAverage.calculate(sensorData,r.getMovingAverageMinutes());
        }

        if (samples != null && samples > 0 && sensorData.size() > samples) {

            SensorData[] data =  sensorData.toArray(new SensorData[sensorData.size()]);

            // reduce the number of samples
            int window = data.length / samples;
            if ((window & 1) == 0) {
                window--;
            }
            List<SensorData> reducedData = new LinkedList<>();
            reducedData.add(data[0]); // add first element

            SensorData[] windowData;
            for (int i = 1; i + window < data.length; i+= window) {
                windowData = copyOfRange(data,i,i+window);
                sort(windowData, (a,b) -> Double.compare(a.getValue(), b.getValue()));
                reducedData.add(windowData[window/2]);
            }
            sensorData = reducedData;
        }
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        if (sensorData.isEmpty()) {
            objectBuilder.add("maxValue", 0.0);
            objectBuilder.add("minValue", 0.0);
            objectBuilder.add("sensorData",Json.createArrayBuilder().build());
        } else {
            objectBuilder.add("maxValue", sensorData.stream().max(comparingDouble(data -> data.getValue())).get().getValue());
            objectBuilder.add("minValue", sensorData.stream().min(comparingDouble(data -> data.getValue())).get().getValue());
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            sensorData.forEach(s -> arrayBuilder.add(Json.createObjectBuilder()
                                                         .add("value", s.getValue())
                                                         .add("time", s.getLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
            objectBuilder.add("sensorData",arrayBuilder);
        }

        return Response.ok(objectBuilder.build().toString(),MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("/{device}/{sensor}/{type}")
    public Response data(@PathParam("device") String device, @PathParam("sensor") String sensor, @PathParam("type") String type) {

        DataBean dataBean = sensorDataService.getLatestSensorData(device,sensor,ESensorType.fromString(type));
        if (dataBean == null) {
            return Response.noContent().build();
        }
        return Response.ok(Json.createObjectBuilder()
                               .add("value", dataBean.getValue())
                               .add("time", dataBean.getLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build().toString(),
                           MediaType.APPLICATION_JSON).build();
    }
}

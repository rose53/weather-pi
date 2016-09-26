package de.rose53.weatherpi.sensordata.boundary;

import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;
import de.rose53.weatherpi.sensordata.entity.DataBean;


@Stateless
@Path("/sensordata")
@Produces(MediaType.APPLICATION_JSON)
public class SensorDataResource {

    @Inject
    SensorDataService sensorDataService;

 
    @GET
    @Path("/{place}/{name}/{type}/{range}")
    public Response getSensorData(@PathParam("place") String place, 
                                  @PathParam("name") String name, 
                                  @PathParam("type") String type,
                                  @PathParam("range") String range,
                                  @QueryParam("samples") Integer samples) {

        List<DataBean> sensorData = sensorDataService.getSensorData(name,ESensorType.fromString(type),ESensorPlace.fromString(place),ERange.fromString(range));
        

        List<SensorDataQueryResult> ensorDataQueryResultList = sensorData.stream().map(s -> new SensorDataQueryResult(s)).collect(toList());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String retVal = null;
        try {
            if (samples != null && samples > 0 && sensorData.size() > samples) {

                SensorDataQueryResult[] data = ensorDataQueryResultList.toArray(new SensorDataQueryResult[ensorDataQueryResultList.size()]);
                // reduce the number of samples
                int window = data.length / samples;
                if ((window & 1) == 0) {
                    window--;
                }
                List<SensorDataQueryResult> reducedData = new LinkedList<>();
                reducedData.add(data[0]); // add first element

                SensorDataQueryResult[] windowData;
                for (int i = 1; i + window < data.length; i+= window) {
                    windowData = copyOfRange(data,i,i+window);
                    sort(windowData, (a,b) -> Double.compare(a.getValue(), b.getValue()));
                    reducedData.add(windowData[window/2]);
                }
                retVal = mapper.writeValueAsString(new SensorDataQueryResponse(reducedData));
            }  else {
                retVal = mapper.writeValueAsString(new SensorDataQueryResponse(ensorDataQueryResultList));
            }
        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
        return Response.ok(retVal,MediaType.APPLICATION_JSON).build();
    }

 
    @GET
    @Path("/{device}/{sensor}/{type}")
    public Response data(@PathParam("device") String device, @PathParam("sensor") String sensor, @PathParam("type") String type) {

        DataBean dataBean = sensorDataService.getLatestSensorData(device,sensor,ESensorType.fromString(type));
        if (dataBean == null) {
            return Response.noContent().build();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String retVal = null;
        try {
            retVal = mapper.writeValueAsString(new SensorDataQueryResult(dataBean));
        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
        return Response.ok(retVal,MediaType.APPLICATION_JSON).build();
    }
}

package de.rose53.weatherpi.web;

import java.util.List;

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

import de.rose53.pi.weatherpi.database.SensorDataQueryResult;

@Path("/sensordata")
@Produces(MediaType.APPLICATION_JSON)
public class SensorDataResource {

    @GET
    @Path("/{sensor}")
    public Response getSensorData(@PathParam("sensor") String sensor,
                                  @QueryParam("place") @DefaultValue("indoor") String place,
                                  @QueryParam("range") String range,
                                  @QueryParam("samples") Integer samples) {


        List<SensorDataQueryResult> data = SensorDataCdiHelper.instance.getSensorData(sensor,place,range);

        if (samples != null && samples > 0) {

        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String retVal = null;
        try {
            retVal = mapper.writeValueAsString(new SensorDataQueryResponse(data));
        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
        return Response.ok(retVal,MediaType.APPLICATION_JSON).build();
    }
}

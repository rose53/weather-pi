package de.rose53.weatherpi.forecast.boundary;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.rose53.weatherpi.forecast.control.ForecastService;

@Path("/forecast")
@Produces(MediaType.APPLICATION_JSON)
public class ForecastResource {

    @Inject
    Logger logger;

    @Inject
    ForecastService forecastService;

    @GET
    @Path("/daily")
    public Response getDaily() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String retVal;
        try {
            retVal = mapper.writeValueAsString(stream(forecastService.getDaily().getData()).map(d -> new ForecastDailyRespone(d,forecastService.getZoneId()))
                                                                                           .collect(toList()));
        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
        return Response.ok(retVal,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/currently")
    public Response getCurrently() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String retVal;
        try {
            retVal = mapper.writeValueAsString(forecastService.getCurrently());
        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
        return Response.ok(retVal,MediaType.APPLICATION_JSON).build();
    }
}

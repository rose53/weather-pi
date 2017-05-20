package de.rose53.weatherpi.forecast.boundary;

import static java.util.Arrays.stream;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

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

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        ZoneId zoneId = forecastService.getZoneId();

        stream(forecastService.getDaily().getData()).forEach(p -> {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            p.toJson(objectBuilder, DateTimeFormatter.ISO_LOCAL_DATE_TIME, zoneId);
            arrayBuilder.add(objectBuilder.build());
        });
        return Response.ok(arrayBuilder.build().toString(),MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/currently")
    public Response getCurrently() {

        ZoneId zoneId = forecastService.getZoneId();

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        forecastService.getCurrently().toJson(objectBuilder, DateTimeFormatter.ISO_LOCAL_DATE_TIME, zoneId);
        return Response.ok(objectBuilder.build().toString(),MediaType.APPLICATION_JSON).build();
    }
}

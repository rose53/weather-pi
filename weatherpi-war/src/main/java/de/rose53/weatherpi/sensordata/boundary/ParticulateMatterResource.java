package de.rose53.weatherpi.sensordata.boundary;

import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

@Stateless
@Path("/particulatematter")
@Produces(MediaType.APPLICATION_JSON)
public class ParticulateMatterResource {


    @Inject
    Logger logger;

    @Inject
    ParticulateMatterService service;

    @GET
    public Response particulatematter(@QueryParam("compensate") boolean compensate) {

        Double pm10 = service.getLatestPM10(compensate);
        Double pm25 = service.getLatestPM25(compensate);

        if (pm10 == null && pm25 == null) {
            logger.debug("particulatematter: returning no content.");
            return noContent().build();
        }
        logger.debug("particulatematter: actual values: pm10: {} µg/m³, pm25: {} µg/m³",pm10,pm25);
        return ok(createObjectBuilder()
                  .add("pm10", pm10)
                  .add("pm25", pm25)
                  .build().toString()).build();
    }
}

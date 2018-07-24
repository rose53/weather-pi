package de.rose53.weatherpi.sensordata.boundary;

import static javax.ws.rs.core.Response.*;
import static javax.json.Json.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.WindspeedUnit;

@Stateless
@Path("/windspeed")
@Produces(MediaType.APPLICATION_JSON)
public class WindspeedResource {

    @Inject
    Logger logger;

    @Inject
    WindspeedFilterService service;

    @GET
    public Response windspeed(@DefaultValue("ms") @QueryParam("unit") WindspeedUnit windspeedUnit) {

        // windspeed in m/s
        Double windspeed = service.getLatestWindspeed();
        if (windspeed == null) {
            logger.debug("windspeed: returning no content.");
            return noContent().build();
        }
        logger.debug("windspeed: actual value: {} m/s",windspeed);
        return ok(createObjectBuilder()
                  .add("windspeed", windspeedUnit.fromMS(windspeed))
                  .add("description", WindspeedUnit.getDescription(windspeed, WindspeedUnit.MS))
                  .build().toString()).build();
    }
}

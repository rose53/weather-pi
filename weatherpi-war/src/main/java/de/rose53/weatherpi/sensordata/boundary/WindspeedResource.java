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

import de.rose53.pi.weatherpi.common.WindspeedUnit;

@Stateless
@Path("/windspeed")
@Produces(MediaType.APPLICATION_JSON)
public class WindspeedResource {

    @Inject
    WindspeedFilterService service;

    @GET
    public Response windspeed(@DefaultValue("ms") @QueryParam("unit") String unitString) {

        WindspeedUnit windspeedUnit = WindspeedUnit.fromString(unitString);

        // windspeed in m/s
        double windspeed = service.getLatestWindspeed();

        return ok(createObjectBuilder()
                  .add("windspeed", windspeedUnit.fromMS(windspeed))
                  .add("description", WindspeedUnit.getDescription(windspeed, WindspeedUnit.MS))
                  .build().toString()).build();
    }
}

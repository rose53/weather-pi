package de.rose53.weatherpi.sensordata.boundary;

import static javax.ws.rs.core.Response.*;
import static javax.json.Json.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.Winddirection;

@Stateless
@Path("/winddirection")
@Produces(MediaType.APPLICATION_JSON)
public class WinddirectionResource {

    @Inject
    Logger logger;

    @Inject
    WindspeedFilterService service;

    @GET
    public Response winddirection() {

        // winddirection
        Double winddirection = service.getLatestWinddirection();
        if (winddirection == null) {
            logger.debug("winddirection: returning no content.");
            return noContent().build();
        }
        logger.debug("winddirection: actual value: {}",winddirection);
        return ok(createObjectBuilder()
                  .add("winddirection", winddirection)
                  .add("description", Winddirection.fromDegrees(winddirection).getDirectionName())
                  .build().toString()).build();
    }
}

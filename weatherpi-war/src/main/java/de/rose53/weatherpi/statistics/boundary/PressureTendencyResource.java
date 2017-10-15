package de.rose53.weatherpi.statistics.boundary;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import de.rose53.weatherpi.statistics.control.PressureTendencyService;

@Path("/pressuretendency")
public class PressureTendencyResource {

    @Inject
    Logger logger;

    @Inject
    PressureTendencyService pressureTendencyService;

    @GET
    @Produces(APPLICATION_JSON)
    public Response pressureTendency() {

        Double pressureTendency = pressureTendencyService.getPressureTendency();
        if (pressureTendency == null) {
            return Response.noContent().build();
        }
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        objectBuilder.add("pressureTendency", pressureTendency);
        objectBuilder.add("meaning", getPressureTendency(pressureTendency));

        return Response.ok(objectBuilder.build().toString(),APPLICATION_JSON).build();
    }

    private String getPressureTendency(double pressureTendency) {

        double abs = Math.abs(pressureTendency);

        String prefix = Math.signum(pressureTendency) > 0?"rising":"falling";

        String suffix = null;
        if (abs < 0.1d) {
            suffix = "more slowly";
        } else if (0.1d <= abs && abs < 1.5d) {
            suffix = "slowly";
        } else if (1.5d <= abs && abs < 3.5d) {
            suffix = "";
        } else if (3.5d <= abs && abs < 6.0d) {
            suffix = "quickly";
        } else {
            suffix = "very rapidly";
        }
        return prefix + " " + suffix;
    }
}

package de.rose53.weatherpi.statistics.boundary;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.LocalDate;

import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.rose53.Zambretti;
import de.rose53.weatherpi.statistics.control.PressureTendencyService;

@Path("/zambretti")
public class ZambrettiResource {

    @Inject
    PressureTendencyService pressureTendencyService;

    @GET
    @Produces(APPLICATION_JSON)
    public Response pressureTendency() {

        Double actualPressure   = pressureTendencyService.getActualPressure();
        Double pressureTendency = pressureTendencyService.getPressureTendency();
        if (actualPressure == null || pressureTendency == null) {
            return Response.noContent().build();
        }

        Zambretti zambretti = new Zambretti();
        return Response.ok(Json.createObjectBuilder()
                               .add("zambretti", zambretti.forecast(pressureTendency, actualPressure, LocalDate.now().getMonth()))
                               .build()
                               .toString(),
                           APPLICATION_JSON)
                       .build();
    }

}

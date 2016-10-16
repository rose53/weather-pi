package de.rose53.weatherpi.configuration.boundary;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/configurations")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationResource {

    @Inject
    ConfigurationService configurationService;

    @GET
    @Path("/string/{key}")
    public Response stringConfiguration(@PathParam("key") String key) {

        String result = configurationService.getValue(key);
        if (result == null) {
            return Response.noContent().build();
        }
        return Response.ok(result,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/stringlist/{key}")
    public Response stringListConfiguration(@PathParam("key") String key) {

        List<String> result = configurationService.getValues(key);
        if (result.isEmpty()) {
            return Response.noContent().build();
        }
        GenericEntity<List<String>> list = new GenericEntity<List<String>>(result) {};
        return Response.ok(list,MediaType.APPLICATION_JSON).build();
    }

}

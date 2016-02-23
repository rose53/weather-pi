package de.rose53.weatherpi.sensordata.boundary;

import java.util.List;
import static java.util.stream.Collectors.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.rose53.weatherpi.sensordata.entity.DeviceBean;

@Stateless
@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DevicesResource {

    @Inject
    DeviceServiceBean deviceService;

    @GET
    public Response devices() {

        List<Device> devices = deviceService.getDevices()
                                            .stream()
                                            .map(Device::new)
                                            .collect(toList());

        GenericEntity<List<Device>> list = new GenericEntity<List<Device>>(devices) {};
        return Response.ok(list,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("{device}/sensors")
    public Response sensors(@PathParam("device") String device) {
        DeviceBean deviceBean = deviceService.getDevice(device);
        if (deviceBean == null) {
            return Response.noContent().build();
        }
        List<Sensor> sensors = deviceBean.getSensors()
                                         .stream()
                                         .map(Sensor::new)
                                         .collect(toList());

        GenericEntity<List<Sensor>> list = new GenericEntity<List<Sensor>>(sensors) {};
        return Response.ok(list,MediaType.APPLICATION_JSON).build();
    }
}

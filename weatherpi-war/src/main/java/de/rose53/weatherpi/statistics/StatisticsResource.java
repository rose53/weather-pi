package de.rose53.weatherpi.statistics;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.EClimatologicClassificationDay;
import de.rose53.weatherpi.statistics.boundary.DayStatisticsService;
import de.rose53.weatherpi.statistics.control.DayStatisticsCalculatorService;
import de.rose53.weatherpi.statistics.control.StatisticsCalculatorService;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

@Path("/stat")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsResource {

    @Inject
    Logger logger;

    @Inject
    DayStatisticsService dayStatisticsService;

    @Inject
    StatisticsCalculatorService statisticsCalculatorService;

    @Inject
    DayStatisticsCalculatorService dayStatisticsCalculatorService;

    @GET
    @Path("/calc/{year}/{month}/{day}")
    public Response calc(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {

        LocalDate calcDay = LocalDate.of(year, month, day);

        DayStatisticBean sensorData = dayStatisticsService.create(calcDay);

        return Response.ok(sensorData,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/climatologicclassificationday/{year}/{month}/{day}")
    public Response climatologicClassificationDay(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {

        LocalDate ccDay = LocalDate.of(year, month, day);

        List<EClimatologicClassificationDay> climatologicClassification = dayStatisticsService.getClimatologicClassification(ccDay);

        GenericEntity<List<EClimatologicClassificationDay>> list = new GenericEntity<List<EClimatologicClassificationDay>>(climatologicClassification) {};
        return Response.ok(list,MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/generate/{year}")
    @Produces(MediaType.APPLICATION_SVG_XML)
    public Response generate(@PathParam("year") int year) {

        String retVal = statisticsCalculatorService.graph(year);

        return Response.ok(retVal,MediaType.APPLICATION_SVG_XML).build();
    }
}

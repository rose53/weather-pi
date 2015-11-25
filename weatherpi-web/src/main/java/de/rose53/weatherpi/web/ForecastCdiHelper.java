package de.rose53.weatherpi.web;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;

import java.util.List;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.forecast.Daily;
import de.rose53.pi.weatherpi.forecast.Forecast;

// TODO change, if I will get CDI working in JAX RS resources
public class ForecastCdiHelper {

    static ForecastCdiHelper instance;

    @Inject
    Logger logger;

    @Inject
    Forecast forecast;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public List<LocalDateTime> getDailyTimes() {

        Daily daily = forecast.getDaily();
        return stream(daily.getData()).map(d -> forecast.getTimeAsDateTime(d.getTime()))
                                      .collect(toList());
    }

    public List<String> getDailyIcons() {

        Daily daily = forecast.getDaily();
        return stream(daily.getData()).map(d -> d.getIcon())
                                      .collect(toList());
    }

    public List<ForecastDailyRespone> getDaily() {

        return stream(forecast.getDaily().getData()).map(d -> new ForecastDailyRespone(d,forecast.getZoneId()))
                                                    .collect(toList());
    }

    public List<ForecastCurrentlyRespone> getCurrently() {

        return stream(forecast.getDaily().getData()).map(d -> new ForecastCurrentlyRespone(d,forecast.getZoneId()))
                                                    .collect(toList());
    }
}

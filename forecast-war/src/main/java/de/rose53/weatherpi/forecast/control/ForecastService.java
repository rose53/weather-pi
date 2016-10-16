package de.rose53.weatherpi.forecast.control;

import java.io.IOException;
import java.time.ZoneId;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.forecast.Currently;
import de.rose53.pi.weatherpi.forecast.Daily;
import de.rose53.pi.weatherpi.forecast.ForecastIO;
import de.rose53.pi.weatherpi.forecast.Forecastdata;
import de.rose53.weatherpi.configuration.StringConfiguration;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ForecastService {

    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key = "forecast.apiKey")
    private String apiKey;

    @Inject
    @StringConfiguration(key = "forecast.latitude",defaultValue = "48.5204")
    private String latitude;

    @Inject
    @StringConfiguration(key = "forecast.longitude", defaultValue = "9.0491")
    private String longitude;

    @Inject
    @StringConfiguration(key = "forecast.units", defaultValue = "si")
    private String units;

    @Inject
    @StringConfiguration(key = "forecast.lang", defaultValue = "de")
    private String lang;

    Forecastdata data;


    private synchronized void updateForecastdata() throws IOException {
        ForecastIO.Builder builder = new ForecastIO.Builder(apiKey,latitude,longitude);

        ForecastIO forecast = builder.units(units)
                                     .lang(lang)
                                     .build();

        data = forecast.getForecastdata();
    }

    @PostConstruct
    public void init() {
        try {
            updateForecastdata();
        } catch (IOException e) {
            logger.error("getCurrently:",e);
        }
    }

    @Schedule(second="0", minute="0",hour="*/1", persistent=false)
    public void updateTimer(){
        try {
            updateForecastdata();
        } catch (IOException e) {
            logger.error("updateTimer:",e);
        }
    }

    public ZoneId getZoneId() {
        if (data == null) {
            return null;
        }
        return ZoneId.of(data.getTimezone());
    }

    public Currently getCurrently() {
        if (data == null) {
            return null;
        }
        return data.getCurrently();
    }

    public Daily getDaily() {
        if (data == null) {
            return null;
        }
        return data.getDaily();
    }

}

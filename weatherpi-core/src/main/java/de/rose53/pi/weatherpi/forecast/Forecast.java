package de.rose53.pi.weatherpi.forecast;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.utils.StringConfiguration;

@ApplicationScoped
public class Forecast {

    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key = "forecast.apiKey", mandatory = true)
    private String apiKey;

    @Inject
    @StringConfiguration(key = "forecast.latitude", mandatory = true, defaultValue = "48.5204")
    private String latitude;

    @Inject
    @StringConfiguration(key = "forecast.longitude", mandatory = true, defaultValue = "9.0491")
    private String longitude;

    @Inject
    @StringConfiguration(key = "forecast.units", defaultValue = "si")
    private String units;

    @Inject
    @StringConfiguration(key = "forecast.lang", defaultValue = "de")
    private String lang;


    Forecastdata data;

    final ScheduledExecutorService clientProcessingPool = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r,"ForecastReader");
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });

    @PostConstruct
    public void init() {
        clientProcessingPool.scheduleAtFixedRate(new ReadForecastDataTask(), 1, 1, TimeUnit.HOURS);
    }

    private Forecastdata getForecastdata() throws IOException {
        if (data == null) {
            updateForecastdata();
        }
        return data;
    }

    public ZoneId getZoneId() {
        try {
            return ZoneId.of(getForecastdata().getTimezone());
        } catch (IOException e) {
            logger.error("getZoneId:",e);
        }
        return null;
    }

    public LocalDateTime getTimeAsDateTime(long time) {

        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time),getZoneId());
    }

    public Currently getCurrently() {
        try {
            return getForecastdata().getCurrently();
        } catch (IOException e) {
            logger.error("getCurrently:",e);
        }
        return null;
    }

    public Daily getDaily() {
        try {
            return getForecastdata().getDaily();
        } catch (IOException e) {
            logger.error("getDaily:",e);
        }
        return null;
    }

    private synchronized void updateForecastdata() throws IOException {
        ForecastIO.Builder builder = new ForecastIO.Builder(apiKey,latitude,longitude);

        ForecastIO forecast = builder.units(units)
                                     .lang(lang)
                                     .build();

        data = forecast.getForecastdata();
    }

    private class ReadForecastDataTask implements Runnable {

        @Override
        public void run() {
            try {
                updateForecastdata();
            } catch (IOException e) {
                logger.error("run:",e);
            }
        }
    }

    public static LocalDateTime getTimeAsDateTime(long time, ZoneId zoneId) {

        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time),zoneId);
    }
}

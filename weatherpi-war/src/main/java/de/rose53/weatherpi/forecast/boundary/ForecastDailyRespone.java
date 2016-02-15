package de.rose53.weatherpi.forecast.boundary;

import java.time.LocalDateTime;
import java.time.ZoneId;

import de.rose53.pi.weatherpi.forecast.DataPoint;
import de.rose53.pi.weatherpi.forecast.Forecast;

public class ForecastDailyRespone extends BaseForecastRespone {

    private final double temperatureMin;
    private final double temperatureMax;
    private final double humidity;
    private final double pressure;
    private final double moonPhase;
    private final LocalDateTime sunriseTime;
    private final LocalDateTime sunsetTime;

    public ForecastDailyRespone(DataPoint d, ZoneId zoneId) {
        super(d,zoneId);
        this.temperatureMin = d.getTemperatureMin();
        this.temperatureMax = d.getTemperatureMax();
        this.humidity = d.getHumidity();
        this.pressure = d.getPressure();
        this.moonPhase = d.getMoonPhase();
        this.sunriseTime = Forecast.getTimeAsDateTime(d.getSunriseTime(), zoneId);
        this.sunsetTime = Forecast.getTimeAsDateTime(d.getSunsetTime(), zoneId);
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public LocalDateTime getSunriseTime() {
        return sunriseTime;
    }

    public LocalDateTime getSunsetTime() {
        return sunsetTime;
    }
}

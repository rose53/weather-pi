package de.rose53.weatherpi.web;

import java.time.ZoneId;

import de.rose53.pi.weatherpi.forecast.DataPoint;

public class ForecastDailyRespone extends BaseForecastRespone {

    private final double temperatureMin;
    private final double temperatureMax;
    private final double humidity;
    private final double pressure;
    private final int    moonPhase;

    public ForecastDailyRespone(DataPoint d, ZoneId zoneId) {
        super(d,zoneId);
        this.temperatureMin = d.getTemperatureMin();
        this.temperatureMax = d.getTemperatureMax();
        this.humidity = d.getHumidity();
        this.pressure = d.getPressure();
        this.moonPhase = d.getMoonPhase();
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

    public int getMoonPhase() {
        return moonPhase;
    }
}

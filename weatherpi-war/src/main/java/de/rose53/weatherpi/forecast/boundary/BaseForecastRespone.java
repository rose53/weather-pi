package de.rose53.weatherpi.forecast.boundary;

import java.time.LocalDateTime;
import java.time.ZoneId;

import de.rose53.pi.weatherpi.forecast.DataPoint;
import de.rose53.pi.weatherpi.forecast.Forecast;

public class BaseForecastRespone {

    private final LocalDateTime time;
    private final String icon;
    private final double visibility;
    private final double windSpeed;
    private final double windBearing;
    private final double cloudCover;


    public BaseForecastRespone(DataPoint d, ZoneId zoneId) {
        super();
        this.time = Forecast.getTimeAsDateTime(d.getTime(), zoneId);
        this.icon = d.getIcon();
        this.visibility = d.getVisibility();
        this.windSpeed = d.getWindSpeed();
        this.windBearing = d.getWindSpeed() > 0.0?d.getWindBearing():-1.0;
        this.cloudCover = d.getCloudCover();
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getIcon() {
        return icon;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindBearing() {
        return windBearing;
    }

    public double getCloudCover() {
        return cloudCover;
    }
}

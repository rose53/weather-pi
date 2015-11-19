package de.rose53.weatherpi.web;

import java.time.ZoneId;

import de.rose53.pi.weatherpi.forecast.DataPoint;

public class ForecastCurrentlyRespone extends BaseForecastRespone {

    private final double nearestStormDistance;
    private final double nearestStormBearing;

    public ForecastCurrentlyRespone(DataPoint d, ZoneId zoneId) {
        super(d,zoneId);
        this.nearestStormDistance = d.getNearestStormDistance();
        this.nearestStormBearing = d.getNearestStormDistance() > 0?d.getNearestStormBearing():-1.0;
    }

    public double getNearestStormDistance() {
        return nearestStormDistance;
    }

    public double getNearestStormBearing() {
        return nearestStormBearing;
    }
}

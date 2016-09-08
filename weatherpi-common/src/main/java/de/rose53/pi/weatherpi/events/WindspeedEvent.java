package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

public class WindspeedEvent extends SensorEvent {

    private double windspeed;

    public WindspeedEvent() {
        super(ESensorType.WINDSPEED);
    }

    public WindspeedEvent(ESensorPlace place, String sensor, double windspeed) {
        super(ESensorType.WINDSPEED,place,sensor);
        this.windspeed = windspeed;
    }

    public double getWindspeed() {
        return windspeed;
    }

    @Override
    public double getValue() {
        return getWindspeed();
    }
}

package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;

public class SensorEvent {

    private String sensor;
    private ESensorType type;
    private ESensorPlace place;

    private double accuracy = 0.0;

    public SensorEvent() {
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor) {
        this(type,place,sensor,0.0);
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor, double accuracy) {
        this.type = type;
        this.sensor = sensor;
        this.place = place;
    }

    public ESensorType getType() {
        return type;
    }

    public ESensorPlace getPlace() {
        return place;
    }

    public String getSensor() {
        return sensor;
    }

    public double getAccuracy() {
        return accuracy;
    }
}

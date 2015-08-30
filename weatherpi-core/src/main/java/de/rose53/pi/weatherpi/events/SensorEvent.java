package de.rose53.pi.weatherpi.events;

public class SensorEvent {

    public enum ESensorType {
        TEMPERATURE,
        HUMIDITY,
        PRESSURE,
        ILLUMINANCE
    }

    public enum ESensorPlace {
        INDOOR,
        OUTDOOR,
    }

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

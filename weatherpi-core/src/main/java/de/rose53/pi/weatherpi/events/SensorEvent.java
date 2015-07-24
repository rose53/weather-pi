package de.rose53.pi.weatherpi.events;

public class SensorEvent {

    private String sensor;

    private double accuracy = 0.0;

    public SensorEvent() {
    }

    public SensorEvent(String sensor) {
        this(sensor,0.0);
    }

    public SensorEvent(String sensor, double accuracy) {
        this.sensor = sensor;
    }

    public String getSensor() {
        return sensor;
    }

    public double getAccuracy() {
        return accuracy;
    }
}

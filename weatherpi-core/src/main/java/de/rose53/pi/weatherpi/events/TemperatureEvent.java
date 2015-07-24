package de.rose53.pi.weatherpi.events;

public class TemperatureEvent extends SensorEvent {

    private double temperature;

    public TemperatureEvent() {
        super();
    }

    public TemperatureEvent(String sensor, double temperature, double accuracy) {
        super(sensor,accuracy);
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }
}

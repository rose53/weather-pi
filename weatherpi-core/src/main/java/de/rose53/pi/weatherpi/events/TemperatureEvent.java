package de.rose53.pi.weatherpi.events;

public class TemperatureEvent extends SensorEvent {

    private double temperature;

    public TemperatureEvent() {
        super();
    }

    public TemperatureEvent(ESensorPlace place, String sensor, double temperature, double accuracy) {
        super(ESensorType.TEMPERATURE,place,sensor,accuracy);
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }
}

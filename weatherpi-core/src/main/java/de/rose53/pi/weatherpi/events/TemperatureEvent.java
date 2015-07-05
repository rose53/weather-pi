package de.rose53.pi.weatherpi.events;

public class TemperatureEvent extends SensorEvent {

    private float temperature;

    public TemperatureEvent() {
    	super();
    }

    public TemperatureEvent(String sensor, float temperature) {
    	super(sensor);
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }
}

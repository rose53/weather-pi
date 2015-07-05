package de.rose53.pi.weatherpi.events;

public class PressureEvent extends SensorEvent {

    private double pressure;

    public PressureEvent() {
    	super();
    }

    public PressureEvent(String sensor, double pressure) {
    	super(sensor);
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}

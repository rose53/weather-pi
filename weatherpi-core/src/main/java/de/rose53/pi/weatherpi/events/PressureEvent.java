package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;

public class PressureEvent extends SensorEvent {

    private double pressure;

    public PressureEvent() {
        super();
    }

    public PressureEvent(ESensorPlace place, String sensor, double pressure) {
        super(ESensorType.PRESSURE,place,sensor);
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}

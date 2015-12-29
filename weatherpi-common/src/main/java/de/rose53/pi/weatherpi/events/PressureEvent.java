package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

public class PressureEvent extends SensorEvent {

    private double pressure;

    public PressureEvent() {
        super(ESensorType.PRESSURE);
    }

    public PressureEvent(ESensorPlace place, String sensor, double pressure) {
        super(ESensorType.PRESSURE,place,sensor);
        this.pressure = pressure;
    }

    public double getPressure() {
        return pressure;
    }
}

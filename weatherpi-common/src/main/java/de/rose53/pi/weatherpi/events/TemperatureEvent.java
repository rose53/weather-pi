package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

public class TemperatureEvent extends SensorEvent {

    private double temperature;

    public TemperatureEvent() {
        super(ESensorType.TEMPERATURE);
    }

    public TemperatureEvent(ESensorPlace place, String sensor, double temperature) {
        super(ESensorType.TEMPERATURE,place,sensor);
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }
}

package de.rose53.weatherpi.web;

import de.rose53.pi.weatherpi.events.TemperatureEvent;

public class TemperatureValue {

    private final double temperature;
    private final double accuracy;

    public TemperatureValue(double temperature, double accuracy) {
        super();
        this.temperature = temperature;
        this.accuracy = accuracy;
    }

    public TemperatureValue(TemperatureEvent event) {
        this(event.getTemperature(),event.getAccuracy());
    }

    public double getTemperature() {
        return temperature;
    }

    public double getAccuracy() {
        return accuracy;
    }
}

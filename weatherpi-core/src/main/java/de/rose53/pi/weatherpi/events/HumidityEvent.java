package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;

public class HumidityEvent extends SensorEvent {

    private double humidity;

    public HumidityEvent() {
        super();
    }

    public HumidityEvent(ESensorPlace place, String sensor, double humidity) {
        super(ESensorType.HUMIDITY,place,sensor);
        this.humidity = humidity;
    }

    public double getHumidity() {
        return humidity;
    }
}

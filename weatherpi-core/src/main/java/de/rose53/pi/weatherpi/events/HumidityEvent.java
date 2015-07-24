package de.rose53.pi.weatherpi.events;

public class HumidityEvent extends SensorEvent {

    private double humidity;

    public HumidityEvent() {
        super();
    }

    public HumidityEvent(String sensor, double humidity) {
        super(sensor);
        this.humidity = humidity;
    }

    public double getHumidity() {
        return humidity;
    }
}

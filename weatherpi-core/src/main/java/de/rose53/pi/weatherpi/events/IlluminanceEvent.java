package de.rose53.pi.weatherpi.events;

public class IlluminanceEvent extends SensorEvent {

    private double illuminance;

    public IlluminanceEvent() {
        super();
    }

    public IlluminanceEvent(String sensor, double illuminance) {
        super(sensor);
        this.illuminance = illuminance;
    }

    public double getIlluminance() {
        return illuminance;
    }
}

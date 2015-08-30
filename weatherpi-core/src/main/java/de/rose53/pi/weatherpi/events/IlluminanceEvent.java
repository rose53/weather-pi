package de.rose53.pi.weatherpi.events;

public class IlluminanceEvent extends SensorEvent {

    private double illuminance;

    public IlluminanceEvent() {
        super();
    }

    public IlluminanceEvent(ESensorPlace place, String sensor, double illuminance) {
        super(ESensorType.ILLUMINANCE,place,sensor);
        this.illuminance = illuminance;
    }

    public double getIlluminance() {
        return illuminance;
    }
}

package de.rose53.pi.weatherpi.events;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;

public class IlluminanceEvent extends SensorEvent {

    private double illuminance;

    public IlluminanceEvent() {
        super(ESensorType.ILLUMINANCE);
    }

    public IlluminanceEvent(ESensorPlace place, String sensor, double illuminance) {
        super(ESensorType.ILLUMINANCE,place,sensor);
        this.illuminance = illuminance;
    }

    public double getIlluminance() {
        return illuminance;
    }
}

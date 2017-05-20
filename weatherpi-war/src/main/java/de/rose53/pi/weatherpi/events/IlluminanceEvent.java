package de.rose53.pi.weatherpi.events;

import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

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

    @Override
    public double getValue() {
        return getIlluminance();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("illuminance", getIlluminance());
    }
}

package de.rose53.pi.weatherpi.events;

import javax.json.JsonObjectBuilder;

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

    @Override
    public double getValue() {
        return getPressure();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("pressure", getPressure());
    }
}

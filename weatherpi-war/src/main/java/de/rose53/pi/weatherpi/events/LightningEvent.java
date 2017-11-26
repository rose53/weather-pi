package de.rose53.pi.weatherpi.events;

import static de.rose53.pi.weatherpi.common.ESensorType.LIGHTNING;


import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;

public class LightningEvent extends SensorEvent {

    private double distance;

    public LightningEvent() {
        super(LIGHTNING);
    }

    public LightningEvent(ESensorPlace place, String sensor, double distance) {
        super(LIGHTNING,place,sensor);
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public double getValue() {
        return getDistance();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("distance", getDistance());
    }
}

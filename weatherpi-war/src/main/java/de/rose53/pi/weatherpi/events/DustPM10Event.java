package de.rose53.pi.weatherpi.events;

import static de.rose53.pi.weatherpi.common.ESensorType.DUST_PM10;


import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;

public class DustPM10Event extends SensorEvent {

    private double pm10;

    public DustPM10Event() {
        super(DUST_PM10);
    }

    public DustPM10Event(ESensorPlace place, String sensor, double pm10) {
        super(DUST_PM10,place,sensor);
        this.pm10 = pm10;
    }

    public double getPM10() {
        return pm10;
    }

    @Override
    public double getValue() {
        return getPM10();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("pm10", getPM10());
    }
}

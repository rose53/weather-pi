package de.rose53.pi.weatherpi.events;

import static de.rose53.pi.weatherpi.common.ESensorType.DUST_PM25;


import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;

public class DustPM25Event extends SensorEvent {

    private double pm25;

    public DustPM25Event() {
        super(DUST_PM25);
    }

    public DustPM25Event(ESensorPlace place, String sensor, double pm25) {
        super(DUST_PM25,place,sensor);
        this.pm25 = pm25;
    }

    public double getPM25() {
        return pm25;
    }

    @Override
    public double getValue() {
        return getPM25();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("pm25", getPM25());
    }
}

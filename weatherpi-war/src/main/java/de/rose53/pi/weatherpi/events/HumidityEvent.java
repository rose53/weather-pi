package de.rose53.pi.weatherpi.events;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

public class HumidityEvent extends SensorEvent {

    private double humidity;

    public HumidityEvent() {
        super(ESensorType.HUMIDITY);
    }

    public HumidityEvent(ESensorPlace place, String sensor, double humidity) {
        super(ESensorType.HUMIDITY,place,sensor);
        this.humidity = humidity;
    }

    public double getHumidity() {
        return humidity;
    }

    @Override
    public double getValue() {
        return getHumidity();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("humidity", getHumidity());
    }
}

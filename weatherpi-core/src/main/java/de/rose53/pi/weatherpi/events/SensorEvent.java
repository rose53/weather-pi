package de.rose53.pi.weatherpi.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
    @JsonSubTypes({
        @Type(value = TemperatureEvent.class, name = "TEMPERATURE"),
        @Type(value = HumidityEvent.class, name = "HUMIDITY"),
        @Type(value = PressureEvent.class, name = "PRESSURE"),
        @Type(value = IlluminanceEvent.class, name = "ILLUMINANCE")})
public class SensorEvent {

    private String sensor;
    private ESensorType type;
    private ESensorPlace place;

    private double accuracy = 0.0;

    public SensorEvent(ESensorType type) {
        this.type = type;
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor) {
        this(type,place,sensor,0.0);
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor, double accuracy) {
        this.type = type;
        this.sensor = sensor;
        this.place = place;
        this.accuracy = accuracy;
    }

    public ESensorType getType() {
        return type;
    }

    public ESensorPlace getPlace() {
        return place;
    }

    public String getSensor() {
        return sensor;
    }

    public double getAccuracy() {
        return accuracy;
    }
}

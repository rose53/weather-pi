package de.rose53.pi.weatherpi.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
    @JsonSubTypes({
        @Type(value = TemperatureEvent.class, name = "TEMPERATURE"),
        @Type(value = HumidityEvent.class, name = "HUMIDITY"),
        @Type(value = PressureEvent.class, name = "PRESSURE"),
        @Type(value = IlluminanceEvent.class, name = "ILLUMINANCE")})
@JsonIgnoreProperties({"accuracy"})
public class SensorEvent {

    private String sensor;
    private ESensorType type;
    private ESensorPlace place;

    public SensorEvent(ESensorType type) {
        this.type = type;
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor) {
        this.type = type;
        this.sensor = sensor;
        this.place = place;
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

}

package de.rose53.pi.weatherpi.events;

import java.time.Instant;
import java.util.Date;

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
public abstract class SensorEvent {

    private String       sensor;
    private ESensorType  type;
    private ESensorPlace place;
    private long         time = Instant.now().getEpochSecond();

    public SensorEvent(ESensorType type) {
        this.type = type;
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor) {
        this(type,place,sensor,Instant.now().getEpochSecond());
    }

    public SensorEvent(ESensorType type, ESensorPlace place, String sensor, long time) {
        this.type = type;
        this.sensor = sensor;
        this.place = place;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public Date getTimeAsDate() {
        return Date.from(Instant.ofEpochSecond(getTime()));
    }

    abstract public double getValue();
}

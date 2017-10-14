package de.rose53.pi.weatherpi.events;

import static de.rose53.pi.weatherpi.common.JsonUtils.has;

import java.time.Instant;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

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

    public static SensorEvent build(JsonObject json) {

        if (!has(json,"type") || !has(json,"place") || !has(json,"sensor")) {
            return null;
        }
        ESensorType  type   = ESensorType.fromString(json.getString("type"));
        ESensorPlace place  = ESensorPlace.fromString(json.getString("place"));
        long         time   = !has(json,"time")?Instant.now().getEpochSecond():json.getJsonNumber("time").longValue();
        String       sensor = json.getString("sensor");
        SensorEvent  retVal = null;

        switch (type) {
        case HUMIDITY:
            if (has(json,"humidity")) {
                double humidity = json.getJsonNumber("humidity").doubleValue();
                retVal = new HumidityEvent(place, sensor, humidity);
            }
            break;
        case ILLUMINANCE:
            if (has(json,"illuminance")) {
                double illuminance = json.getJsonNumber("illuminance").doubleValue();
                retVal = new IlluminanceEvent(place, sensor, illuminance);
            }
            break;
        case PRESSURE:
            if (has(json,"pressure")) {
                double pressure = json.getJsonNumber("pressure").doubleValue();
                retVal = new PressureEvent(place, sensor, pressure);
            }
            break;
        case TEMPERATURE:
            if (has(json,"temperature")) {
                double temperature = json.getJsonNumber("temperature").doubleValue();
                retVal = new TemperatureEvent(place, sensor, temperature);
            }
            break;
        case WINDSPEED:
               if (has(json,"windspeed")) {
                double windspeed = json.getJsonNumber("windspeed").doubleValue();
                retVal = new WindspeedEvent(place, sensor, windspeed);
            }
            break;

        }
        if (retVal != null) {
            retVal.time = time;
        }
        return retVal;
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

    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        objectBuilder.add("sensor", sensor);
        objectBuilder.add("type", type.toString());
        objectBuilder.add("place", place.toString());
        objectBuilder.add("time", time);
    }

    public JsonObject toJson() {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        addtoJsonObject(objectBuilder);
        return objectBuilder.build();
    }
}

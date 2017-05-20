package de.rose53.weatherpi.forecast;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class BaseDataPoint {

    /**
     *  A human-readable text summary of this data point.
     */
    private final String summary;
    /**
     *  A machine-readable text summary of this data point, suitable for selecting an icon for display. If defined, this property will have one of the following values: clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night. (Developers should ensure that a sensible default is defined, as additional values, such as hail, thunderstorm, or tornado, may be defined in the future.)
     */
    private final String icon;

    public BaseDataPoint(JsonObject data) {
        if (data.containsKey("summary") && !data.isNull("summary")) {
            summary = data.getString("summary");
        } else {
            summary = null;
        }
        if (data.containsKey("icon") && !data.isNull("icon")) {
            icon = data.getString("icon");
        } else {
            icon = null;
        }
    }

    public String getSummary() {
        return summary;
    }

    public String getIcon() {
        return icon;
    }

    public void toJson(JsonObjectBuilder builder, DateTimeFormatter formatter, ZoneId zoneId) {

        addJsonValueOrNull(builder,"icon", getIcon());
        addJsonValueOrNull(builder,"summary", getSummary());

    }

    protected void addJsonValueOrNull(JsonObjectBuilder builder, String name, String value) {
        if (value == null) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }
    }

    protected void addJsonValueOrNull(JsonObjectBuilder builder, String name, Double value) {
        if (value == null) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }
    }

    protected void addJsonValueOrNull(JsonObjectBuilder builder, String name, Long value) {
        if (value == null) {
            builder.addNull(name);
        } else {
            builder.add(name, value);
        }
    }

    protected void addJsonDateTimeOrNull(JsonObjectBuilder builder, String name, Long value, DateTimeFormatter formatter, ZoneId zoneId) {
        if (value == null) {
            builder.addNull(name);
        } else {
            if (formatter == null || zoneId == null) {
                builder.add(name, value);
            } else {
                builder.add(name, formatter.format(LocalDateTime.ofInstant(Instant.ofEpochSecond(value),zoneId)));
            }
        }
    }
}

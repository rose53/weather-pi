package de.rose53.weatherpi.forecast;

import javax.json.JsonObject;

public class Forecastdata {

    /**
     * The requested latitude.
     */
    private final double latitude;

    /**
     * The requested longitude.
     */
    private final double longitude;

    /**
     * The IANA timezone name for the requested location (e.g. America/New_York).
     * This is the timezone used for text forecast summaries and for determining the exact start time of daily data points.
     */
    private final String timezone;

    /**
     * The current timezone offset in hours from GMT.
     */
    private final int offset;

    /**
     * A data point (see below) containing the current weather conditions at the requested location.
     */
    private final Currently currently;

    /**
     * A data block containing the weather conditions hour-by-hour for the next two days.
     */
    private final Hourly hourly;

    /**
     * A data block containing the weather conditions day-by-day for the next week.
     */
    private final Daily daily;

    /**
     * A flags object containing miscellaneous metadata about the request.
     */
    private final Flags flags;

    public Forecastdata(JsonObject data) {
        latitude  = data.getJsonNumber("latitude").doubleValue();
        longitude = data.getJsonNumber("longitude").doubleValue();
        timezone  = data.getString("timezone");
        offset    = data.getInt("offset");

        if (data.containsKey("currently") && !data.isNull("currently")) {
            currently = new Currently(data.getJsonObject("currently"));
        } else {
            currently = null;
        }

        if (data.containsKey("hourly") && !data.isNull("hourly")) {
            hourly = new Hourly(data.getJsonObject("hourly"));
        } else {
            hourly = null;
        }

        if (data.containsKey("daily") && !data.isNull("daily")) {
            daily = new Daily(data.getJsonObject("daily"));
        } else {
            daily = null;
        }

        if (data.containsKey("flags") && !data.isNull("flags")) {
            flags = new Flags(data.getJsonObject("flags"));
        } else {
            flags = null;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getOffset() {
        return offset;
    }

    public Currently getCurrently() {
        return currently;
    }

    public Hourly getHourly() {
        return hourly;
    }

    public Daily getDaily() {
        return daily;
    }

    public Flags getFlags() {
        return flags;
    }

}

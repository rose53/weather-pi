package de.rose53.weatherpi.forecast;

public class Forecastdata {

    /**
     * The requested latitude.
     */
    private double latitude;

    /**
     * The requested longitude.
     */
    private double longitude;

    /**
     * The IANA timezone name for the requested location (e.g. America/New_York).
     * This is the timezone used for text forecast summaries and for determining the exact start time of daily data points.
     */
    private String timezone;

    /**
     * The current timezone offset in hours from GMT.
     */
    private int offset;

    /**
     * A data point (see below) containing the current weather conditions at the requested location.
     */
    private Currently currently;

    private Hourly hourly;

    private Daily daily;

    private Flags flags;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Currently getCurrently() {
        return currently;
    }

    public void setCurrently(Currently currently) {
        this.currently = currently;
    }

    public Hourly getHourly() {
        return hourly;
    }

    public void setHourly(Hourly hourly) {
        this.hourly = hourly;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }



}

package de.rose53.pi.weatherpi.forecast;

public class DataPoint extends BaseDataPoint {

    /**
     * The UNIX time (that is, seconds since midnight GMT on 1 Jan 1970) at which this data point occurs.
     */
    private long time;

    /**
     * (only defined on daily data points): The UNIX time (that is, seconds since midnight GMT on 1 Jan 1970)
     * of the last sunrise before and first sunset after the solar noon closest to local noon on the given day.
     * (Note: near the poles, these may occur on a different day entirely!)
     */
    private long sunriseTime;

    private long sunsetTime;

    /**
     * (only defined on daily data points): A number representing the fractional part of the lunation number of the given day. This can be thought of as the “percentage complete” of the current lunar month: a value of 0 represents a new moon, a value of 0.25 represents a first quarter moon, a value of 0.5 represents a full moon, and a value of 0.75 represents a last quarter moon. (The ranges in between these represent waxing crescent, waxing gibbous, waning gibbous, and waning crescent moons, respectively.)
     */
    private double moonPhase;

    /**
     * A numerical value representing the distance to the nearest storm in miles.<p>
     * (This value is very approximate and should not be used in scenarios requiring accurate results. In particular, a storm distance of zero doesn’t necessarily
     * refer to a storm at the requested location, but rather a storm in the vicinity of that location.)
     */
    private double nearestStormDistance;

    /**
     * A numerical value representing the direction of the nearest storm in degrees, with true north at 0° and progressing clockwise. (If nearestStormDistance is zero, then this value will not be defined. The caveats that apply to nearestStormDistance also apply to this value.)
     */
    private double nearestStormBearing;

    /**
     * A numerical value representing the average expected intensity (in inches of liquid water per hour) of precipitation occurring at the given time conditional on probability (that is, assuming any precipitation occurs at all). A very rough guide is that a value of 0 in./hr. corresponds to no precipitation, 0.002 in./hr. corresponds to very light precipitation, 0.017 in./hr. corresponds to light precipitation, 0.1 in./hr. corresponds to moderate precipitation, and 0.4 in./hr. corresponds to heavy precipitation.
     */
    private double precipIntensity;

    /**
     *  (only defined on daily data points): numerical values representing the maximumum expected intensity of precipitation (and the UNIX time at which it occurs) on the given day in inches of liquid water per hour.
     */
    private double precipIntensityMax;

    private long precipIntensityMaxTime;

    /**
     * A numerical value between 0 and 1 (inclusive) representing the probability of precipitation occuring at the given time.
     */
    private double precipProbability;

    /**
     * A string representing the type of precipitation occurring at the given time. If defined, this property will have one of the following values: rain, snow, sleet (which applies to each of freezing rain, ice pellets, and “wintery mix”), or hail. (If precipIntensity is zero, then this property will not be defined.)
     */
    private String precipType;

    /**
     * (only defined on hourly and daily data points): the amount of snowfall accumulation expected to occur on the given day, in inches. (If no accumulation is expected, this property will not be defined.)
     */
    private double precipAccumulation;

    /**
     * (not defined on daily data points): A numerical value representing the temperature at the given time in degrees Fahrenheit.
     */
    private double temperature;

    /**
     * (only defined on daily data points): numerical values representing the minimum and maximum temperatures (and the UNIX times at which they occur) on the given day in degrees Fahrenheit.
     */
    private double temperatureMin;
    private long temperatureMinTime;

    private double temperatureMax;

    private long  temperatureMaxTime;

    /**
     *  (not defined on daily data points): A numerical value representing the apparent (or “feels like”) temperature at the given time in degrees Fahrenheit.
     */
    private double apparentTemperature;

    private double apparentTemperatureMin;
    private long   apparentTemperatureMinTime;
    private double apparentTemperatureMax;
    private long   apparentTemperatureMaxTime;

    private double dewPoint;

    private double windSpeed;

    private double windBearing;

    private double cloudCover;

    private double humidity;

    private double pressure;

    private double visibility;

    private double ozone;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public long getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(long sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public double getNearestStormDistance() {
        return nearestStormDistance;
    }

    public void setNearestStormDistance(double nearestStormDistance) {
        this.nearestStormDistance = nearestStormDistance;
    }

    public double getNearestStormBearing() {
        return nearestStormBearing;
    }

    public void setNearestStormBearing(double nearestStormBearing) {
        this.nearestStormBearing = nearestStormBearing;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public double getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public void setPrecipIntensityMax(double precipIntensityMax) {
        this.precipIntensityMax = precipIntensityMax;
    }

    public long getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public void setPrecipIntensityMaxTime(long precipIntensityMaxTime) {
        this.precipIntensityMaxTime = precipIntensityMaxTime;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }


    public double getPrecipAccumulation() {
        return precipAccumulation;
    }

    public void setPrecipAccumulation(double precipAccumulation) {
        this.precipAccumulation = precipAccumulation;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }


    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public long getTemperatureMinTime() {
        return temperatureMinTime;
    }

    public void setTemperatureMinTime(long temperatureMinTime) {
        this.temperatureMinTime = temperatureMinTime;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public long getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    public void setTemperatureMaxTime(long temperatureMaxTime) {
        this.temperatureMaxTime = temperatureMaxTime;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }


    public double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public void setApparentTemperatureMin(double apparentTemperatureMin) {
        this.apparentTemperatureMin = apparentTemperatureMin;
    }

    public long getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    public void setApparentTemperatureMinTime(long apparentTemperatureMinTime) {
        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
    }

    public double getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    public void setApparentTemperatureMax(double apparentTemperatureMax) {
        this.apparentTemperatureMax = apparentTemperatureMax;
    }

    public long getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    public void setApparentTemperatureMaxTime(long apparentTemperatureMaxTime) {
        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(double windBearing) {
        this.windBearing = windBearing;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public double getOzone() {
        return ozone;
    }

    public void setOzone(double ozone) {
        this.ozone = ozone;
    }

}

package de.rose53.weatherpi.forecast;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class DataPoint extends BaseDataPoint {

    /**
     * The UNIX time (that is, seconds since midnight GMT on 1 Jan 1970) at which this data point occurs.
     */
    private final long time;

    /**
     * (only defined on daily data points): The UNIX time (that is, seconds since midnight GMT on 1 Jan 1970)
     * of the last sunrise before and first sunset after the solar noon closest to local noon on the given day.
     * (Note: near the poles, these may occur on a different day entirely!)
     */
    private final Long sunriseTime;

    private final Long sunsetTime;

    /**
     * (only defined on daily data points): A number representing the fractional part of the lunation number
     * of the given day. This can be thought of as the “percentage complete” of the current lunar
     * month: a value of 0 represents a new moon, a value of 0.25 represents a first quarter moon, a
     * value of 0.5 represents a full moon, and a value of 0.75 represents a last quarter moon.
     * (The ranges in between these represent waxing crescent, waxing gibbous, waning gibbous, and waning
     * crescent moons, respectively.)
     */
    private final Double moonPhase;

    /**
     * A numerical value representing the distance to the nearest storm in miles.<p>
     * (This value is very approximate and should not be used in scenarios requiring accurate results. In particular, a storm distance of zero doesn’t necessarily
     * refer to a storm at the requested location, but rather a storm in the vicinity of that location.)
     */
    private final Double nearestStormDistance;

    /**
     * A numerical value representing the direction of the nearest storm in degrees, with true north at 0° and progressing clockwise. (If nearestStormDistance is zero, then this value will not be defined. The caveats that apply to nearestStormDistance also apply to this value.)
     */
    private final Double nearestStormBearing;

    /**
     * A numerical value representing the average expected intensity (in inches of liquid water per hour) of precipitation occurring at the given time conditional on probability (that is, assuming any precipitation occurs at all). A very rough guide is that a value of 0 in./hr. corresponds to no precipitation, 0.002 in./hr. corresponds to very light precipitation, 0.017 in./hr. corresponds to light precipitation, 0.1 in./hr. corresponds to moderate precipitation, and 0.4 in./hr. corresponds to heavy precipitation.
     */
    private final Double precipIntensity;

    /**
     *  (only defined on daily data points): numerical values representing the maximumum expected intensity of precipitation (and the UNIX time at which it occurs) on the given day in inches of liquid water per hour.
     */
    private final Double precipIntensityMax;

    private final Long precipIntensityMaxTime;

    /**
     * A numerical value between 0 and 1 (inclusive) representing the probability of precipitation occuring at the given time.
     */
    private final Double precipProbability;

    /**
     * A string representing the type of precipitation occurring at the given time. If defined, this property will have one of the following values: rain, snow, sleet (which applies to each of freezing rain, ice pellets, and “wintery mix”), or hail. (If precipIntensity is zero, then this property will not be defined.)
     */
    private final String precipType;

    /**
     * (only defined on hourly and daily data points): the amount of snowfall accumulation expected to occur on the given day, in inches. (If no accumulation is expected, this property will not be defined.)
     */
    private final Double precipAccumulation;

    /**
     * (not defined on daily data points): A numerical value representing the temperature at the given time in degrees Fahrenheit.
     */
    private final Double temperature;

    /**
     * (only defined on daily data points): numerical values representing the minimum and maximum temperatures (and the UNIX times at which they occur) on the given day in degrees Fahrenheit.
     */
    private final Double temperatureMin;
    private final Long temperatureMinTime;

    private final Double temperatureMax;

    private final Long  temperatureMaxTime;

    /**
     *  (not defined on daily data points): A numerical value representing the apparent (or “feels like”) temperature at the given time in degrees Fahrenheit.
     */
    private final Double apparentTemperature;

    private final Double apparentTemperatureMin;
    private final Long   apparentTemperatureMinTime;
    private final Double apparentTemperatureMax;
    private final Long   apparentTemperatureMaxTime;

    /**
     * The dew point in degrees Fahrenheit.
     */
    private final Double dewPoint;
    /**
     * A numerical value representing the wind speed in miles per hour.
     */
    private final Double windSpeed;

    /**
     * A numerical value representing the direction that the wind is coming from in degrees, with
     * true north at 0° and progressing clockwise.
     * (If windSpeed is zero, then this value will not be defined.)
     */
    private final Double windBearing;

    /**
     * The percentage of sky occluded by clouds, between 0 and 1, inclusive.
     */
    private final Double cloudCover;

    /**
     * The relative humidity, between 0 and 1, inclusive.
     */
    private final Double humidity;

    /**
     * The sea-level air pressure in millibars.
     */
    private final Double pressure;

    /**
     * The average visibility in miles, capped at 10 miles.
     */
    private final Double visibility;

    /**
     * The columnar density of total atmospheric ozone at the given time in Dobson units.
     */
    private final Double ozone;

    public DataPoint(JsonObject data) {
        super(data);

        if (data.containsKey("apparentTemperature") && !data.isNull("apparentTemperature")) {
            apparentTemperature = data.getJsonNumber("apparentTemperature").doubleValue();
        } else {
            apparentTemperature = null;
        }
        if (data.containsKey("apparentTemperatureMin") && !data.isNull("apparentTemperatureMin")) {
            apparentTemperatureMin = data.getJsonNumber("apparentTemperatureMin").doubleValue();
        } else {
            apparentTemperatureMin = null;
        }
        if (data.containsKey("apparentTemperatureMinTime") && !data.isNull("apparentTemperatureMinTime")) {
            apparentTemperatureMinTime = data.getJsonNumber("apparentTemperatureMinTime").longValue();
        } else {
            apparentTemperatureMinTime = null;
        }
        if (data.containsKey("apparentTemperatureMax") && !data.isNull("apparentTemperatureMax")) {
            apparentTemperatureMax = data.getJsonNumber("apparentTemperatureMax").doubleValue();
        } else {
            apparentTemperatureMax = null;
        }
        if (data.containsKey("apparentTemperatureMaxTime") && !data.isNull("apparentTemperatureMaxTime")) {
            apparentTemperatureMaxTime = data.getJsonNumber("apparentTemperatureMaxTime").longValue();
        } else {
            apparentTemperatureMaxTime = null;
        }
        if (data.containsKey("cloudCover") && !data.isNull("cloudCover")) {
            cloudCover = data.getJsonNumber("cloudCover").doubleValue();
        } else {
            cloudCover = null;
        }
        if (data.containsKey("dewPoint") && !data.isNull("dewPoint")) {
            dewPoint = data.getJsonNumber("dewPoint").doubleValue();
        } else {
            dewPoint = null;
        }
        if (data.containsKey("humidity") && !data.isNull("humidity")) {
            humidity = data.getJsonNumber("humidity").doubleValue();
        } else {
            humidity = null;
        }
        if (data.containsKey("nearestStormBearing") && !data.isNull("nearestStormBearing")) {
            nearestStormBearing = data.getJsonNumber("nearestStormBearing").doubleValue();
        } else {
            nearestStormBearing = null;
        }
        if (data.containsKey("nearestStormDistance") && !data.isNull("nearestStormDistance")) {
            nearestStormDistance = data.getJsonNumber("nearestStormDistance").doubleValue();
        } else {
            nearestStormDistance = null;
        }
        if (data.containsKey("ozone") && !data.isNull("ozone")) {
            ozone = data.getJsonNumber("ozone").doubleValue();
        } else {
            ozone = null;
        }
        if (data.containsKey("precipAccumulation") && !data.isNull("precipAccumulation")) {
            precipAccumulation = data.getJsonNumber("precipAccumulation").doubleValue();
        } else {
            precipAccumulation = null;
        }
        if (data.containsKey("precipIntensity") && !data.isNull("precipIntensity")) {
            precipIntensity = data.getJsonNumber("precipIntensity").doubleValue();
        } else {
            precipIntensity = null;
        }
        if (data.containsKey("precipIntensityMax") && !data.isNull("precipIntensityMax")) {
            precipIntensityMax = data.getJsonNumber("precipIntensityMax").doubleValue();
        } else {
            precipIntensityMax = null;
        }
        if (data.containsKey("precipIntensityMaxTime") && !data.isNull("precipIntensityMaxTime")) {
            precipIntensityMaxTime = data.getJsonNumber("precipIntensityMaxTime").longValue();
        } else {
            precipIntensityMaxTime = null;
        }
        if (data.containsKey("precipProbability") && !data.isNull("precipProbability")) {
            precipProbability = data.getJsonNumber("precipProbability").doubleValue();
        } else {
            precipProbability = null;
        }
        if (data.containsKey("precipType") && !data.isNull("precipType")) {
            precipType  = data.getString("precipType");
        } else {
            precipType  = null;
        }
        if (data.containsKey("pressure") && !data.isNull("pressure")) {
            pressure = data.getJsonNumber("pressure").doubleValue();
        } else {
            pressure = null;
        }
        if (data.containsKey("sunriseTime") && !data.isNull("sunriseTime")) {
            sunriseTime = data.getJsonNumber("sunriseTime").longValue();
        } else {
            sunriseTime = null;
        }
        if (data.containsKey("sunsetTime") && !data.isNull("sunsetTime")) {
            sunsetTime = data.getJsonNumber("sunsetTime").longValue();
        } else {
            sunsetTime = null;
        }
        if (data.containsKey("temperature") && !data.isNull("temperature")) {
            temperature = data.getJsonNumber("temperature").doubleValue();
        } else {
            temperature = null;
        }
        if (data.containsKey("temperatureMin") && !data.isNull("temperatureMin")) {
            temperatureMin = data.getJsonNumber("temperatureMin").doubleValue();
        } else {
            temperatureMin = null;
        }
        if (data.containsKey("temperatureMinTime") && !data.isNull("temperatureMinTime")) {
            temperatureMinTime = data.getJsonNumber("temperatureMinTime").longValue();
        } else {
            temperatureMinTime = null;
        }
        if (data.containsKey("temperatureMax") && !data.isNull("temperatureMax")) {
            temperatureMax = data.getJsonNumber("temperatureMax").doubleValue();
        } else {
            temperatureMax = null;
        }
        if (data.containsKey("temperatureMaxTime") && !data.isNull("temperatureMaxTime")) {
            temperatureMaxTime = data.getJsonNumber("temperatureMaxTime").longValue();
        } else {
            temperatureMaxTime = null;
        }
        time = data.getJsonNumber("time").longValue();
        if (data.containsKey("visibility") && !data.isNull("visibility")) {
            visibility = data.getJsonNumber("visibility").doubleValue();
        } else {
            visibility = null;
        }
        if (data.containsKey("windBearing") && !data.isNull("windBearing")) {
            windBearing = data.getJsonNumber("windBearing").doubleValue();
        } else {
            windBearing = null;
        }
        if (data.containsKey("windSpeed") && !data.isNull("windSpeed")) {
            windSpeed = data.getJsonNumber("windSpeed").doubleValue();
        } else {
            windSpeed = null;
        }
        if (data.containsKey("moonPhase") && !data.isNull("moonPhase")) {
            moonPhase = data.getJsonNumber("moonPhase").doubleValue();
        } else {
            moonPhase = null;
        }
    }

    public long getTime() {
        return time;
    }

    public Long getSunriseTime() {
        return sunriseTime;
    }

    public Long getSunsetTime() {
        return sunsetTime;
    }

    public Double getMoonPhase() {
        return moonPhase;
    }

    public Double getNearestStormDistance() {
        return nearestStormDistance;
    }

    public Double getNearestStormBearing() {
        return nearestStormBearing;
    }

    public Double getPrecipIntensity() {
        return precipIntensity;
    }

    public Double getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public Long getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public Double getPrecipProbability() {
        return precipProbability;
    }


    public String getPrecipType() {
        return precipType;
    }

    public Double getPrecipAccumulation() {
        return precipAccumulation;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getTemperatureMin() {
        return temperatureMin;
    }

    public Long getTemperatureMinTime() {
        return temperatureMinTime;
    }

    public Double getTemperatureMax() {
        return temperatureMax;
    }

    public Long getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    public Double getApparentTemperature() {
        return apparentTemperature;
    }

    public Double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public Long getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    public Double getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    public Long getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    public Double getDewPoint() {
        return dewPoint;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Double getWindBearing() {
        return windBearing;
    }

    public Double getCloudCover() {
        return cloudCover;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getVisibility() {
        return visibility;
    }

    public Double getOzone() {
        return ozone;
    }

    @Override
    public void toJson(JsonObjectBuilder builder, DateTimeFormatter formatter, ZoneId zoneId) {
        super.toJson(builder,formatter,zoneId);

        addJsonDateTimeOrNull(builder,"time",time,formatter,zoneId);
        addJsonDateTimeOrNull(builder,"sunriseTime",sunriseTime,formatter,zoneId);
        addJsonDateTimeOrNull(builder,"sunsetTime",sunsetTime,formatter,zoneId);
        addJsonValueOrNull(builder,"moonPhase",moonPhase);
        addJsonValueOrNull(builder,"nearestStormDistance",nearestStormDistance);
        addJsonValueOrNull(builder,"nearestStormBearing",nearestStormBearing);
        addJsonValueOrNull(builder,"precipIntensity",precipIntensity);
        addJsonValueOrNull(builder,"moonPhase",moonPhase);
        addJsonValueOrNull(builder,"precipIntensityMax",precipIntensityMax);
        addJsonDateTimeOrNull(builder,"precipIntensityMaxTime",precipIntensityMaxTime,formatter,zoneId);
        addJsonValueOrNull(builder,"precipProbability",precipProbability);
        addJsonValueOrNull(builder,"precipType",precipType);
        addJsonValueOrNull(builder,"precipAccumulation",precipAccumulation);
        addJsonValueOrNull(builder,"temperature",temperature);
        addJsonValueOrNull(builder,"temperatureMin",temperatureMin);
        addJsonDateTimeOrNull(builder,"temperatureMinTime",temperatureMinTime,formatter,zoneId);
        addJsonValueOrNull(builder,"temperatureMax",temperatureMax);
        addJsonDateTimeOrNull(builder,"temperatureMaxTime",temperatureMaxTime,formatter,zoneId);
        addJsonValueOrNull(builder,"apparentTemperature",apparentTemperature);
        addJsonValueOrNull(builder,"apparentTemperatureMin",apparentTemperatureMin);
        addJsonDateTimeOrNull(builder,"apparentTemperatureMinTime",apparentTemperatureMinTime,formatter,zoneId);
        addJsonValueOrNull(builder,"apparentTemperatureMax",apparentTemperatureMax);
        addJsonDateTimeOrNull(builder,"apparentTemperatureMaxTime",apparentTemperatureMaxTime,formatter,zoneId);
        addJsonValueOrNull(builder,"dewPoint",dewPoint);
        addJsonValueOrNull(builder,"windSpeed",windSpeed);
        addJsonValueOrNull(builder,"windBearing",windBearing);
        addJsonValueOrNull(builder,"cloudCover",cloudCover);
        addJsonValueOrNull(builder,"humidity",humidity);
        addJsonValueOrNull(builder,"pressure",pressure);
        addJsonValueOrNull(builder,"visibility",visibility);
        addJsonValueOrNull(builder,"ozone",ozone);
    }
}

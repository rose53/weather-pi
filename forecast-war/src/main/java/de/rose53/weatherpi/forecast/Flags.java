package de.rose53.weatherpi.forecast;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Flags {

    /**
     * The presence of this property indicates that the Dark Sky data source supports the given location, but a temporary error (such as a radar station being down for maintenace) has made the data unavailable.
     */
    private final String darkskyUnavailable;

    /**
     * This property contains an array of IDs for each radar station utilized in servicing this request.
     */
    //@JsonProperty("darksky-stations")
    private  String[] darkskyStations;

    /**
     * This property contains an array of IDs for each DataPoint station utilized in servicing this request.
     */
    //@JsonProperty("datapoint-stations")
    private  String[] datapointStations;

    /**
     * This property contains an array of IDs for each ISD station utilized in servicing this request.
     */
    //@JsonProperty("isd-stations")
    private  String[] isdStations;

    /**
     * This property contains an array of IDs for each LAMP station utilized in servicing this request.
     */
    //@JsonProperty("lamp-stations")
    private  String[] lampStations;

    /**
     * This property contains an array of IDs for each METAR station utilized in servicing this request.
     */
    //@JsonProperty("metar-stations")
    private  String[] metarStations;

    /**
     * This property contains an array of IDs for each MADIS station utilized in servicing this request.
     */
    //@JsonProperty("madis-stations")
    private  String[] madisStations;

    /**
     *  The presence of this property indicates that data from api.met.no was utilized in order to facilitate this request (as per their license agreement).
     */
    //@JsonProperty("metno-license")
    private  String metnoLicense;

    /**
     * This property contains an array of IDs for each data source utilized in servicing this request. (For more information, see data sources, below.)
     */
    private final String[] sources;

    /**
     *  The presence of this property indicates which units were used for the data in this request. (For more information, see options, below.)
     */
    private final String units;

    public Flags(JsonObject jsonObject) {

        units = jsonObject.getString("units");
        JsonArray jsonSources = jsonObject.getJsonArray("sources");
        sources = new String[jsonSources.size()];
        for (int i = 0; i < jsonSources.size();i++) {
            sources[i] = jsonSources.getString(i);
        }
        if (jsonObject.containsKey("darksky-unavailable") && !jsonObject.isNull("darksky-unavailable")) {
            darkskyUnavailable  = jsonObject.getString("darksky-unavailable");
        } else {
            darkskyUnavailable  = null;
        }
    }

    public String getDarkskyUnavailable() {
        return darkskyUnavailable;
    }


    public String[] getDarkskyStations() {
        return darkskyStations;
    }

    public String[] getDatapointStations() {
        return datapointStations;
    }

    public String[] getIsdStations() {
        return isdStations;
    }

    public String[] getLampStations() {
        return lampStations;
    }

    public String[] getMetarStations() {
        return metarStations;
    }

    public String getMetnoLicense() {
        return metnoLicense;
    }

    public String[] getSources() {
        return sources;
    }

    public String getUnits() {
        return units;
    }

    public String[] getMadisStations() {
        return madisStations;
    }

}

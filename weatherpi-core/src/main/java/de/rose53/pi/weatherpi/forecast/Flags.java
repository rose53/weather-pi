package de.rose53.pi.weatherpi.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flags {

    /**
     * The presence of this property indicates that the Dark Sky data source supports the given location, but a temporary error (such as a radar station being down for maintenace) has made the data unavailable.
     */
    @JsonProperty("darksky-unavailable")
    private String darkskyUnavailable;

    /**
     * This property contains an array of IDs for each radar station utilized in servicing this request.
     */
    @JsonProperty("darksky-stations")
    private String[] darkskyStations;

    /**
     * This property contains an array of IDs for each DataPoint station utilized in servicing this request.
     */
    @JsonProperty("datapoint-stations")
    private String[] datapointStations;

    /**
     * This property contains an array of IDs for each ISD station utilized in servicing this request.
     */
    @JsonProperty("isd-stations")
    private String[] isdStations;

    /**
     * This property contains an array of IDs for each LAMP station utilized in servicing this request.
     */
    @JsonProperty("lamp-stations")
    private String[] lampStations;

    /**
     * This property contains an array of IDs for each METAR station utilized in servicing this request.
     */
    @JsonProperty("metar-stations")
    private String[] metarStations;

    /**
     * This property contains an array of IDs for each MADIS station utilized in servicing this request.
     */
    @JsonProperty("madis-stations")
    private String[] madisStations;

    /**
     *  The presence of this property indicates that data from api.met.no was utilized in order to facilitate this request (as per their license agreement).
     */
    @JsonProperty("metno-license")
    private String metnoLicense;

    /**
     * This property contains an array of IDs for each data source utilized in servicing this request. (For more information, see data sources, below.)
     */
    private String[] sources;

    /**
     *  The presence of this property indicates which units were used for the data in this request. (For more information, see options, below.)
     */
    private String units;

    public String getDarkskyUnavailable() {
        return darkskyUnavailable;
    }

    public void setDarkskyUnavailable(String darkskyUnavailable) {
        this.darkskyUnavailable = darkskyUnavailable;
    }

    public String[] getDarkskyStations() {
        return darkskyStations;
    }

    public void setDarkskyStations(String[] darkskyStations) {
        this.darkskyStations = darkskyStations;
    }

    public String[] getDatapointStations() {
        return datapointStations;
    }

    public void setDatapointStations(String[] datapointStations) {
        this.datapointStations = datapointStations;
    }

    public String[] getIsdStations() {
        return isdStations;
    }

    public void setIsdStations(String[] isdStations) {
        this.isdStations = isdStations;
    }

    public String[] getLampStations() {
        return lampStations;
    }

    public void setLampStations(String[] lampStations) {
        this.lampStations = lampStations;
    }

    public String[] getMetarStations() {
        return metarStations;
    }

    public void setMetarStations(String[] metarStations) {
        this.metarStations = metarStations;
    }

    public String getMetnoLicense() {
        return metnoLicense;
    }

    public void setMetnoLicense(String metnoLicense) {
        this.metnoLicense = metnoLicense;
    }

    public String[] getSources() {
        return sources;
    }

    public void setSources(String[] sources) {
        this.sources = sources;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String[] getMadisStations() {
        return madisStations;
    }

    public void setMadisStations(String[] madisStations) {
        this.madisStations = madisStations;
    }


}

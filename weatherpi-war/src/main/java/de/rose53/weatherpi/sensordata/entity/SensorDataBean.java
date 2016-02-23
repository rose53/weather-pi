package de.rose53.weatherpi.sensordata.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SENSOR_DATA")
@NamedQueries({
     @NamedQuery(name = SensorDataBean.findRange,
                 query= "SELECT s FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findHumidityIndoorRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.humidity) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findHumidityBirdhouseRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.humidityBird) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findHumidityOutdoorRange,
                  query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.humidityOut) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findTemperatureIndoorRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.temperature) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findTemperatureOutdoorRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.temperatureOut) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findTemperatureBirdhouseRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.temperatureBird) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findPressureRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.pressure) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC "),
     @NamedQuery(name = SensorDataBean.findIlluminationRange,
                 query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(s.time,s.illumination) FROM SensorDataBean s WHERE s.time  BETWEEN :pastTime AND :actualTime order by s.time DESC ")

})
public class SensorDataBean implements Serializable {

    private static final long serialVersionUID = 4988494460990539534L;

    public static final String findRange                     = "SensorDataBean.findRange";
    public static final String findHumidityIndoorRange       = "SensorDataBean.findHumidityIndoorRange";
    public static final String findHumidityOutdoorRange      = "SensorDataBean.findHumidityIOutdoorRange";
    public static final String findHumidityBirdhouseRange    = "SensorDataBean.findHumidityBirdhouseRange";
    public static final String findTemperatureIndoorRange    = "SensorDataBean.findTemperatureIndoorRange";
    public static final String findTemperatureOutdoorRange   = "SensorDataBean.findTemperatureOutdoorRange";
    public static final String findTemperatureBirdhouseRange = "SensorDataBean.findTemperatureBirdhouseRange";
    public static final String findPressureRange             = "SensorDataBean.findPressureRange";
    public static final String findIlluminationRange         = "SensorDataBean.findIlluminationRange";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="TIME", updatable=false,nullable=false)
    private Date time;

    @Column(name="TEMPERATURE", nullable=false, updatable=false)
    private double temperature;

    @Column(name="PRESSURE", nullable=false, updatable=false)
    private double pressure;

    @Column(name="HUMIDITY", nullable=false, updatable=false)
    private double humidity;

    @Column(name="ILLUMINATION", nullable=false, updatable=false)
    private double illumination;

    @Column(name="TEMPERATURE_OUT", nullable=false, updatable=false)
    private double temperatureOut;

    @Column(name="HUMIDITY_OUT", nullable=false, updatable=false)
    private double humidityOut;

    @Column(name="TEMPERATURE_BIRD", nullable=false, updatable=false)
    private double temperatureBird;

    @Column(name="HUMIDITY_BIRD", nullable=false, updatable=false)
    private double humidityBird;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getIllumination() {
        return illumination;
    }

    public void setIllumination(double illumination) {
        this.illumination = illumination;
    }

    public double getTemperatureOut() {
        return temperatureOut;
    }

    public void setTemperatureOut(double temperatureOut) {
        this.temperatureOut = temperatureOut;
    }

    public double getHumidityOut() {
        return humidityOut;
    }

    public void setHumidityOut(double humidityOut) {
        this.humidityOut = humidityOut;
    }

    public double getTemperatureBird() {
        return temperatureBird;
    }

    public void setTemperatureBird(double temperatureBird) {
        this.temperatureBird = temperatureBird;
    }

    public double getHumidityBird() {
        return humidityBird;
    }

    public void setHumidityBird(double humidityBird) {
        this.humidityBird = humidityBird;
    }


}

package de.rose53.weatherpi.sensordata.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity: DeviceBean
 *
 */
@Entity
@Table(name="DATA")
@NamedQueries({
    @NamedQuery(name = DataBean.findByTimeTypePlace,
                query= "SELECT new de.rose53.weatherpi.sensordata.boundary.SensorDataQueryResult(d.time,d.value) FROM DataBean d "
                        + "WHERE d.time  BETWEEN :pastTime AND :actualTime AND d.sensor.type = :type AND d.sensor.device.place = :place order by d.time DESC "),
    @NamedQuery(name = DataBean.findByTimeNameTypePlace,
                query= "SELECT d FROM DataBean d WHERE d.time BETWEEN :pastTime AND :actualTime AND d.sensor.name = :name AND d.sensor.type = :type AND d.sensor.device.place = :place order by d.time DESC ")
})
public class DataBean implements Serializable {

    private static final long serialVersionUID = -6828632135642482038L;

    public static final String findByTimeTypePlace     = "DataBean.findByTimeTypePlace";
    public static final String findByTimeNameTypePlace = "DataBean.findByTimeNameTypePlace";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="TIME", updatable=false,nullable=false)
    private Date time;

    @Column(name="VALUE", nullable=false, updatable=false)
    private double value;

    @ManyToOne
    @JoinColumn(name="SENSOR_ID")
    private SensorBean sensor;

    public long getId() {
        return this.id;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public SensorBean getSensor() {
        return sensor;
    }

    public void setDevice(SensorBean sensor) {
        this.sensor = sensor;
    }

}

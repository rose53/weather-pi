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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity: DeviceBean
 *
 */
@Entity
@Table(name="DATA")

public class DataBean implements Serializable {

    private static final long serialVersionUID = -6828632135642482038L;

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
    private SensorBean device;

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

    public SensorBean getDevice() {
        return device;
    }

    public void setDevice(SensorBean device) {
        this.device = device;
    }

}

package de.rose53.weatherpi.sensordata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.rose53.pi.weatherpi.common.ESensorType;

/**
 * Entity implementation class for Entity: DeviceBean
 *
 */
@Entity
@Table(name="SENSOR")
@NamedQueries({
    @NamedQuery(name = SensorBean.findAll,
                query= "SELECT s FROM SensorBean s"),
    @NamedQuery(name = SensorBean.findByPlaceTypeName,
                query= "SELECT s FROM SensorBean s WHERE s.type = :type AND s.name = :name AND s.device.place = :place")
})
public class SensorBean implements Serializable {

    private static final long serialVersionUID = -8749309434246595197L;

    public static final String findAll             = "SensorBean.findAll";
    public static final String findByPlaceTypeName = "SensorBean.findByPlaceTypeName";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(name="NAME", nullable=false, updatable=true)
    private String name;

    @Column(name="TYPE", nullable=false, updatable=true)
    @Enumerated(EnumType.STRING)
    private ESensorType type;

    @ManyToOne
    @JoinColumn(name="DEVICE_ID")
    private DeviceBean device;

    public SensorBean() {
        super();
    }
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ESensorType getType() {
        return type;
    }

    public void setType(ESensorType type) {
        this.type = type;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }
}

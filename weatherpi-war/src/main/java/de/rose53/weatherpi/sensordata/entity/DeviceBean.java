package de.rose53.weatherpi.sensordata.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.rose53.pi.weatherpi.common.ESensorPlace;

/**
 * Entity implementation class for Entity: DeviceBean
 *
 */
@Entity
@Table(name="DEVICE")
@NamedQueries({
    @NamedQuery(name = DeviceBean.findAll,
                query= "SELECT d FROM DeviceBean d"),
    @NamedQuery(name = DeviceBean.findByName,
                query= "SELECT d FROM DeviceBean d where d.name = :name")
})
public class DeviceBean implements Serializable {

    private static final long serialVersionUID = 5986284170575787579L;

    public static final String findAll    = "DeviceBean.findAll";
    public static final String findByName = "DeviceBean.findByName";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(name="NAME", nullable=false, updatable=true)
    private String name;

    @Column(name="PLACE", nullable=false, updatable=true)
    @Enumerated(EnumType.STRING)
    private ESensorPlace place;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="device")
    private List<SensorBean> sensors;

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
    public ESensorPlace getPlace() {
        return place;
    }
    public void setPlace(ESensorPlace place) {
        this.place = place;
    }

    public List<SensorBean> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorBean> sensors) {
        this.sensors = sensors;
    }

}

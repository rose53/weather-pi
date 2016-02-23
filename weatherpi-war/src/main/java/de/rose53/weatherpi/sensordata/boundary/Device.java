package de.rose53.weatherpi.sensordata.boundary;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.weatherpi.sensordata.entity.DeviceBean;

public class Device {

    private String name;

    private ESensorPlace place;

    private List<String> sensors;

    public Device() {
        super();
        this.sensors = Collections.emptyList();
    }

    public Device(DeviceBean bean) {
        super();
        this.name = bean.getName();
        this.place = bean.getPlace();
        this.sensors = new LinkedList<>(bean.getSensors().stream().map(s -> s.getName()).collect(Collectors.toList()));
    }

    public Device(String name, ESensorPlace place) {
        this();
        this.name = name;
        this.place = place;
    }

    public String getName() {
        return name;
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

    public List<String> getSensors() {
        return sensors;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }
}

package de.rose53.weatherpi.sensordata.boundary;

import de.rose53.pi.weatherpi.common.ESensorType;
import de.rose53.weatherpi.sensordata.entity.SensorBean;

public class Sensor {

    private String name;

    private ESensorType type;

    public Sensor() {

    }

    public Sensor(SensorBean sensorBean) {
        this.name = sensorBean.getName();
        this.type = sensorBean.getType();
    }

    public String getName() {
        return name;
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

}

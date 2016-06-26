package de.rose53.weatherpi.sensordata.boundary;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import de.rose53.weatherpi.sensordata.entity.DataBean;

public class SensorDataQueryResult {

    private final LocalDateTime time;
    private final double        value;

    public SensorDataQueryResult(LocalDateTime time, double value) {
        this.time = time;
        this.value = value;
    }

    public SensorDataQueryResult(Date date, double value) {
        this.time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        this.value = value;
    }

    public SensorDataQueryResult(DataBean data) {
        this(data.getLocalDateTime(),data.getValue());
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

}

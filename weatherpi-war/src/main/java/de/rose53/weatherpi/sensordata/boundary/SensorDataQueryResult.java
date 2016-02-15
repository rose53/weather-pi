package de.rose53.weatherpi.sensordata.boundary;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

    public LocalDateTime getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

}

package de.rose53.pi.weatherpi.database;

import java.time.LocalDateTime;

public class SensorDataQueryResult {

    private final LocalDateTime time;
    private final double        value;

    public SensorDataQueryResult(LocalDateTime time, double value) {
        this.time = time;
        this.value = value;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

}

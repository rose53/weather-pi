package de.rose53.pi.weatherpi.common;

import java.time.LocalDateTime;

public class SensorDataImpl implements SensorData {

    private final LocalDateTime dateTime;
    private final double        value;

    public SensorDataImpl(LocalDateTime dateTime, double value) {
        super();
        this.dateTime = dateTime;
        this.value = value;
    }

    @Override
    public LocalDateTime getLocalDateTime() {
        return dateTime;
    }

    @Override
    public double getValue() {
        return value;
    }

}

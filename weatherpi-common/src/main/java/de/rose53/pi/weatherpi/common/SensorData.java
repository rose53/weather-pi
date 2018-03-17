package de.rose53.pi.weatherpi.common;

import java.time.LocalDateTime;

public interface SensorData {

    LocalDateTime getLocalDateTime();
    double        getValue();
}

package de.rose53.weatherpi.sensordata.boundary;

import java.time.LocalDateTime;

public final class LocalDateTimeRange {

    private final LocalDateTime from;
    private final LocalDateTime to;

    public LocalDateTimeRange(LocalDateTime from, LocalDateTime to) {
        super();
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }
}

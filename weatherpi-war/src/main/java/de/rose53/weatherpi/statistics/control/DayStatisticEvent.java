package de.rose53.weatherpi.statistics.control;

import java.time.LocalDate;

public class DayStatisticEvent {

    private final LocalDate day;
    private final Double tMin;
    private final Double tMax;
    private final Double tMed;

    public DayStatisticEvent() {
        this(null,null,null,null);
    }

    public DayStatisticEvent(LocalDate day, Double tMin, Double tMax, Double tMed) {
        super();
        this.day = day;
        this.tMin = tMin;
        this.tMax = tMax;
        this.tMed = tMed;
    }

    public LocalDate getDay() {
        return day;
    }

    public Double gettMin() {
        return tMin;
    }

    public Double gettMax() {
        return tMax;
    }

    public Double gettMed() {
        return tMed;
    }
}

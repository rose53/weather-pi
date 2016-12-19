package de.rose53.weatherpi.statistics.control;

import static java.util.Collections.*;


import java.time.LocalDate;
import java.util.List;

import de.rose53.pi.weatherpi.common.EClimatologicClassificationDay;

public class DayStatisticEvent {

    private final LocalDate                day;
    private final Double                   tMin;
    private final Double                   tMax;
    private final Double                   tMed;
    private final List<EClimatologicClassificationDay> classificationDay;

    public DayStatisticEvent() {
        this(null,null,null,null,emptyList());
    }

    public DayStatisticEvent(LocalDate day, Double tMin, Double tMax, Double tMed, List<EClimatologicClassificationDay> classificationDay) {
        super();
        this.day = day;
        this.tMin = tMin;
        this.tMax = tMax;
        this.tMed = tMed;
        if (classificationDay == null) {
            this.classificationDay = emptyList();
        } else {
            this.classificationDay = classificationDay;
        }
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

    public List<EClimatologicClassificationDay> getClassificationDay() {
        return classificationDay;
    }
}

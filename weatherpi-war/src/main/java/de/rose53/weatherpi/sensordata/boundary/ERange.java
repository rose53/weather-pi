package de.rose53.weatherpi.sensordata.boundary;

import static java.time.LocalDateTime.*;

import java.time.LocalDateTime;

public enum ERange {

    ACTUAL(5) {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusMinutes(5);
        }
    },
    HOUR(30) {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusHours(1);
        }
    },
    DAY(60) {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusDays(1);
        }
    },
    WEEK(24 * 60) {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusWeeks(1);
        }
    },
    MONTH(24 * 60) {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusMonths(1);
        }
    },
    YEAR(24 * 60) {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusYears(1);
        }
    };

    private final int movingAverageMinutes;

    private ERange(int movingAverageMinutes) {
        this.movingAverageMinutes = movingAverageMinutes;
    }

    public int getMovingAverageMinutes() {
        return movingAverageMinutes;
    }

    abstract public LocalDateTime getPastTime();


    public LocalDateTimeRange getRange(boolean movingAverage) {
        return !movingAverage?new LocalDateTimeRange(getPastTime(), now()):new LocalDateTimeRange(getPastTime().minusMinutes(movingAverageMinutes), now());
    }

    public static ERange fromString(String text) {
        for (ERange b : ERange.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return ACTUAL;
    }


}
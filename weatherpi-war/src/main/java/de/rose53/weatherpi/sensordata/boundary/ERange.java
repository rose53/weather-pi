package de.rose53.weatherpi.sensordata.boundary;

import static java.time.LocalDateTime.*;

import java.time.LocalDateTime;

public enum ERange {

    ACTUAL {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusMinutes(5);
        }
    },
    HOUR {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusHours(1);
        }
    },
    DAY {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusDays(1);
        }
    },
    WEEK {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusWeeks(1);
        }
    },
    MONTH {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusMonths(1);
        }
    },
    YEAR {
        @Override
        public LocalDateTime getPastTime() {
            return now().minusYears(1);
        }
    };

    public static ERange fromString(String text) {
        for (ERange b : ERange.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return ACTUAL;
    }

    abstract public LocalDateTime getPastTime();
}
package de.rose53.weatherpi.statistics.control;

import static java.util.Arrays.stream;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * http://www.dwd.de/DE/service/lexikon/Functions/glossar.html?nn=103346&lv2=101334&lv3=101452
 * https://de.wikipedia.org/wiki/W%C3%BCstentag_%28Meteorologie%29
 *
 */
public enum EClimatologicClassificationDay {

    DESERT_DAY(0,"desert") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMax >= 35.0;
        }
    },
    HOT_DAY(1,"hot") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMax >= 30.0;
        }
    },
    TROPICAL_NIGHT(2,"tropical night") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMin >= 20;
        }
    },
    SUMMER_DAY(3,"summer") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMax >= 25.0;
        }
    },
    HEATING_DAY(4,"heating") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMed < 12.0;
        }
    },
    VEGETATION_DAY(5,"vegetation") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMin >= 5.0;
        }
    },
    MAIN_VEGETATION_DAY(6,"main vegetation") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMin >= 10.0;
        }
    },
    FROST_DAY(7,"frost") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMin < 0.0;
        }
    },
    WINTER_DAY(8,"winter") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMed < 0.0;
        }
    },
    ICE_DAY(9,"icy") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMax < 0.0;
        }
    },
    WARM_DAY(10,"warm") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMax > 20.0;
        }
    },
    COLD_DAY(11,"cold") {
        @Override
        public boolean equals(double tMin, double tMax, double tMed) {
            return tMax < 10.0;
        }
    };

    private final int priority;
    private final String twitterFeed;

    private EClimatologicClassificationDay(int priority,String twitterFeed) {
        this.priority    = priority;
        this.twitterFeed = twitterFeed;
    }

    public int getPriority() {
        return priority;
    }

    public String getTwitterFeed() {
        return twitterFeed;
    }


    public abstract boolean equals(double tMin, double tMax, double tMed);

    static public String getTwitterFeed(List<EClimatologicClassificationDay> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().map(o -> o.twitterFeed).reduce((t,u) -> t + "," + u).get();
    }

    static List<EClimatologicClassificationDay> calculateClimatologicClassificationDay(double tMin, double tMax, double tMed) {
        return stream(values()).filter(c -> c.equals(tMin, tMax, tMed))
                               .sorted((o1, o2)-> Integer.compare(o1.getPriority(), o2.getPriority()))
                               .collect(Collectors.toList());
    }
}

package de.rose53.weatherpi.statistics.control;

import static de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay.*;

import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

public class MonthClimatologicClassification {

    private Month                                    month;
    private List<EClimatologicClassificationDay>     statList = new LinkedList<>();

    static public MonthClimatologicClassification build(Month month, List<DayStatisticBean> dayStatistics) {
        MonthClimatologicClassification retVal = new MonthClimatologicClassification();

        retVal.month = month;

        dayStatistics.stream()
                     .filter(s -> Instant.ofEpochMilli(s.getDay().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().getMonth() == month)
                     .forEach(s -> retVal.statList.addAll(calculateClimatologicClassificationDay(s.gettMin(), s.gettMax(), s.gettMed())));
        return retVal;
    }

    public Month getMonth() {
        return month;
    }

    public String getMonthName() {
        return getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    public long getCountForClassification(EClimatologicClassificationDay ccd) {
        return statList.stream().filter(c -> c == ccd).count();
    }

    public long getCountIcy() {
        return getCountForClassification(ICE_DAY);
    }

    public long getCountFrost() {
        return getCountForClassification(FROST_DAY);
    }

    public long getCountWinter() {
        return getCountForClassification(WINTER_DAY);
    }

    public long getCountWarm() {
        return getCountForClassification(WARM_DAY);
    }

    public long getCountSummer() {
        return getCountForClassification(SUMMER_DAY);
    }

    public long getCountHot() {
        return getCountForClassification(HOT_DAY);
    }

    public long getCountTropical() {
        return getCountForClassification(TROPICAL_NIGHT);
    }

    public long getCountHeating() {
        return getCountForClassification(HEATING_DAY);
    }

    public long getCountVegetation() {
        return getCountForClassification(VEGETATION_DAY);
    }

    public long getCountMainVegetation() {
        return getCountForClassification(MAIN_VEGETATION_DAY);
    }

    public long getCountDesert() {
        return getCountForClassification(DESERT_DAY);
    }
}

package de.rose53.weatherpi.statistics.control;

import java.util.Comparator;

public class ClimatologicClassificationDayComparator implements Comparator<EClimatologicClassificationDay> {

    @Override
    public int compare(EClimatologicClassificationDay o0, EClimatologicClassificationDay o1) {
        return Integer.compare(o0.getPriority(), o1.getPriority());
    }

}

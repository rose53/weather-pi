package de.rose53.pi.weatherpi.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovingAverage {


    public List<? extends SensorData> calculate(List<? extends SensorData> sensorDataList, int window) {

        if (sensorDataList == null || sensorDataList.isEmpty()) {
            return Collections.emptyList();
        }


        sensorDataList.sort(new Comparator<SensorData>() {

            @Override
            public int compare(SensorData o1, SensorData o2) {
                return o1.getLocalDateTime().compareTo(o2.getLocalDateTime());
            }

        });

        ArrayList<? extends SensorData> arrayList = new ArrayList<>(sensorDataList);

        // find window size
        LocalDateTime start = null;
        int windowSize = 0;
        for (SensorData sensorData : arrayList) {
            if (start == null) {
                start = sensorData.getLocalDateTime();
            }
            if (start.plusMinutes(window).isBefore(sensorData.getLocalDateTime())) {
                // got it
                break;
            }
            windowSize++;
        }

        if (sensorDataList.size() <= windowSize) {
            return sensorDataList;
        }

        ArrayList<SensorData> retList = new ArrayList<>();
        for (int i = windowSize - 1; i < arrayList.size(); i++) {

            retList.add(new SensorDataImpl(arrayList.get(i).getLocalDateTime(),arrayList.subList(i - windowSize + 1, i).stream().mapToDouble(SensorData::getValue).average().orElse(0)));
        }

        return retList;
    }
}

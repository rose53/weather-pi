package de.rose53.weatherpi.web;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingDouble;

import java.util.List;

import de.rose53.pi.weatherpi.database.SensorDataQueryResult;

class SensorDataQueryResponse {

    private final List<SensorDataQueryResult> sensorData;
    private double maxValue = 0.0;
    private double minValue = 0.0;

    public SensorDataQueryResponse(List<SensorDataQueryResult> sensorData) {
        if (sensorData == null) {
            this.sensorData = emptyList();
        } else {
            this.sensorData = sensorData;
        }
        if (!sensorData.isEmpty()) {
            maxValue = sensorData.stream().max(comparingDouble(data -> data.getValue())).get().getValue();
            minValue = sensorData.stream().min(comparingDouble(data -> data.getValue())).get().getValue();
        }
    }

    public SensorDataQueryResponse(SensorDataQueryResult[] sensorData) {
        this(asList(sensorData));
    }

    public List<SensorDataQueryResult> getSensorData() {
        return sensorData;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }
}

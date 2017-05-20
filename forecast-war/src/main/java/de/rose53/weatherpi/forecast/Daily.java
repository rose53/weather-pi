package de.rose53.weatherpi.forecast;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Daily extends BaseDataPoint  {


    private final DataPoint[] data;

    public Daily(JsonObject jsonObject) {
        super(jsonObject);
        if (jsonObject.containsKey("data") && !jsonObject.isNull("data")) {
            JsonArray dataArray = jsonObject.getJsonArray("data");
            data = new DataPoint[dataArray.size()];
            for (int i = 0; i < dataArray.size(); i++) {
                data[i] = new DataPoint(dataArray.getJsonObject(i));
            }
        } else {
            data = new DataPoint[0];
        }
    }

    public DataPoint[] getData() {
        return data;
    }
}

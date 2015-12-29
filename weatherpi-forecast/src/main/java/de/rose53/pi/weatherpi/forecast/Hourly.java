package de.rose53.pi.weatherpi.forecast;


public class Hourly extends BaseDataPoint  {

    private DataPoint[] data;

    public DataPoint[] getData() {
        return data;
    }

    public void setData(DataPoint[] data) {
        this.data = data;
    }


}

package de.rose53.pi.weatherpi.database;

public class RowData {

    private final double temperatureIndoor;
    private final double pressureIndoor;
    private final double humidityIndoor;
    private final double illuminanceIndoor;
    private final double temperatureOutdoor;
    private final double humidityOutdoor;
    private final double temperatureBirdhouse;
    private final double humidityBirdhouse;

    public RowData(double temperatureIndoor, double pressureIndoor, double humidityIndoor, double illuminanceIndoor,
            double temperatureOutdoor, double humidityOutdoor, double temperatureBirdhouse, double humidityBirdhouse) {
        super();
        this.temperatureIndoor = temperatureIndoor;
        this.pressureIndoor = pressureIndoor;
        this.humidityIndoor = humidityIndoor;
        this.illuminanceIndoor = illuminanceIndoor;
        this.temperatureOutdoor = temperatureOutdoor;
        this.humidityOutdoor = humidityOutdoor;
        this.temperatureBirdhouse = temperatureBirdhouse;
        this.humidityBirdhouse = humidityBirdhouse;
    }

    public double getTemperatureIndoor() {
        return temperatureIndoor;
    }

    public double getPressureIndoor() {
        return pressureIndoor;
    }

    public double getHumidityIndoor() {
        return humidityIndoor;
    }

    public double getIlluminanceIndoor() {
        return illuminanceIndoor;
    }

    public double getTemperatureOutdoor() {
        return temperatureOutdoor;
    }

    public double getHumidityOutdoor() {
        return humidityOutdoor;
    }

    public double getTemperatureBirdhouse() {
        return temperatureBirdhouse;
    }

    public double getHumidityBirdhouse() {
        return humidityBirdhouse;
    }

}

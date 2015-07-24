package de.rose53.pi.weatherpi;

class TemperatureValue {

    private final double temperature;
    private final double accuracy;

    public TemperatureValue(double temperature, double accuracy) {
        super();
        this.temperature = temperature;
        this.accuracy = accuracy;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getAccuracy() {
        return accuracy;
    }
}

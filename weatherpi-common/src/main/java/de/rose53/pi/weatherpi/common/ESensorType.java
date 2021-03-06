package de.rose53.pi.weatherpi.common;

public enum ESensorType {
    TEMPERATURE,
    HUMIDITY,
    PRESSURE,
    ILLUMINANCE,
    WINDSPEED,
    WINDDIRECTION,
    LIGHTNING,
    DUST_PM10,
    DUST_PM25;

    public static ESensorType fromString(String text) {
        for (ESensorType b : ESensorType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
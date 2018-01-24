package de.rose53.pi.weatherpi.common;

public enum ESensorPlace {
    INDOOR,
    OUTDOOR,
    BIRDHOUSE,
    ANEMOMETER,
    LIGHTNING,
    DUSTSENSOR;

    public static ESensorPlace fromString(String text) {
        for (ESensorPlace b : ESensorPlace.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return ESensorPlace.BIRDHOUSE;
    }
}
package de.rose53.pi.weatherpi;

public enum ESensorPlace {
    INDOOR,
    OUTDOOR,
    BIRDHOUSE;

    public static ESensorPlace fromString(String text) {
        for (ESensorPlace b : ESensorPlace.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return ESensorPlace.INDOOR;
    }
}
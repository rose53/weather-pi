package de.rose53.pi.weatherpi.display;

public enum EBlinkRate {

    BLINK_OFF(0),
    BLINK_2HZ(1),
    BLINK_1HZ(2),
    BLINK_HALFHZ(3);

    private final int rate;

    private EBlinkRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }
}
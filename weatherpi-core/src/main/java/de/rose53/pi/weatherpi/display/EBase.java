package de.rose53.pi.weatherpi.display;

public enum EBase {
    BIN(2),
    OCT(8),
    DEC(10),
    HEX(16);

    private final int base;

    private EBase(int base) {
        this.base = base;
    }

    public int getBase() {
        return base;
    }
}
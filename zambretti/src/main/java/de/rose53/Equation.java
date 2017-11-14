package de.rose53;

public enum Equation implements EquationStrategy<Double> {

    /**
     * Falling barometer
     */
    Zf(p -> 130.28 - 0.1236 * p ),
    Zs(p -> 147.24 - 0.1331 * p ),
    Zr(p -> 179.35 - 0.155 * p);

    private EquationStrategy<Double> equationStrategy;

    Equation(final EquationStrategy<Double> equationStrategy) {
        this.equationStrategy = equationStrategy;
    }

    @Override
    public Double calculate(Double p) {
        return equationStrategy.calculate(p);
    }

    static public long z(Double pressureTendency, Double pressure) {

        Double z = null;
        if (pressureTendency < 0) {
            z = Zf.calculate(pressure);
        } else if (pressureTendency > 0) {
            z = Zr.calculate(pressure);
        } else {
            z = Zs.calculate(pressure);
        }
        return Math.round(z.doubleValue());
    }
}

package de.rose53.pi.weatherpi.common;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * http://www.opengeiger.de/Feinstaub/FeuchteKompensation.pdf
 *
 */
@ApplicationScoped
public class HumidityCompensation {

    private static final double A = 1.0;
    private static final double B = 0.25;

    /**
     *
     * @param humidity in [%]
     * @param windVelocity in [km/h]
     * @return
     */
    public double compensate(double humidity, double pm) {

        humidity = humidity / 100.0;

        double gf = A + (B * humidity * humidity) / (1 - humidity);

        return pm / gf;
    }
}

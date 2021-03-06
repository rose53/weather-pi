package de.rose53.pi.weatherpi.common;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * https://de.wikipedia.org/wiki/Windchill
 *
 */
@ApplicationScoped
public class WindchillCalculator {

    /**
     *
     * @param airTemperature in [C°]
     * @param windVelocity in [km/h]
     * @return
     */
    public Double calculate(double airTemperature, double windVelocity) {

        if (airTemperature > 10.0) {
            return null;
        }

        if (windVelocity < 5) {
            return null;
        }

        return 13.12 + 0.6215 * airTemperature + (0.3965 * airTemperature - 11.37) * Math.pow(windVelocity, 0.16) ;
    }
}

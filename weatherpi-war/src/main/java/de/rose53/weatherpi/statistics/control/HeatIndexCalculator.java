package de.rose53.weatherpi.statistics.control;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 *
 * http://www.climate-service-center.de/049116/index_0049116.html.de
 *
 */
@ApplicationScoped
public class HeatIndexCalculator {

    private static final double C1 = -8.7847;
    private static final double C2 = 1.6114;
    private static final double C3 = 2.338;
    private static final double C4 = -0.1461;
    private static final double C5 = -1.231E-2;
    private static final double C6 = -1.6425E-2;
    private static final double C7 = 2.2117E-3;
    private static final double C8 = 7.2546E-4;
    private static final double C9 = -3.582E-6;

    @Inject
    Logger logger;

    public Long calculate(double temperature, double humidity) {

        if (temperature < 22.0) {
            logger.debug("calculate: temperature too low, returning null");
            return null;
        }
        return Math.round(  C1 + C2 * temperature + C3 * humidity + C4 * temperature * humidity + C5 * temperature * temperature
                          + C6 * humidity * humidity + C7 * temperature * temperature * humidity + C8 * temperature * humidity * humidity
                          + C9 * temperature * temperature * humidity * humidity);
    }
}

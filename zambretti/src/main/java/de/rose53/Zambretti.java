package de.rose53;

import java.io.IOException;
import java.io.InputStream;
import java.time.Month;
import java.util.Properties;


/**
 * https://web.archive.org/web/20110610213848/http:/www.meteormetrics.com/zambretti.htm
 *
 *
 *
 */
public class Zambretti {

    private static Properties forcastProperties = new Properties();

    static {
        try (InputStream inputStream = Zambretti.class.getClassLoader().getResourceAsStream("forecast.properties")) {
            forcastProperties.load(inputStream);
        } catch (IOException e) {
        }
    }

    /**
     * @param pressureTendency the pressure tendency
     *        <ul>
     *        <li>  < 0: falling</li>
     *        <li> == 0: steady</li>
     *        <li>  > 0: rising</li>
     *        </ul>
     * @param pressure is Sea Level Adjusted (Relative) pressure in hPa or mB
     * @param month  the actual month
     * @return
     */
    public String forecast(Double pressureTendency, Double pressure, Month month) {

        if (pressureTendency == null || pressure == null || month == null) {
            return null;
        }

        if (pressure < 947 || pressure > 1050) {
            return null;
        }

        long z = Equation.z(pressureTendency, pressure);

        // if falling and winter lower Z by one
        if (pressureTendency < 0 && isWinter(month)) {
            z = z - 1;
        }

        // if rising and summer increment Z by one
        if (pressureTendency > 0 && isSummer(month)) {
            z = z + 1;
        }
        return forcastProperties.getProperty(Long.toString(z));
    }

    private boolean isWinter(Month month) {
        if (Month.DECEMBER == month) {
            return true;
        }
        if (Month.JANUARY == month) {
            return true;
        }
        if (Month.FEBRUARY == month) {
            return true;
        }
        return false;
    }

    private boolean isSummer(Month month) {
        if (Month.JUNE == month) {
            return true;
        }
        if (Month.JULY == month) {
            return true;
        }
        if (Month.AUGUST == month) {
            return true;
        }
        return false;
    }

}

package de.rose53.pi.weatherpi.common;

import javax.enterprise.context.ApplicationScoped;

/**
*
* https://de.wikipedia.org/wiki/Hitzeindex
*
*/
@ApplicationScoped
public class HeatindexCalculator {

    /**
    *
    * @param airTemperature in [C°]
    * @param relativeHumidity in [%]
    * @return
    */
   public Double calculate(double airTemperature, double relativeHumidity) {

       if (airTemperature < 27.0) {
           return null;
       }

       if (relativeHumidity < 40) {
           return null;
       }

       return - 8.784695
              + 1.61139411 * airTemperature
              + 2.338549 * relativeHumidity
              - 0.14611605 * airTemperature * relativeHumidity
              - 1.2308094 * Math.pow(10,-2) * airTemperature * airTemperature
              - 1.6424828 * Math.pow(10,-2) * relativeHumidity * relativeHumidity
              + 2.211732 * Math.pow(10,-3) * airTemperature * airTemperature * relativeHumidity
              + 7.2546 * Math.pow(10,-4) * airTemperature * relativeHumidity * relativeHumidity
              - 3.582 * Math.pow(10,-6) * airTemperature * airTemperature * relativeHumidity * relativeHumidity;
   }
}

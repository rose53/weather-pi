# WeatherPi

Java 8 program to display various sensors or components on a [7-segment display](https://learn.adafruit.com/adafruit-led-backpack/1-2-inch-7-segment-backpack). For the communication with the display and the sensors, [Pi4J](http://pi4j.com/) is used.

## Sensors
The list of actually implemented sensors.

*   BMP085: Temperature and Pressure
*   DHT22: Temperature and Humidity
*   LDR (VT43N2): Illuminance
*   TGS2600: Air quality

## Components
The list of actually implemented components.

*   Clock: Shows the actual time

## Database
The sensor data is written to a [MySQL](http://www.mysql.com/) database for later processing of the data.

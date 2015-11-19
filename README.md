# ![WeatherPi](./src/main/resources/images/weather_pi_logo.png "WeatherPi") WeatherPi

Java 8 program to display various sensors or components connected to a RaspberryPi on a [7-segment display](https://learn.adafruit.com/adafruit-led-backpack/1-2-inch-7-segment-backpack). For the communication with the display and the sensors, [Pi4J](http://pi4j.com/) is used. Additionally, there is a touch screen displaying data.
The outdoor sensors are connected to an ESP8266 WiFi module.

## Sensors
The list of actually implemented sensors. The indoor sensors are connected to the RaspberryPi.

Indoor:
*   BMP085: Temperature and Pressure
*   HTU21D: Temperature and Humidity
*   LDR (VT43N2): Illuminance
*   TGS2600: Air quality

Outdoor
*   DHT22: Temperature and Humidity

## Components
The list of actually implemented components.

*   Clock: Shows the actual time

## MQTT
Both, the RaspberryPi and the ESP8266 are publishing their sensor data via MQTT to a local installed
[Mosquitto broker](http://www.eclipse.org/mosquitto/). [Here](http://www.mymakerprojects.com/index.php/setup-mosquitto-mqtt-server-on-the-raspberry-pi/) are
some instructions for the installation of the Mosquitto MQTT Server on the RaspberryPi.


## Database
The sensor data is written to a [MySQL](http://www.mysql.com/) database for later processing of the data.


## 7'' Touch Screen
The touch screen used is the 7'' display set from [Polin](http://www.pollin.de/shop/dt/NTMwOTc4OTk-/Bauelemente_Bauteile/Aktive_Bauelemente/Displays/7_17_78_cm_Display_Set_mit_Touchscreen_LS_7T_HDMI_DVI_VGA_CVBS.html). It is connected via the HDMI port to the RaspberryPi.

![](./src/main/resources/images/touch_screen.jpg "Touch Screen")

Chrome is started in Kiosk mode to display the data. Updates of the sensor data is pushed via WebSockets.

## Twitter
Actual weather data is pushed via the [Twitter REST API](https://dev.twitter.com/rest/public). This is done using the
[Twitter4J](http://twitter4j.org/en/index.html) library.

## ForecastIO
Reading weather forecast from [ForecastIO](http://forecast.io) using their [API](https://developer.forecast.io/docs/v2). The data is queried each hour and cached internally. The icons used for displaying some weather informations are from [Adam Whitcroft](http://adamwhitcroft.com/climacons/)

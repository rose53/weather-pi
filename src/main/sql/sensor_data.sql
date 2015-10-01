CREATE TABLE `SENSOR_DATA` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The PK',
  `TIME` datetime NOT NULL COMMENT 'The time when the sensor data was entered.',
  `TEMPERATURE` double NOT NULL COMMENT 'The temperature in celsius degrees.',
  `PRESSURE` double NOT NULL COMMENT 'The pressure relative to the sealevel in hPa',
  `HUMIDITY` double NOT NULL COMMENT 'The humidity ',
  `ILLUMINATION` double NOT NULL COMMENT 'The illumination in LUX',
  `TEMPERATURE_OUT` double NOT NULL COMMENT 'The outdoor temperature in celsius degrees.',
  `HUMIDITY_OUT` double NOT NULL COMMENT 'The outdoor humidity ',
  PRIMARY KEY (`ID`)
);

CREATE TABLE `SENSOR` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The PK',
  `NAME` varchar(256) NOT NULL COMMENT 'The name of the sensor.',
  `TYPE` varchar(256) NULL COMMENT 'The type of the sensor.',
  `DEVICE_ID` INT(11) NOT NULL COMMENT 'FK to device',
  PRIMARY KEY (`ID`),
  FOREIGN KEY `FK1_SENSOR` (`DEVICE_ID`) REFERENCES `DEVICE`(`ID`),
  UNIQUE KEY `UK1_SENSOR` (`NAME`,`TYPE`)
);
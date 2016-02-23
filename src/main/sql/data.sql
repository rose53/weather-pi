CREATE TABLE `DATA` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The PK',
  `TIME` datetime NOT NULL COMMENT 'The time when the sensor data was entered.',
  `VALUE` double NOT NULL COMMENT 'The value of the data.',
  `SENSOR_ID` INT(11) NOT NULL COMMENT 'FK to the sensor',
  PRIMARY KEY (`ID`),
  FOREIGN KEY `FK1_DATA` (`SENSOR_ID`) REFERENCES `SENSOR`(`ID`),
  INDEX `IDX1_DATA` (`TIME`)
);

CREATE TABLE `DEVICE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The PK',
  `NAME` varchar(256) NOT NULL COMMENT 'The name of the device.',
  `PLACE` varchar(256) NULL COMMENT 'The place of the device.',
  PRIMARY KEY (`ID`),
  UNIQUE KEY UK1_DEVICE (`NAME`),
  UNIQUE KEY UK2_DEVICE (`PLACE`)
);
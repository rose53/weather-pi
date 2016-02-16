CREATE TABLE `CONFIGURATION` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The PK',
  `KEY` varchar(256) NOT NULL COMMENT 'The configuration key.',
  `VALUE` varchar(1024) NULL COMMENT 'The configuration value.',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK1_CONFIGURATION` (`KEY`)
);
-- switch to the content database
use world_content;

-- 2.1.1

-- Stats
ALTER TABLE `stat` 
CHANGE COLUMN `stat_mob_base` `mob_base` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `stat_mob_level_increase` `mob_level_increase` INT(11) NULL DEFAULT NULL ,
CHANGE COLUMN `stat_mob_level_percent_increase` `mob_level_percent_increase` FLOAT NULL DEFAULT NULL ,
ADD COLUMN `min` INT NOT NULL DEFAULT '0' AFTER `mob_level_percent_increase`,
ADD COLUMN `maxstat` VARCHAR(45) DEFAULT NULL AFTER `min`,
ADD COLUMN `shiftPlayerOnly` TINYINT(1) NOT NULL DEFAULT '0' AFTER `maxstat`,
ADD COLUMN `shiftValue` INT NULL AFTER `shiftPlayerOnly`,
ADD COLUMN `shiftReverseValue` INT NULL AFTER `shiftValue`,
ADD COLUMN `shiftInterval` INT NULL AFTER `shiftReverseValue`,
ADD COLUMN `isShiftPercent` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftInterval`,
ADD COLUMN `onMaxHit` VARCHAR(45) NULL AFTER `isShiftPercent`,
ADD COLUMN `onMinHit` VARCHAR(45) NULL AFTER `onMaxHit`,
ADD COLUMN `shiftReq1` VARCHAR(45) NULL AFTER `onMinHit`,
ADD COLUMN `shiftReq1State` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftReq1`,
ADD COLUMN `shiftReq1SetReverse` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftReq1State`,
ADD COLUMN `shiftReq2` VARCHAR(45) NULL AFTER `shiftReq1SetReverse`,
ADD COLUMN `shiftReq2State` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftReq2`,
ADD COLUMN `shiftReq2SetReverse` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftReq2State`,
ADD COLUMN `shiftReq3` VARCHAR(45) NULL AFTER `shiftReq2SetReverse`,
ADD COLUMN `shiftReq3State` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftReq3`,
ADD COLUMN `shiftReq3SetReverse` TINYINT(1) NOT NULL DEFAULT '0' AFTER `shiftReq3State`;

UPDATE `stat` SET `shiftValue` = 0, `shiftReverseValue` = 0, `shiftInterval` = 0 where min = 0;

INSERT INTO `stat` (`name`, `type`, `stat_function`, `mob_base`, `mob_level_increase`, `mob_level_percent_increase`, `min`, `maxstat`, `shiftPlayerOnly`, `shiftValue`, `shiftReverseValue`, `shiftInterval`, `isShiftPercent`, `shiftReq1`, `shiftReq1State`, `shiftReq1SetReverse`, `shiftReq2`, `shiftReq2State`, `shiftReq2SetReverse`) VALUES ('health', '2', 'Health', '0', '0', '0', '0', '', '0', '3', '0', '2', '1', 'deadstate', '0', '0', 'combatstate', '0', '0');
INSERT INTO `stat` (`name`, `type`, `stat_function`, `mob_base`, `mob_level_increase`, `mob_level_percent_increase`, `min`, `maxstat`, `shiftPlayerOnly`, `shiftValue`, `shiftReverseValue`, `shiftInterval`, `isShiftPercent`, `shiftReq1`, `shiftReq1State`) VALUES ('mana', '2', 'Mana', '0', '0', '0', '0', '', '0', '3', '0', '2', '1', 'deadstate', '0');
INSERT INTO `stat` (`name`, `type`, `stat_function`, `mob_base`, `mob_level_increase`, `mob_level_percent_increase`, `min`, `maxstat`, `shiftPlayerOnly`, `shiftValue`, `shiftReverseValue`, `shiftInterval`, `isShiftPercent`, `shiftReq1`, `shiftReq1State`) VALUES ('movement_speed', '0', '~ none ~', '7', '0', '0', '0', '', '0', '0', '0', '0', '0', '', '0');

-- Editor Options
INSERT INTO `editor_option` (`optionType`, `deletable`) VALUES ('Stat Shift Requirement', '0');
INSERT INTO `editor_option_choice` (`optionTypeID`, `choice`) VALUES (LAST_INSERT_ID(), 'combatstate'), (LAST_INSERT_ID(), 'deadstate');

INSERT INTO `editor_option` (`optionType`, `deletable`) VALUES ('Stat Shift Action', '0');
INSERT INTO `editor_option_choice` (`optionTypeID`, `choice`) VALUES (LAST_INSERT_ID(), 'death');

INSERT INTO editor_option_choice (`optionTypeID`, `choice`) VALUES ((SELECT id from editor_option where optionType = 'Stat Functions') , 'Health');
INSERT INTO editor_option_choice (`optionTypeID`, `choice`) VALUES ((SELECT id from editor_option where optionType = 'Stat Functions') , 'Mana');

ALTER TABLE `world_content`.`character_create_template` 
DROP COLUMN `mana`,
DROP COLUMN `health`;

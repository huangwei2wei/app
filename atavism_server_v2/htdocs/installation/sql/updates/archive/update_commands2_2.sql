-- switch to the content database
use world_content;

-- 2.2
INSERT INTO `stat` (`name`, `type`, `stat_function`, `mob_base`, `mob_level_increase`, `mob_level_percent_increase`, `min`, `maxstat`, `shiftPlayerOnly`, `shiftValue`, `shiftReverseValue`, `shiftInterval`, `isShiftPercent`, `shiftReq1State`, `shiftReq1SetReverse`, `shiftReq2State`, `shiftReq2SetReverse`, `shiftReq3State`, `shiftReq3SetReverse`) 
VALUES ('attack_speed', '0', '~ none ~', '2000', '0', '0', '1000', '10000', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');

INSERT INTO character_create_stats (character_create_id, stat, value, levelIncrease, levelPercentIncrease) 
SELECT DISTINCT id, 'attack_speed', 2000, 0, 0 FROM character_create_template;

ALTER TABLE `stat` 
CHANGE COLUMN `shiftPlayerOnly` `shiftTarget` SMALLINT NULL DEFAULT '0';

ALTER TABLE `claim` ADD COLUMN `claimItemTemplate` INT(11) NULL DEFAULT -1 AFTER `currency`;

-- admin
ALTER TABLE `admin`.`islands` 
ADD COLUMN `populationLimit` INT NULL DEFAULT -1 AFTER `size`, RENAME TO  `admin`.`instance_template`;

-- Mana/health - these may cause an error so they go last
ALTER TABLE `character_create_template` 
DROP COLUMN `mana`,
DROP COLUMN `health`;

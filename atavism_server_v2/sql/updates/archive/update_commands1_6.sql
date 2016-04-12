-- switch to the content database
use world_content;

-- 1.6

-- Crafting
CREATE TABLE `crafting_recipes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NULL,
  `icon` VARCHAR(256) NULL,
  `resultItemID` INT NULL,
  `skillID` INT NULL,
  `skillLevelReq` INT NULL,
  `stationReq` VARCHAR(45) NULL,
  `recipeItemID` INT NULL,
  `qualityChangeable` TINYINT(1) NULL,
  `allowDyes` TINYINT(1) NULL,
  `allowEssences` TINYINT(1) NULL,
  `component1` INT NULL DEFAULT -1,
  `component1count` INT NULL,
  `component2` INT NULL DEFAULT -1,
  `component2count` INT NULL,
  `component3` INT NULL DEFAULT -1,
  `component3count` INT NULL,
  `component4` INT NULL DEFAULT -1,
  `component4count` INT NULL,
  `component5` INT NULL DEFAULT -1,
  `component5count` INT NULL,
  `component6` INT NULL DEFAULT -1,
  `component6count` INT NULL,
  `component7` INT NULL DEFAULT -1,
  `component7count` INT NULL,
  `component8` INT NULL DEFAULT -1,
  `component8count` INT NULL,
  `component9` INT NULL DEFAULT -1,
  `component9count` INT NULL,
  `component10` INT NULL DEFAULT -1,
  `component10count` INT NULL,
  `component11` INT NULL DEFAULT -1,
  `component11count` INT NULL,
  `component12` INT NULL DEFAULT -1,
  `component12count` INT NULL,
  `component13` INT NULL DEFAULT -1,
  `component13count` INT NULL,
  `component14` INT NULL DEFAULT -1,
  `component14count` INT NULL,
  `component15` INT NULL DEFAULT -1,
  `component15count` INT NULL,
  `component16` INT NULL DEFAULT -1,
  `component16count` INT NULL,
  PRIMARY KEY (`id`));

-- Stats
ALTER TABLE `stat` 
ADD COLUMN `stat_mob_base` INT NOT NULL DEFAULT 1 AFTER `stat_function`,
ADD COLUMN `stat_mob_level_increase` INT NOT NULL DEFAULT 0 AFTER `stat_mob_base`,
ADD COLUMN `stat_mob_level_percent_increase` FLOAT NOT NULL DEFAULT 0 AFTER `stat_mob_level_increase`;

ALTER TABLE `character_create_stats` 
ADD COLUMN `levelIncrease` FLOAT NOT NULL DEFAULT 0 AFTER `value`,
ADD COLUMN `levelPercentIncrease` FLOAT NOT NULL DEFAULT 0 AFTER `levelIncrease`;

ALTER TABLE `coordinated_effects` 
CHANGE COLUMN `coordType` `prefab` VARCHAR(256) NOT NULL ;

DROP TABLE `coord_melee_strike_effects`;
DROP TABLE `coord_particle_effects`;
DROP TABLE `coord_projectile_effects`;
DROP TABLE `island_developers`;

ALTER TABLE `stat_effects`
DROP COLUMN `modifyStatsbyValue`, 
CHANGE COLUMN `modifyStatsByPercent` `modifyStatsByPercent` TINYINT(1) NULL DEFAULT NULL,
CHANGE COLUMN `stat1Modification` `stat1Modification` FLOAT NULL DEFAULT NULL ,
CHANGE COLUMN `stat2Modification` `stat2Modification` FLOAT NULL DEFAULT NULL ,
CHANGE COLUMN `stat3Modification` `stat3Modification` FLOAT NULL DEFAULT NULL ,
ADD COLUMN `stat4Name` VARCHAR(32) NULL AFTER `stat3Modification`,
ADD COLUMN `stat4Modification` FLOAT NULL AFTER `stat4Name`,
ADD COLUMN `stat5Name` VARCHAR(32) NULL AFTER `stat4Modification`,
ADD COLUMN `stat5Modification` FLOAT NULL AFTER `stat5Name`;
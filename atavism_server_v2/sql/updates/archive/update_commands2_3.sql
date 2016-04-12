-- switch to the content database
use world_content;

-- 2.3
ALTER TABLE character_create_template ADD UNIQUE `unique_index`(`race`, `aspect`);

ALTER TABLE `crafting_recipes` 
ADD COLUMN `creationTime` INT NULL DEFAULT 0 AFTER `stationReq`,
ADD COLUMN `skillLevelMax` INT NULL AFTER `skillLevelReq`,
ADD COLUMN `layoutReq` TINYINT(1) NULL DEFAULT 1;

ALTER TABLE `claim_object` 
ADD COLUMN `objectState` VARCHAR(64) NULL AFTER `itemID`;

CREATE TABLE `resource_drop` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `resource_template` INT NULL,
  `item` INT NULL,
  `min` INT NULL,
  `max` INT NULL,
  `chance` FLOAT NULL,
  PRIMARY KEY (`id`));
  
CREATE TABLE `resource_node_template` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `skill` INT NULL,
  `skillLevel` INT NULL,
  `skillLevelMax` INT NULL,
  `weaponReq` VARCHAR(45) NULL,
  `equipped` TINYINT(1) NULL,
  `gameObject` VARCHAR(128) NULL,
  `coordEffect` VARCHAR(128) NULL,
  `instance` VARCHAR(45) NULL,
  `respawnTime` INT NULL,
  `locX` FLOAT NULL,
  `locY` FLOAT NULL,
  `locZ` FLOAT NULL,
  `harvestCount` INT NULL,
  PRIMARY KEY (`id`));
  
CREATE TABLE `resource_node_spawn` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `instance` VARCHAR(45) NULL,
  `resourceTemplate` INT NULL,
  `respawnTime` INT NULL,
  `locX` FLOAT NULL,
  `locY` FLOAT NULL,
  `locZ` FLOAT NULL,
  PRIMARY KEY (`id`));
  
INSERT INTO `game_setting` (`name`, `datatype`, `value`) VALUES ('PLAYER_BAG_COUNT', 'int', '4'),
('PLAYER_DEFAULT_BAG_SIZE', 'int', '16'),
('MOB_DEATH_EXP', 'bool', 'true');
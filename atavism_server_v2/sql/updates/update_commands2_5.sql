-- switch to the content database
use world_content;

-- 2.5
ALTER TABLE `abilities` 
ADD COLUMN `reqTarget` TINYINT(1) NULL DEFAULT 1 AFTER `aoeRadius`;

ALTER TABLE `effects` 
ADD COLUMN `intValue1` INT(11) NOT NULL DEFAULT 0 AFTER `pulseParticle`,
ADD COLUMN `intValue2` INT(11) NOT NULL DEFAULT 0 AFTER `intValue1`,
ADD COLUMN `intValue3` INT(11) NOT NULL DEFAULT 0 AFTER `intValue2`,
ADD COLUMN `intValue4` INT(11) NOT NULL DEFAULT 0 AFTER `intValue3`,
ADD COLUMN `intValue5` INT(11) NOT NULL DEFAULT 0 AFTER `intValue4`,
ADD COLUMN `floatValue1` FLOAT NOT NULL DEFAULT 0 AFTER `intValue5`,
ADD COLUMN `floatValue2` FLOAT NOT NULL DEFAULT 0 AFTER `floatValue1`,
ADD COLUMN `floatValue3` FLOAT NOT NULL DEFAULT 0 AFTER `floatValue2`,
ADD COLUMN `floatValue4` FLOAT NOT NULL DEFAULT 0 AFTER `floatValue3`,
ADD COLUMN `floatValue5` FLOAT NOT NULL DEFAULT 0 AFTER `floatValue4`,
ADD COLUMN `stringValue1` VARCHAR(256) NOT NULL AFTER `floatValue5`,
ADD COLUMN `stringValue2` VARCHAR(256) NOT NULL AFTER `stringValue1`,
ADD COLUMN `stringValue3` VARCHAR(256) NOT NULL AFTER `stringValue2`,
ADD COLUMN `stringValue4` VARCHAR(256) NOT NULL AFTER `stringValue3`,
ADD COLUMN `stringValue5` VARCHAR(256) NOT NULL AFTER `stringValue4`,
ADD COLUMN `boolValue1` TINYINT(1) NOT NULL DEFAULT 0 AFTER `stringValue5`,
ADD COLUMN `boolValue2` TINYINT(1) NOT NULL DEFAULT 0 AFTER `boolValue1`,
ADD COLUMN `boolValue3` TINYINT(1) NOT NULL DEFAULT 0 AFTER `boolValue2`,
ADD COLUMN `boolValue4` TINYINT(1) NOT NULL DEFAULT 0 AFTER `boolValue3`,
ADD COLUMN `boolValue5` TINYINT(1) NOT NULL DEFAULT 0 AFTER `boolValue4`;

UPDATE effects inner join damage_effects on effects.id = damage_effects.id set effects.stringValue1 = damage_effects.damageProperty, effects.stringValue2 = damage_effects.damageType, 
effects.floatValue1 = damage_effects.damageMod, effects.floatValue2 = damage_effects.healthTransferRate, effects.intValue1 = damage_effects.damageAmount,
effects.intValue2 = damage_effects.bonusDamageEffect, effects.intValue3 = damage_effects.bonusDamageAmount;
 
UPDATE effects inner join heal_effects on effects.id = heal_effects.id set effects.stringValue1 = heal_effects.healProperty,
effects.floatValue1 = heal_effects.healthTransferRate, effects.intValue1 = heal_effects.healAmount; 

UPDATE effects inner join stat_effects on effects.id = stat_effects.id set effects.stringValue1 = stat_effects.stat1Name,
effects.floatValue1 = stat_effects.stat1Modification, effects.stringValue2 = stat_effects.stat2Name,
effects.floatValue2 = stat_effects.stat2Modification, effects.stringValue3 = stat_effects.stat3Name,
effects.floatValue3 = stat_effects.stat3Modification, effects.stringValue4 = stat_effects.stat4Name,
effects.floatValue4 = stat_effects.stat4Modification, effects.stringValue5 = stat_effects.stat5Name,
effects.floatValue5 = stat_effects.stat5Modification, effects.boolValue1 = stat_effects.modifyStatsByPercent;

UPDATE effects set effectMainType = 'Restore' where effectMainType = 'Heal';

DROP TABLE `alter_skill_current_effects`;
DROP TABLE `cooldown_effects`;
DROP TABLE `create_item_effects`;
DROP TABLE `damage_effects`;
DROP TABLE `damage_mitigation_effects`;
DROP TABLE `despawn_effects`;
DROP TABLE `faction_effects`;
DROP TABLE `heal_effects`;
DROP TABLE `message_effects`;
DROP TABLE `property_effects`;
DROP TABLE `spawn_effects`;
DROP TABLE `stat_effects`;
DROP TABLE `stun_effects`;
DROP TABLE `task_effects`;
DROP TABLE `teleport_effects`;

CREATE TABLE `build_object_template` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `icon` VARCHAR(256) NOT NULL,
  `skill` INT NOT NULL DEFAULT 0,
  `skillLevelReq` INT(11) NOT NULL DEFAULT 0,
  `weaponReq` VARCHAR(45) NOT NULL,
  `distanceReq` FLOAT NOT NULL DEFAULT 1,
  `firstStageID` INT NOT NULL DEFAULT 0,
  `availableFromItemOnly` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));
  
CREATE TABLE `build_object_stage` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `gameObject` VARCHAR(256) NOT NULL,
  `nextStage` INT(11) NOT NULL DEFAULT -1,
  `buildTimeReq` FLOAT NOT NULL DEFAULT 0,
  `itemReq1` INT(11) NULL,
  `itemReq1Count` INT(11) NULL,
  `itemReq2` INT(11) NULL,
  `itemReq2Count` INT(11) NULL,
  `itemReq3` INT NULL,
  `itemReq3Count` INT NULL,
  `itemReq4` INT NULL,
  `itemReq4Count` INT NULL,
  `itemReq5` INT NULL,
  `itemReq5Count` INT NULL,
  `itemReq6` INT NULL,
  `itemReq6Count` INT NULL,
  PRIMARY KEY (`id`));
  
ALTER TABLE `claim_object` 
ADD COLUMN `template` INT NULL AFTER `claimID`,
ADD COLUMN `stage` INT NOT NULL DEFAULT 0 AFTER `template`,
ADD COLUMN `complete` TINYINT(1) NOT NULL DEFAULT 0 AFTER `stage`,
ADD COLUMN `objectState` varchar(64) DEFAULT NULL AFTER `itemID`,
ADD COLUMN `health` INT NULL DEFAULT 1 AFTER `objectState`,
ADD COLUMN `maxHealth` INT NULL DEFAULT 1 AFTER `health`,
ADD COLUMN `item1` INT NULL DEFAULT -1 AFTER `maxHealth`,
ADD COLUMN `item1Count` INT NULL DEFAULT 0 AFTER `item1`,
ADD COLUMN `item2` INT NULL DEFAULT -1 AFTER `item1Count`,
ADD COLUMN `item2Count` INT NULL DEFAULT 0 AFTER `item2`,
ADD COLUMN `item3` INT NULL DEFAULT -1 AFTER `item2Count`,
ADD COLUMN `item3Count` INT NULL DEFAULT 0 AFTER `item3`,
ADD COLUMN `item4` INT NULL DEFAULT -1 AFTER `item3Count`,
ADD COLUMN `item4Count` INT NULL DEFAULT 0 AFTER `item4`,
ADD COLUMN `item5` INT NULL DEFAULT -1 AFTER `item4Count`,
ADD COLUMN `item5Count` INT NULL DEFAULT 0 AFTER `item5`,
ADD COLUMN `item6` INT NULL DEFAULT -1 AFTER `item5Count`,
ADD COLUMN `item6Count` INT NULL DEFAULT 0 AFTER `item6`;

ALTER TABLE `patrol_paths` 
DROP COLUMN `pauseSpot2`,
DROP COLUMN `pauseSpot1`,
DROP COLUMN `pauseDuration`,
DROP COLUMN `lastMarkerNum`,
DROP COLUMN `firstMarkerNum`,
DROP COLUMN `baseMarker`, 
ADD COLUMN `startingPoint` TINYINT(1) NOT NULL AFTER `name`,
ADD COLUMN `locX` FLOAT NOT NULL AFTER `travelReverse`,
ADD COLUMN `locY` FLOAT NOT NULL AFTER `locX`,
ADD COLUMN `locZ` FLOAT NOT NULL AFTER `locY`,
ADD COLUMN `lingerTime` FLOAT NOT NULL DEFAULT 0 AFTER `locZ`,
ADD COLUMN `nextPoint` INT NOT NULL DEFAULT -1 AFTER `lingerTime`, RENAME TO `patrol_path`;

CREATE TABLE `claim_permission` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `claimID` INT NOT NULL,
  `playerOid` BIGINT NULL,
  `playerName` VARCHAR(45) NULL,
  `permissionLevel` INT NULL,
  `dateGiven` DATETIME NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `mob_stat` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `mobTemplate` INT NOT NULL,
  `stat` VARCHAR(45) NOT NULL,
  `value` INT NOT NULL,
  PRIMARY KEY (`id`));
  
ALTER TABLE `spawn_data` 
CHANGE COLUMN `locX` `locX` FLOAT(8,4) NULL DEFAULT NULL ,
CHANGE COLUMN `locY` `locY` FLOAT(8,4) NULL DEFAULT NULL ,
CHANGE COLUMN `locZ` `locZ` FLOAT(8,4) NULL DEFAULT NULL ;

ALTER TABLE `claim` 
CHANGE COLUMN `currency` `currency` INT(11) NULL DEFAULT 1 ,
ADD COLUMN `sellerName` VARCHAR(45) NULL AFTER `currency`;
  
  
-- Backdating implementation --

ALTER TABLE abilities ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE achievement_categories ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE achievement_criteria ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE achievement_subcategories ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE achievements ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE arena_categories ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE arena_teams ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE arena_templates ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE aspect ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE build_object_stage ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE build_object_template ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE building_grids ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE character_create_items ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE character_create_skills ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE character_create_stats ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE character_create_template ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE claim ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE claim_action ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE claim_object ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE claim_permission ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE coordinated_effects ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE crafting_recipes ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE currencies ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE damage_type ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE dialogue ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE editor_option ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE editor_option_choice ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE effects ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE events ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE faction_stances ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE factions ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE game_setting ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE item_templates ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE item_weights ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE level_xp_requirements ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE loot_tables ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE merchant_item ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE merchant_tables ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE mob_display ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE mob_loot ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE mob_stat ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE mob_templates ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE npcdisplay ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE patrol_path ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE quest_objectives ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE quests ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE resource_drop ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE resource_grids ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE resource_node_spawn ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE resource_node_template ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE skill_ability_gain ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE skills ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE spawn_data ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE stat ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE survivalarenatemplates ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE users ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE voxeland_changes ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';
ALTER TABLE voxelands ADD COLUMN `isactive` tinyint(1) DEFAULT 1, ADD COLUMN `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00', ADD COLUMN `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00';

DROP TRIGGER IF EXISTS `updatetimestamp`;
DELIMITER ;;
CREATE TRIGGER `updatetimestamp` BEFORE UPDATE ON `item_templates` FOR EACH ROW BEGIN
SET NEW.updatetimestamp = CURRENT_TIMESTAMP();
END
;;
DELIMITER


-- Table for the core atavism database. If you renamed it, change the name here

use atavism; 

-- ----------------------------
-- Table structure for backdating_tables
-- ----------------------------
DROP TABLE IF EXISTS `backdating_tables`;
CREATE TABLE `backdating_tables` (
  `DBName` varchar(255) DEFAULT NULL,
  `TableName` varchar(255) DEFAULT NULL,
  `oid_manager_Type` varchar(255) DEFAULT NULL,
  `DisplayName` varchar(255) DEFAULT NULL,
  `LastUpdate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of backdating_tables
-- ----------------------------
INSERT INTO `backdating_tables` VALUES ('world_content', 'item_templates', 'ITEM', 'Item Template DB', '2015-08-15 00:15:33.000000');
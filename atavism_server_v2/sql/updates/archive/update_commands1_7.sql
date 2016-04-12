-- switch to the content database
use world_content;

-- 1.7

ALTER TABLE `character_create_template` 
ADD COLUMN `autoAttack` INT NOT NULL DEFAULT -1 AFTER `faction`;

ALTER TABLE `mob_templates` 
ADD COLUMN `autoAttack` INT NOT NULL DEFAULT -1 AFTER `secondaryWeapon`;

UPDATE mob_templates set mobType = 1 where mobType = -1;

UPDATE stat set stat_function = "Physical Accuracy" where stat_function = "physical_accuracy";
UPDATE stat set stat_function = "Health Mod" where stat_function = "health_mod";
UPDATE stat set stat_function = "Magical Accuracy" where stat_function = "magical_accuracy";
UPDATE stat set stat_function = "Magical Power" where stat_function = "magical_power";
UPDATE stat set stat_function = "Physical Power" where stat_function = "physical_power";
UPDATE stat set stat_function = "Mana Mod" where stat_function = "mana_mod";

CREATE TABLE `editor_option` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `optionType` VARCHAR(45) NOT NULL,
  `deletable` TINYINT(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `optionType_UNIQUE` (`optionType` ASC));
  
INSERT INTO `editor_option` VALUES 
(1,'Item Type',1),(2,'Weapon Type',1),
(3,'Armor Type',1),(4,'Species',1),
(5,'Race',1),(6,'Class',1),
(7,'Crafting Station',1),(8,'Dialogue Action',1),
(9,'Mob Type',1),(10,'Stat Functions',1),
(11,'Target Type',1),(12,'Item Effect Type',1),
(13,'Quest Objective Type',1);

CREATE TABLE `editor_option_choice` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `optionTypeID` INT NOT NULL,
  `choice` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));
  
INSERT INTO `editor_option_choice` VALUES 
(1,1,'Weapon'),(2,1,'Armor'),(3,1,'Consumable'),
(4,1,'Material'),(5,2,'Sword'),(6,2,'Axe'),
(7,2,'Mace'),(8,2,'Staff'),(9,2,'Bow'),
(10,2,'Gun'),(11,3,'Cloth'),(12,3,'Leather'),
(13,3,'Mail'),(14,3,'Plate'),(15,1,'Junk'),
(16,4,'Humanoid'),(17,4,'Beast'),(18,4,'Dragon'),
(19,4,'Elemental'),(20,4,'Undead'),(22,5,'Human'),
(23,6,'Warrior'),(24,6,'Mage'),(25,6,'Rogue'),
(26,7,'Anvil'),(27,7,'Smelter'),(28,7,'Pot'),
(29,7,'Oven'),(30,7,'Cauldron'),(31,7,'Sawmill'),
(32,7,'Loom'),(33,7,'Sewing Table'),(34,7,'Tannery'),
(35,7,'Masonry Table'),(36,8,'Dialogue'),(37,8,'Quest'),
(38,8,'Ability'),(39,9,'Normal'),(40,9,'Untargetable'),
(41,9,'Boss'),(42,9,'Rare'),(43,10,'Health Mod'),
(44,10,'Mana Mod'),(45,10,'Physical Power'),(46,10,'Magical Power'),
(47,10,'Physical Accuracy'),(48,10,'Magical Accuracy'),(49,11,'Enemy'),
(50,11,'Self'),(51,11,'Friendly'),(52,11,'Friend Not Self'),
(53,11,'Group'),(54,11,'AoE Enemy'),(55,11,'AoE Friendly'),
(56,1,'Quest'),(57,12,'Stat'),(58,12,'UseAbility'),
(59,12,'AutoAttack'),(60,13,'item'),(61,13,'mob'),
(62,1,'Bag'),(63,1,'Container');
  
CREATE TABLE `game_setting` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `datatype` VARCHAR(45) NOT NULL,
  `value` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));
  
  
ALTER TABLE `item_templates` 
CHANGE COLUMN `stat1type` `effect1type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `stat1value` `effect1value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `stat2type` `effect2type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `stat2value` `effect2value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `stat3type` `effect3type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `stat3value` `effect3value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `stat4type` `effect4type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `stat4value` `effect4value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `stat5type` `effect5type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `stat5value` `effect5value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `stat6type` `effect6type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `stat6value` `effect6value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `res1type` `effect7type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `res1value` `effect7value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `res2type` `effect8type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `res2value` `effect8value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `res3type` `effect9type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `res3value` `effect9value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `res4type` `effect10type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `res4value` `effect10value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `res5type` `effect11type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `res5value` `effect11value` VARCHAR(64) NULL DEFAULT '0' ,
CHANGE COLUMN `res6type` `effect12type` VARCHAR(32) NULL DEFAULT NULL ,
CHANGE COLUMN `res6value` `effect12value` VARCHAR(64) NULL DEFAULT '0' ,
ADD COLUMN `effect1name` VARCHAR(45) NULL AFTER `effect1type`,
ADD COLUMN `effect2name` VARCHAR(45) NULL AFTER `effect2type`,
ADD COLUMN `effect3name` VARCHAR(45) NULL AFTER `effect3type`,
ADD COLUMN `effect4name` VARCHAR(45) NULL AFTER `effect4type`,
ADD COLUMN `effect5name` VARCHAR(45) NULL AFTER `effect5type`,
ADD COLUMN `effect6name` VARCHAR(45) NULL AFTER `effect6type`,
ADD COLUMN `effect7name` VARCHAR(45) NULL AFTER `effect7type`,
ADD COLUMN `effect8name` VARCHAR(45) NULL AFTER `effect8type`,
ADD COLUMN `effect9name` VARCHAR(45) NULL AFTER `effect9type`,
ADD COLUMN `effect10name` VARCHAR(45) NULL AFTER `effect10type`,
ADD COLUMN `effect11name` VARCHAR(45) NULL AFTER `effect11type`,
ADD COLUMN `effect12name` VARCHAR(45) NULL AFTER `effect12type`;

ALTER TABLE `item_templates` 
DROP COLUMN `clickEffect`,
DROP COLUMN `useAbility`;

ALTER TABLE `currencies` 
CHANGE COLUMN `icon` `icon` VARCHAR(256) NOT NULL ,
CHANGE COLUMN `maximum` `maximum` INT(11) NOT NULL DEFAULT 999999 ,
ADD COLUMN `subCurrency1Name` VARCHAR(45) NULL AFTER `external`,
ADD COLUMN `subCurrency1Icon` VARCHAR(256) NULL AFTER `subCurrency1Name`,
ADD COLUMN `subCurrency1Maximum` INT NULL DEFAULT 100 AFTER `subCurrency1Icon`,
ADD COLUMN `subCurrency2Name` VARCHAR(45) NULL AFTER `subCurrency1Maximum`,
ADD COLUMN `subCurrency2Icon` VARCHAR(256) NULL AFTER `subCurrency2Name`,
ADD COLUMN `subCurrency2Maximum` INT NULL DEFAULT 100 AFTER `subCurrency2Icon`;
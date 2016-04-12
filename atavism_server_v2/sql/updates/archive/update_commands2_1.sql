-- switch to the content database
use world_content;

-- 2.1
CREATE TABLE `merchant_item` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `tableID` INT NOT NULL,
  `itemID` INT NOT NULL,
  `count` INT NULL,
  `refreshTime` INT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `merchant_tables` 
DROP COLUMN `item10count`,
DROP COLUMN `item10`,
DROP COLUMN `item9count`,
DROP COLUMN `item9`,
DROP COLUMN `item8count`,
DROP COLUMN `item8`,
DROP COLUMN `item7count`,
DROP COLUMN `item7`,
DROP COLUMN `item6count`,
DROP COLUMN `item6`,
DROP COLUMN `item5count`,
DROP COLUMN `item5`,
DROP COLUMN `item4count`,
DROP COLUMN `item4`,
DROP COLUMN `item3count`,
DROP COLUMN `item3`,
DROP COLUMN `item2count`,
DROP COLUMN `item2`,
DROP COLUMN `item1count`,
DROP COLUMN `item1`;

ALTER TABLE `currencies` 
DROP COLUMN `subCurrency2Maximum`,
DROP COLUMN `subCurrency2Icon`,
DROP COLUMN `subCurrency1Maximum`,
DROP COLUMN `subCurrency1Icon`,
CHANGE COLUMN `subCurrency1Name` `subCurrency1` INT(11) NULL DEFAULT -1 ,
CHANGE COLUMN `subCurrency2Name` `subCurrency2` INT(11) NULL DEFAULT -1 ,
ADD COLUMN `isSubCurrency` TINYINT(1) NULL DEFAULT 0 AFTER `subCurrency2`;

INSERT INTO `currencies` VALUES (1,1,'Gold','','',999999,0,0,-1,-1);

CREATE TABLE `claim_object` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `claimID` INT NULL,
  `gameObject` varchar(256) NOT NULL,
  `locX` FLOAT NULL,
  `locY` FLOAT NULL,
  `locZ` FLOAT NULL,
  `orientX` FLOAT NULL,
  `orientY` FLOAT NULL,
  `orientZ` FLOAT NULL,
  `orientW` FLOAT NULL,
  `itemID` INT NULL,
  PRIMARY KEY (`id`));
  
ALTER TABLE `claim` 
ADD COLUMN `forSale` TINYINT(1) NULL DEFAULT 0 AFTER `size`,
ADD COLUMN `cost` INT NULL DEFAULT 0 AFTER `forSale`,
ADD COLUMN `currency` INT NULL AFTER `cost`;

ALTER TABLE `item_templates` 
CHANGE COLUMN `effect1value` `effect1value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect2value` `effect2value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect3value` `effect3value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect4value` `effect4value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect5value` `effect5value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect6value` `effect6value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect7value` `effect7value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect8value` `effect8value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect9value` `effect9value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect10value` `effect10value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect11value` `effect11value` VARCHAR(256) NULL DEFAULT '0',
CHANGE COLUMN `effect12value` `effect12value` VARCHAR(256) NULL DEFAULT '0';

UPDATE `item_templates` SET `purchaseCurrency` = 1 where id > 0;

INSERT INTO `editor_option_choice` (optionTypeID, choice) VALUES 
(12,'ClaimObject'),(12,'CreateClaim'),
(12,'StartQuest');

-- After first test
ALTER TABLE `claim` 
ADD COLUMN `name` VARCHAR(45) NULL AFTER `id`;

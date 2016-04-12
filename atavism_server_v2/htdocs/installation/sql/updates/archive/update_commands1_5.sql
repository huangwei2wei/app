-- switch to the atavism database
use world_content;

-- Change time based fields to floats and divide by 1000 to convert milliseconds to seconds
ALTER TABLE abilities MODIFY COLUMN activationLength FLOAT;
ALTER TABLE abilities MODIFY COLUMN cooldown1Duration FLOAT;
UPDATE abilities SET activationLength = activationLength / 1000 WHERE activationLength > 500;
UPDATE abilities SET cooldown1Duration = cooldown1Duration / 1000 WHERE cooldown1Duration > 500;
ALTER TABLE abilities ADD COLUMN tooltip VARCHAR(256);

ALTER TABLE effects MODIFY COLUMN duration FLOAT;
UPDATE effects SET duration = duration / 1000 WHERE duration > 500;

ALTER TABLE item_templates MODIFY COLUMN delay FLOAT;
UPDATE item_templates SET delay = delay / 1000 WHERE delay > 500;

ALTER TABLE mob_templates MODIFY COLUMN attackSpeed FLOAT;
UPDATE mob_templates SET attackSpeed = attackSpeed / 1000 WHERE attackSpeed > 500;

-- Add new field to skills for the parent skill linking
ALTER TABLE `skills` 
ADD COLUMN `parentSkill` INT NULL AFTER `fourthStat`;

-- New effect type
CREATE TABLE `message_effects` (
  `id` INT NOT NULL,
  `messageType` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

-- Removed a couple unused fields, renamed chest field  
ALTER TABLE `spawn_data` 
DROP COLUMN `lootTable`,
DROP COLUMN `domeID`,
CHANGE COLUMN `chestOpenLootTable` `isChest` TINYINT NOT NULL DEFAULT 0 ;

-- Rename group to factionGroup to avoid SQL issues
ALTER TABLE `factions` 
CHANGE COLUMN `group` `factionGroup` VARCHAR(64) NULL DEFAULT NULL ;

-- Add dialogue table
CREATE TABLE `dialogue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `openingDialogue` tinyint(1) DEFAULT '1',
  `repeatable` tinyint(1) DEFAULT '0',
  `prereqDialogue` int(11) DEFAULT '-1',
  `prereqQuest` int(11) DEFAULT '-1',
  `prereqFaction` int(11) DEFAULT '-1',
  `prereqFactionStance` int(11) DEFAULT '1',
  `reactionAutoStart` tinyint(1) DEFAULT '0',
  `text` text,
  `option1text` varchar(256) DEFAULT NULL,
  `option1action` varchar(45) DEFAULT NULL,
  `option1actionID` int(11) DEFAULT NULL,
  `option2text` varchar(256) DEFAULT NULL,
  `option2action` varchar(45) DEFAULT NULL,
  `option2actionID` int(11) DEFAULT NULL,
  `option3text` varchar(256) DEFAULT NULL,
  `option3action` varchar(45) DEFAULT NULL,
  `option3actionID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Add starts dialogues to spawn data  
ALTER TABLE `spawn_data` 
ADD COLUMN `startsDialogues` VARCHAR(256) NOT NULL AFTER `endsQuests`;

-- Increase size of item templates icon field, it was too small
ALTER TABLE `item_templates` 
CHANGE COLUMN `icon` `icon` VARCHAR(256) NULL DEFAULT NULL ;
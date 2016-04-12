-- switch to the content database
use world_content;

ALTER TABLE `skill_ability_gain` 
ADD COLUMN `automaticallyLearn` TINYINT(1) NULL DEFAULT 1 AFTER `abilityID`;

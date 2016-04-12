-- switch to the content database
use world_content;

ALTER TABLE `skill_ability_gain` CHANGE COLUMN `level` `skillLevelReq` INT(11) NULL DEFAULT 1 ;

UPDATE `skill_ability_gain` set `skillLevelReq` = 1 where `skillLevelReq` = 0 and id > 0;
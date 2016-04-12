-- switch to the content database
use world_content;

-- 1.6.3

ALTER TABLE `skills`
ADD COLUMN `maxLevel` INT NULL DEFAULT 1 AFTER `fourthStat`,
ADD COLUMN `automaticallyLearn` TINYINT(1) NULL DEFAULT 1 AFTER `maxLevel`,
ADD COLUMN `skillPointCost` INT NULL DEFAULT 0 AFTER `automaticallyLearn`,
ADD COLUMN `parentSkillLevelReq` INT NULL DEFAULT 1 AFTER `parentSkill`, 
ADD COLUMN `prereqSkill1` INT NULL DEFAULT 0 AFTER `parentSkillLevelReq`,
ADD COLUMN `prereqSkill1Level` INT NULL DEFAULT 1 AFTER `prereqSkill1`,
ADD COLUMN `prereqSkill2` INT NULL DEFAULT 0 AFTER `prereqSkill1Level`,
ADD COLUMN `prereqSkill2Level` INT NULL DEFAULT 1 AFTER `prereqSkill2`,
ADD COLUMN `prereqSkill3` INT NULL DEFAULT 0 AFTER `prereqSkill2Level`,
ADD COLUMN `prereqSkill3Level` INT NULL DEFAULT 1 AFTER `prereqSkill3`,
ADD COLUMN `playerLevelReq` INT NULL DEFAULT 1 AFTER `prereqSkill3Level`;

ALTER TABLE `skills` CHANGE COLUMN `parentSkill` `parentSkill` INT(11) NULL DEFAULT 0;

UPDATE skills set maxLevel = 1, automaticallyLearn = 1, skillPointCost = 0, parentSkill = 0, parentSkillLevelReq = 1, prereqSkill1 = 0, prereqSkill1Level = 1, prereqSkill2 = 0, prereqSkill2Level = 1, prereqSkill3 = 0, prereqSkill3Level = 1, playerLevelReq = 1 where id > 0;


ALTER TABLE `skill_ability_gain` 
ADD COLUMN `automaticallyLearn` TINYINT(1) NULL DEFAULT 1 AFTER `abilityID`,
CHANGE COLUMN `level` `skillLevelReq` INT(11) NULL DEFAULT 1 ;

UPDATE `skill_ability_gain` set `skillLevelReq` = 1 where `skillLevelReq` = 0 and id > 0;
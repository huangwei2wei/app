-- switch to the content database
use world_content;

-- 1.6.2

INSERT INTO `abilities` VALUES (4,'Fireball','','MagicalAttackAbility',2,0,0,'mana',0,'','',0,0,0,0,'None',-1,0,20,0,1,'Enemy',1,'Any','',1,'',0,0,4,'target',0,'target',0,'target','completed','Fireball','activating','~ none ~','');

INSERT INTO `character_create_skills` VALUES (1,1,1),(2,1,2);

INSERT INTO `coordinated_effects` VALUES (6,'Fireball','Assets/Resources/Content/CoordinatedEffects/FireballEffect.prefab');

INSERT INTO `effects` VALUES (4,'Fireball Damage',NULL,'','Damage','MagicalStrikeEffect',0,-1,0,0,0,0,0,1,'',-1,0,-1,'');
INSERT INTO `damage_effects` VALUES (4,20,'crush','health',1,-1,0,0);


INSERT INTO `skills` VALUES (1,'Hammer Swing',NULL,'Warrior','Mage','strength','endurance','dexterity','willpower',NULL),(2,'Destruction',NULL,'Mage','Warrior','intelligence','willpower','potential','dexterity',NULL);
INSERT INTO `skill_ability_gain` VALUES (1,2,0,4);


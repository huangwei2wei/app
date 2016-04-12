drop database if exists world_content;

-- create a database called world_content
create database world_content;

-- switch to the world_content database
use world_content;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `abilities`
--

DROP TABLE IF EXISTS `abilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `abilities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `icon` varchar(256) DEFAULT NULL,
  `abilityType` varchar(64) DEFAULT NULL,
  `skill` int(11) DEFAULT NULL,
  `passive` tinyint(1) DEFAULT NULL,
  `activationCost` int(11) DEFAULT NULL,
  `activationCostType` varchar(32) DEFAULT NULL,
  `activationLength` float DEFAULT NULL,
  `activationAnimation` varchar(32) DEFAULT NULL,
  `activationParticles` varchar(32) DEFAULT NULL,
  `casterEffectRequired` int(11) DEFAULT NULL,
  `casterEffectConsumed` tinyint(1) DEFAULT NULL,
  `targetEffectRequired` int(11) DEFAULT NULL,
  `targetEffectConsumed` tinyint(1) DEFAULT NULL,
  `weaponRequired` varchar(32) DEFAULT NULL,
  `reagentRequired` int(11) NOT NULL DEFAULT '-1',
  `reagentConsumed` tinyint(1) DEFAULT NULL,
  `maxRange` int(11) DEFAULT NULL,
  `minRange` int(11) DEFAULT NULL,
  `aoeRadius` int(11) NOT NULL DEFAULT '0',
  `reqTarget` tinyint(1) DEFAULT '1',
  `targetType` varchar(32) DEFAULT NULL,
  `targetState` int(11) DEFAULT NULL,
  `speciesTargetReq` varchar(32) DEFAULT NULL,
  `specificTargetReq` varchar(64) DEFAULT NULL,
  `globalCooldown` tinyint(1) DEFAULT NULL,
  `cooldown1Type` varchar(32) DEFAULT NULL,
  `cooldown1Duration` float DEFAULT NULL,
  `weaponCooldown` tinyint(1) DEFAULT NULL,
  `activationEffect1` int(1) DEFAULT NULL,
  `activationTarget1` varchar(32) DEFAULT NULL,
  `activationEffect2` int(11) DEFAULT NULL,
  `activationTarget2` varchar(32) DEFAULT NULL,
  `activationEffect3` int(11) DEFAULT NULL,
  `activationTarget3` varchar(32) DEFAULT NULL,
  `coordEffect1event` varchar(32) DEFAULT NULL,
  `coordEffect1` varchar(64) DEFAULT NULL,
  `coordEffect2event` varchar(32) DEFAULT NULL,
  `coordEffect2` varchar(64) DEFAULT NULL,
  `tooltip` varchar(256) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abilities`
--

LOCK TABLES `abilities` WRITE;
/*!40000 ALTER TABLE `abilities` DISABLE KEYS */;
INSERT INTO `abilities` VALUES (1,'player attack ability','','CombatMeleeAbility',-1,0,0,'mana',0,'','',0,0,0,0,'None',-1,0,4,0,0,1,'Enemy',1,'Any','',0,'',0,0,1,'target',0,'target',0,'target','activating','Attack Effect','','','',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Fireball','Assets/AtavismUnity/Editor/Resources/AT_button_category_server.png','MagicalAttackAbility',2,0,0,'mana',0,'','',0,0,0,0,'~ none ~',0,0,20,0,1,1,'Enemy',1,'~ none ~','',1,'',0,0,2,'target',0,'target',0,'target','completed','Fireball','activating','~ none ~','',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Brown Horse Mount','Assets/AtavismUnity/Editor/Resources/AT_button_category_mob.png','FriendlyEffectAbility',0,0,0,'mana',1,'','',0,0,0,0,'~ none ~',0,0,4,0,1,1,'Self',1,'~ none ~','',1,'',0,0,3,'target',0,'target',0,'target','completed','~ none ~','completed','~ none ~','',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `abilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `achievement_categories`
--

DROP TABLE IF EXISTS `achievement_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievement_categories` (
  `name` varchar(64) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievement_categories`
--

LOCK TABLES `achievement_categories` WRITE;
/*!40000 ALTER TABLE `achievement_categories` DISABLE KEYS */;
INSERT INTO `achievement_categories` VALUES ('General',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('Minigames',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('Tasks',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('World Events',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `achievement_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `achievement_criteria`
--

DROP TABLE IF EXISTS `achievement_criteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievement_criteria` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `achievementID` int(11) NOT NULL,
  `event` varchar(32) NOT NULL,
  `eventCount` int(11) DEFAULT NULL,
  `resetEvent1` varchar(32) DEFAULT NULL,
  `resetEvent2` varchar(32) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievement_criteria`
--

LOCK TABLES `achievement_criteria` WRITE;
/*!40000 ALTER TABLE `achievement_criteria` DISABLE KEYS */;
/*!40000 ALTER TABLE `achievement_criteria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `achievement_subcategories`
--

DROP TABLE IF EXISTS `achievement_subcategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievement_subcategories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `category` varchar(64) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  KEY `category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievement_subcategories`
--

LOCK TABLES `achievement_subcategories` WRITE;
/*!40000 ALTER TABLE `achievement_subcategories` DISABLE KEYS */;
INSERT INTO `achievement_subcategories` VALUES (1,'Muncher','Minigames',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Bomber','Minigames',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'CTF','Minigames',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,'Summer Festival','World Events',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,'Halloween','World Events',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `achievement_subcategories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `achievements`
--

DROP TABLE IF EXISTS `achievements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievements` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `category` varchar(32) NOT NULL,
  `subcategory` varchar(32) DEFAULT NULL,
  `points` int(11) NOT NULL,
  `text` text,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievements`
--

LOCK TABLES `achievements` WRITE;
/*!40000 ALTER TABLE `achievements` DISABLE KEYS */;
/*!40000 ALTER TABLE `achievements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `arena_categories`
--

DROP TABLE IF EXISTS `arena_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_categories` (
  `id` int(11) NOT NULL,
  `skin1` varchar(64) NOT NULL,
  `skin2` varchar(64) DEFAULT NULL,
  `skin3` varchar(64) DEFAULT NULL,
  `skin4` varchar(64) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `arena_categories`
--

LOCK TABLES `arena_categories` WRITE;
/*!40000 ALTER TABLE `arena_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `arena_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `arena_teams`
--

DROP TABLE IF EXISTS `arena_teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_teams` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `arenaID` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `size` int(11) NOT NULL,
  `race` varchar(32) DEFAULT NULL,
  `goal` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `arena_teams`
--

LOCK TABLES `arena_teams` WRITE;
/*!40000 ALTER TABLE `arena_teams` DISABLE KEYS */;
/*!40000 ALTER TABLE `arena_teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `arena_templates`
--

DROP TABLE IF EXISTS `arena_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_templates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `arenaType` int(11) NOT NULL,
  `arenaDifficulty` int(11) NOT NULL,
  `worldFile` varchar(64) NOT NULL,
  `numTeams` int(11) NOT NULL,
  `team1Name` varchar(32) NOT NULL,
  `team1Size` int(11) NOT NULL,
  `team1Race` varchar(32) DEFAULT NULL,
  `team2Name` varchar(32) DEFAULT NULL,
  `team2Size` int(11) DEFAULT NULL,
  `team2Race` varchar(32) DEFAULT NULL,
  `team3Name` varchar(32) DEFAULT NULL,
  `team3Size` int(11) DEFAULT NULL,
  `team3Race` varchar(32) DEFAULT NULL,
  `team4Name` varchar(32) DEFAULT NULL,
  `team4Size` int(11) DEFAULT NULL,
  `team4Race` varchar(32) DEFAULT NULL,
  `victoryCurrency` int(11) DEFAULT NULL,
  `victoryPayment` int(11) DEFAULT NULL,
  `defeatCurrency` int(11) DEFAULT NULL,
  `defeatPayment` int(11) DEFAULT NULL,
  `victoryExp` int(11) DEFAULT NULL,
  `defeatExp` int(11) DEFAULT NULL,
  `length` int(11) DEFAULT NULL,
  `team1Goal` int(11) DEFAULT NULL,
  `team2Goal` int(11) DEFAULT NULL,
  `team3Goal` int(11) DEFAULT NULL,
  `team4Goal` int(11) DEFAULT NULL,
  `victoryCondition` int(11) DEFAULT NULL,
  `raceOption1` varchar(32) DEFAULT NULL,
  `raceOption2` varchar(32) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `arena_templates`
--

LOCK TABLES `arena_templates` WRITE;
/*!40000 ALTER TABLE `arena_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `arena_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aspect`
--

DROP TABLE IF EXISTS `aspect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aspect` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aspect`
--

LOCK TABLES `aspect` WRITE;
/*!40000 ALTER TABLE `aspect` DISABLE KEYS */;
INSERT INTO `aspect` VALUES (1,'Warrior',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Mage',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Rogue',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `aspect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `build_object_stage`
--

DROP TABLE IF EXISTS `build_object_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `build_object_stage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gameObject` varchar(256) NOT NULL,
  `nextStage` int(11) NOT NULL DEFAULT '-1',
  `buildTimeReq` float NOT NULL DEFAULT '0',
  `itemReq1` int(11) DEFAULT NULL,
  `itemReq1Count` int(11) DEFAULT NULL,
  `itemReq2` int(11) DEFAULT NULL,
  `itemReq2Count` int(11) DEFAULT NULL,
  `itemReq3` int(11) DEFAULT NULL,
  `itemReq3Count` int(11) DEFAULT NULL,
  `itemReq4` int(11) DEFAULT NULL,
  `itemReq4Count` int(11) DEFAULT NULL,
  `itemReq5` int(11) DEFAULT NULL,
  `itemReq5Count` int(11) DEFAULT NULL,
  `itemReq6` int(11) DEFAULT NULL,
  `itemReq6Count` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `build_object_stage`
--

LOCK TABLES `build_object_stage` WRITE;
/*!40000 ALTER TABLE `build_object_stage` DISABLE KEYS */;
/*!40000 ALTER TABLE `build_object_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `build_object_template`
--

DROP TABLE IF EXISTS `build_object_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `build_object_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `icon` varchar(256) NOT NULL,
  `skill` int(11) NOT NULL DEFAULT '0',
  `skillLevelReq` int(11) NOT NULL DEFAULT '0',
  `weaponReq` varchar(45) NOT NULL,
  `distanceReq` float NOT NULL DEFAULT '1',
  `firstStageID` int(11) NOT NULL DEFAULT '0',
  `availableFromItemOnly` tinyint(1) NOT NULL DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `build_object_template`
--

LOCK TABLES `build_object_template` WRITE;
/*!40000 ALTER TABLE `build_object_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `build_object_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_grids`
--

DROP TABLE IF EXISTS `building_grids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_grids` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance` varchar(45) NOT NULL,
  `locX` float DEFAULT NULL,
  `locY` float DEFAULT NULL,
  `locZ` float DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `owner` bigint(20) DEFAULT NULL,
  `layer_count` int(11) DEFAULT '1',
  `building1` varchar(45) DEFAULT NULL,
  `building1_rotation` float DEFAULT NULL,
  `building2` varchar(45) DEFAULT NULL,
  `building2_rotation` float DEFAULT NULL,
  `building3` varchar(45) DEFAULT NULL,
  `building3_rotation` float DEFAULT NULL,
  `building4` varchar(45) DEFAULT NULL,
  `building4_rotation` float DEFAULT NULL,
  `building5` varchar(45) DEFAULT NULL,
  `building5_rotation` float DEFAULT NULL,
  `layer_height` float DEFAULT NULL,
  `blueprint1` int(11) DEFAULT '-1',
  `blueprint2` int(11) DEFAULT '-1',
  `blueprint3` int(11) DEFAULT '-1',
  `blueprint4` int(11) DEFAULT '-1',
  `blueprint5` int(11) DEFAULT '-1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_grids`
--

LOCK TABLES `building_grids` WRITE;
/*!40000 ALTER TABLE `building_grids` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_grids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_create_items`
--

DROP TABLE IF EXISTS `character_create_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_create_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `character_create_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '1',
  `equipped` tinyint(1) DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_create_items`
--

LOCK TABLES `character_create_items` WRITE;
/*!40000 ALTER TABLE `character_create_items` DISABLE KEYS */;
INSERT INTO `character_create_items` VALUES (1,1,4,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `character_create_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_create_skills`
--

DROP TABLE IF EXISTS `character_create_skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_create_skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `character_create_id` int(11) NOT NULL,
  `skill` int(11) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_create_skills`
--

LOCK TABLES `character_create_skills` WRITE;
/*!40000 ALTER TABLE `character_create_skills` DISABLE KEYS */;
INSERT INTO `character_create_skills` VALUES (1,1,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,1,2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `character_create_skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_create_stats`
--

DROP TABLE IF EXISTS `character_create_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_create_stats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `character_create_id` int(11) NOT NULL,
  `stat` varchar(45) NOT NULL,
  `value` int(11) NOT NULL,
  `levelIncrease` float NOT NULL DEFAULT '0',
  `levelPercentIncrease` float NOT NULL DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_create_stats`
--

LOCK TABLES `character_create_stats` WRITE;
/*!40000 ALTER TABLE `character_create_stats` DISABLE KEYS */;
INSERT INTO `character_create_stats` VALUES (1,1,'strength',20,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,1,'dexterity',20,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,1,'potential',20,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,1,'intelligence',10,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,1,'endurance',10,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,1,'willpower',20,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(7,1,'crush_resistance',5,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(8,1,'slash_resistance',5,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(9,1,'pierce_resistance',5,1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(10,1,'health',20,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(11,1,'mana',20,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(12,1,'movement_speed',7,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(13,1,'attack_speed',2000,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `character_create_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character_create_template`
--

DROP TABLE IF EXISTS `character_create_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_create_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `race` varchar(45) NOT NULL,
  `aspect` varchar(45) NOT NULL,
  `instanceName` varchar(45) NOT NULL,
  `pos_x` float NOT NULL,
  `pos_y` float NOT NULL,
  `pos_z` float NOT NULL,
  `orientation` float NOT NULL,
  `faction` int(11) NOT NULL DEFAULT '1',
  `autoAttack` int(11) NOT NULL DEFAULT '-1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_index` (`race`,`aspect`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character_create_template`
--

LOCK TABLES `character_create_template` WRITE;
/*!40000 ALTER TABLE `character_create_template` DISABLE KEYS */;
INSERT INTO `character_create_template` VALUES (1,'Human','Warrior','MainWorld',-128.985,-34,15.7763,1,1,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `character_create_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `claim`
--

DROP TABLE IF EXISTS `claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claim` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `instance` varchar(45) NOT NULL,
  `locX` float NOT NULL,
  `locY` float NOT NULL,
  `locZ` float NOT NULL,
  `owner` bigint(20) DEFAULT NULL,
  `size` int(11) DEFAULT '30',
  `forSale` tinyint(1) DEFAULT '0',
  `sellerName` VARCHAR(45) NULL, 
  `cost` int(11) DEFAULT '0',
  `currency` int(11) DEFAULT 1,
  `claimItemTemplate` int(11) DEFAULT '-1',
  `priority` int(11) NOT NULL DEFAULT '1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claim`
--

LOCK TABLES `claim` WRITE;
/*!40000 ALTER TABLE `claim` DISABLE KEYS */;
/*!40000 ALTER TABLE `claim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `claim_action`
--

DROP TABLE IF EXISTS `claim_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claim_action` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `claimID` int(11) DEFAULT NULL,
  `action` varchar(45) DEFAULT NULL,
  `brushType` varchar(45) NOT NULL,
  `locX` float NOT NULL,
  `locY` float NOT NULL,
  `locZ` float NOT NULL,
  `material` smallint(6) NOT NULL,
  `normalX` float NOT NULL,
  `normalY` float NOT NULL,
  `normalZ` float NOT NULL,
  `sizeX` float NOT NULL,
  `sizeY` float NOT NULL,
  `sizeZ` float NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claim_action`
--

LOCK TABLES `claim_action` WRITE;
/*!40000 ALTER TABLE `claim_action` DISABLE KEYS */;
/*!40000 ALTER TABLE `claim_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `claim_object`
--

DROP TABLE IF EXISTS `claim_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claim_object` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `claimID` int(11) DEFAULT NULL,
  `template` int(11) DEFAULT NULL,
  `stage` int(11) NOT NULL DEFAULT '0',
  `complete` tinyint(1) NOT NULL DEFAULT '0',
  `gameObject` varchar(256) DEFAULT NULL,
  `locX` float DEFAULT NULL,
  `locY` float DEFAULT NULL,
  `locZ` float DEFAULT NULL,
  `orientX` float DEFAULT NULL,
  `orientY` float DEFAULT NULL,
  `orientZ` float DEFAULT NULL,
  `orientW` float DEFAULT NULL,
  `itemID` int(11) DEFAULT NULL,
  `objectState` varchar(64) DEFAULT NULL,
  `health` int(11) DEFAULT '1',
  `maxHealth` int(11) DEFAULT '1',
  `item1` int(11) DEFAULT '-1',
  `item1Count` int(11) DEFAULT '0',
  `item2` int(11) DEFAULT '-1',
  `item2Count` int(11) DEFAULT '0',
  `item3` int(11) DEFAULT '-1',
  `item3Count` int(11) DEFAULT '0',
  `item4` int(11) DEFAULT '-1',
  `item4Count` int(11) DEFAULT '0',
  `item5` int(11) DEFAULT '-1',
  `item5Count` int(11) DEFAULT '0',
  `item6` int(11) DEFAULT '-1',
  `item6Count` int(11) DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claim_object`
--

LOCK TABLES `claim_object` WRITE;
/*!40000 ALTER TABLE `claim_object` DISABLE KEYS */;
/*!40000 ALTER TABLE `claim_object` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `claim_permission`
--

DROP TABLE IF EXISTS `claim_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `claim_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `claimID` int(11) NOT NULL,
  `playerOid` bigint(20) DEFAULT NULL,
  `playerName` varchar(45) DEFAULT NULL,
  `permissionLevel` int(11) DEFAULT NULL,
  `dateGiven` datetime DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claim_permission`
--

LOCK TABLES `claim_permission` WRITE;
/*!40000 ALTER TABLE `claim_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `claim_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coordinated_effects`
--

DROP TABLE IF EXISTS `coordinated_effects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coordinated_effects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `prefab` varchar(256) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coordinated_effects`
--

LOCK TABLES `coordinated_effects` WRITE;
/*!40000 ALTER TABLE `coordinated_effects` DISABLE KEYS */;
INSERT INTO `coordinated_effects` VALUES (2,'Attack Effect','Assets/Resources/Content/CoordinatedEffects/StandardMeleeAttack.prefab',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Attack Effect Special','Assets/Resources/Content/CoordinatedEffects/SpecialMeleeAttack.prefab',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,'Attack Effect Special 2','Assets/Resources/Content/CoordinatedEffects/SpecialMeleeAttack2.prefab',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,'Fireball','Assets/Resources/Content/CoordinatedEffects/FireballEffect.prefab',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `coordinated_effects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `crafting_recipes`
--

DROP TABLE IF EXISTS `crafting_recipes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crafting_recipes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `icon` varchar(256) DEFAULT NULL,
  `resultItemID` int(11) DEFAULT NULL,
  `resultItemCount` int(11) DEFAULT '1',
  `skillID` int(11) DEFAULT NULL,
  `skillLevelReq` int(11) DEFAULT NULL,
  `skillLevelMax` int(11) DEFAULT NULL,
  `stationReq` varchar(45) DEFAULT NULL,
  `creationTime` int(11) DEFAULT '0',
  `recipeItemID` int(11) DEFAULT NULL,
  `layoutReq` tinyint(1) DEFAULT '1',
  `qualityChangeable` tinyint(1) DEFAULT NULL,
  `allowDyes` tinyint(1) DEFAULT NULL,
  `allowEssences` tinyint(1) DEFAULT NULL,
  `component1` int(11) DEFAULT '-1',
  `component1count` int(11) DEFAULT NULL,
  `component2` int(11) DEFAULT '-1',
  `component2count` int(11) DEFAULT NULL,
  `component3` int(11) DEFAULT '-1',
  `component3count` int(11) DEFAULT NULL,
  `component4` int(11) DEFAULT '-1',
  `component4count` int(11) DEFAULT NULL,
  `component5` int(11) DEFAULT '-1',
  `component5count` int(11) DEFAULT NULL,
  `component6` int(11) DEFAULT '-1',
  `component6count` int(11) DEFAULT NULL,
  `component7` int(11) DEFAULT '-1',
  `component7count` int(11) DEFAULT NULL,
  `component8` int(11) DEFAULT '-1',
  `component8count` int(11) DEFAULT NULL,
  `component9` int(11) DEFAULT '-1',
  `component9count` int(11) DEFAULT NULL,
  `component10` int(11) DEFAULT '-1',
  `component10count` int(11) DEFAULT NULL,
  `component11` int(11) DEFAULT '-1',
  `component11count` int(11) DEFAULT NULL,
  `component12` int(11) DEFAULT '-1',
  `component12count` int(11) DEFAULT NULL,
  `component13` int(11) DEFAULT '-1',
  `component13count` int(11) DEFAULT NULL,
  `component14` int(11) DEFAULT '-1',
  `component14count` int(11) DEFAULT NULL,
  `component15` int(11) DEFAULT '-1',
  `component15count` int(11) DEFAULT NULL,
  `component16` int(11) DEFAULT '-1',
  `component16count` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crafting_recipes`
--

LOCK TABLES `crafting_recipes` WRITE;
/*!40000 ALTER TABLE `crafting_recipes` DISABLE KEYS */;
/*!40000 ALTER TABLE `crafting_recipes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currencies`
--

DROP TABLE IF EXISTS `currencies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currencies` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `icon` varchar(256) NOT NULL,
  `description` varchar(225) DEFAULT NULL,
  `maximum` int(11) NOT NULL DEFAULT '999999',
  `external` tinyint(1) DEFAULT '0',
  `isSubCurrency` tinyint(1) DEFAULT '0',
  `subCurrency1` int(11) DEFAULT '-1',
  `subCurrency2` int(11) DEFAULT '-1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currencies`
--

LOCK TABLES `currencies` WRITE;
/*!40000 ALTER TABLE `currencies` DISABLE KEYS */;
INSERT INTO `currencies` VALUES (1,1,'Gold','','',999999,0,0,-1,-1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `currencies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `damage_type`
--

DROP TABLE IF EXISTS `damage_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `damage_type` (
  `name` varchar(45) NOT NULL,
  `resistance_stat` varchar(45) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`name`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `damage_type`
--

LOCK TABLES `damage_type` WRITE;
/*!40000 ALTER TABLE `damage_type` DISABLE KEYS */;
INSERT INTO `damage_type` VALUES ('crush','crush_resistance',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('pierce','pierce_resistance',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('slash','slash_resistance',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `damage_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dialogue`
--

DROP TABLE IF EXISTS `dialogue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dialogue`
--

LOCK TABLES `dialogue` WRITE;
/*!40000 ALTER TABLE `dialogue` DISABLE KEYS */;
/*!40000 ALTER TABLE `dialogue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `editor_option`
--

DROP TABLE IF EXISTS `editor_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `editor_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `optionType` varchar(45) NOT NULL,
  `deletable` tinyint(1) DEFAULT '1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `optionType_UNIQUE` (`optionType`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `editor_option`
--

LOCK TABLES `editor_option` WRITE;
/*!40000 ALTER TABLE `editor_option` DISABLE KEYS */;
INSERT INTO `editor_option` VALUES (1,'Item Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Weapon Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Armor Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,'Species',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,'Race',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,'Class',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(7,'Crafting Station',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(8,'Dialogue Action',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(9,'Mob Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(10,'Stat Functions',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(11,'Target Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(12,'Item Effect Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(13,'Quest Objective Type',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(14,'Stat Shift Requirement',0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(15,'Stat Shift Action',0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `editor_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `editor_option_choice`
--

DROP TABLE IF EXISTS `editor_option_choice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `editor_option_choice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `optionTypeID` int(11) NOT NULL,
  `choice` varchar(45) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `editor_option_choice`
--

LOCK TABLES `editor_option_choice` WRITE;
/*!40000 ALTER TABLE `editor_option_choice` DISABLE KEYS */;
INSERT INTO `editor_option_choice` VALUES (1,1,'Weapon',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,1,'Armor',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,1,'Consumable',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,1,'Material',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,2,'Sword',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,2,'Axe',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(7,2,'Mace',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(8,2,'Staff',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(9,2,'Bow',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(10,2,'Gun',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(11,3,'Cloth',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(12,3,'Leather',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(13,3,'Mail',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(14,3,'Plate',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(15,1,'Junk',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(16,4,'Humanoid',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(17,4,'Beast',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(18,4,'Dragon',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(19,4,'Elemental',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(20,4,'Undead',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(22,5,'Human',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(23,6,'Warrior',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(24,6,'Mage',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(25,6,'Rogue',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(26,7,'Anvil',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(27,7,'Smelter',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(28,7,'Pot',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(29,7,'Oven',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(30,7,'Cauldron',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(31,7,'Sawmill',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(32,7,'Loom',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(33,7,'Sewing',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(34,7,'Tannery',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(35,7,'Masonry',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(36,8,'Dialogue',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(37,8,'Quest',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(38,8,'Ability',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(39,9,'Normal',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(40,9,'Untargetable',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(41,9,'Boss',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(42,9,'Rare',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(43,10,'Health Mod',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(44,10,'Mana Mod',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(45,10,'Physical Power',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(46,10,'Magical Power',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(47,10,'Physical Accuracy',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(48,10,'Magical Accuracy',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(49,11,'Enemy',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(50,11,'Self',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(51,11,'Friendly',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(52,11,'Friend Not Self',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(53,11,'Group',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(54,11,'AoE Enemy',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(55,11,'AoE Friendly',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(56,1,'Quest',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(57,12,'Stat',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(58,12,'UseAbility',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(59,12,'AutoAttack',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(60,13,'item',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(61,13,'mob',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(62,1,'Bag',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(63,1,'Container',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(64,12,'ClaimObject',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(65,12,'CreateClaim',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(66,12,'StartQuest',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(67,14,'combatstate',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(68,14,'deadstate',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(69,15,'death',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(70,10,'Health',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(71,10,'Mana',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(72,12,'Currency',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(73,12,'BuildingMaterial',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `editor_option_choice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `effects`
--

DROP TABLE IF EXISTS `effects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `effects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `displayName` varchar(64) DEFAULT NULL,
  `icon` varchar(256) DEFAULT NULL,
  `effectMainType` varchar(64) DEFAULT NULL,
  `effectType` varchar(64) DEFAULT NULL,
  `isBuff` tinyint(1) NOT NULL DEFAULT '0',
  `skillType` int(11) DEFAULT NULL,
  `skillLevelMod` float DEFAULT '0',
  `passive` tinyint(1) DEFAULT NULL,
  `stackLimit` int(11) DEFAULT NULL,
  `allowMultiple` tinyint(1) DEFAULT NULL,
  `duration` float DEFAULT NULL,
  `pulseCount` int(11) DEFAULT NULL,
  `tooltip` varchar(255) DEFAULT NULL,
  `bonusEffectReq` int(11) NOT NULL DEFAULT '-1',
  `bonusEffectReqConsumed` tinyint(1) DEFAULT NULL,
  `bonusEffect` int(11) NOT NULL DEFAULT '-1',
  `pulseParticle` varchar(32) DEFAULT NULL,
  `intValue1` int(11) NOT NULL DEFAULT '0',
  `intValue2` int(11) NOT NULL DEFAULT '0',
  `intValue3` int(11) NOT NULL DEFAULT '0',
  `intValue4` int(11) NOT NULL DEFAULT '0',
  `intValue5` int(11) NOT NULL DEFAULT '0',
  `floatValue1` float NOT NULL DEFAULT '0',
  `floatValue2` float NOT NULL DEFAULT '0',
  `floatValue3` float NOT NULL DEFAULT '0',
  `floatValue4` float NOT NULL DEFAULT '0',
  `floatValue5` float NOT NULL DEFAULT '0',
  `stringValue1` varchar(256) NOT NULL,
  `stringValue2` varchar(256) NOT NULL,
  `stringValue3` varchar(256) NOT NULL,
  `stringValue4` varchar(256) NOT NULL,
  `stringValue5` varchar(256) NOT NULL,
  `boolValue1` tinyint(1) NOT NULL DEFAULT '0',
  `boolValue2` tinyint(1) NOT NULL DEFAULT '0',
  `boolValue3` tinyint(1) NOT NULL DEFAULT '0',
  `boolValue4` tinyint(1) NOT NULL DEFAULT '0',
  `boolValue5` tinyint(1) NOT NULL DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `effects`
--

LOCK TABLES `effects` WRITE;
/*!40000 ALTER TABLE `effects` DISABLE KEYS */;
INSERT INTO `effects` VALUES (1,'Player Attack Effect',NULL,'','Damage','MeleeStrikeEffect',0,-1,0,0,0,0,0,1,'',-1,0,-1,'',10,-1,0,0,0,1,0,0,0,0,'health','crush','','','',0,0,0,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Fireball Effect',NULL,'','Damage','MagicalStrikeEffect',0,-1,0,0,0,0,0,1,'',-1,0,-1,'',20,-1,0,0,0,1,0,0,0,0,'health','crush','','','',0,0,0,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Brown Horse Mount Effect',NULL,'Assets/AtavismUnity/Editor/Resources/AT_button_category_mob.png','Mount','MountEffect',0,-1,0,0,0,0,0,1,'',-1,0,-1,'',0,60,0,0,0,0,0,0,0,0,'Assets/Resources/Horse.prefab','','','','',0,0,0,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `effects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `data_type` varchar(32) DEFAULT NULL,
  `save_data` tinyint(1) NOT NULL DEFAULT '0',
  `description` varchar(256) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'Level Up',NULL,0,'Called when a player levels up',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Mob Death','ID',1,'Sends through the template ID of the mob killed',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Player Death',NULL,1,'A player died',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,'Item Looted','ID',1,'A player picks up an item. Can be from either a loot bag a dead mob dropped or a random spawned item',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,'Item Purchased','ID',1,'A player purchases an item',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,'Currency Looted','ID',1,'Currency is looted by the player',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(7,'Item Sold','ID',1,'A player sells an item',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(9,'Log out',NULL,0,'Called when a player logs out. Can be used to reset achievements that require the player complete during a single login session',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(10,'Arena Completed','Category',1,'Sends through the category of the arena when upon completion. Often used to unlock skins through achievements',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(11,'Achievement Completed','ID',1,'Used so other achievements can be activated',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(12,'Muncher Victory',NULL,1,'Victory in the Muncher Arena',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(13,'Muncher Defeat',NULL,1,'Defeat in the Muncher Arena',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(14,'Muncher Completed',NULL,1,'Muncher Arena Completed',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(15,'Bomber Victory',NULL,1,'Victory in the Bomber Arena',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(16,'Bomber Defeat',NULL,1,'Defeat in the Bomber Arena',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(17,'Bomber Completed',NULL,1,'Bomber Arena Completed',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(18,'CTF Victory',NULL,1,'Victory in the CTF Arena',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(19,'CTF Defeat',NULL,1,'Defeat in the CTF Arena',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(20,'CTF Completed',NULL,1,'CTF Arena Completed',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faction_stances`
--

DROP TABLE IF EXISTS `faction_stances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `faction_stances` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factionID` int(11) NOT NULL,
  `otherFaction` int(11) NOT NULL DEFAULT '-1',
  `defaultStance` int(11) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faction_stances`
--

LOCK TABLES `faction_stances` WRITE;
/*!40000 ALTER TABLE `faction_stances` DISABLE KEYS */;
/*!40000 ALTER TABLE `faction_stances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factions`
--

DROP TABLE IF EXISTS `factions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `factions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `factionGroup` varchar(64) DEFAULT NULL,
  `public` tinyint(1) NOT NULL DEFAULT '0',
  `defaultStance` int(11) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factions`
--

LOCK TABLES `factions` WRITE;
/*!40000 ALTER TABLE `factions` DISABLE KEYS */;
INSERT INTO `factions` VALUES (1,1,'Human','',1,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,1,'Haters',NULL,0,-2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,1,'Friendly',NULL,0,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,1,'Neutral',NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `factions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_setting`
--

DROP TABLE IF EXISTS `game_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `datatype` varchar(45) NOT NULL,
  `value` varchar(45) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_setting`
--

LOCK TABLES `game_setting` WRITE;
/*!40000 ALTER TABLE `game_setting` DISABLE KEYS */;
INSERT INTO `game_setting` VALUES (1,'PLAYER_BAG_COUNT','int','4',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'PLAYER_DEFAULT_BAG_SIZE','int','16',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'MOB_DEATH_EXP','bool','true',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `game_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_templates`
--

DROP TABLE IF EXISTS `item_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_templates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `icon` varchar(256) DEFAULT NULL,
  `category` varchar(64) DEFAULT NULL,
  `subcategory` varchar(64) DEFAULT NULL,
  `itemType` varchar(64) DEFAULT NULL,
  `subType` varchar(64) DEFAULT NULL,
  `slot` varchar(64) DEFAULT NULL,
  `display` varchar(128) DEFAULT NULL,
  `itemQuality` tinyint(11) DEFAULT NULL,
  `binding` tinyint(11) DEFAULT NULL,
  `isUnique` tinyint(1) DEFAULT NULL,
  `stackLimit` int(11) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `purchaseCurrency` tinyint(11) DEFAULT NULL,
  `purchaseCost` int(11) DEFAULT NULL,
  `sellable` tinyint(1) DEFAULT '1',
  `levelReq` int(11) DEFAULT NULL,
  `aspectReq` varchar(64) DEFAULT NULL,
  `raceReq` varchar(64) DEFAULT NULL,
  `damage` int(11) NOT NULL DEFAULT '0',
  `damageType` varchar(32) DEFAULT NULL,
  `delay` float DEFAULT NULL,
  `toolTip` varchar(255) DEFAULT NULL,
  `triggerEvent` varchar(32) DEFAULT NULL,
  `triggerAction1Type` varchar(32) DEFAULT NULL,
  `triggerAction1Data` varchar(32) DEFAULT NULL,
  `effect1type` varchar(32) DEFAULT NULL,
  `effect1name` varchar(45) DEFAULT NULL,
  `effect1value` varchar(256) DEFAULT '0',
  `effect2type` varchar(32) DEFAULT NULL,
  `effect2name` varchar(45) DEFAULT NULL,
  `effect2value` varchar(256) DEFAULT '0',
  `effect3type` varchar(32) DEFAULT NULL,
  `effect3name` varchar(45) DEFAULT NULL,
  `effect3value` varchar(256) DEFAULT '0',
  `effect4type` varchar(32) DEFAULT NULL,
  `effect4name` varchar(45) DEFAULT NULL,
  `effect4value` varchar(256) DEFAULT '0',
  `effect5type` varchar(32) DEFAULT NULL,
  `effect5name` varchar(45) DEFAULT NULL,
  `effect5value` varchar(256) DEFAULT '0',
  `effect6type` varchar(32) DEFAULT NULL,
  `effect6name` varchar(45) DEFAULT NULL,
  `effect6value` varchar(256) DEFAULT '0',
  `effect7type` varchar(32) DEFAULT NULL,
  `effect7name` varchar(45) DEFAULT NULL,
  `effect7value` varchar(256) DEFAULT '0',
  `effect8type` varchar(32) DEFAULT NULL,
  `effect8name` varchar(45) DEFAULT NULL,
  `effect8value` varchar(256) DEFAULT '0',
  `effect9type` varchar(32) DEFAULT NULL,
  `effect9name` varchar(45) DEFAULT NULL,
  `effect9value` varchar(256) DEFAULT '0',
  `effect10type` varchar(32) DEFAULT NULL,
  `effect10name` varchar(45) DEFAULT NULL,
  `effect10value` varchar(256) DEFAULT '0',
  `effect11type` varchar(32) DEFAULT NULL,
  `effect11name` varchar(45) DEFAULT NULL,
  `effect11value` varchar(256) DEFAULT '0',
  `effect12type` varchar(32) DEFAULT NULL,
  `effect12name` varchar(45) DEFAULT NULL,
  `effect12value` varchar(256) DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_templates`
--

LOCK TABLES `item_templates` WRITE;
/*!40000 ALTER TABLE `item_templates` DISABLE KEYS */;
INSERT INTO `item_templates` VALUES (1,'Pink Star','Assets/Characters/Princess/FBX/wand.fbm/weapons_princess1_1.dds','0','0','Material','Sword','Main Hand','',1,0,0,1,NULL,1,0,0,0,NULL,'0',0,'Slash',1.5,'',NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',NULL,NULL,'0',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Wooden Stick','Assets/Terrain Demo Assets/Trees Ambient-Occlusion/Banyan/BanyanBark.png','0','0','Material','Sword','Main Hand','',1,0,0,99,NULL,1,0,0,0,NULL,'0',0,'Slash',1.5,'',NULL,NULL,NULL,'BuildingMaterial','','10','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Princess Wand','Assets/Characters/Princess/FBX/wand.fbm/weapons_princess1_1.dds','0','0','Weapon','Staff','Main Hand','Assets/Resources/Content/EquipmentDisplay/Princess Wand.prefab',2,0,0,1,NULL,1,0,0,0,NULL,'0',10,'crush',1.5,'',NULL,NULL,NULL,'Stat','endurance','5','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,'Brown Horse Whistle','Assets/AtavismUnity/Editor/Resources/AT_button_category_mob.png','0','0','Junk','Sword','Main Hand','',1,0,0,1,NULL,-1,0,1,0,NULL,'0',0,'crush',1.5,'Summos a Brown Horse to ride around the world on. Increases speed by 60%',NULL,NULL,NULL,'UseAbility','','3','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `item_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_weights`
--

DROP TABLE IF EXISTS `item_weights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_weights` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `prefix` tinyint(1) DEFAULT NULL,
  `stat1` varchar(64) DEFAULT NULL,
  `weight1` int(11) DEFAULT NULL,
  `stat2` varchar(64) DEFAULT NULL,
  `weight2` int(11) DEFAULT NULL,
  `stat3` varchar(64) DEFAULT NULL,
  `weight3` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_weights`
--

LOCK TABLES `item_weights` WRITE;
/*!40000 ALTER TABLE `item_weights` DISABLE KEYS */;
/*!40000 ALTER TABLE `item_weights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `level_xp_requirements`
--

DROP TABLE IF EXISTS `level_xp_requirements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `level_xp_requirements` (
  `level` int(11) NOT NULL,
  `xpRequired` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `level_xp_requirements`
--

LOCK TABLES `level_xp_requirements` WRITE;
/*!40000 ALTER TABLE `level_xp_requirements` DISABLE KEYS */;
INSERT INTO `level_xp_requirements` VALUES (1,200,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,500,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,800,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,1100,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,1400,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,1700,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(7,2000,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `level_xp_requirements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loot_tables`
--

DROP TABLE IF EXISTS `loot_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loot_tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `item1` int(11) NOT NULL DEFAULT '-1',
  `item1count` int(11) DEFAULT NULL,
  `item1chance` int(11) DEFAULT NULL,
  `item2` int(11) NOT NULL DEFAULT '-1',
  `item2count` int(11) DEFAULT NULL,
  `item2chance` int(11) DEFAULT NULL,
  `item3` int(11) NOT NULL DEFAULT '-1',
  `item3count` int(11) DEFAULT NULL,
  `item3chance` int(11) DEFAULT NULL,
  `item4` int(11) NOT NULL DEFAULT '-1',
  `item4count` int(11) DEFAULT NULL,
  `item4chance` int(11) DEFAULT NULL,
  `item5` int(11) NOT NULL DEFAULT '-1',
  `item5count` int(11) DEFAULT NULL,
  `item5chance` int(11) DEFAULT NULL,
  `item6` int(11) NOT NULL DEFAULT '-1',
  `item6count` int(11) DEFAULT NULL,
  `item6chance` int(11) DEFAULT NULL,
  `item7` int(11) NOT NULL DEFAULT '-1',
  `item7count` int(11) DEFAULT NULL,
  `item7chance` int(11) DEFAULT NULL,
  `item8` int(11) NOT NULL DEFAULT '-1',
  `item8count` int(11) DEFAULT NULL,
  `item8chance` int(11) DEFAULT NULL,
  `item9` int(11) NOT NULL DEFAULT '-1',
  `item9count` int(11) DEFAULT NULL,
  `item9chance` int(11) DEFAULT NULL,
  `item10` int(11) NOT NULL DEFAULT '-1',
  `item10count` int(11) DEFAULT NULL,
  `item10chance` int(11) DEFAULT NULL,
  `category` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `item1` (`item1`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loot_tables`
--

LOCK TABLES `loot_tables` WRITE;
/*!40000 ALTER TABLE `loot_tables` DISABLE KEYS */;
INSERT INTO `loot_tables` VALUES (1,'Example Loot Table',3,1,10,2,1,100,-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,NULL,NULL,-1,NULL,NULL,-1,NULL,NULL,-1,NULL,NULL,NULL,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `loot_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_item`
--

DROP TABLE IF EXISTS `merchant_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tableID` int(11) NOT NULL,
  `itemID` int(11) NOT NULL,
  `count` int(11) DEFAULT NULL,
  `refreshTime` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_item`
--

LOCK TABLES `merchant_item` WRITE;
/*!40000 ALTER TABLE `merchant_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_tables`
--

DROP TABLE IF EXISTS `merchant_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_tables`
--

LOCK TABLES `merchant_tables` WRITE;
/*!40000 ALTER TABLE `merchant_tables` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mob_display`
--

DROP TABLE IF EXISTS `mob_display`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mob_display` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `prefab` varchar(64) NOT NULL,
  `race` varchar(64) DEFAULT NULL,
  `gender` varchar(32) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mob_display`
--

LOCK TABLES `mob_display` WRITE;
/*!40000 ALTER TABLE `mob_display` DISABLE KEYS */;
/*!40000 ALTER TABLE `mob_display` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mob_loot`
--

DROP TABLE IF EXISTS `mob_loot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mob_loot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(11) NOT NULL DEFAULT '1',
  `mobTemplate` int(11) NOT NULL,
  `lootTable` int(11) DEFAULT NULL,
  `dropChance` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  KEY `mobTemplate` (`mobTemplate`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mob_loot`
--

LOCK TABLES `mob_loot` WRITE;
/*!40000 ALTER TABLE `mob_loot` DISABLE KEYS */;
INSERT INTO `mob_loot` VALUES (1,0,1,1,100,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `mob_loot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mob_stat`
--

DROP TABLE IF EXISTS `mob_stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mob_stat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobTemplate` int(11) NOT NULL,
  `stat` varchar(45) NOT NULL,
  `value` int(11) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mob_stat`
--

LOCK TABLES `mob_stat` WRITE;
/*!40000 ALTER TABLE `mob_stat` DISABLE KEYS */;
/*!40000 ALTER TABLE `mob_stat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mob_templates`
--

DROP TABLE IF EXISTS `mob_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mob_templates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `subTitle` varchar(64) DEFAULT NULL,
  `mobType` int(11) NOT NULL,
  `display1` varchar(128) NOT NULL DEFAULT '-1',
  `display2` varchar(128) DEFAULT NULL,
  `display3` varchar(128) DEFAULT NULL,
  `display4` varchar(128) DEFAULT NULL,
  `scale` float DEFAULT NULL,
  `hitbox` int(11) DEFAULT NULL,
  `baseAnimationState` int(11) NOT NULL DEFAULT '1',
  `faction` int(11) NOT NULL DEFAULT '0',
  `attackable` tinyint(1) NOT NULL,
  `minLevel` int(11) NOT NULL,
  `maxLevel` int(11) DEFAULT NULL,
  `species` varchar(64) NOT NULL,
  `subSpecies` varchar(64) NOT NULL,
  `questCategory` varchar(32) DEFAULT NULL,
  `specialUse` varchar(32) DEFAULT NULL,
  `speed_walk` float DEFAULT NULL,
  `speed_run` float DEFAULT NULL,
  `minDmg` int(11) DEFAULT NULL,
  `maxDmg` int(11) DEFAULT NULL,
  `attackSpeed` float DEFAULT NULL,
  `dmgType` varchar(20) DEFAULT NULL,
  `primaryWeapon` int(11) DEFAULT NULL,
  `secondaryWeapon` int(11) DEFAULT NULL,
  `autoAttack` int(11) NOT NULL DEFAULT '-1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mob_templates`
--

LOCK TABLES `mob_templates` WRITE;
/*!40000 ALTER TABLE `mob_templates` DISABLE KEYS */;
INSERT INTO `mob_templates` VALUES (1,0,'Evil Princess','',0,'Assets/Resources/Princess.prefab','','','',1,1,1,2,1,1,1,'Humanoid','','','',0,0,5,10,1.7,'',-1,-1,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `mob_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `npcdisplay`
--

DROP TABLE IF EXISTS `npcdisplay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `npcdisplay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `race` varchar(64) DEFAULT NULL,
  `gender` varchar(32) NOT NULL,
  `skinColour` int(11) NOT NULL DEFAULT '1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `npcdisplay`
--

LOCK TABLES `npcdisplay` WRITE;
/*!40000 ALTER TABLE `npcdisplay` DISABLE KEYS */;
INSERT INTO `npcdisplay` VALUES (1,'Red Smoo','Smoo','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Blue Smoo','Smoo','Male',2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(3,'Yellow Smoo','Smoo','Male',3,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(4,'Green Smoo','Smoo','Male',4,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(5,'Red Valkyrie','Valkyrie','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(6,'Blue Valkyrie','Valkyrie','Male',2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(7,'Yellow Valkyrie','Valkyrie','Male',3,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(8,'Green Valkyrie','Valkyrie','Male',4,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(10,'Red Robot','Robot','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(11,'Blue Robot','Robot','Male',2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(12,'Yellow Robot','Robot','Male',3,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(13,'Red Viking','Viking','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(14,'Green Robot','Robot','Male',4,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(15,'Yellow Ice Cream','Ice Cream','Male',3,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(16,'Blue Ice Cream','Ice Cream','Male',2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(17,'Red Ice Cream','Ice Cream','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(19,'Pine Tree','Pine Tree','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(20,'Red Knight','Knight','Male',1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `npcdisplay` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patrol_path`
--

DROP TABLE IF EXISTS `patrol_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patrol_path` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `startingPoint` tinyint(1) NOT NULL,
  `travelReverse` tinyint(1) NOT NULL,
  `locX` float NOT NULL,
  `locY` float NOT NULL,
  `locZ` float NOT NULL,
  `lingerTime` float NOT NULL DEFAULT '0',
  `nextPoint` int(11) NOT NULL DEFAULT '-1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patrol_path`
--

LOCK TABLES `patrol_path` WRITE;
/*!40000 ALTER TABLE `patrol_path` DISABLE KEYS */;
/*!40000 ALTER TABLE `patrol_path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quest_objectives`
--

DROP TABLE IF EXISTS `quest_objectives`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quest_objectives` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questID` int(11) NOT NULL,
  `primaryObjective` tinyint(1) NOT NULL,
  `objectiveType` varchar(16) NOT NULL,
  `target` int(11) NOT NULL DEFAULT '-1',
  `targetCount` int(11) NOT NULL,
  `targetText` varchar(64) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quest_objectives`
--

LOCK TABLES `quest_objectives` WRITE;
/*!40000 ALTER TABLE `quest_objectives` DISABLE KEYS */;
/*!40000 ALTER TABLE `quest_objectives` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quests`
--

DROP TABLE IF EXISTS `quests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `faction` int(11) NOT NULL,
  `chain` varchar(64) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `zone` varchar(64) DEFAULT NULL,
  `numGrades` int(11) NOT NULL,
  `repeatable` tinyint(1) NOT NULL,
  `description` varchar(512) NOT NULL,
  `objectiveText` varchar(512) NOT NULL,
  `progressText` varchar(512) NOT NULL,
  `deliveryItem1` int(11) NOT NULL DEFAULT '-1',
  `deliveryItem2` int(11) NOT NULL DEFAULT '-1',
  `deliveryItem3` int(11) NOT NULL DEFAULT '-1',
  `questPrereq` int(11) NOT NULL DEFAULT '-1',
  `questStartedReq` int(11) NOT NULL DEFAULT '-1',
  `levelReq` int(11) DEFAULT NULL,
  `raceReq` varchar(32) DEFAULT NULL,
  `aspectReq` varchar(32) DEFAULT NULL,
  `skillReq` int(11) DEFAULT NULL,
  `skillLevelReq` int(11) DEFAULT NULL,
  `repReq` varchar(64) DEFAULT NULL,
  `repLevelReq` int(11) DEFAULT NULL,
  `completionText` varchar(512) DEFAULT NULL,
  `experience` int(11) DEFAULT NULL,
  `item1` int(11) DEFAULT NULL,
  `item1count` int(11) DEFAULT NULL,
  `item2` int(11) DEFAULT NULL,
  `item2count` int(11) DEFAULT NULL,
  `item3` int(11) DEFAULT NULL,
  `item3count` int(11) DEFAULT NULL,
  `item4` int(11) DEFAULT NULL,
  `item4count` int(11) DEFAULT NULL,
  `chooseItem1` int(11) DEFAULT NULL,
  `chooseItem1count` int(11) DEFAULT NULL,
  `chooseItem2` int(11) DEFAULT NULL,
  `chooseItem2count` int(11) DEFAULT NULL,
  `chooseItem3` int(11) DEFAULT NULL,
  `chooseItem3count` int(11) DEFAULT NULL,
  `chooseItem4` int(11) DEFAULT NULL,
  `chooseItem4count` int(11) DEFAULT NULL,
  `currency1` int(11) DEFAULT NULL,
  `currency1count` int(11) DEFAULT NULL,
  `currency2` int(11) DEFAULT NULL,
  `currency2count` int(11) DEFAULT NULL,
  `rep1` int(11) DEFAULT NULL,
  `rep1gain` int(11) DEFAULT NULL,
  `rep2` int(11) DEFAULT NULL,
  `rep2gain` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quests`
--

LOCK TABLES `quests` WRITE;
/*!40000 ALTER TABLE `quests` DISABLE KEYS */;
/*!40000 ALTER TABLE `quests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_drop`
--

DROP TABLE IF EXISTS `resource_drop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_drop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_template` int(11) DEFAULT NULL,
  `item` int(11) DEFAULT NULL,
  `min` int(11) DEFAULT NULL,
  `max` int(11) DEFAULT NULL,
  `chance` float DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_drop`
--

LOCK TABLES `resource_drop` WRITE;
/*!40000 ALTER TABLE `resource_drop` DISABLE KEYS */;
INSERT INTO `resource_drop` VALUES (1,1,2,2,3,100,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,1,1,1,1,20,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `resource_drop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_grids`
--

DROP TABLE IF EXISTS `resource_grids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_grids` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `locX` float DEFAULT NULL,
  `locY` float DEFAULT NULL,
  `locZ` float DEFAULT NULL,
  `rotation` float DEFAULT NULL,
  `instance` varchar(45) DEFAULT NULL,
  `resource1_type` varchar(45) DEFAULT NULL,
  `resource1_chance` int(11) DEFAULT NULL,
  `resource2_type` varchar(45) DEFAULT NULL,
  `resource2_chance` int(11) DEFAULT NULL,
  `resource3_type` varchar(45) DEFAULT NULL,
  `resource3_chance` int(11) DEFAULT NULL,
  `resource4_type` varchar(45) DEFAULT NULL,
  `resource4_chance` int(11) DEFAULT NULL,
  `resource5_type` varchar(45) DEFAULT NULL,
  `resource5_chance` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_grids`
--

LOCK TABLES `resource_grids` WRITE;
/*!40000 ALTER TABLE `resource_grids` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_grids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_node_spawn`
--

DROP TABLE IF EXISTS `resource_node_spawn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_node_spawn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance` varchar(45) DEFAULT NULL,
  `resourceTemplate` int(11) DEFAULT NULL,
  `respawnTime` int(11) DEFAULT NULL,
  `locX` float DEFAULT NULL,
  `locY` float DEFAULT NULL,
  `locZ` float DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_node_spawn`
--

LOCK TABLES `resource_node_spawn` WRITE;
/*!40000 ALTER TABLE `resource_node_spawn` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_node_spawn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_node_template`
--

DROP TABLE IF EXISTS `resource_node_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_node_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `skill` int(11) DEFAULT NULL,
  `skillLevel` int(11) DEFAULT NULL,
  `skillLevelMax` int(11) DEFAULT NULL,
  `weaponReq` varchar(45) DEFAULT NULL,
  `equipped` tinyint(1) DEFAULT NULL,
  `gameObject` varchar(128) DEFAULT NULL,
  `coordEffect` varchar(128) DEFAULT NULL,
  `instance` varchar(45) DEFAULT NULL,
  `respawnTime` int(11) DEFAULT NULL,
  `locX` float DEFAULT NULL,
  `locY` float DEFAULT NULL,
  `locZ` float DEFAULT NULL,
  `harvestCount` int(11) DEFAULT NULL,
  `harvestTimeReq` float DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_node_template`
--

LOCK TABLES `resource_node_template` WRITE;
/*!40000 ALTER TABLE `resource_node_template` DISABLE KEYS */;
INSERT INTO `resource_node_template` VALUES (1,'ExampleResourceNodeRock',2,0,0,'None',1,'','HarvestRock','MainWorld',60,-127.581,-35.9034,2.55486,5,2,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `resource_node_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skill_ability_gain`
--

DROP TABLE IF EXISTS `skill_ability_gain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skill_ability_gain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skillID` int(11) DEFAULT NULL,
  `skillLevelReq` int(11) DEFAULT '1',
  `abilityID` int(11) DEFAULT NULL,
  `automaticallyLearn` tinyint(1) DEFAULT '1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skill_ability_gain`
--

LOCK TABLES `skill_ability_gain` WRITE;
/*!40000 ALTER TABLE `skill_ability_gain` DISABLE KEYS */;
INSERT INTO `skill_ability_gain` VALUES (2,2,0,2,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `skill_ability_gain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skills`
--

DROP TABLE IF EXISTS `skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `icon` varchar(256) DEFAULT NULL,
  `aspect` varchar(45) DEFAULT NULL,
  `oppositeAspect` varchar(45) DEFAULT NULL,
  `primaryStat` varchar(45) NOT NULL,
  `secondaryStat` varchar(45) NOT NULL,
  `thirdStat` varchar(45) NOT NULL,
  `fourthStat` varchar(45) NOT NULL,
  `maxLevel` int(11) DEFAULT '1',
  `automaticallyLearn` tinyint(1) DEFAULT '1',
  `skillPointCost` int(11) DEFAULT '0',
  `parentSkill` int(11) DEFAULT '0',
  `parentSkillLevelReq` int(11) DEFAULT '1',
  `prereqSkill1` int(11) DEFAULT '0',
  `prereqSkill1Level` int(11) DEFAULT '1',
  `prereqSkill2` int(11) DEFAULT '0',
  `prereqSkill2Level` int(11) DEFAULT '1',
  `prereqSkill3` int(11) DEFAULT '0',
  `prereqSkill3Level` int(11) DEFAULT '1',
  `playerLevelReq` int(11) DEFAULT '1',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skills`
--

LOCK TABLES `skills` WRITE;
/*!40000 ALTER TABLE `skills` DISABLE KEYS */;
INSERT INTO `skills` VALUES (1,'Hammer Swing',NULL,'Warrior','Mage','strength','endurance','dexterity','willpower',1,1,0,0,1,0,1,0,1,0,1,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),(2,'Destruction','','Mage','Warrior','intelligence','willpower','potential','dexterity',1,1,0,-1,1,-1,1,-1,1,-1,1,1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spawn_data`
--

DROP TABLE IF EXISTS `spawn_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spawn_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` int(11) NOT NULL DEFAULT '1',
  `name` varchar(64) NOT NULL,
  `mobTemplate` int(11) NOT NULL DEFAULT '-1',
  `markerName` varchar(64) DEFAULT NULL,
  `locX` float(8,4) DEFAULT NULL,
  `locY` float(8,4) DEFAULT NULL,
  `locZ` float(8,4) DEFAULT NULL,
  `orientX` float(8,4) DEFAULT NULL,
  `orientY` float(8,4) DEFAULT NULL,
  `orientZ` float(8,4) DEFAULT NULL,
  `orientW` float(8,4) DEFAULT NULL,
  `instance` varchar(64) DEFAULT NULL,
  `numSpawns` int(11) DEFAULT NULL,
  `spawnRadius` int(11) DEFAULT NULL,
  `respawnTime` int(11) DEFAULT NULL,
  `corpseDespawnTime` int(11) DEFAULT NULL,
  `combat` tinyint(1) NOT NULL,
  `roamRadius` int(11) NOT NULL,
  `startsQuests` varchar(256) NOT NULL,
  `endsQuests` varchar(256) NOT NULL,
  `startsDialogues` varchar(256) NOT NULL,
  `baseAction` varchar(32) NOT NULL,
  `weaponSheathed` tinyint(1) NOT NULL,
  `merchantTable` int(11) NOT NULL,
  `questOpenLootTable` int(11) NOT NULL,
  `isChest` tinyint(4) NOT NULL DEFAULT '0',
  `pickupItem` int(11) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spawn_data`
--

LOCK TABLES `spawn_data` WRITE;
/*!40000 ALTER TABLE `spawn_data` DISABLE KEYS */;
INSERT INTO `spawn_data` VALUES (1,1,'spawn',1,'',-91,-34,-31,0.0000,0.3185,0.0000,-0.9479,'MainWorld',1,0,60000,50000,1,0,'','','','',0,-1,-1,0,-1,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `spawn_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stat`
--

DROP TABLE IF EXISTS `stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stat` (
  `name` varchar(45) NOT NULL,
  `type` int(11) DEFAULT '0',
  `stat_function` varchar(45) DEFAULT NULL,
  `mob_base` int(11) DEFAULT NULL,
  `mob_level_increase` int(11) DEFAULT NULL,
  `mob_level_percent_increase` float DEFAULT NULL,
  `min` int(11) NOT NULL DEFAULT '0',
  `maxstat` varchar(45) DEFAULT NULL,
  `shiftTarget` smallint(6) DEFAULT '0',
  `shiftValue` int(11) DEFAULT NULL,
  `shiftReverseValue` int(11) DEFAULT NULL,
  `shiftInterval` int(11) DEFAULT NULL,
  `isShiftPercent` tinyint(1) NOT NULL DEFAULT '0',
  `onMaxHit` varchar(45) DEFAULT NULL,
  `onMinHit` varchar(45) DEFAULT NULL,
  `shiftReq1` varchar(45) DEFAULT NULL,
  `shiftReq1State` tinyint(1) NOT NULL DEFAULT '0',
  `shiftReq1SetReverse` tinyint(1) NOT NULL DEFAULT '0',
  `shiftReq2` varchar(45) DEFAULT NULL,
  `shiftReq2State` tinyint(1) NOT NULL DEFAULT '0',
  `shiftReq2SetReverse` tinyint(1) NOT NULL DEFAULT '0',
  `shiftReq3` varchar(45) DEFAULT NULL,
  `shiftReq3State` tinyint(1) NOT NULL DEFAULT '0',
  `shiftReq3SetReverse` tinyint(1) NOT NULL DEFAULT '0',
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`name`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stat`
--

LOCK TABLES `stat` WRITE;
/*!40000 ALTER TABLE `stat` DISABLE KEYS */;
INSERT INTO `stat` VALUES ('attack_speed',0,NULL,2000,0,0,1000,'10000',0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('crush_resistance',1,'~ none ~',2,1,0,0,'',0,0,0,0,0,'','','',0,0,'',0,0,'',0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('dexterity',0,'Physical Accuracy',10,1,0,0,'',0,0,0,0,0,'','','',0,0,'',0,0,'',0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('endurance',0,'Health Mod',10,2,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('health',2,'Health',0,0,0,0,'',0,3,0,2,1,NULL,NULL,'deadstate',0,0,'combatstate',0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('intelligence',0,'Magical Accuracy',20,1,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('mana',2,'Mana',0,0,0,0,'',0,3,0,2,1,NULL,NULL,'deadstate',0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('movement_speed',0,NULL,7,0,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('pierce_resistance',1,NULL,5,1,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('potential',0,'Magical Power',20,1,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('slash_resistance',1,NULL,5,1,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('strength',0,'Physical Power',20,1,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000'),('willpower',0,'Mana Mod',10,2,0,0,NULL,0,0,0,0,0,NULL,NULL,NULL,0,0,NULL,0,0,NULL,0,0,1,'0000-00-00 00:00:00.000000','0000-00-00 00:00:00.000000');
/*!40000 ALTER TABLE `stat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survivalarenatemplates`
--

DROP TABLE IF EXISTS `survivalarenatemplates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survivalarenatemplates` (
  `id` int(11) NOT NULL,
  `numRounds` int(11) NOT NULL,
  `round1Spawns` varchar(128) NOT NULL,
  `round2Spawns` varchar(128) DEFAULT NULL,
  `round3Spawns` varchar(128) DEFAULT NULL,
  `round4Spawns` varchar(128) DEFAULT NULL,
  `round5Spawns` varchar(128) DEFAULT NULL,
  `round6Spawns` varchar(128) DEFAULT NULL,
  `round7Spawns` varchar(128) DEFAULT NULL,
  `round8Spawns` varchar(128) DEFAULT NULL,
  `round9Spawns` varchar(128) DEFAULT NULL,
  `round10Spawns` varchar(128) DEFAULT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`),
  CONSTRAINT `survivalArenaTemplates_ibfk_1` FOREIGN KEY (`id`) REFERENCES `arena_templates` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survivalarenatemplates`
--

LOCK TABLES `survivalarenatemplates` WRITE;
/*!40000 ALTER TABLE `survivalarenatemplates` DISABLE KEYS */;
/*!40000 ALTER TABLE `survivalarenatemplates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voxeland_changes`
--

DROP TABLE IF EXISTS `voxeland_changes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voxeland_changes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `voxelandid` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voxeland_changes`
--

LOCK TABLES `voxeland_changes` WRITE;
/*!40000 ALTER TABLE `voxeland_changes` DISABLE KEYS */;
/*!40000 ALTER TABLE `voxeland_changes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voxelands`
--

DROP TABLE IF EXISTS `voxelands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voxelands` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance` varchar(45) NOT NULL,
  `isactive` tinyint(1) DEFAULT '1',
  `creationtimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00.000000',
  `updatetimestamp` datetime DEFAULT '0000-00-00 00:00:00.000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voxelands`
--

LOCK TABLES `voxelands` WRITE;
/*!40000 ALTER TABLE `voxelands` DISABLE KEYS */;
/*!40000 ALTER TABLE `voxelands` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

DROP TRIGGER IF EXISTS `updatetimestamp`;
DELIMITER ;;
CREATE TRIGGER `updatetimestamp` BEFORE UPDATE ON `item_templates` FOR EACH ROW BEGIN
SET NEW.updatetimestamp = CURRENT_TIMESTAMP();
END ;;
DELIMITER ; 
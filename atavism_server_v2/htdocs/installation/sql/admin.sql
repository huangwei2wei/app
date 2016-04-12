drop database if exists admin;

-- create a database called atavism
create database admin;

-- switch to the atavism database
use admin;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL,
  `username` varchar(32) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_login` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `character_slots` int(11) NOT NULL DEFAULT 10,
  `coin_current` int(11) NOT NULL,
  `coin_total` int(11) DEFAULT NULL,
  `coin_used` int(11) NOT NULL DEFAULT '0',
  `islands_available` int(11) NOT NULL DEFAULT '1',
  `last_logout` timestamp NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

CREATE TABLE `account_character` (
  `characterId` BIGINT(20) NOT NULL,
  `characterName` VARCHAR(45) NOT NULL,
  `accountId` BIGINT(20) NOT NULL,
  PRIMARY KEY (`characterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `account_purchases`
--

DROP TABLE IF EXISTS `account_purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_purchases` (
  `account_id` bigint(20) NOT NULL,
  `itemID` int(11) DEFAULT NULL,
  `itemPurchaseDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `itemClaims` text,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_friends`
--

DROP TABLE IF EXISTS `character_friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_friends` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `character_id` bigint(20) NOT NULL,
  `friend_id` bigint(20) NOT NULL,
  `friend_name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_mail`
--

DROP TABLE IF EXISTS `character_mail`;
CREATE TABLE `character_mail` (
  `mailId` int(11) NOT NULL AUTO_INCREMENT,
  `mailArchive` tinyint(1) NOT NULL,
  `recipientId` bigint(11) NOT NULL,
  `recipientName` varchar(255) DEFAULT NULL,
  `senderId` bigint(11) NOT NULL,
  `senderName` varchar(255) DEFAULT NULL,
  `mailRead` tinyint(1) NOT NULL,
  `mailSubject` varchar(255) NOT NULL,
  `mailMessage` text NOT NULL,
  `currencyType` int(11) DEFAULT NULL,
  `currencyAmount` int(11) DEFAULT NULL,
  `currencyTaken` TINYINT(1) NULL DEFAULT 0,
  `CoD` tinyint(1) NOT NULL DEFAULT 0,
  `mailAttachmentItemId1Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId1` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId2Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId2` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId3Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId3` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId4Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId4` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId5Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId5` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId6Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId6` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId7Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId7` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId8Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId8` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId9Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId9` bigint(11) DEFAULT NULL,
  `mailAttachmentItemId10Taken` tinyint(1) DEFAULT NULL,
  `mailAttachmentItemId10` bigint(11) DEFAULT NULL,
  `expiry` DATETIME DEFAULT NULL, 
  `dateCreated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `dateUpdated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `character_purchases`
--

DROP TABLE IF EXISTS `character_purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_purchases` (
  `character_id` bigint(20) NOT NULL,
  `itemID` int(11) DEFAULT NULL,
  `itemPurchaseDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `used` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `data_logs`;
CREATE TABLE IF NOT EXISTS `data_logs` (
       `id` BIGINT NOT NULL AUTO_INCREMENT,
       `world_name` VARCHAR(64) NOT NULL,
       `data_name` VARCHAR(64) NOT NULL,
       `data_timestamp` TIMESTAMP NOT NULL DEFAULT 0,
       `source_oid` BIGINT NOT NULL,
       `target_oid` BIGINT NOT NULL DEFAULT 0,
       `account_id` BIGINT NOT NULL DEFAULT 0,
       `additional_data` TEXT,
       `process_timestamp` TIMESTAMP DEFAULT NOW(),
       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `island_developers`
--

DROP TABLE IF EXISTS `island_developers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `island_developers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `island` int(11) NOT NULL,
  `developer` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `island_friends`
--

DROP TABLE IF EXISTS `island_friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `island_friends` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `island` int(11) NOT NULL,
  `user` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `island_templates`
--

DROP TABLE IF EXISTS `island_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `island_templates` (
  `id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `size` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `island_portals`
--

DROP TABLE IF EXISTS `island_portals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `island_portals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `island` int(11) NOT NULL,
  `portalType` int(11) NOT NULL,
  `faction` int(11) NOT NULL,
  `locX` int(11) NOT NULL,
  `locY` int(11) NOT NULL,
  `locZ` int(11) NOT NULL,
  `orientX` int(11) NOT NULL,
  `orientY` int(11) NOT NULL,
  `orientZ` int(11) NOT NULL,
  `orientW` int(11) NOT NULL,
  `displayID` int(11) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `gameObject` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `island_portals`
--

LOCK TABLES `island_portals` WRITE;
/*!40000 ALTER TABLE `island_portals` DISABLE KEYS */;
INSERT INTO `island_portals` VALUES (7,29,1,0,-129,-34,15,0,0,0,1,1,'spawn','');
/*!40000 ALTER TABLE `island_portals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `islands`
--

DROP TABLE IF EXISTS `instance_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `island_name` varchar(64) NOT NULL,
  `template` varchar(64) NOT NULL,
  `administrator` int(11) NOT NULL,
  `category` int(11) NOT NULL,
  `status` varchar(32) NOT NULL,
  `subscription` datetime DEFAULT NULL,
  `public` tinyint(1) NOT NULL DEFAULT '0',
  `password` varchar(64) NOT NULL,
  `rating` int(11) NOT NULL DEFAULT '0',
  `islandType` int(11) NOT NULL DEFAULT '0',
  `createOnStartup` tinyint(1) NOT NULL DEFAULT '0',
  `style` varchar(64) NOT NULL,
  `recommendedLevel` int(11) NOT NULL,
  `description` text NOT NULL,
  `size` int(11) NOT NULL,
  `populationLimit` int(11) NOT NULL DEFAULT '-1',
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dateCreated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `island_name` (`island_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `islands`
--

LOCK TABLES `instance_template` WRITE;
/*!40000 ALTER TABLE `instance_template` DISABLE KEYS */;
INSERT INTO `instance_template` VALUES (29,'MainWorld','',1,1,'Active',NULL,1,'',0,0,1,'',0,'',-1,-1,'2013-11-30 02:28:52','0000-00-00 00:00:00');
/*!40000 ALTER TABLE `instance_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server_stats`
--

DROP TABLE IF EXISTS `server_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_stats` (
  `players_online` int(11) NOT NULL DEFAULT '0',
  `last_login` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `logins_since_restart` int(11) NOT NULL DEFAULT '0',
  `last_restart` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`players_online`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `server_stats` WRITE;
/*!40000 ALTER TABLE `server_stats` DISABLE KEYS */;
INSERT INTO `server_stats` VALUES (0,'0000-00-00 00:00:00',0,'0000-00-00 00:00:00');
/*!40000 ALTER TABLE `server_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopitems`
--

DROP TABLE IF EXISTS `shopitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shopitems` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `cost` int(11) NOT NULL,
  `category` varchar(32) NOT NULL,
  `imageAddress` varchar(128) NOT NULL,
  `newItem` tinyint(1) DEFAULT NULL,
  `costImage` varchar(128) DEFAULT NULL,
  `buyImage` varchar(128) DEFAULT NULL,
  `purchaseType` varchar(32) DEFAULT NULL,
  `objectName` varchar(32) DEFAULT NULL,
  `purchaselimit` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `templateportals`
--

DROP TABLE IF EXISTS `templateportals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templateportals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `templateID` int(11) NOT NULL,
  `portalType` int(11) NOT NULL,
  `faction` int(11) NOT NULL,
  `locX` int(11) NOT NULL,
  `locY` int(11) NOT NULL,
  `locZ` int(11) NOT NULL,
  `displayID` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `templates`
--

DROP TABLE IF EXISTS `templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templates` (
  `id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `size` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


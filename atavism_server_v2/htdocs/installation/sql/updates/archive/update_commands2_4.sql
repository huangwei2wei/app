-- switch to the content database
use world_content;

-- 2.4
ALTER TABLE `resource_node_template` 
ADD COLUMN `harvestTimeReq` FLOAT NULL DEFAULT 0 AFTER `harvestCount`;

ALTER TABLE `crafting_recipes` 
ADD COLUMN `resultItemCount` INT NULL DEFAULT 1 AFTER `resultItemID`;

ALTER TABLE `claim` 
ADD COLUMN `priority` INT(11) NOT NULL DEFAULT 1 AFTER `claimItemTemplate`;


-- switch to the admin database
use admin;

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
--
-- Create schema calendar
--

CREATE DATABASE IF NOT EXISTS calendar;
USE calendar;

/*
DROP TABLE IF EXISTS `accessright`;
CREATE TABLE `accessright` (
  `accessRightId` int(11) NOT NULL auto_increment,
  `parameters` text,
  `interceptionPointId` int(11) NOT NULL default '0',
  PRIMARY KEY  (`accessRightId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `accessrightgroup`;
CREATE TABLE `accessrightgroup` (
  `accessRightGroupId` int(11) NOT NULL auto_increment,
  `accessRightId` int(11) NOT NULL default '0',
  `groupName` varchar(150) NOT NULL default '',
  PRIMARY KEY  (`accessRightGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `accessrightrole`;
CREATE TABLE `accessrightrole` (
  `accessRightRoleId` int(11) NOT NULL auto_increment,
  `accessRightId` int(11) NOT NULL default '0',
  `roleName` varchar(150) NOT NULL default '',
  PRIMARY KEY  (`accessRightRoleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `accessrightuser`;
CREATE TABLE `accessrightuser` (
  `accessRightUserId` int(11) NOT NULL auto_increment,
  `accessRightId` int(11) NOT NULL default '0',
  `userName` varchar(150) NOT NULL default '',
  PRIMARY KEY  (`accessRightUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `interceptionpoint`;
CREATE TABLE `interceptionpoint` (
  `id` int(11) NOT NULL auto_increment,
  `category` text NOT NULL,
  `name` varchar(255) NOT NULL default '',
  `description` text NOT NULL,
  `uses_extra_data` int(11) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

*/

DROP TABLE IF EXISTS `Asset`;
CREATE TABLE `Asset` (
  `id` bigint(20) NOT NULL auto_increment,
  `assetKey` varchar(85) default NULL,
  `assetBlob` blob NOT NULL,
  `event_id` bigint(20) default NULL,
  `fileName` varchar(85) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKEF86282E1093C0E0` (`event_id`),
  KEY `FKEF86282E41A9A85C` (`event_id`)
) ENGINE=MyISAM AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Calendar`;
CREATE TABLE `Calendar` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(85) default NULL,
  `description` varchar(85) default NULL,
  `owner` varchar(85) default NULL,
  `eventtype_id` bigint(20) unsigned default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Calendar_Group`;
CREATE TABLE `Calendar_Group` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `calendar_id` bigint(20) unsigned NOT NULL default '0',
  `groupName` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=98 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Calendar_Language`;
CREATE TABLE `Calendar_Language` (
  `calendar_id` bigint(20) NOT NULL default '0',
  `language_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`calendar_id`,`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Calendar_Role`;
CREATE TABLE `Calendar_Role` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `calendar_id` bigint(20) unsigned NOT NULL default '0',
  `roleName` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=116 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Category`;
CREATE TABLE `Category` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `active` tinyint(4) NOT NULL default '1',
  `parent_id` int(11) default NULL,
  `internalName` varchar(45) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Content_Type_Definition`;
CREATE TABLE `Content_Type_Definition` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `schemaValue` text NOT NULL,
  `name` varchar(255) NOT NULL default '',
  `type` tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Entry`;
CREATE TABLE `Entry` (
  `id` bigint(20) NOT NULL auto_increment,
  `firstName` varchar(85) default NULL,
  `lastName` varchar(85) default NULL,
  `email` varchar(85) default NULL,
  `event_id` bigint(20) default NULL,
  `organisation` varchar(85) default NULL,
  `address` varchar(85) default NULL,
  `zipcode` varchar(85) default NULL,
  `city` varchar(85) default NULL,
  `phone` varchar(85) default NULL,
  `fax` varchar(85) default NULL,
  `message` varchar(85) default NULL,
  `metadata` text,
  `attributes` text,
  PRIMARY KEY  (`id`),
  KEY `FK40018521093C0E0` (`event_id`),
  KEY `FK400185241A9A85C` (`event_id`)
) ENGINE=MyISAM AUTO_INCREMENT=111 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Event`;
CREATE TABLE `Event` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(85) default NULL,
  `description` varchar(85) default NULL,
  `endDateTime` datetime default NULL,
  `startDateTime` datetime default NULL,
  `calendar_id` bigint(20) default NULL,
  `lecturer` varchar(85) default NULL,
  `isOrganizedByGU` tinyint(1) default NULL,
  `lastRegistrationDateTime` datetime default NULL,
  `longDescription` text,
  `maximumParticipants` int(11) default NULL,
  `contactEmail` varchar(85) default NULL,
  `shortDescription` text,
  `organizerName` varchar(85) default NULL,
  `contactPhone` varchar(85) default NULL,
  `price` varchar(255) default NULL,
  `customLocation` varchar(85) default NULL,
  `isInternal` tinyint(1) default NULL,
  `eventUrl` varchar(85) default NULL,
  `contactName` varchar(85) default NULL,
  `stateId` tinyint(1) default NULL,
  `creator` varchar(255) default NULL,
  `alternativeLocation` varchar(255) default NULL,
  `entryFormId` int(10) unsigned default NULL,
  `attributes` text,
  PRIMARY KEY  (`id`),
  KEY `FK403827A1818C5BC` (`calendar_id`),
  KEY `name` (`name`),
  KEY `FK403827A20EA88D8` (`calendar_id`)
) ENGINE=MyISAM AUTO_INCREMENT=186 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Event_Calendar`;
CREATE TABLE `Event_Calendar` (
  `event_id` bigint(20) NOT NULL default '0',
  `calendar_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`event_id`,`calendar_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Event_Category`;
CREATE TABLE `Event_Category` (
  `id` bigint(20) NOT NULL auto_increment,
  `event_id` bigint(20) default '0',
  `category_id` bigint(20) NOT NULL default '0',
  `eventtype_categoryattribute_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=840 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Event_Location`;
CREATE TABLE `Event_Location` (
  `event_id` bigint(20) NOT NULL default '0',
  `location_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`event_id`,`location_id`),
  KEY `FK5DDBFA20EBB9E5` (`location_id`),
  KEY `FK5DDBFA1093C0E0` (`event_id`),
  KEY `FK5DDBFA41A9A85C` (`event_id`),
  KEY `FK5DDBFAA5AE2178` (`location_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Event_Version`;
CREATE TABLE `Event_Version` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `name` varchar(1024) default NULL,
  `title` varchar(1024) default NULL,
  `description` varchar(1024) default NULL,
  `lecturer` varchar(1024) default NULL,
  `longDescription` text,
  `contactEmail` varchar(1024) default NULL,
  `shortDescription` text,
  `organizerName` varchar(1024) default NULL,
  `contactPhone` varchar(1024) default NULL,
  `price` varchar(1024) default NULL,
  `customLocation` varchar(1024) default NULL,
  `eventUrl` varchar(1024) default NULL,
  `contactName` varchar(1024) default NULL,
  `event_id` int(11) default NULL,
  `attributes` text,
  `alternativeLocation` varchar(1024) default NULL,
  `language_id` bigint(20) unsigned default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Events`;
CREATE TABLE `Events` (
  `uid_1` bigint(20) NOT NULL default '0',
  `elm_1` varchar(85) default NULL,
  KEY `FKB307E1196A1FC02` (`uid_1`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `EventType`;
CREATE TABLE `EventType` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `schemaValue` text,
  `type` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `EventType_Category`;
CREATE TABLE `EventType_Category` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `eventtype_id` bigint(20) NOT NULL default '0',
  `category_id` bigint(20) NOT NULL default '0',
  `internalName` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Language`;
CREATE TABLE `Language` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `isoCode` varchar(10) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `Location`;
CREATE TABLE `Location` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(85) default NULL,
  `description` varchar(85) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Participant`;
CREATE TABLE `Participant` (
  `id` bigint(20) NOT NULL auto_increment,
  `userName` varchar(85) default NULL,
  `event_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK912797131093C0E0` (`event_id`),
  KEY `FK9127971341A9A85C` (`event_id`)
) ENGINE=MyISAM AUTO_INCREMENT=175 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Property`;
CREATE TABLE `Property` (
  `id` bigint(20) NOT NULL auto_increment,
  `nameSpace` varchar(100) NOT NULL default '',
  `name` varchar(155) NOT NULL default '',
  `value` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Resources`;
CREATE TABLE `Resources` (
  `uid_3` bigint(20) NOT NULL default '0',
  `elm_3` varchar(85) default NULL,
  KEY `FK89CCBE256A1FC04` (`uid_3`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Subscriber`;
CREATE TABLE `Subscriber` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `calendar_id` bigint(20) unsigned NOT NULL default '0',
  `email` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
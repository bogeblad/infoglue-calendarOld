# EMS MySQL Manager 1.9.6.5
# ---------------------------------------
# Host     : localhost
# Port     : 3306
# Database : calendar


CREATE DATABASE calendar;

USE calendar;

#
# Structure for table calendar : 
#

CREATE TABLE `calendar` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `owner` varchar(255) default NULL,
  `eventtype_id` bigint(20),
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) TYPE=MyISAM;

CREATE TABLE `calendar_group` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `calendar_id` bigint(20) unsigned NOT NULL default '0',
  `groupName` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8

CREATE TABLE `calendar`.`calendar_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `calendar_id` BIGINT UNSIGNED NOT NULL,
  `roleName` VARCHAR(255) NOT NULL,
  PRIMARY KEY(`id`)
)
ENGINE = MYISAM;

DROP TABLE IF EXISTS event_calendar;
CREATE TABLE event_calendar (
  event_id bigint(20) NOT NULL default '0',
  calendar_id bigint(20) NOT NULL default '0',
  PRIMARY KEY  (event_id,calendar_id)
)

#
# Structure for table category : 
#

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `active` TINYINT(4) NOT NULL default '1',
  `parent_id` INTEGER(11),
  PRIMARY KEY  (`id`)
) TYPE=MyISAM;

#
# Structure for table entry : 
#

CREATE TABLE `entry` (
  `id` bigint(20) NOT NULL auto_increment,
  `firstName` varchar(255) default NULL,
  `lastName` varchar(255) default NULL,
  `email` varchar(255) default NULL,
  `event_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK40018521093C0E0` (`event_id`)
) TYPE=MyISAM;

#
# Structure for table event : 
#

CREATE TABLE event (
  id int NOT NULL ,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  endDateTime date default NULL,
  startDateTime date default NULL,
  lecturer varchar(1024) default NULL,
  isOrganizedByGU int default NULL,
  lastRegistrationDateTime date default NULL,
  longDescription text NULL,
  maximumParticipants int default NULL,
  contactEmail varchar(255) default NULL,
  shortDescription text NULL,
  organizerName varchar(255) default NULL,
  contactPhone varchar(255) default NULL,
  price float default NULL,
  customLocation varchar(255) default NULL,
  isInternal int default NULL,
  eventUrl varchar(255) default NULL,
  contactName varchar(255) default NULL,
  stateId int default NULL,
  creator varchar(255) NULL,
  calendar_id int default NULL,
  PRIMARY KEY  (id)
);

#
# Structure for table event_category : 
#

CREATE TABLE `event_category` (
  `id` bigint(20) NOT NULL auto_increment,
  `event_id` bigint(20) NOT NULL default '0',
  `category_id` bigint(20) NOT NULL default '0',
  `eventtype_categoryattribute_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) TYPE=MyISAM;

#
# Structure for table event_location : 
#

CREATE TABLE `event_location` (
  `event_id` bigint(20) NOT NULL default '0',
  `location_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`event_id`,`location_id`),
  KEY `FK5DDBFA20EBB9E5` (`location_id`),
  KEY `FK5DDBFA1093C0E0` (`event_id`)
) TYPE=MyISAM;

#
# Structure for table location : 
#

CREATE TABLE `location` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) TYPE=MyISAM;

#
# Structure for table eventtype : 
#

CREATE TABLE `eventtype` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) TYPE=MyISAM;

#
# Structure for table eventtype_category : 
#

CREATE TABLE `eventtype_category` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `eventtype_id` bigint(20) NOT NULL default '0',
  `category_id` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`id`) 
) TYPE=MyISAM;


#
# Structure for table participant : 
#

CREATE TABLE `participant` (
  `id` bigint(20) NOT NULL auto_increment,
  `userName` varchar(255) default NULL,
  `event_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK912797131093C0E0` (`event_id`)
) TYPE=MyISAM;

#
# Structure for table resource : 
#

CREATE TABLE `asset` (
  `id` bigint(20) NOT NULL auto_increment,
  `fileName` varchar(255) default NULL,
  `assetKey` varchar(255) default NULL,
  `assetBlob` blob NOT NULL,
  `event_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKEF86282E1093C0E0` (`event_id`)
) TYPE=MyISAM;


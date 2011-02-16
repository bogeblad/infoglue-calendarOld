CREATE TABLE language (
  id BIGINT unsigned NOT NULL auto_increment,
  name varchar(255) default NULL,
  isoCode varchar(10) default NULL,
  PRIMARY KEY  (id)
);

DROP TABLE IF EXISTS `calendar`.`event_version`;
CREATE TABLE  `calendar`.`event_version` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `lecturer` varchar(1024) default NULL,
  `longDescription` text,
  `shortDescription` text,
  `contactPhone` varchar(255) default NULL,
  `customLocation` varchar(255) default NULL,
  `eventUrl` varchar(255) default NULL,
  `event_id` int(11) default NULL,
  `attributes` text,
  `alternativeLocation` varchar(255) default NULL,
  `language_id` bigint(20) unsigned default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS Calendar_Language;
CREATE TABLE  Calendar_Language (
  calendar_id bigint(20) NOT NULL default '0',
  language_id bigint(20) NOT NULL default '0',
  PRIMARY KEY  (calendar_id,language_id)
);
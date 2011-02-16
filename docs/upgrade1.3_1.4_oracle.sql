CREATE TABLE language (
  id number NOT NULL,
  name varchar2(255) default NULL,
  isoCode varchar2(10) default NULL,
  PRIMARY KEY  (id)
);

DROP TABLE event_version;
CREATE TABLE event_version (
  id number NOT NULL,
  name varchar2(255) default NULL,
  description varchar2(255) default NULL,
  lecturer varchar2(1024) default NULL,
  longDescription CLOB NULL,
  shortDescription CLOB NULL,
  organizerName varchar2(255) default NULL,
  customLocation varchar2(255) default NULL,
  ALTERNATIVELOCATION varchar2(255) default NULL,
  eventUrl varchar2(255) default NULL,
  event_id number default NULL,
  language_id number default NULL,
  attributes CLOB NULL,
  PRIMARY KEY  (id)
);

DROP TABLE Calendar_Language;
CREATE TABLE  Calendar_Language 
(
  calendar_id number NOT NULL,
  language_id number NOT NULL
);
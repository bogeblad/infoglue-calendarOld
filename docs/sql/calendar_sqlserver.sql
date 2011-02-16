--
-- Create schema calendar
--

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[asset]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE asset;

CREATE TABLE asset (
  id [int] IDENTITY (1, 1) NOT NULL,
  assetKey varchar(85) NOT NULL,
  assetBlob image NOT NULL,
  event_id [int] default NULL,
  fileName varchar(85) NOT NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[calendar]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE calendar;

CREATE TABLE calendar (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(85) default NULL,
  description varchar(85) default NULL,
  owner varchar(85) default NULL,
  eventtype_id [int] default NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[calendar_group]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE calendar_group;

CREATE TABLE calendar_group (
  id [int] IDENTITY (1, 1) NOT NULL,
  calendar_id [int] default NULL,
  groupName varchar(255) NOT NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[calendar_language]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE calendar_language;

CREATE TABLE calendar_language (
  calendar_id [int] NOT NULL,
  language_id [int] NOT NULL,
  PRIMARY KEY  (calendar_id,language_id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[calendar_role]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE calendar_role;

CREATE TABLE calendar_role (
  id [int] IDENTITY (1, 1) NOT NULL,
  calendar_id [int] default NULL,
  roleName varchar(255) NOT NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[category]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE category;

CREATE TABLE category (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(85) default NULL,
  internalName varchar(85) default NULL,
  description varchar(85) default NULL,
  active [int] default '1' NOT NULL,
  parent_id [int] default NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[content_type_definition]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE content_type_definition;

CREATE TABLE content_type_definition (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(255) default NULL,
  schemaValue ntext default NULL,
  type [int] default '0' NOT NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[entry]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE entry;

CREATE TABLE entry (
  id [int] IDENTITY (1, 1) NOT NULL,
  firstName varchar(85) default NULL,
  lastName varchar(85) default NULL,
  email varchar(85) default NULL,
  event_id [int] default NULL,
  organisation varchar(85) default NULL,
  address varchar(85) default NULL,
  zipcode varchar(85) default NULL,
  city varchar(85) default NULL,
  phone varchar(85) default NULL,
  fax varchar(85) default NULL,
  message varchar(85) default NULL,
  metadata ntext,
  attributes ntext,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[event]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE event;

CREATE TABLE event (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(85) default NULL,
  description varchar(85) default NULL,
  endDateTime datetime default NULL,
  startDateTime datetime default NULL,
  calendar_id [int] default NULL,
  lecturer varchar(85) default NULL,
  isOrganizedByGU [int] default NULL,
  lastRegistrationDateTime datetime default NULL,
  longDescription ntext,
  maximumParticipants [int] default NULL,
  contactEmail varchar(85) default NULL,
  shortDescription ntext,
  organizerName varchar(85) default NULL,
  contactPhone varchar(85) default NULL,
  price varchar(255) default NULL,
  customLocation varchar(85) default NULL,
  isInternal [int] default NULL,
  eventUrl varchar(85) default NULL,
  contactName varchar(85) default NULL,
  stateId [int] default NULL,
  creator varchar(255) default NULL,
  alternativeLocation varchar(255) default NULL,
  entryFormId [int] default NULL,
  attributes ntext,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[event_calendar]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE event_calendar;
  
CREATE TABLE event_calendar (
  event_id [int] NOT NULL default '0',
  calendar_id [int] NOT NULL default '0',
  PRIMARY KEY  (event_id,calendar_id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[event_category]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE event_category;

CREATE TABLE event_category (
  id [int] IDENTITY (1, 1) NOT NULL,
  event_id [int] default '0',
  category_id [int] NOT NULL default '0',
  eventtype_categoryattribute_id [int] NOT NULL default '0',
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[event_location]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE event_location;

CREATE TABLE event_location (
  event_id [int] NOT NULL default '0',
  location_id [int] NOT NULL default '0',
  PRIMARY KEY  (event_id,location_id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[event_version]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE event_version;

CREATE TABLE event_version (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(1024) default NULL,
  title varchar(1024) default NULL,
  description varchar(1024) default NULL,
  lecturer varchar(1024) default NULL,
  longDescription ntext,
  contactEmail varchar(1024) default NULL,
  shortDescription ntext,
  organizerName varchar(1024) default NULL,
  contactPhone varchar(1024) default NULL,
  price varchar(1024) default NULL,
  customLocation varchar(1024) default NULL,
  eventUrl varchar(1024) default NULL,
  contactName varchar(1024) default NULL,
  event_id [int] default NULL,
  attributes ntext,
  alternativeLocation varchar(1024) default NULL,
  language_id [int] default NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[eventtype]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE eventtype;

CREATE TABLE eventtype (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  schemaValue ntext,
  type [int] NOT NULL default '0',
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[eventtype_category]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE eventtype_category;

CREATE TABLE eventtype_category (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(255) default NULL,
  eventtype_id [int] NOT NULL default '0',
  category_id [int] NOT NULL default '0',
  internalName varchar(255) NOT NULL default '',
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[language]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE language;

CREATE TABLE language (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(255) default NULL,
  isoCode varchar(10) default NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[location]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE location;

CREATE TABLE location (
  id [int] IDENTITY (1, 1) NOT NULL,
  name varchar(85) default NULL,
  description varchar(85) default NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[participant]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE participant;

CREATE TABLE participant (
  id [int] IDENTITY (1, 1) NOT NULL,
  userName varchar(85) default NULL,
  event_id [int] default NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[property]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE property;

CREATE TABLE property (
  id [int] IDENTITY (1, 1) NOT NULL,
  nameSpace varchar(100) NOT NULL default '',
  name varchar(155) NOT NULL default '',
  value ntext NOT NULL,
  PRIMARY KEY  (id)
)

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[subscriber]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE subscriber;

CREATE TABLE subscriber (
  id [int] IDENTITY (1, 1) NOT NULL,
  calendar_id [int] NOT NULL default '0',
  email varchar(255) NOT NULL default '',
  PRIMARY KEY  (id)
)
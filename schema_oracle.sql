CREATE TABLE calendar (
  id int NOT NULL ,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  owner varchar(255) default NULL,
  eventtype_id int,
  PRIMARY KEY  (id)
);

CREATE TABLE calendar_role (
  id int NOT NULL,
  calendar_id int NULL,
  roleName varchar(255) NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE calendar_group (
  id int NOT NULL,
  calendar_id int NULL,
  groupName varchar(255) NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE event_calendar (
  event_id int NOT NULL,
  calendar_id int NOT NULL,
  PRIMARY KEY  (event_id, calendar_id)
);

CREATE TABLE category (
  id int NOT NULL ,
  internalName varchar(255) default NULL,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  active int default '1' NOT NULL ,
  parent_id int,  
  PRIMARY KEY  (id)
);

CREATE TABLE entry (
  id int NOT NULL ,
  firstName varchar(255) default NULL,
  lastName varchar(255) default NULL,
  email varchar(255) default NULL,
  organisation varchar(255) default NULL,
  address varchar(255) default NULL,
  zipcode varchar(255) default NULL,
  city varchar(255) default NULL,
  phone varchar(255) default NULL,
  fax varchar(255) default NULL,
  message varchar(1024) default NULL,
  event_id int default NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE event (
  id int NOT NULL ,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  endDateTime date default NULL,
  startDateTime date default NULL,
  lecturer varchar(1024) default NULL,
  isOrganizedByGU int default NULL,
  lastRegistrationDateTime date default NULL,
  longDescription clob NULL,
  maximumParticipants int default NULL,
  contactEmail varchar(255) default NULL,
  shortDescription clob NULL,
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

CREATE TABLE event_category (
  id int NOT NULL,
  event_id int default 0 NULL,
  category_id int default 0 NOT NULL,
  EVENTTYPE_CATEGORYATTRIBUTE_ID int default 0 NOT NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE event_location (
  event_id int default 0 NULL,
  location_id int default 0 NOT NULL,
  PRIMARY KEY  (event_id,location_id)
);

CREATE TABLE location (
  id int NOT NULL ,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE location (
  id int NOT NULL ,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE participant (
  id int NOT NULL ,
  userName varchar(255) default NULL,
  event_id int default NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE asset (
  id int NOT NULL ,
  fileName varchar(255) default NULL,
  assetKey varchar(255) default NULL,
  assetBlob blob NOT NULL,
  event_id int default NULL,
  PRIMARY KEY  (id)
);

#
# Structure for table eventtype : 
#

CREATE TABLE eventtype (
  id int NOT NULL,
  name varchar(255) default NULL,
  description varchar(255) default NULL,
  PRIMARY KEY  (id)
);

#
# Structure for table eventtype_category : 
#

CREATE TABLE eventtype_category (
  id int NOT NULL,
  internalName varchar(255) default NULL,
  name varchar(255) default NULL,
  eventtype_id int NOT NULL,
  category_id int NOT NULL,
  PRIMARY KEY (id)
);


create sequence hibernate_sequence;

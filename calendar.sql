drop table if exists Resource
drop table if exists Participant
drop table if exists Calendar
drop table if exists Event_Location
drop table if exists Event
drop table if exists Category
drop table if exists Event_Category
drop table if exists Location
create table Resource (
   id BIGINT NOT NULL AUTO_INCREMENT,
   assetKey VARCHAR(255),
   file BLOB not null,
   event_id BIGINT,
   primary key (id)
)
create table Participant (
   id BIGINT NOT NULL AUTO_INCREMENT,
   userName VARCHAR(255),
   event_id BIGINT,
   primary key (id)
)
create table Calendar (
   id BIGINT NOT NULL AUTO_INCREMENT,
   name VARCHAR(255) unique,
   description VARCHAR(255),
   primary key (id)
)
create table Event_Location (
   event_id BIGINT not null,
   location_id BIGINT not null,
   primary key (event_id, location_id)
)
create table Event (
   id BIGINT NOT NULL AUTO_INCREMENT,
   name VARCHAR(255) unique,
   description VARCHAR(255),
   endDateTime DATETIME,
   startDateTime DATETIME,
   calendar_id BIGINT,
   primary key (id)
)
create table Category (
   id BIGINT NOT NULL AUTO_INCREMENT,
   name VARCHAR(255) unique,
   description VARCHAR(255),
   primary key (id)
)
create table Event_Category (
   event_id BIGINT not null,
   category_id BIGINT not null,
   primary key (event_id, category_id)
)
create table Location (
   id BIGINT NOT NULL AUTO_INCREMENT,
   name VARCHAR(255) unique,
   description VARCHAR(255),
   primary key (id)
)
alter table Resource add index FKEF86282E1093C0E0 (event_id), add constraint FKEF86282E1093C0E0 foreign key (event_id) references Event (id)
alter table Participant add index FK912797131093C0E0 (event_id), add constraint FK912797131093C0E0 foreign key (event_id) references Event (id)
alter table Event_Location add index FK5DDBFA20EBB9E5 (location_id), add constraint FK5DDBFA20EBB9E5 foreign key (location_id) references Location (id)
alter table Event_Location add index FK5DDBFA1093C0E0 (event_id), add constraint FK5DDBFA1093C0E0 foreign key (event_id) references Event (id)
alter table Event add index FK403827A1818C5BC (calendar_id), add constraint FK403827A1818C5BC foreign key (calendar_id) references Calendar (id)
alter table Event_Category add index FK9210F9431093C0E0 (event_id), add constraint FK9210F9431093C0E0 foreign key (event_id) references Event (id)
alter table Event_Category add index FK9210F9435BA8ABFC (category_id), add constraint FK9210F9435BA8ABFC foreign key (category_id) references Category (id)

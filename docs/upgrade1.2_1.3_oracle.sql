ALTER TABLE "EVENTTYPE" ADD (TYPE NUMBER(10) DEFAULT 0 NOT NULL);
ALTER TABLE "EVENTTYPE" ADD (schemaValue CLOB DEFAULT '');
ALTER TABLE "EVENT" ADD (entryFormId NUMBER(10) DEFAULT 0);
ALTER TABLE "ENTRY" ADD (attributes CLOB DEFAULT '');
ALTER TABLE "EVENT" ADD (attributes CLOB DEFAULT '');

update Event set entryFormId = 3089;

CREATE TABLE Property (
  id number NOT NULL,
  nameSpace varchar2(100) NOT NULL,
  name varchar2(155) NOT NULL,
  value CLOB NOT NULL,
  PRIMARY KEY  (id)
);

alter table event add (ALTERNATIVELOCATION varchar2(1024) default NULL);
alter table category add (PARENT_ID NUMBER(10) default 0);
alter table category drop column PARENTID;

alter table event modify lecturer varchar2(1024) default NULL;
alter table event modify ALTERNATIVELOCATION varchar2(1024) default NULL;
alter table event modify NAME varchar2(1024) default NULL;
alter table event modify CREATOR varchar2(1024) default NULL;
alter table event modify CONTACTNAME varchar2(1024) default NULL;
alter table event modify EVENTURL varchar2(1024) default NULL;
alter table event modify CUSTOMLOCATION varchar2(1024) default NULL;
alter table event modify ORGANIZERNAME varchar2(1024) default NULL;


-- create tables here

create table GALA (
   PK bigint identity primary key,
   VERSION bigint not null,
   LAST_UPDATED datetime not null,
   
   EVENT_DATE datetime not null,
   AT_HOME tinyint not null,
   POSTCODE varchar(8) not null,
   NAME varchar(100) not null,
   LEAGUE varchar(100) not null,
   EVENT_DATE_OF_BIRTH_DATE datetime not null
   );
   

create table SWIMMER (
   PK bigint identity primary key,
   VERSION bigint not null,
   LAST_UPDATED datetime not null,
   
   SURNAME varchar(8) not null,
   FIRSTNAME varchar(8) not null,
   
   DATE_OF_BIRTH datetime not null,
   
   MALE tinyint not null
   
   );
   
   
create table RACE (
   PK bigint identity primary key,
   VERSION bigint not null,
   LAST_UPDATED datetime not null,
   
   GALA_FK bigint not null,
   
   RACE_TYPE varchar(8) not null,
   DISTANCE bigint not null,
   MALE tinyint not null,
   STROKE varchar(10) not null,
   AGE bigint not null,
   );


update PARMS_VERSION set VERSION = '0001', LAST_UPD = CURRENT_TIMESTAMP;
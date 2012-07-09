-- delete all parms tables and constraints
drop table PARMS_AGENT_GSP_LINK cascade constraints;
drop table PARMS_DPI_FILE_DATA cascade constraints;
drop table PARMS_P0028_ACTIVE cascade constraints;
drop table PARMS_AGENT cascade constraints;
drop table PARMS_GRID_SUPPLY_POINT cascade constraints;
drop table PARMS_DPI_FILE cascade constraints;
drop table PARMS_P0028_DATA cascade constraints;
drop table PARMS_P0028_FILE_DATA cascade constraints;
drop table PARMS_P0028_FILE cascade constraints;
drop table PARMS_P0028_UPLOAD_ERROR cascade constraints;
drop table PARMS_SP04_DATA cascade constraints;
drop table PARMS_SP04_FILE cascade constraints;
drop table PARMS_SUPPLIER cascade constraints;
drop table PARMS_AUDIT cascade constraints;
drop table PARMS_SP04_FROM_AFMS_MPANS cascade constraints;
drop table PARMS_CONFIGURATION_PARAMETER cascade constraints;
drop table PARMS_GSP_DEFINITION cascade constraints;
drop table PARMS_SERIAL_CONFIG cascade constraints;
drop table PARMS_VERSION cascade constraints;
drop table PARMS_VERSION_HISTORY cascade constraints;

drop table QRTZ_CALENDARS cascade constraints;
drop table QRTZ_CRON_TRIGGERS cascade constraints;
drop table QRTZ_BLOB_TRIGGERS cascade constraints;
drop table QRTZ_FIRED_TRIGGERS cascade constraints;
drop table QRTZ_PAUSED_TRIGGER_GRPS cascade constraints;
drop table QRTZ_TRIGGER_LISTENERS cascade constraints;
drop table QRTZ_SIMPLE_TRIGGERS cascade constraints;
drop table QRTZ_TRIGGERS cascade constraints;
drop table QRTZ_SCHEDULER_STATE cascade constraints;
drop table QRTZ_JOB_LISTENERS cascade constraints;
drop table QRTZ_JOB_DETAILS cascade constraints;
drop table QRTZ_LOCKS cascade constraints;

drop sequence SEQ_PARMS_VER_HIST;
drop trigger version_hist_trg1;
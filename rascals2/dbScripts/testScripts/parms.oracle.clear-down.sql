-- clear down data on parms tables

delete from parms_Agent_gsp_link;
delete from parms_dpi_file_data;
delete from parms_agent;
delete from parms_dpi_file;
delete from parms_grid_supply_point;

delete from PARMS_P0028_ACTIVE;
delete from PARMS_P0028_DATA;
delete from PARMS_P0028_FILE_DATA;
delete from PARMS_P0028_FILE;
delete from PARMS_P0028_UPLOAD_ERROR;

delete from PARMS_SP04_DATA;
delete from PARMS_SP04_FILE;
delete from parms_supplier;
delete from PARMS_AUDIT;
delete from parms_sp04_from_afms_mpans;

-- clear down for quartz schedule
delete from QRTZ_CALENDARS;
delete from QRTZ_CRON_TRIGGERS;
delete from QRTZ_BLOB_TRIGGERS;
delete from QRTZ_FIRED_TRIGGERS;
delete from QRTZ_TRIGGERS;
delete from QRTZ_PAUSED_TRIGGER_GRPS;
delete from QRTZ_SCHEDULER_STATE;
delete from QRTZ_JOB_DETAILS;
delete from QRTZ_JOB_LISTENERS;
delete from QRTZ_SIMPLE_TRIGGERS;
delete from QRTZ_TRIGGER_LISTENERS;
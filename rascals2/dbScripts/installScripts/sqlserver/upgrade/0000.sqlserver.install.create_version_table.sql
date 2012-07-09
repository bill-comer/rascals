
-- create version table
create table PARMS_VERSION (
     LAST_UPD datetime not null,
     VERSION varchar(4) not null,
     primary key (VERSION)
     );

GO

-- create version history table
create table PARMS_VERSION_HISTORY (
     VERSION_HISTORY_PK bigint identity primary key,
     LAST_UPD datetime  not null,
     VERSION varchar(4) not null
     );

GO

CREATE TRIGGER p_hist_trg1 ON [PARMS_VERSION]
FOR UPDATE
AS
BEGIN
DECLARE @v varchar(4)
SELECT @v = version from inserted;
INSERT INTO PARMS_VERSION_HISTORY (LAST_UPD, VERSION)
     VALUES (CURRENT_TIMESTAMP, @v);
END
GO

insert into PARMS_VERSION (LAST_UPD, VERSION) VALUES (CURRENT_TIMESTAMP, '0000');
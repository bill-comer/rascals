@ECHO ON
SET dbname=<database_name>
SET installscriptpath=<install_path_e.g.C:\Utilisoft\parms\automated_database_scripts\install>
@ECHO starting %installscriptpath%\parms.sqlserver.multiscript.install.bat for database %dbname% ...
SET dbuser=sa
SET dbpassword=formfill
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0000.sqlserver.install.create_version_table.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0001.sqlserver.install.createParmsTables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0002.sqlserver.install.createParmsStandingData.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0003.sqlserver.install.createParmsSp04Tables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0004.sqlserver.install.createGspDefinition.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0005.sqlserver.install.createP0028ErrorTables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0006.sqlserver.install.renameMeterRegisterid.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0007.sqlserver.install.P0028FileData.table.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0008.sqlserver.install.updateListOfSerials.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0009.sqlserver.install.addColumnsToGSP.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0010.sqlserver.install.addSecurityChanges.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0011.sqlserver.install.add.has.errors.column.to.p0028.file.table.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0012.sqlserver.install.newSerials.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0013.sqlserver.install.createParmsAuditTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0014.sqlserver.install.addMpanUniqIdColumnToParmsP0028DataTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0015.sqlserver.install.addCurrentMeasurementClassColumnToParmsP0028DataTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0016.sqlserver.install.sp04.enhancements.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0017.sqlserver.install.quartz.tables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0018.sqlserver.install.addP0028UploadWarnings.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0019.sqlserver.install.populateParmsSupplier.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0020.sqlserver.install.addCalculatedStandardsToSp04FromAfmsMpanTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0021.sqlserver.install.removeGspGroupU.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0022.sqlserver.install.addAfmsMpanEfdAndEtdColumnsToSp04FromAfmsMpanTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0023.sqlserver.install.addMaxDemandColumnToSp04FromAfmsMpansTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0024.sqlserver.install.renameColumnsToGSP.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0025.sqlserver.install.addHalfHourlyQualifyingMpansReport.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0026.sqlserver.install.alterSp04FromAfmsMpansTableColumnMeterRegisterFk.sql
@ECHO finished running %installscriptpath%\parms.sqlserver.multiscript.install.bat!!!
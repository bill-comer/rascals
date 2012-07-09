@ECHO ON
SET dbname=<database_name>
SET installscriptpath=<install_path_e.g.C:\Utilisoft\parms\automated_database_scripts\install>
@ECHO starting %installscriptpath%\parms.sqlserver.multiscript.backout.bat for database %dbname% ...
SET dbuser=sa
SET dbpassword=formfill
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0026.sqlserver.backout.alterSp04FromAfmsMpansTableColumnMeterRegisterFk.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0025.sqlserver.backout.addHalfHourlyQualifyingMpansReport.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0024.sqlserver.backout.renameColumnsToGSP.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0023.sqlserver.backout.addMaxDemandColumnToSp04FromAfmsMpansTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0022.sqlserver.backout.addAfmsMpanEfdAndEtdColumnsToSp04FromAfmsMpanTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0021.sqlserver.backout.removeGspGroupU.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0020.sqlserver.backout.addCalculatedStandardsToSp04FromAfmsMpanTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0019.sqlserver.backout.populateParmeSupplier.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0018.sqlserver.backout.addP0028UploadWarnings.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0017.sqlserver.backout.quartz.tables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0016.sqlserver.backout.sp04.enhancements.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0015.sqlserver.backout.addCurrentMeasurementClassColumnToParmsP0028DataTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0014.sqlserver.backout.addMpanUniqIdColumnToParmsP0028DataTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0013.sqlserver.backout.createParmsAuditTable.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0012.sqlserver.backout.newSerials.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0011.sqlserver.backout.add.has.errors.column.to.p0028.file.table.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0010.sqlserver.backout.addSecurityChanges.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0009.sqlserver.backout.addColumnsToGSP.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0008.sqlserver.backout.updateListOfSerials.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0007.sqlserver.backout.P0028FileData.table.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0006.sqlserver.backout.renameMeterRegisterid.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0005.sqlserver.backout.createP0028ErrorTables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0004.sqlserver.backout.dropGspDefinition.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0003.sqlserver.backout.dropParmsSp04Tables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0002.sqlserver.backout.ParmsStandingData.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0001.sqlserver.backout.createParmsTables.sql
SQLCMD -U %dbuser% -P %dbpassword% -d%dbname% -i%installscriptpath%\0000.sqlserver.backout.version.sql
@ECHO finished running %installscriptpath%\parms.sqlserver.multiscript.backout.bat!!!
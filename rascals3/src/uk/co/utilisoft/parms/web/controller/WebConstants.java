package uk.co.utilisoft.parms.web.controller;

/**
 *
 */
public final class WebConstants
{
  public static final String LOGGED_ON_USERS_RECORDS = "LoggedOnUserRecords";

  public static final String COMMAND_NAME = "command";
  public static final String ADMIN_COMMAND = "adminListCommand";
  public static final String SYSTEM_PARAMS_COMMAND = "adminCommand";
  public static final String PARMS_REPORT_COMMAND = "parmsReportCommand";
  public static final String PARMS_SP04_GEN_COMMAND = "sp04GenCommand";
  public static final String PARMS_REPORT_ADMIN_COMMAND = "dpiGenerationCommand";
  public static final String PARMS_MOP_GEN_COMMAND = "mopGenerationCommand";
  public static final String PARMS_DPI_FILE_DATA_REPORT_COMMAND = "parmsDpiFileDataReportCommand";
  public static final String PARMS_REPORT_EDIT_COMMAND = "parmsReportEditCommand";
  public static final String P0028_UPLOAD_COMMAND = "p0028UploadCommand";
  public static final String HALF_HOURLY_QUALIFYING_MPANS_COMMAND = "halfHourlyQualifyingMpansCommand";
  public static final String REQUESTED_ACTION = "requestedAction";
  public static final String COMMAND_NAME_SEARCH = "commandSearch";
  public static final String DPI_FILE_REPORT_DATA_FILTER_COMMAND = "dpi_file_report_data_filter_command";
  public static final String VIEW_FILTER = "jsp/tiles/table/filter";

  public static final String REPORT_ADMIN_REQUESTED_ACTION = "dpiGenerationRequestedAction";
  public static final String ACTION_GENERATE_DPI_FILE = "generateParmsDpiFile";
  
  public static final String REPORT_MOP_GEN_REQUESTED_ACTION = "mopGenerationRequestedAction";
  public static final String ACTION_GENERATE_MOP_REPORT = "generateParmsMopReport";
  
  public static final String ACTION_EXCLUDE_MPANS_FROM_SP04_REPORT = "excludeMpansFromSp04Report";
  public static final String ACTION_DOWNLOAD_P0028_UPLOAD_WARNINGS = "downloadP0028UploadWarnings";


  public static final String MONTH_YEAR_DATE_FORMAT = "MMM yyyy";
  public static final String DAY_MONTH_YEAR_DATE_FORMAT = "dd/MM/yyyy";
  public static final String DISPLAY_MONTH_YEAR_DATE_FORMAT = "MM/yyyy";
  public static final String DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
  public static final String DISPLAY_DAY_MONTH_YEAR_DATE_FORMAT = "dd/MM/yyyy";
  public static final String SCREEN_DATE_FORMAT = "dd/MM/yyyy";
  public static final String SCREEN_TIME_FORMAT = "HH:mm:ss";

  //screen titles
  public static final String PARMS_REPORT_ADMIN_SCREEN_TITLE = "DPI Generation";
  public static final String PARMS_MOP_GEN_SCREEN_TITLE = "Parms Generation";
  public static final String PARMS_REPORT_SP04_ERROR_REPORT = "SP04 Exception Report";
  public static final String PARMS_REPORT_SP04_ELLIGIBLE_MPANS = "SP04 Eligible MPANs";

  // response message keys
  public static final String P0028_FILE_UPLOADED_SUCCESS = "p0028.file.upload.success";
  public static final String P0028_FILE_UPLOADED_SUCCESS_WITH_ERRORS = "p0028.file.upload.success.with.errors";
  public static final String SP04_FILE_GENERATED_SUCCESS = "sp04.file.generated.success";
  public static final String P0028_DOWNLOAD_ERROR_REPORT_SUCCESS = "p0028.download.error.report.success";
  public static final String DPI_FILE_DOWNLOAD_REPORT_FAILURE = "dpi.file.download.report.failure";
  public static final String DPI_FILE_DOWNLOAD_REPORT_SUCCESS = "dpi.file.download.report.success";
  public static final String SP04_FILE_DOWNLOAD_REPORT_SUCCESS = "sp04.file.download.report.success";
  public static final String SP04_FILE_DOWNLOAD_REPORT_FAILURE = "sp04.file.download.report.failure";
  public static final String DPI_FILE_BUILD_SUCCESS = "dpi.file.build.success";
  public static final String DPI_FILE_BUILD_FAILURE = "dpi.file.build.failure";

  // request attributes
  public static final String PARMS_RESPONSE_MESSAGE_DATA = "RESPONSE_MESSAGE_DATA";

  // error message keys
  public static final String INVALID_END_REPORTING_PERIOD = "error.invalid.end.reporting.period.format";
  public static final String INVALID_FROM_DATE_CREATED = "error.invalid.from.date.created.format";
  public static final String INVALID_TO_DATE_CREATED = "error.invalid.to.date.created.format";
  public static final String INVALID_REPORTING_PERIOD = "error.invalid.reporting.period.format";
  public static final String INVALID_P0028_RECEIVED = "error.invalid.p0028.received.format";
  public static final String INVALID_MPAN = "error.invalid.mpan";

  // search filter
  public static final String TEXT_LIKE_COMPARATOR = "TEXT_LIKE_COMPARATOR";

  private WebConstants() {  }
}


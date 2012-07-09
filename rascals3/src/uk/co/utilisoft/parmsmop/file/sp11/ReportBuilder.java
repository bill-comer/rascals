package uk.co.utilisoft.parmsmop.file.sp11;

import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;

public interface ReportBuilder
{
  ParmsMopReport buildReport();
  
  String getSerial();
  
  void addParmsReportSummaries(ParmsMopReport report);
}

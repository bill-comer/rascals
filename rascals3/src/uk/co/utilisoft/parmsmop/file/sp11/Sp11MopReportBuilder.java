package uk.co.utilisoft.parmsmop.file.sp11;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;


@Service("parmsmop.sp11ReportBuilder")
public class Sp11MopReportBuilder extends MopReportBuilder 
{
  
  @Override
  public void addParmsReportSummaries(ParmsMopReport report)
  {
    List<ParmsReportSummary> allParmsReporttSummaries = getParmsMopReportSummaryDao().getSp11DataForStandard1(report);
    
    addIfUnique(allParmsReporttSummaries, getParmsMopReportSummaryDao().getSp11MissingActiveSuppliersForNHH(report) );
    addIfUnique(allParmsReporttSummaries, getParmsMopReportSummaryDao().getSp11MissingActiveSuppliersForHH(report));
    
    report.setParmsReportSummary(allParmsReporttSummaries);
  }

  
  

  @Override
  public String getSerial()
  {
    return "SP11";
  }

}

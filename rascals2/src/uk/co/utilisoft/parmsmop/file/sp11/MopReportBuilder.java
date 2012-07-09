package uk.co.utilisoft.parmsmop.file.sp11;

import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.parmsmop.dao.ParmsMopReportDao;
import uk.co.utilisoft.parmsmop.dao.ParmsMopSourceDataDao;
import uk.co.utilisoft.parmsmop.dao.ParmsReportSummaryDaoImpl;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReportData;
import uk.co.utilisoft.parmsmop.domain.ParmsMopSourceData;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;

public abstract class MopReportBuilder implements ReportBuilder
{
  @Autowired(required = true)
  @Qualifier("parms.mopSourceDataDao")
  private ParmsMopSourceDataDao mParmsMopSourceDataDao;
  

  @Autowired(required = true)
  @Qualifier("parms.mopReportDao")
  private ParmsMopReportDao mParmsMopReportDao;
  
  @Autowired(required = true)
  @Qualifier("parms.mopReportSummaryDao")
  private ParmsReportSummaryDaoImpl mParmsMopReportSummaryDao;

  public ParmsReportSummaryDaoImpl getParmsMopReportSummaryDao()
  {
    return mParmsMopReportSummaryDao;
  }

  public ParmsMopReportDao getParmsMopReportDao()
  {
    return mParmsMopReportDao;
  }

  public ParmsMopSourceDataDao getParmsMopSourceDataDao()
  {
    return mParmsMopSourceDataDao;
  }
  
  @Override
  public final ParmsMopReport buildReport()
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setSerial(getSerial());
    report.setUsername("FRED");
    report.setRunDate(new DateTime());
    
    DateMidnight todayLastMonth = new DateMidnight().minusMonths(1);
    
    DateMidnight startDate = new DateMidnight(todayLastMonth.getYear(), todayLastMonth.getMonthOfYear(), 1);
    DateMidnight endDate = startDate.plusMonths(1).minusDays(1);
    
    //get source data
    List<ParmsMopSourceData> srcData = getParmsMopSourceDataDao().getMopReportSourceData(getSerial(), new DateTime(startDate), new DateTime(endDate), report);
    List<ParmsMopReportData> reportData = ParmsMopReportData.replicate(srcData);
    report.setParmsMopReportData(reportData);
    
    //get ParmsReportSummary data for SP11
    addParmsReportSummaries(report);
    
    getParmsMopReportDao().makePersistent(report);
    
    return report;
  }
  
  void addIfUnique(List<ParmsReportSummary> allParmsReporttSummaries,
              List<ParmsReportSummary> additionalParmsReportSummaries)
  {
    for (ParmsReportSummary parmsReportSummary : additionalParmsReportSummaries)
    {
      if (!allParmsReporttSummaries.contains(parmsReportSummary))
      {
        allParmsReporttSummaries.add(parmsReportSummary);
      }
    }
  }
}

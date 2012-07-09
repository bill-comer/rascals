package uk.co.utilisoft.parmsmop.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;
import uk.co.utilisoft.utils.Freeze;

public class MopReportServiceIntegrationTest extends BaseDaoIntegrationTest
{

  @Autowired(required = true)
  @Qualifier("parmsmop.mopReportService")
  private MopReportServiceImpl mMopReportServiceImpl;
  
  @Test
  public void testcreateNewReport() throws Exception
  {
    assertNotNull(mMopReportServiceImpl);

    Freeze.freeze(new DateTime(2012, 5, 29, 0, 0, 0, 00));

    //test method
    ParmsMopReport report = mMopReportServiceImpl.createNewReport("SP11");
    
    assertNotNull(report.getParmsMopReportData());
    assertTrue("should have found some", report.getParmsMopReportData().size() > 0);
    assertEquals("should have found some", 1, report.getParmsMopReportData().size() );
    
   /* assertNotNull(report.getParmsReportSummary());
    assertTrue("should have found some ParmsReportSummary", report.getParmsReportSummary().size() > 0);
    assertEquals("should have found one ParmsReportSummary", 1, report.getParmsReportSummary().size() );
    
    ParmsReportSummary prs = report.getParmsReportSummary().get(0);
    assertNotNull(prs);*/
  }
}

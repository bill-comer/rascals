package uk.co.utilisoft.parmsmop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;

public class ParmsReportSummaryDaoIntegrationTest extends BaseDaoIntegrationTest
{

  @Autowired(required = true)
  @Qualifier("parms.mopReportSummaryDao")
  private ParmsReportSummaryDaoImpl mParmsMopReportSummaryDao;

  @Test
  public void test_getById() throws Exception
  {
    ParmsReportSummary row = mParmsMopReportSummaryDao.getById(new BigInteger("1"));
    assertNotNull("oops - no row found", row);
    
    assertEquals("EBES", row.getParticipantId());
  }
  

  @Test
  public void test_getForStandard1() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setPk(188L);
    
    //test method
    List<ParmsReportSummary> data = mParmsMopReportSummaryDao.getSp11DataForStandard1(report);
    assertNotNull("ooops - did not get owt back", data);
    
    assertTrue("ooops - no rows found", data.size() > 0);
    assertEquals(1, data.size());
    
    ParmsReportSummary pps = data.get(0);
    assertEquals("NATP", pps.getParticipantId());
    assertEquals("_E", pps.getReportString2());
    assertEquals("10", pps.getReportString3());
    assertEquals("0.00", pps.getReportString4());
    assertEquals("0.00", pps.getReportString5());
    assertEquals("0.00", pps.getReportString6());
    assertEquals("0.00", pps.getReportString7());
    assertEquals("0.00", pps.getReportString8());
    assertEquals("0.00", pps.getReportString9());
    assertEquals("0.00", pps.getReportString10());
    assertEquals("N", pps.getHalfHourlyIndicator());
    assertFalse( pps.isHalfHourlyIndicatorAsBoolean());
    
    assertNotNull(pps.getParmsMopReport());
    assertEquals(new Long(188), pps.getParmsMopReport().getPk());
  }
  
  @Test
  public void test_getSp11ActiveSuppliers() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setPk(188L);
    
    //test method
    List<ParmsReportSummary> data = mParmsMopReportSummaryDao.getSp11ActiveSuppliers(report);
    assertNotNull("ooops - did not get owt back", data);
    
    assertEquals(14, data.size());
  }
  
  @Test
  public void test_getSp11MissingActiveSuppliersForNHH() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setPk(188L);
    
    //test method
    List<ParmsReportSummary> data = mParmsMopReportSummaryDao.getSp11MissingActiveSuppliersForNHH(report);
    assertNotNull("ooops - did not get owt back", data);
    
    assertEquals(0, data.size());
  }
  

  @Test
  public void test_getSp11MissingActiveSuppliersForHH() throws Exception
  {
    ParmsMopReport report = new ParmsMopReport();
    report.setPk(188L);
    
    //test method
    List<ParmsReportSummary> data = mParmsMopReportSummaryDao.getSp11MissingActiveSuppliersForHH(report);
    assertNotNull("ooops - did not get owt back", data);
    
    assertEquals(0, data.size());
  }
}

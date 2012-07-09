package uk.co.utilisoft.parmsmop.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReportData;
import uk.co.utilisoft.parmsmop.domain.ParmsMopSourceData;
import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;
import uk.co.utilisoft.utils.Freeze;

@SuppressWarnings("rawtypes")
public class ParmsMopDaoIntegrationTest  extends BaseDaoIntegrationTest
{

  @Autowired(required = true)
  @Qualifier("parms.mopReportDao")
  private ParmsMopReportDao mParmsMopReportDao;
  
  @Autowired(required = true)
  @Qualifier("parms.mopSourceDataDao")
  private ParmsMopSourceDataDao mParmsMopSourceDataDao;
  
  @Test
  public void testGetSomeReports() throws Exception
  {
    assertNotNull("failed to inject Dao", mParmsMopReportDao);
    
    List<ParmsMopReport> reports = mParmsMopReportDao.getAllMopReports();
    
    assertNotNull("should always get a List object back", reports);
    assertTrue("should be atleast 12 rows", reports.size() > 0);
    assertEquals("should be atleast 12 rows", 12, reports.size());
    
    ParmsMopReport report = reports.get(0);
    assertNotNull("not got a report", report);
    assertNotNull("No summaries", report.getParmsReportSummary());
    assertTrue(report.getParmsReportSummary().size() > 0);
   
    ParmsReportSummary summary = report.getParmsReportSummary().get(0);
    assertNotNull(summary);
    

  }
  
  @Test
  public void testGetSomeReportsFor188() throws Exception
  {
    assertNotNull("failed to inject Dao", mParmsMopReportDao);
    
    
    ParmsMopReport report = mParmsMopReportDao.getById(188L);
    assertNotNull("not got a report", report);
    assertNotNull("No summaries", report.getParmsReportSummary());
    assertTrue(report.getParmsReportSummary().size() > 0);
   
    ParmsReportSummary summary = report.getParmsReportSummary().get(0);
    assertNotNull(summary);
    
    List<ParmsMopReportData> allReportData = report.getParmsMopReportData();
    assertNotNull(allReportData);
    assertTrue(allReportData.size() > 0);
    
    assertEquals(10, allReportData.size());
    ParmsMopReportData data = allReportData.get(0);
    assertNotNull(data);
    assertEquals("SP15", data.getSerialType());
    
    
  }
  
  
  @Test
  public void testParmsData() throws Exception
  {
    assertNotNull("failed to inject Dao mParmsMopDataDao ", mParmsMopSourceDataDao);
    
    BigInteger bigInt = new BigInteger("596315");
    ParmsMopSourceData data =  mParmsMopSourceDataDao.getById(bigInt);
    assertNotNull("failed to find PARMS_DATA for id 596315L", data);
    assertEquals("1460001717978", data.getMpan().getValue());
  }
  

  @Test
  public void test_getCopyMopReportSourceData() throws Exception
  {
    //2012-4-30

    Freeze.freeze(new DateTime(2012, 4, 30, 0, 0, 0, 00));
    
    String serial = "SP11";
    DateTime startDate = new DateTime().minusMonths(2);
    DateTime endDate = new DateTime().plusMonths(2);
    ParmsMopReport report = new ParmsMopReport();
    
    List<ParmsMopSourceData> data = mParmsMopSourceDataDao.getMopReportSourceData(serial, startDate, endDate, report);
    
    assertNotNull(data);
    assertTrue(data.size() > 0); 
    assertEquals(1, data.size());
    
  }
  
  @Test
  public void testParticipant() throws Exception
  {
    String part = mParmsMopReportDao.getParticipant();
    assertNotNull(part);
    assertEquals("LBSL", part);
  }
}

package uk.co.utilisoft.parmsmop.file.sp11;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;

public class MopReportBuilderTest
{

  @Test
  public void test_addIfUnique_nothingToAdd() throws Exception
  {
    TestMopReportBuilder builder = new TestMopReportBuilder();
    
    List<ParmsReportSummary> allParmsReporttSummaries = new ArrayList<ParmsReportSummary>();
    List<ParmsReportSummary> additionalParmsReportSummaries = new ArrayList<ParmsReportSummary>();
    
    assertEquals(0, allParmsReporttSummaries.size());
    
    //testMethod
    builder.addIfUnique(allParmsReporttSummaries, additionalParmsReportSummaries);
    
    assertEquals(0, allParmsReporttSummaries.size());
  }
  

  @Test
  public void test_addIfUnique_OneToAddToEmptyList() throws Exception
  {
    TestMopReportBuilder builder = new TestMopReportBuilder();
    
    List<ParmsReportSummary> allParmsReporttSummaries = new ArrayList<ParmsReportSummary>();
    List<ParmsReportSummary> additionalParmsReportSummaries = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary prs1ToAdd = new ParmsReportSummary();
    prs1ToAdd.setPk(new BigInteger("1"));
    additionalParmsReportSummaries.add(prs1ToAdd);
    
    assertEquals(0, allParmsReporttSummaries.size());
    
    //testMethod
    builder.addIfUnique(allParmsReporttSummaries, additionalParmsReportSummaries);
    
    assertEquals(1, allParmsReporttSummaries.size());
    assertEquals(new BigInteger("1"), allParmsReporttSummaries.get(0).getPk());
  }
  

  @Test
  public void test_addIfUnique_OneToAddToListOfOne() throws Exception
  {
    TestMopReportBuilder builder = new TestMopReportBuilder();
    
    List<ParmsReportSummary> allParmsReporttSummaries = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary prs0ToAdd = new ParmsReportSummary();
    prs0ToAdd.setPk(new BigInteger("0"));
    prs0ToAdd.setParticipantId("0");
    prs0ToAdd.setReportString2("0");
    prs0ToAdd.setHalfHourlyIndicator("N");
    allParmsReporttSummaries.add(prs0ToAdd);
    
    
    List<ParmsReportSummary> additionalParmsReportSummaries = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary prs1ToAdd = new ParmsReportSummary();
    prs1ToAdd.setPk(new BigInteger("1"));
    prs1ToAdd.setParticipantId("1");
    prs0ToAdd.setReportString2("1");
    prs0ToAdd.setHalfHourlyIndicator("H");
    additionalParmsReportSummaries.add(prs1ToAdd);
    
    assertEquals(1, allParmsReporttSummaries.size());
    
    //testMethod
    builder.addIfUnique(allParmsReporttSummaries, additionalParmsReportSummaries);
    
    assertEquals(2, allParmsReporttSummaries.size());
    
    boolean found0 = false, found1 = false;
    for (ParmsReportSummary prs : allParmsReporttSummaries)
    {
      if (prs.getPk().equals(new BigInteger("0")))
      {
        found0 = true;
      }

      if (prs.getPk().equals(new BigInteger("1")))
      {
        found1 = true;
      }
    }
    
    assertTrue("not found 0", found0);
    assertTrue("not found 1", found1);
    
    //assertEquals(new BigInteger("1"), allParmsReporttSummaries.get(0).getPk());
  }
  


  @Test
  public void test_addIfUnique_OneToAddToListOfOne_butSame() throws Exception
  {
    TestMopReportBuilder builder = new TestMopReportBuilder();
    
    List<ParmsReportSummary> allParmsReporttSummaries = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary prs0ToAdd = new ParmsReportSummary();
    prs0ToAdd.setPk(new BigInteger("0"));
    prs0ToAdd.setParticipantId("0");
    prs0ToAdd.setReportString2("0");
    prs0ToAdd.setHalfHourlyIndicator("N");
    allParmsReporttSummaries.add(prs0ToAdd);
    
    
    List<ParmsReportSummary> additionalParmsReportSummaries = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary prs1ToAdd = new ParmsReportSummary();
    prs1ToAdd.setPk(new BigInteger("0"));
    prs1ToAdd.setParticipantId("0");
    prs1ToAdd.setReportString2("0");
    prs1ToAdd.setHalfHourlyIndicator("N");
    additionalParmsReportSummaries.add(prs1ToAdd);
    
    assertEquals(1, allParmsReporttSummaries.size());
    
    assertTrue("they should be the same", prs0ToAdd.equals(prs1ToAdd));
    
    //testMethod
    builder.addIfUnique(allParmsReporttSummaries, additionalParmsReportSummaries);
    
    //should be just the onhe as they are equal
    assertEquals(1, allParmsReporttSummaries.size());

    assertEquals(1, allParmsReporttSummaries.size());
    assertEquals(new BigInteger("0"), allParmsReporttSummaries.get(0).getPk());
  }
  
  
  
  /**
   * Test class
   * @author comerb
   *
   */
  class TestMopReportBuilder extends MopReportBuilder
  {

    @Override
    public String getSerial()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void addParmsReportSummaries(ParmsMopReport report)
    {
      // TODO Auto-generated method stub
      
    }
    
  }
}

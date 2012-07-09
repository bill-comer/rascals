package uk.co.utilisoft.parmsmop.file.sp11;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;
import uk.co.utilisoft.utils.Freeze;

public class Nm12MopRowBuilderTest
{
  @Test
  public void testCreateAllRows_sameMO() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return false;
      }
    };
    summar1.setParticipantId("11");
    summar1.setHalfHourlyIndicator("N");
    summar1.setReportString2("2222");
    summar1.setReportString3("13");
    summar1.setReportString4("14");
    summar1.setReportString5("15");
    summar1.setReportString6("16");
    summar1.setReportString7("17");
    summar1.setReportString8("18");
    summar1.setReportString9("19");
    summar1.setReportString10("110");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return false;
      }
    };
    summar2.setParticipantId("21");
    summar2.setHalfHourlyIndicator("N");
    summar2.setReportString2("2222");
    summar2.setReportString3("23");
    summar2.setReportString4("24");
    summar2.setReportString5("25");
    summar2.setReportString6("26");
    summar2.setReportString7("27");
    summar2.setReportString8("28");
    summar2.setReportString9("29");
    summar2.setReportString10("210");

    // add rows
    inputRows.add(summar1);
    inputRows.add(summar2);
    
    Nm12MopRowBuilder builder = new Nm12MopRowBuilder()
    {
      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksumCalculator();
      }
    };
    
    //test method
    
    List<String> resultRows = builder.buildAllRows("FRED", inputRows);
    
    assertNotNull(resultRows);
    assertTrue(resultRows.size() > 0);
    assertEquals(5, resultRows.size());
    
    assertEquals("ZHD|P0234001|M|FRED|Z|POOL|20050426111050", resultRows.get(0));
    
    String row1 = resultRows.get(1);
    String row2 = resultRows.get(2);
    String row3 = resultRows.get(3);
    String footer = resultRows.get(4);

    assertEquals("SUB|N|2222|20050331|M", row1);

    assertEquals("2NM|13|11|14|15|16|17|18|19|110", row2);
    assertEquals("2NM|23|21|24|25|26|27|28|29|210", row3);
    
    assertEquals("ZPT|5|998877", footer);
  }
  

  @Test
  public void testCreateAllRows_difftMO() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return false;
      }
    };
    summar1.setParticipantId("11");
    summar1.setHalfHourlyIndicator("N");
    summar1.setReportString2("12");
    summar1.setReportString3("13");
    summar1.setReportString4("14");
    summar1.setReportString5("15");
    summar1.setReportString6("16");
    summar1.setReportString7("17");
    summar1.setReportString8("18");
    summar1.setReportString9("19");
    summar1.setReportString10("110");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return false;
      }
    };
    summar2.setParticipantId("21");
    summar2.setHalfHourlyIndicator("N");
    summar2.setReportString2("22");
    summar2.setReportString3("23");
    summar2.setReportString4("24");
    summar2.setReportString5("25");
    summar2.setReportString6("26");
    summar2.setReportString7("27");
    summar2.setReportString8("28");
    summar2.setReportString9("29");
    summar2.setReportString10("210");

    // add rows
    inputRows.add(summar1);
    inputRows.add(summar2);
    
    Nm12MopRowBuilder builder = new Nm12MopRowBuilder()
    {
      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksumCalculator();
      }
    };
    
    //test method
    
    List<String> resultRows = builder.buildAllRows("FRED", inputRows);
    
    assertNotNull(resultRows);
    assertTrue(resultRows.size() > 0);
    assertEquals(6, resultRows.size());
    
    assertEquals("ZHD|P0234001|M|FRED|Z|POOL|20050426111050", resultRows.get(0));
    
    String row1 = resultRows.get(1);
    String row2 = resultRows.get(2);
    String row3 = resultRows.get(3);
    String row4 = resultRows.get(4);
    String footer = resultRows.get(5);

    assertEquals("SUB|N|12|20050331|M", row1);
    assertEquals("2NM|13|11|14|15|16|17|18|19|110", row2);
    
    assertEquals("SUB|N|22|20050331|M", row3);
    assertEquals("2NM|23|21|24|25|26|27|28|29|210", row4);
    
    assertEquals("ZPT|6|998877", footer);
  }

}

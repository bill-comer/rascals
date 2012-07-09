package uk.co.utilisoft.parmsmop.file.sp11;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.parms.file.PoolChecksumCalculator;
import uk.co.utilisoft.parmsmop.domain.ParmsReportSummary;
import uk.co.utilisoft.utils.Freeze;

public class Sp11MopRowBuilderTest
{

  @Test
  public void test_SP11Header() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    builder.setParticpantId("FRED");
    
    //C D or M ?
    assertEquals("ZHD|P0224001|M|FRED|Z|POOL|20050426111050", builder.getHeader());
  }
  

  @Test
  public void test_SP11SubRecord_nonHH() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    
    ParmsReportSummary summary = new ParmsReportSummary();
    summary.setParticipantId("FRED");
    summary.setHalfHourlyIndicator("N");
    
    //N or H ?
    assertEquals("SUB|N|X|FRED|20050331|M", builder.getSubRecord(summary));
  }
  
  @Test
  public void test_SP11SubRecord_HH() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    
    ParmsReportSummary summary = new ParmsReportSummary();
    summary.setParticipantId("BERT");
    summary.setHalfHourlyIndicator("H");
    
    //N or H ?
    assertEquals("SUB|H|X|BERT|20050331|M", builder.getSubRecord(summary));
  }
  

  @Test
  public void test_SP11_lineForSummary_allFieldsNull() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    
    ParmsReportSummary summary = new ParmsReportSummary();
    summary.setParticipantId("BERT");
    summary.setHalfHourlyIndicator("H");
    
    //N or H ?
    assertEquals("X11|UUUU|0|0|0|0|0|0|0|0", builder.createLineForSerial(summary));
  }
  

  @Test
  public void test_SP11_lineForSummary_AllFieldsPopulated() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    
    ParmsReportSummary summary = new ParmsReportSummary();
    summary.setParticipantId("BERT");
    summary.setHalfHourlyIndicator("H");
    
    summary.setReportString2("BILL");
    summary.setReportString3("3");
    summary.setReportString4("4");
    summary.setReportString5("5");
    summary.setReportString6("6");
    summary.setReportString7("7");
    summary.setReportString8("8");
    summary.setReportString9("9");
    summary.setReportString10("10");
    
    //N or H ?
    assertEquals("X11|BILL|3|4|5|6|7|8|9|10", builder.createLineForSerial(summary));
  }
  
  @Test
  public void test_buildRows_oneRow() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summary = new ParmsReportSummary();
    summary.setParticipantId("BERT");
    summary.setHalfHourlyIndicator("H");
    summary.setReportString2("BILL");
    summary.setReportString3("3");
    summary.setReportString4("4");
    summary.setReportString5("5");
    summary.setReportString6("6");
    summary.setReportString7("7");
    summary.setReportString8("8");
    summary.setReportString9("9");
    summary.setReportString10("10");
    inputRows.add(summary);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    

    List<String> resultRows = new ArrayList<String>();
    
    //test method
    builder.buildBodyOfReport(inputRows, resultRows);
    
    assertNotNull(resultRows);
    assertEquals(2, resultRows.size());
    
    String row1 = resultRows.get(0);
    assertEquals("SUB|H|X|BERT|20050331|M", row1);

    String row2 = resultRows.get(1);
    assertEquals("X11|BILL|3|4|5|6|7|8|9|10", row2);
  }
  

  @Test
  public void test_buildRows_twoRows_sameSuppliers() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar1.setParticipantId("AAAA");
    summar1.setHalfHourlyIndicator("H");
    summar1.setReportString2("BB11");
    summar1.setReportString3("31");
    summar1.setReportString4("41");
    summar1.setReportString5("51");
    summar1.setReportString6("61");
    summar1.setReportString7("71");
    summar1.setReportString8("81");
    summar1.setReportString9("91");
    summar1.setReportString10("101");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      @Override
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar2.setParticipantId("AAAA");
    summar2.setHalfHourlyIndicator("H");
    summar2.setReportString2("AA11");
    summar2.setReportString3("32");
    summar2.setReportString4("42");
    summar2.setReportString5("52");
    summar2.setReportString6("62");
    summar2.setReportString7("72");
    summar2.setReportString8("82");
    summar2.setReportString9("92");
    summar2.setReportString10("102");

    inputRows.add(summar1);
    inputRows.add(summar2);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    

    List<String> resultRows = new ArrayList<String>();
    
    //test method
    builder.buildBodyOfReport(inputRows, resultRows);
    
    assertNotNull(resultRows);
    assertEquals(3, resultRows.size());
    
    String row1 = resultRows.get(0);
    assertEquals("SUB|H|X|AAAA|20050331|M", row1);

    String row2 = resultRows.get(1);
    String row3 = resultRows.get(2);

    assertEquals("X11|BB11|31|41|51|61|71|81|91|101", row2);
    assertEquals("X11|AA11|32|42|52|62|72|82|92|102", row3);
  }
  

  @Test
  public void test_buildRows_threeRows_sameSuppliers() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar1.setParticipantId("AAAA");
    summar1.setHalfHourlyIndicator("H");
    summar1.setReportString2("BB11");
    summar1.setReportString3("31");
    summar1.setReportString4("41");
    summar1.setReportString5("51");
    summar1.setReportString6("61");
    summar1.setReportString7("71");
    summar1.setReportString8("81");
    summar1.setReportString9("91");
    summar1.setReportString10("101");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar2.setParticipantId("AAAA");
    summar2.setHalfHourlyIndicator("H");
    summar2.setReportString2("AA11");
    summar2.setReportString3("32");
    summar2.setReportString4("42");
    summar2.setReportString5("52");
    summar2.setReportString6("62");
    summar2.setReportString7("72");
    summar2.setReportString8("82");
    summar2.setReportString9("92");
    summar2.setReportString10("102");
    

    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar3 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar3.setParticipantId("AAAA");
    summar3.setHalfHourlyIndicator("H");
    summar3.setReportString2("CC11");
    summar3.setReportString3("33");
    summar3.setReportString4("43");
    summar3.setReportString5("53");
    summar3.setReportString6("63");
    summar3.setReportString7("73");
    summar3.setReportString8("83");
    summar3.setReportString9("93");
    summar3.setReportString10("103");

    inputRows.add(summar1);
    inputRows.add(summar2);
    inputRows.add(summar3);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    

    List<String> resultRows = new ArrayList<String>();
    
    //test method
    builder.buildBodyOfReport(inputRows, resultRows);
    
    assertNotNull(resultRows);
    assertEquals(4, resultRows.size());
    
    String row1 = resultRows.get(0);
    assertEquals("SUB|H|X|AAAA|20050331|M", row1);

    String row2 = resultRows.get(1);
    String row3 = resultRows.get(2);
    String row4 = resultRows.get(3);

    assertEquals("X11|BB11|31|41|51|61|71|81|91|101", row2);
    assertEquals("X11|AA11|32|42|52|62|72|82|92|102", row3);
    assertEquals("X11|CC11|33|43|53|63|73|83|93|103", row4);
  }
  
  @Test
  public void test_buildRows_twoRows_difftSuppliers() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar1.setParticipantId("BBBB");
    summar1.setHalfHourlyIndicator("H");
    summar1.setReportString2("BB11");
    summar1.setReportString3("31");
    summar1.setReportString4("41");
    summar1.setReportString5("51");
    summar1.setReportString6("61");
    summar1.setReportString7("71");
    summar1.setReportString8("81");
    summar1.setReportString9("91");
    summar1.setReportString10("101");
    inputRows.add(summar1);
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar2.setParticipantId("AAAA");
    summar2.setHalfHourlyIndicator("H");
    summar2.setReportString2("AA11");
    summar2.setReportString3("32");
    summar2.setReportString4("42");
    summar2.setReportString5("52");
    summar2.setReportString6("62");
    summar2.setReportString7("72");
    summar2.setReportString8("82");
    summar2.setReportString9("92");
    summar2.setReportString10("102");
    inputRows.add(summar2);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    

    List<String> resultRows = new ArrayList<String>();
    
    //test method
    builder.buildBodyOfReport(inputRows, resultRows);
    
    assertNotNull(resultRows);
    assertEquals(4, resultRows.size());
    
    String row1 = resultRows.get(0);
    assertEquals("SUB|H|X|AAAA|20050331|M", row1);

    String row2 = resultRows.get(1);
    assertEquals("X11|AA11|32|42|52|62|72|82|92|102", row2);
    

    String row3 = resultRows.get(2);
    assertEquals("SUB|H|X|BBBB|20050331|M", row3);

    String row4 = resultRows.get(3);
    assertEquals("X11|BB11|31|41|51|61|71|81|91|101", row4);
  }
  

  @Test
  public void test_buildRows_twoRows_difftSuppliers_oppositeOrder() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar1.setParticipantId("BBBB");
    summar1.setHalfHourlyIndicator("H");
    summar1.setReportString2("BB11");
    summar1.setReportString3("31");
    summar1.setReportString4("41");
    summar1.setReportString5("51");
    summar1.setReportString6("61");
    summar1.setReportString7("71");
    summar1.setReportString8("81");
    summar1.setReportString9("91");
    summar1.setReportString10("101");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar2.setParticipantId("AAAA");
    summar2.setHalfHourlyIndicator("H");
    summar2.setReportString2("AA11");
    summar2.setReportString3("32");
    summar2.setReportString4("42");
    summar2.setReportString5("52");
    summar2.setReportString6("62");
    summar2.setReportString7("72");
    summar2.setReportString8("82");
    summar2.setReportString9("92");
    summar2.setReportString10("102");

    inputRows.add(summar2);
    inputRows.add(summar1);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    

    List<String> resultRows = new ArrayList<String>();
    
    //test method
    builder.buildBodyOfReport(inputRows, resultRows);
    
    assertNotNull(resultRows);
    assertEquals(4, resultRows.size());
    
    String row1 = resultRows.get(0);
    assertEquals("SUB|H|X|AAAA|20050331|M", row1);

    String row2 = resultRows.get(1);
    assertEquals("X11|AA11|32|42|52|62|72|82|92|102", row2);
    

    String row3 = resultRows.get(2);
    assertEquals("SUB|H|X|BBBB|20050331|M", row3);

    String row4 = resultRows.get(3);
    assertEquals("X11|BB11|31|41|51|61|71|81|91|101", row4);
  }
  

  @Test
  public void test_buildRows_twoRows_sameSuppliers_difftHH() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar1.setParticipantId("BBBB");
    summar1.setHalfHourlyIndicator("H");
    summar1.setReportString2("BB11");
    summar1.setReportString3("31");
    summar1.setReportString4("41");
    summar1.setReportString5("51");
    summar1.setReportString6("61");
    summar1.setReportString7("71");
    summar1.setReportString8("81");
    summar1.setReportString9("91");
    summar1.setReportString10("101");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar2.setParticipantId("BBBB");
    summar2.setHalfHourlyIndicator("N");
    summar2.setReportString2("AA11");
    summar2.setReportString3("32");
    summar2.setReportString4("42");
    summar2.setReportString5("52");
    summar2.setReportString6("62");
    summar2.setReportString7("72");
    summar2.setReportString8("82");
    summar2.setReportString9("92");
    summar2.setReportString10("102");

    // add rows
    inputRows.add(summar1);
    inputRows.add(summar2);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder();
    

    List<String> resultRows = new ArrayList<String>();
    
    //test method
    builder.buildBodyOfReport(inputRows, resultRows);
    
    assertNotNull(resultRows);
    assertEquals(4, resultRows.size());
    
    String row1 = resultRows.get(0);
    assertEquals("SUB|N|X|BBBB|20050331|M", row1);

    String row2 = resultRows.get(1);
    assertEquals("X11|AA11|32|42|52|62|72|82|92|102", row2);
    

    String row3 = resultRows.get(2);
    assertEquals("SUB|H|X|BBBB|20050331|M", row3);

    String row4 = resultRows.get(3);
    assertEquals("X11|BB11|31|41|51|61|71|81|91|101", row4);
  }
  
  @Test
  public void testCreateAllRows() throws Exception
  {
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    
    //test data
    List<ParmsReportSummary> inputRows = new ArrayList<ParmsReportSummary>();
    ParmsReportSummary summar1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar1.setParticipantId("BBBB");
    summar1.setHalfHourlyIndicator("H");
    summar1.setReportString2("BB11");
    summar1.setReportString3("31");
    summar1.setReportString4("41");
    summar1.setReportString5("51");
    summar1.setReportString6("61");
    summar1.setReportString7("71");
    summar1.setReportString8("81");
    summar1.setReportString9("91");
    summar1.setReportString10("101");
    
    //second report but Supplier is less alphabeically so should appers first
    ParmsReportSummary summar2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    summar2.setParticipantId("BBBB");
    summar2.setHalfHourlyIndicator("N");
    summar2.setReportString2("AA11");
    summar2.setReportString3("32");
    summar2.setReportString4("42");
    summar2.setReportString5("52");
    summar2.setReportString6("62");
    summar2.setReportString7("72");
    summar2.setReportString8("82");
    summar2.setReportString9("92");
    summar2.setReportString10("102");

    // add rows
    inputRows.add(summar1);
    inputRows.add(summar2);
    
    Sp11MopRowBuilder builder = new Sp11MopRowBuilder()
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
    
    assertEquals("ZHD|P0224001|M|FRED|Z|POOL|20050426111050", resultRows.get(0));
    
    String row1 = resultRows.get(1);
    assertEquals("SUB|N|X|BBBB|20050331|M", row1);

    String row2 = resultRows.get(2);
    assertEquals("X11|AA11|32|42|52|62|72|82|92|102", row2);
    

    String row3 = resultRows.get(3);
    assertEquals("SUB|H|X|BBBB|20050331|M", row3);

    String row4 = resultRows.get(4);
    assertEquals("X11|BB11|31|41|51|61|71|81|91|101", row4);
    
    String footer = resultRows.get(5);
    assertEquals("ZPT|6|998877", footer);
  }
  
}

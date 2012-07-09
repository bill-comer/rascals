package uk.co.utilisoft.parms.file.sp04;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.domain.GSPDefinition;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;
import uk.co.utilisoft.utils.Freeze;


public class Sp04RowBuilderTest
{

  @Test
  public void buildAllRows_withNoActiveData_dateGreaterThan7_noStandardGSPs() throws Exception
  { 
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    /**
     * Date is greater than 7 so period end date is end of current month
     */
    Supplier supplier = new Supplier("FRED");
    Sp04RowBuilder builder = new Sp04RowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        return gsps;
      }

      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksumCalculator();
      }
    };
    
    List<Sp04Data> activeRows = new ArrayList<Sp04Data>();
    
    //test method
    List<String> rows = builder.buildAllRows(supplier, activeRows);
    
    assertNotNull("what - no rows", rows);
    assertEquals("should be header, SUB & footer", 3, rows.size());
    assertEquals("looks like the HEADER is stuffed", "ZHD|P0142001|X|FRED|Z|POOL|20050426111050", rows.get(0));
    assertEquals("looks like the SUB is stuffed","SUB|H|X|FRED|20050331|M", rows.get(1));
    assertEquals("looks like the FOOTER is stuffed","ZPT|3|998877", rows.get(2));
  }
  

  @Test
  public void buildAllRows_withNoActiveData_dateGreaterThan7_TwoStandardGSPs() throws Exception
  { 
    Freeze.freeze(new DateTime(2005, 4, 26, 11, 10, 50, 00));
    /**
     * Date is greater than 7 so period end date is end of current month
     */
    Supplier supplier = new Supplier("FRED");
    Sp04RowBuilder builder = new Sp04RowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        
        GSPDefinition gsp1 = new GSPDefinition("_A");
        gsps.add(gsp1);
        GSPDefinition gsp2 = new GSPDefinition("_B");
        gsps.add(gsp2);
        
        return gsps;
      }

      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksumCalculator();
      }
    };
    
    List<Sp04Data> activeRows = new ArrayList<Sp04Data>();
    
    //test method
    List<String> rows = builder.buildAllRows(supplier, activeRows);
    
    assertNotNull("what - no rows", rows);
    assertEquals("should be header, SUB & footer & 2 SP4s, both NULL", 5, rows.size());
    assertEquals("looks like the HEADER is stuffed", "ZHD|P0142001|X|FRED|Z|POOL|20050426111050", rows.get(0));
    assertEquals("looks like the SUB is stuffed","SUB|H|X|FRED|20050331|M", rows.get(1));
    assertEquals("shud be null row for _A","SP4|_A||||", rows.get(2));
    assertEquals("shud be null row for _B","SP4|_B||||", rows.get(3));
    assertEquals("looks like the FOOTER is stuffed","ZPT|5|998877", rows.get(4));
  }
  


  @Test
  public void buildAllRows_withNoActiveData_dateStartOfMonth() throws Exception
  { 
    Freeze.freeze(new DateTime(2005, 4, 3, 11, 10, 50, 00));
    /**
     * Date is greater than 7 so period end date is end of current month
     */
    Supplier supplier = new Supplier("FRED");
    Sp04RowBuilder builder = new Sp04RowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        
        GSPDefinition gsp1 = new GSPDefinition("_A");
        gsps.add(gsp1);
        GSPDefinition gsp2 = new GSPDefinition("_B");
        gsps.add(gsp2);
        
        return gsps;
      }

      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksumCalculator();
      }
    };
    

    List<Sp04Data> activeRows = new ArrayList<Sp04Data>();
    
    
    //test method
    List<String> rows = builder.buildAllRows(supplier, activeRows);
    
    assertNotNull("what - no rows", rows);
    assertEquals("should be header, SUB & footer & 2 SP4s, both NULL", 5, rows.size());
    assertEquals("looks like the HEADER is stuffed", "ZHD|P0142001|X|FRED|Z|POOL|20050403111050", rows.get(0));
    assertEquals("looks like the SUB is stuffed","SUB|H|X|FRED|20050331|M", rows.get(1));
    assertEquals("shud be null row for _A","SP4|_A||||", rows.get(2));
    assertEquals("shud be null row for _B","SP4|_B||||", rows.get(3));
    assertEquals("looks like the FOOTER is stuffed","ZPT|5|998877", rows.get(4));
  }
  


  @Test
  public void buildAllRows_withOneRowActiveData_dateStartOfMonth() throws Exception
  { 
    Freeze.freeze(new DateTime(2005, 4, 3, 11, 10, 50, 00));
    /**
     * Date is greater than 7 so period end date is end of current month
     */
    Supplier supplier = new Supplier("FRED");
    Sp04RowBuilder builder = new Sp04RowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        
        GSPDefinition gsp1 = new GSPDefinition("_G");
        gsps.add(gsp1);
        GSPDefinition gsp2 = new GSPDefinition("_B");
        gsps.add(gsp2);
        
        return gsps;
      }

      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksumCalculator();
      }
    };
    
    List<Sp04Data> activeRows = new ArrayList<Sp04Data>();
    MPANCore mpan = new MPANCore("1000000000003");
    Sp04Data sp04Serial = new Sp04Data();
    sp04Serial.setGspGroupId("_G");
    sp04Serial.setStandard1(10L);
    sp04Serial.setStandard2(20L);
    sp04Serial.setStandard3(30.33F);
    sp04Serial.setMpanCore(mpan);
    
    activeRows.add(sp04Serial);
    
    //test method
    List<String> rows = builder.buildAllRows(supplier, activeRows);
    
    assertNotNull("what - no rows", rows);
    assertEquals("should be header, SUB, 2 SP4 record & footer", 5, rows.size());
    assertEquals("looks like the HEADER is stuffed", "ZHD|P0142001|X|FRED|Z|POOL|20050403111050", rows.get(0));
    assertEquals("looks like the SUB is stuffed","SUB|H|X|FRED|20050331|M", rows.get(1));
    assertEquals("looks like the SP04 Serial row is stuffed","SP4|_G|1000000000003|10|20|30.33", rows.get(2));
    assertEquals("shud be null row for _B","SP4|_B||||", rows.get(3));
    assertEquals("looks like the FOOTER is stuffed","ZPT|5|998877", rows.get(4));
  }
  
  @Test
  public void Sp04SubRecord_endofPeriod_dateAtStartOfMonth() throws Exception
  { 
    Freeze.freeze(3, 4, 2010);
    Sp04SubRecord record = new Sp04SubRecord();
    assertEquals("20100331", record.getPeriodEndDate());
  }
  
  @Test
  public void createLineForSerial_forOneLine() throws Exception
  { 
    Sp04RowBuilder builder = new Sp04RowBuilder();
    
    MPANCore mpan = new MPANCore("1000000000003");
    Sp04Data sp04Serial = new Sp04Data();
    sp04Serial.setGspGroupId("_G");
    sp04Serial.setStandard1(10L);
    sp04Serial.setStandard2(20L);
    sp04Serial.setStandard3(30.33F);
    sp04Serial.setMpanCore(mpan);
    
    assertEquals("SP4|_G|1000000000003|10|20|30.33", builder.createLineForSerial(sp04Serial) );
  }
  

  @Test
  public void Sp04SubRecord_endofPeriod_dateAtEndOfMonth() throws Exception
  { 
    Freeze.freeze(25, 4, 2010);
    Sp04SubRecord record = new Sp04SubRecord();
    assertEquals("20100331", record.getPeriodEndDate());
  }

  
  @After
  public void thaw()
  {
    Freeze.thaw();
  }
}



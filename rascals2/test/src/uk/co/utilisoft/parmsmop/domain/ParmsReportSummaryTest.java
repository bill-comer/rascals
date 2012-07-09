package uk.co.utilisoft.parmsmop.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParmsReportSummaryTest
{

  @Test
  public void testEquals_thesame_noOtherfields() throws Exception
  {
    ParmsReportSummary s1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    s1.setParticipantId("s1");
    s1.setHalfHourlyIndicator("H");
    
    ParmsReportSummary s2 = new ParmsReportSummary();
    s2.setParticipantId("s1");
    s2.setHalfHourlyIndicator("H");
    
    //test method
    assertTrue(s1.equals(s2));
    assertEquals(0, s1.compareTo(s2));
  }
  

  @Test
  public void testEquals_thesame_oneOtherfields() throws Exception
  {
    ParmsReportSummary s1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    s1.setParticipantId("s1");
    s1.setHalfHourlyIndicator("H");
    s1.setReportString10("2222");
    
    ParmsReportSummary s2 = new ParmsReportSummary();
    s2.setParticipantId("s1");
    s2.setHalfHourlyIndicator("H");
    s2.setReportString10("1010");
   
    
    //test method
    assertTrue(s1.equals(s2));
    assertEquals(0, s1.compareTo(s2));
  }
  

  @Test
  public void testEquals_difftHH_oneOtherfields() throws Exception
  {
    ParmsReportSummary s1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    s1.setParticipantId("s1");
    s1.setHalfHourlyIndicator("H");
    s1.setReportString10("2222");
    
    ParmsReportSummary s2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    s2.setParticipantId("s1");
    s2.setHalfHourlyIndicator("N");
    s2.setReportString10("1010");
   
    
    //test method
    assertFalse(s1.equals(s2));
    assertEquals(1, s1.compareTo(s2));
    assertEquals(-1, s2.compareTo(s1));
  }
  

  @Test
  public void testEquals_difftSupplier_oneOtherfields() throws Exception
  {
    ParmsReportSummary s1 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    s1.setParticipantId("s1");
    s1.setHalfHourlyIndicator("H");
    s1.setReportString10("2222");
    
    ParmsReportSummary s2 = new ParmsReportSummary()
    {
      public boolean isHHStatusRequired() {
        return true;
      }
    };
    s2.setParticipantId("s2");
    s2.setHalfHourlyIndicator("H");
    s2.setReportString10("1010");
   
    //test method
    assertFalse(s1.equals(s2));
    assertEquals(-1, s1.compareTo(s2));
    assertEquals(1, s2.compareTo(s1));
  }
}

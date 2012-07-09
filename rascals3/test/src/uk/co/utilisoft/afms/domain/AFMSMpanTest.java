package uk.co.utilisoft.afms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.aspectj.lang.annotation.Before;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.utils.Freeze;

public class AFMSMpanTest
{

  @Test
  public void test_getHalfHourlyMeter_noneToGet() throws Exception
  {
    AFMSMpan mpan = new AFMSMpan();
    
    assertNull("no meters so should return null", mpan.getFirstCreatedHalfHourlyMeter());
  }
  
  @Test
  public void test_getHalfHourlyMeter_noHHMeters() throws Exception
  {
    AFMSMpan mpan = new AFMSMpan();
    AFMSMeter meter1 = new AFMSMeter();
    meter1.setSettlementDate(new DateTime().minusDays(1));

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setSettlementDate(new DateTime().minusDays(1));
    mpan.getMeters().add(meter2);
    
    assertNull("no HH meters so should return null", mpan.getFirstCreatedHalfHourlyMeter());
  }
  

  @Test
  public void test_getHalfHourlyMeter_oneSmartMeters() throws Exception
  {
    AFMSMpan mpan = new AFMSMpan();
    AFMSMeter meter1 = new AFMSMeter();
    meter1.setSettlementDate(new DateTime().minusDays(1));
    meter1.setOutstationId("OUT");
    meter1.setMeterType("NCAMR");
    mpan.getMeters().add(meter1);

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setSettlementDate(new DateTime().minusDays(1));
    mpan.getMeters().add(meter2);
    
    assertNull("no HH meters so should return null", mpan.getFirstCreatedHalfHourlyMeter());
  }
  


  @Test
  public void test_getHalfHourlyMeter_oneHHMeters() throws Exception
  {
    AFMSMpan mpan = new AFMSMpan();
    
    AFMSMeter meter1_HH = new AFMSMeter();
    meter1_HH.setSettlementDate(new DateTime().minusDays(1));
    meter1_HH.setOutstationId("OUT");
    meter1_HH.setMeterType("H");
    mpan.getMeters().add(meter1_HH);

    AFMSMeter meter2_nonHH = new AFMSMeter();
    meter2_nonHH.setSettlementDate(new DateTime().minusDays(1));
    mpan.getMeters().add(meter2_nonHH);
    
    AFMSMeter foundMeter = mpan.getFirstCreatedHalfHourlyMeter();
    assertNotNull("one HH meter so should return meter1", foundMeter);
    assertEquals("OUT", "OUT", foundMeter.getOutstationId());
  }
  


  @Test
  public void test_getHalfHourlyMeter_twoHHMeters_getFirst() throws Exception
  {
    AFMSMpan mpan = new AFMSMpan();
    
    AFMSMeter meter1_HH = new AFMSMeter();
    meter1_HH.setSettlementDate(new DateTime().minusDays(1));
    meter1_HH.setOutstationId("OUT1");
    meter1_HH.setMeterType("H");
    mpan.getMeters().add(meter1_HH);

    AFMSMeter meter2_HH = new AFMSMeter();
    meter2_HH.setSettlementDate(new DateTime().minusDays(2));
    meter2_HH.setOutstationId("OUT2");
    meter2_HH.setMeterType("H");
    mpan.getMeters().add(meter2_HH);
    

    AFMSMeter meter3_nonHH = new AFMSMeter();
    meter3_nonHH.setSettlementDate(new DateTime());
    mpan.getMeters().add(meter3_nonHH);
    
    assertNotNull("one HH meter so should return meter2", mpan.getFirstCreatedHalfHourlyMeter());
    assertEquals("meter2 is created first so should be OUT2", "OUT2", mpan.getFirstCreatedHalfHourlyMeter().getOutstationId());
  }
}

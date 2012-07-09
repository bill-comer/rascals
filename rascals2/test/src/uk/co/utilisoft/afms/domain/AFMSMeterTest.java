package uk.co.utilisoft.afms.domain;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class AFMSMeterTest
{

  @Test
  public void isThisMeterActiveNow_bothset_andisAcive()
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setSettlementDate(new DateTime().minusYears(1));
    meter.setEffectiveToDateMSID(new DateTime().plusYears(1));
    
    assertTrue(meter.isThisMeterActiveNow());
  }
  

  @Test
  public void isThisMeterActiveNow_bothset_settlementInFuture()
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setSettlementDate(new DateTime().plusDays(1));
    meter.setEffectiveToDateMSID(new DateTime().plusYears(1));
    
    assertFalse(meter.isThisMeterActiveNow());
  }
  

  @Test
  public void isThisMeterActiveNow_bothset_effectiveToInPast()
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setSettlementDate(new DateTime().minusYears(1));
    meter.setEffectiveToDateMSID(new DateTime().minusDays(1));
    
    assertFalse(meter.isThisMeterActiveNow());
  }


  @Test
  public void isThisMeterActiveNow_noEffectiveTo_settlementInPast()
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setSettlementDate(new DateTime().minusYears(1));
    
    assertTrue(meter.isThisMeterActiveNow());
  }
  

  @Test
  public void isThisMeterActiveNow_noEffectiveTo_settlementInFuture()
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setSettlementDate(new DateTime().plusYears(1));
    
    assertFalse(meter.isThisMeterActiveNow());
  }
  
  @Test
  public void test_isThisASmartMeter_noOutstationID() throws Exception
  {
    AFMSMeter meter = new AFMSMeter();
    assertFalse("null outstaion id is not a smart meter", meter.isThisASmartMeter());
    
    meter.setOutstationId("");
    assertFalse("no outstaion id is not a smart meter", meter.isThisASmartMeter());
  }
  

  @Test
  public void test_isThisASmartMeter_noVALIDMeterType() throws Exception
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setOutstationId("OUT");
    
    assertFalse("null meterType is not a smart meter", meter.isThisASmartMeter());
    
    meter.setMeterType("");
    assertFalse("no meterType is not a smart meter", meter.isThisASmartMeter());
    

    meter.setMeterType("H");
    assertFalse("H meterType is not a smart meter", meter.isThisASmartMeter());
  }
  


  @Test
  public void test_isThisASmartMeter_VALIDMeterType() throws Exception
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setOutstationId("OUT");
    
    meter.setMeterType("NCAMR");
    assertTrue("NCAMR meterType is  a smart meter", meter.isThisASmartMeter());

    meter.setMeterType("RCAMY");
    assertTrue("RCAMY meterType is  a smart meter", meter.isThisASmartMeter());

    meter.setMeterType("RCAMR");
    assertTrue("RCAMR meterType is  a smart meter", meter.isThisASmartMeter());
  }
  
  @Test
  public void test_isMeterHalfHourly_noOutstationID() throws Exception
  {
    AFMSMeter meter = new AFMSMeter();
    assertFalse("null outstaion id is not a HH meter", meter.isHalfHourlyMeter());
    
    meter.setOutstationId("");
    assertFalse("no outstaion id is not a HH meter", meter.isHalfHourlyMeter());
  }
  

  @Test
  public void test_isMeterHalfHourly_INValidMeterType() throws Exception
  {
    AFMSMeter meter = new AFMSMeter();
    meter.setOutstationId("OUT");

    meter.setMeterType("NCAMR");
    assertFalse("NCAMR meterType is not a HH meter", meter.isHalfHourlyMeter());

    meter.setMeterType("RCAMY");
    assertFalse("RCAMY meterType is not a HH meter", meter.isHalfHourlyMeter());

    meter.setMeterType("RCAMR");
    assertFalse("RCAMR meterType is not a HH meter", meter.isHalfHourlyMeter());
  }

}

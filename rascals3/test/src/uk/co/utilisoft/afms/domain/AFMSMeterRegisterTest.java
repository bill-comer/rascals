package uk.co.utilisoft.afms.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class AFMSMeterRegisterTest
{

  @Test
  public void testisMaxDemandMeterRegisterType_null() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    assertFalse(register.isMeterRegisterTypeMaxDemand());
  }
  

  @Test
  public void testisMaxDemandMeterRegisterType_C() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("C");
    assertFalse(register.isMeterRegisterTypeMaxDemand());
  }
  

  @Test
  public void testisMaxDemandMeterRegisterType_M() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    assertTrue("M is a MaxDemand Type", register.isMeterRegisterTypeMaxDemand());
  }
  

  @Test
  public void testisMaxDemandMeterRegisterType_3() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("3");
    assertTrue("3 is MaxDemand Type", register.isMeterRegisterTypeMaxDemand());
  }
  
  @Test
  public void testisMeasurementQuantityKW_null() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    assertFalse(register.isMeasurementQuantityKW());
  }

  @Test
  public void testisMeasurementQuantityKW_X() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeasurementQuantityId("X");
    assertFalse(register.isMeasurementQuantityKW());
  }
  

  @Test
  public void testisMeasurementQuantityKW_kw() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeasurementQuantityId("kw");
    assertTrue(register.isMeasurementQuantityKW());
  }
  

  @Test
  public void testisMeasurementQuantityKW_KW() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeasurementQuantityId("KW");
    assertTrue(register.isMeasurementQuantityKW());
  }
  

  @Test
  public void testisMeterRegisterIdMD_null() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    assertFalse(register.isMeterRegisterIdMaxDemand());
  }

  @Test
  public void testisMeterRegisterIdMD_X() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegisterId("X");
    assertFalse(register.isMeterRegisterIdMaxDemand());
  }
  

  @Test
  public void testisMeterRegisterIdMD_md() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegisterId("md");
    assertTrue(register.isMeterRegisterIdMaxDemand());
  }
  

  @Test
  public void testisMeterRegisterIdMD_MD() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegisterId("MD");
    assertTrue(register.isMeterRegisterIdMaxDemand());
  }
}

package uk.co.utilisoft.afms.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.utilisoft.afms.domain.MeasurementClassification;


public class MeasurementClassificationTest
{

  @Test   
  public void testValues() throws Exception
  { 
    MeasurementClassification meas = new MeasurementClassification("A");
    assertFalse(meas.isHalfHourly());
    meas = new MeasurementClassification("B");
    assertFalse(meas.isHalfHourly());
    meas = new MeasurementClassification("C");
    assertTrue(meas.isHalfHourly());
    meas = new MeasurementClassification("D");
    assertTrue(meas.isHalfHourly());
    meas = new MeasurementClassification("E");
    assertTrue(meas.isHalfHourly());
    
    meas = new MeasurementClassification("a");
    assertFalse(meas.isHalfHourly());
    meas = new MeasurementClassification("b");
    assertFalse(meas.isHalfHourly());
    meas = new MeasurementClassification("c");
    assertTrue(meas.isHalfHourly());
    meas = new MeasurementClassification("d");
    assertTrue(meas.isHalfHourly());
    try {
    meas = new MeasurementClassification("e");
    assertTrue(meas.isHalfHourly());
    
      meas = new MeasurementClassification("x");
      meas.isHalfHourly();
      fail("Should have thrown a RException");
    } catch (RuntimeException e)
    {}
    try {
      meas = new MeasurementClassification("23423423");
      meas.isHalfHourly();
      fail("Should have thrown a RException");
    } catch (RuntimeException e)
    {}
    		
  }
}

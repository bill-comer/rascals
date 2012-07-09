package uk.co.utilisoft.parms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import static junit.framework.Assert.*;

public class ConfigurationParameterUnitTest
{
  @Test
  public void testHashCode()
  {
    try
    {
      ConfigurationParameter cp = new ConfigurationParameter();
      cp.hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }

  @Test
  public void parmsDpiFileLocation_valueIsAsExpected() throws Exception
  {
    assertEquals("PARMS_DPI_FILE_LOCATION", ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION.toString());
  }

  @Test
  public void valueOfPARMS_DPI_FILE_LOCATION() throws Exception
  {
    ConfigurationParameter.NAME value =  ConfigurationParameter.getName("PARMS_DPI_FILE_LOCATION");
    assertNotNull(value);
  }


  @Test
  public void valueOfInvalidValue() throws Exception
  {
    ConfigurationParameter.NAME value =  ConfigurationParameter.getName("somutDuff");
    assertTrue("invalid value should return null", value == null);
  }
  
  @Test
  public void Equals_same() throws Exception
  { 
    ConfigurationParameter v1 = new ConfigurationParameter(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION, "v1", "d1");
    ConfigurationParameter v2 = new ConfigurationParameter(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION, "v1", "d1");
    
    assertTrue(v1.equals(v2));
  }
  

  @Test
  public void Equals_same_difftVal() throws Exception
  { 
    ConfigurationParameter v1 = new ConfigurationParameter(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION, "v1", "d1");
    ConfigurationParameter v2 = new ConfigurationParameter(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION, "v2", "d1");
    
    assertTrue(v1.equals(v2));
  }
  

  @Test
  public void Equals_not_same() throws Exception
  { 
    ConfigurationParameter v1 = new ConfigurationParameter(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION, "v1", "d1");
    ConfigurationParameter v2 = new ConfigurationParameter(ConfigurationParameter.NAME.P0028_UPLOAD_ERROR_FILE_LOCATION, "v1", "d1");
    
    assertFalse(v1.equals(v2));
  }
  

  @Test
  public void Equals_not_same2() throws Exception
  { 
    ConfigurationParameter v1 = new ConfigurationParameter(ConfigurationParameter.NAME.PARMS_DPI_FILE_LOCATION, "v1", "d1");
    String fred = new String();
    
    assertFalse(v1.equals(fred));
  }
}

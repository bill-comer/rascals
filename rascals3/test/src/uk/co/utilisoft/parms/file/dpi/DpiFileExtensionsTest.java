package uk.co.utilisoft.parms.file.dpi;

import org.junit.Test;

import uk.co.utilisoft.parms.file.dpi.DpiFileExtensions;

import static org.junit.Assert.*;

public class DpiFileExtensionsTest
{

  @Test
  public void testAllMonths() throws Exception
  { 
    assertEquals("JAN", DpiFileExtensions.getExtension(1));
    assertEquals("FEB", DpiFileExtensions.getExtension(2));
    assertEquals("MAR", DpiFileExtensions.getExtension(3));
    assertEquals("APR", DpiFileExtensions.getExtension(4));
    assertEquals("MAY", DpiFileExtensions.getExtension(5));
    assertEquals("JUN", DpiFileExtensions.getExtension(6));
    assertEquals("JUL", DpiFileExtensions.getExtension(7));
    assertEquals("AUG", DpiFileExtensions.getExtension(8));
    assertEquals("SEP", DpiFileExtensions.getExtension(9));
    assertEquals("OCT", DpiFileExtensions.getExtension(10));
    assertEquals("NOV", DpiFileExtensions.getExtension(11));
    assertEquals("DEC", DpiFileExtensions.getExtension(12));
    

    try 
    {
     DpiFileExtensions.getExtension(0);
     fail("should have thrown a Runtime Exception");
    } catch (Exception e)
    {}
    try 
    {
     DpiFileExtensions.getExtension(-1);
     fail("should have thrown a Runtime Exception");
    } catch (Exception e)
    {}
    try 
    {
     DpiFileExtensions.getExtension(13);
     fail("should have thrown a Runtime Exception");
    } catch (Exception e)
    {}
    
    
  }
}

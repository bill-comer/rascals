package uk.co.utilisoft.parms.file.dpi;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.BaseTest;


public class DpiPoolHeaderTest extends BaseTest
{
  @Test
  public void testCreateTestHeader() throws Exception
  { 
    DpiPoolHeader header = new DpiPoolHeader();
    
    assertEquals(
        "ZHD|P0135001|X|ATESTSUPPLIERID|Z|POOL|20050326111050", 
        header.createHeader("aTestSupplierId"));
  }
  
  @Before
  public void freezeTheTime()
  {
    super.freezeTime(new DateTime(2005, 3, 26, 11, 10, 50, 00));
  }
  

}

package uk.co.utilisoft.parms.file;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.BaseTest;


public class PoolheaderTest extends BaseTest
{

  @Test
  public void testCreationTime() throws Exception
  { 
    TestPoolHeader header = new TestPoolHeader();
    
    assertEquals("20050326111050", header.getCreationDate());
  }
  
  
  @Test
  public void testCreateTestHeader() throws Exception
  { 
    TestPoolHeader header = new TestPoolHeader();
    
    assertEquals(
        "testrecordtype|testfiletype|X|ATESTSUPPLIERID|Z|POOL|20050326111050", 
        header.createHeader("aTestSupplierId"));
  }
  
  @Before
  public void freezeTheTime()
  {
    super.freezeTime(new DateTime(2005, 3, 26, 11, 10, 50, 00));
  }

  class TestPoolHeader extends PoolHeader
  {
    @Override
    public String getFileType()
    {
      return "testfiletype";
    }

    @Override
    public String getRecordType()
    {
      return "testrecordtype";
    }
    
  }
  
}


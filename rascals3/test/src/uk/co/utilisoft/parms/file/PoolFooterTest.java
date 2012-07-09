package uk.co.utilisoft.parms.file;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PoolFooterTest
{
  @Test
  public void testCreateFooter() throws Exception
  {
    PoolFooter footer = new PoolFooter();
    TestChecksum checksum = new TestChecksum();
    assertEquals("ZPT|102|998877",footer.createFooter(998877L, 101));
  }
  

  class TestChecksum extends PoolChecksumCalculator implements ChecksumCalculator
  {
    @Override
    public long getCheckSum()
    {
      // TODO Auto-generated method stub
      return 998877;
    }
    
    @Override
    public int getRecordCount()
    {
      // TODO Auto-generated method stub
      return 101;
    }
    
  }
}

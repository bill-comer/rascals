package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;

import org.junit.Test;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class P0028FileDataUnitTest
{
  /**
   * Test for nullpointer on domain object
   */
  @Test
  public void testHashCode()
  {
    try
    {
      new P0028FileData().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }
}

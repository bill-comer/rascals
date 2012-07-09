package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;

import org.junit.Test;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class DpiFileDataTest
{
  @Test
  public void testHashCode()
  {
    try
    {
      new DpiFileData().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }
}

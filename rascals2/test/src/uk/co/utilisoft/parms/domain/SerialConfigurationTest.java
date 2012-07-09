package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;

import org.junit.Test;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class SerialConfigurationTest
{
  @Test
  public void testHashCode()
  {
    try
    {
      new SerialConfiguration().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }
}

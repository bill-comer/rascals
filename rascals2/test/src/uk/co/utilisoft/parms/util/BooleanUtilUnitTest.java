package uk.co.utilisoft.parms.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class BooleanUtilUnitTest
{
  /**
   * Check a java.lang.String value cannot be converted to a java.lang.Boolean.
   */
  @Test
  public void testConvertInvalidStringToBoolean()
  {
    assertEquals(Boolean.FALSE, BooleanUtil.convertibleToBoolean("abc"));
  }

  /**
   * Check a java.lang.String value can be converted to a java.lang.Boolean.
   */
  @Test
  public void testConvertValidStringToBoolean()
  {
    assertEquals(Boolean.TRUE, BooleanUtil.convertibleToBoolean("true"));
  }
}

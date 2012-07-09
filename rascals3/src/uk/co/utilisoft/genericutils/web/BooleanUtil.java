package uk.co.utilisoft.genericutils.web;

import org.apache.commons.lang.StringUtils;

/**
 * @author Philip Lau
 * @version 1.0
 */
public final class BooleanUtil
{
  private BooleanUtil()
  { }

  /**
   * Check if a a java.lang.String can be converted to a java.lang.Boolean.
   *
   * @param aValue the java.lang.String to convert to type java.lang.Boolean
   * @return true if string can be converted to boolean true or false
   */
  public static Boolean convertibleToBoolean(String aValue)
  {
    if (StringUtils.isBlank(aValue))
    {
      return Boolean.FALSE;
    }

    if (!(aValue.trim().equalsIgnoreCase("false") || aValue.trim().equalsIgnoreCase("true")))
    {
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }
}

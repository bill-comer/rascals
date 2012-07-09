package uk.co.utilisoft.parms;

import org.apache.commons.lang.StringUtils;

public class ReasonForChange
{

  public static final String MeterChange = "MC";
  public static final String CHANGE_OF_AGENT = "CA";

  public static boolean isMeterChange(String aValue)
  {
    if (aValue != null && aValue.equals(MeterChange))
    {
      return true;
    }

    return false;
  }

  public static boolean isChangeOfAgent(String aValue)
  {
    if (StringUtils.isNotBlank(aValue))
    {
      return aValue.equals(CHANGE_OF_AGENT);
    }
    return false;
  }
}

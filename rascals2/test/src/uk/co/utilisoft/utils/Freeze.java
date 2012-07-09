package uk.co.utilisoft.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public final class Freeze
{
  private Freeze()
  {
  }

  /**
   * @param frozenDateTime chosen time to freeze at
   */
  public static void freeze(DateTime frozenDateTime)
  {
    DateTimeUtils.setCurrentMillisFixed(frozenDateTime.getMillis());
  }
  

  /**
   * @param frozenDateTime chosen time to freeze at
   */
  public static void freeze(int day, int month, int year)
  {
    DateTime time = new DateTime(year, month, day, 0, 0, 0, 0);
    DateTimeUtils.setCurrentMillisFixed(time.getMillis());
  }

  /**
   * thaws the time
   */
  public static void thaw()
  {
    DateTimeUtils.setCurrentMillisSystem();
  }
}

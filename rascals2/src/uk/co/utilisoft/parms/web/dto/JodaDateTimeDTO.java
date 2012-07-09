package uk.co.utilisoft.parms.web.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * @author Kirk Hawksworth
 * @version 1.0
 */
public class JodaDateTimeDTO implements Serializable
{
  private static final long serialVersionUID = -6005627851688830672L;
  private DateTime mDateTime;
  private int mHours, mMinutes, mSeconds;

  public DateTime getDateTime()
  {
    if (mDateTime == null)
    {
      return null;
    }
    int year = mDateTime.getYear();
    int month = mDateTime.getMonthOfYear();
    int day = mDateTime.getDayOfMonth();
    return new DateTime(year, month, day, mHours, mMinutes, mSeconds, 0);
  }

  public void setDateTime(DateTime aDateTime)
  {
    mDateTime = aDateTime;
    mHours = mDateTime.getHourOfDay();
    mMinutes = mDateTime.getMinuteOfHour();
    mSeconds = mDateTime.getSecondOfMinute();
  }
  /**
   * @param aHours The hours to set.
   */
  public void setHours(Integer aHours)
  {
    mHours = aHours;
  }
  /**
   * @param aMinutes The minutes to set.
   */
  public void setMinutes(Integer aMinutes)
  {
    mMinutes = aMinutes;
  }
  /**
   * @param aSeconds The seconds to set.
   */
  public void setSeconds(Integer aSeconds)
  {
    mSeconds = aSeconds;
  }
}
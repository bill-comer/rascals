package uk.co.utilisoft.parms;

import java.io.Serializable;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * a dateTime object representing
 * the month that for which a file is being created.
 * DateTime getStart() - returns 1st of Month 00:00:00
 * DateTime getEnd() - returns 1st of next Month 00:00:00 constructor takes a joda Month & a Joda Years object
 * @author comerb
 *
 */
@SuppressWarnings("serial")
public class ParmsReportingPeriod implements Serializable
{
  private final DateMidnight mStartingMonthInPeriod;  // the start of the month at midnight


  private DateTimeFormatter mDpiDateFormat = DateTimeFormat.forPattern("yyyyMMdd");
  private DateTimeFormatter mDpiDisplayDateFormat = DateTimeFormat.forPattern("MMM yyyy");

  public ParmsReportingPeriod(DateMidnight aStartingMonthInPeriod)
  {
    mStartingMonthInPeriod = new DateMidnight(aStartingMonthInPeriod.getYear(), aStartingMonthInPeriod.getMonthOfYear(), 1);
  }

  public ParmsReportingPeriod(int aMonth, int aYear)
  {
    // only interested in the month & year
    mStartingMonthInPeriod = new DateMidnight(aYear, aMonth, 1);
  }

  public DateTime getStartOfMonth(boolean isMonthT)
  {
    if (isMonthT)
    {
      return getStartOfFirstMonthInPeriod().plusMonths(1).toDateTime();
    }
    else
    {
      return getStartOfFirstMonthInPeriod().toDateTime();
    }
  }

  public DateTime getEndOfMonth(boolean isMonthT)
  {
    if (isMonthT)
    {
      return getStartOfFirstMonthInPeriod().plusMonths(2).toDateTime();
    }
    else
    {
      return getStartOfFirstMonthInPeriod().plusMonths(1).toDateTime();
    }
  }

  public DateMidnight getStartOfFirstMonthInPeriod()
  {
    return mStartingMonthInPeriod;
  }

  public DateMidnight getStartOfNextMonthInPeriod()
  {
    return mStartingMonthInPeriod.plusMonths(1);
  }

  public ParmsReportingPeriod getPreviousReportingPeriod()
  {
    return new ParmsReportingPeriod(getStartOfFirstMonthInPeriod().minusMonths(1));
  }


  public ParmsReportingPeriod getNextReportingPeriod()
  {
    return new ParmsReportingPeriod(getStartOfFirstMonthInPeriod().plusMonths(1));
  }

  public String getDpiRowDateFormat()
  {
    return getStartOfNextMonthInPeriod().minusDays(1).toString(mDpiDateFormat);
  }

  public String getEndDpiDisplayDateFormat()
  {
    return getStartOfNextMonthInPeriod().toString(mDpiDisplayDateFormat  );
  }


  @Override
  public boolean equals(Object aObj)
  {
    if (this == aObj)
    {
      return true;
    }

    if (!(aObj instanceof ParmsReportingPeriod))
    {
      return false;
    }

    final ParmsReportingPeriod parmsReportingPeriod = (ParmsReportingPeriod) aObj;

    if (!mStartingMonthInPeriod.equals(parmsReportingPeriod.getStartOfFirstMonthInPeriod()))
    {
      return false;
    }

    return true;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    int result;
    result = 29 * mStartingMonthInPeriod.hashCode();
    return result;
  }

  public String toString()
  {
    return "start:" + getStartOfFirstMonthInPeriod().toString(mDpiDateFormat);
  }
}

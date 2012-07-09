package uk.co.utilisoft.parmsmop.file;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MopSubRecord
{
  
  public String getPeriodEndDate()
  {
    DateTime endOfPeriod = new DateTime().minusMonths(1).dayOfMonth().withMaximumValue();

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
    return endOfPeriod.toString(fmt);
  }
  
  public String create(String aParticipantId, boolean isHalfHourly)
  {
    return "SUB|" + getHalfHourlyIndicator(isHalfHourly) +  "|X|" + aParticipantId + "|" + getPeriodEndDate() + "|M";
  }

  public String getHalfHourlyIndicator(boolean isHalfHourly)
  {
    String halfHourlyIndicator = "N";
    if (isHalfHourly)
    {
      halfHourlyIndicator = "H";
    }
    return halfHourlyIndicator;
  }
}

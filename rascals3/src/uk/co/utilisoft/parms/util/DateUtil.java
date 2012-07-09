package uk.co.utilisoft.parms.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateUtil
{
  private DateUtil() { }

  private static DateTimeFormatter getFormatter(String aPattern)
  {
    return DateTimeFormat.forPattern(aPattern);
  }

  public static String formatLongDate(String aPattern, BaseDateTime aDate)
  {
    if (aDate != null)
    {
      return getFormatter(aPattern).print(aDate);
    }
    return "";
  }

  /*public static String formatDate(String aPattern, DateTime aDate)
  {
    return formatLongDate(aPattern, aDate.getMillis().getTime());
  }*/

  public static Date parseAsDate(String aPattern, String aDateAsText)
  {
    return parseAsDateTime(aPattern, aDateAsText).toDate();
  }

  public static DateTime parseAsDateTime(String aPattern, String aDateAsText)
  {
    return getFormatter(aPattern).parseDateTime(aDateAsText);
  }

  public static DateTime parseTextAsJodaDateTime(String aDateAsText, DateTimeFormatter ... aFormatters)
  {
    if (StringUtils.isNotBlank(aDateAsText))
    {
      // DateTimeFormatter has a problem with 'jUn 1888', but 'JUN 1888' is ok
      String dateText = aDateAsText.toUpperCase();

      dateParseLoop:
      for (DateTimeFormatter dtFmt : aFormatters)
      {
        try
        {
          return dtFmt.parseDateTime(dateText);
        }
        catch (IllegalArgumentException iae)
        {
          continue dateParseLoop;
        }
      }
    }

    return null;
  }
}

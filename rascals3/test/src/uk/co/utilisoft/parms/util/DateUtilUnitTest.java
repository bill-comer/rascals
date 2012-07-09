package uk.co.utilisoft.parms.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static uk.co.utilisoft.parms.util.DateUtil.formatLongDate;
import static uk.co.utilisoft.parms.web.controller.WebConstants.DISPLAY_DAY_MONTH_YEAR_DATE_FORMAT;;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class DateUtilUnitTest
{
  /**
   * Test dates such as 15th Oct 2010 are formatted correctly for dd/MM/yyyy
   */
  @Test
  public void formatDDMMYYYY()
  {
    DateTime oct152010 = new DateTime(2010, 10, 15, 13, 13, 13, 0);
    String fmtDate = formatLongDate(DISPLAY_DAY_MONTH_YEAR_DATE_FORMAT, oct152010);
    String expFmtDate = "15/10/2010";
    assertEquals(expFmtDate, fmtDate);
  }
}

package uk.co.utilisoft.parms;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;

import uk.co.utilisoft.utils.Freeze;

public class ParmsReportingMonthTest
{
  
  @After
  public void cleanup()
  {
    Freeze.thaw();
  }

  @Test
  public void testMonthCreation()
  {
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    DateMidnight expMonthStart = new DateMidnight(2010, 11, 1);
    DateMidnight expMonthEnd = new DateMidnight(2010, 12, 1);

    assertEquals(expMonthStart, period.getStartOfFirstMonthInPeriod());
    assertEquals(expMonthEnd, period.getStartOfNextMonthInPeriod());
  }


  @Test
  public void testMonthCreationUsingDateMidnight()
  {
    DateTime timeToFreezeAt = new DateTime(2010,11 , 5,    00, 00, 00, 00);
    Freeze.freeze(timeToFreezeAt);

    DateMidnight date = new DateMidnight();
    ParmsReportingPeriod period = new ParmsReportingPeriod(date);

    DateTime expMonthStart = new DateTime(2010,11, 1,   0,0,0,0);
    DateTime expMonthEnd = new DateTime(2010, 12, 1, 0,0,0,0);

    assertEquals("month start is wrong", expMonthStart, period.getStartOfFirstMonthInPeriod());
    assertEquals("month end is wrong", expMonthEnd, period.getStartOfNextMonthInPeriod());
  }


  @Test
  public void testNextAndPreviousMonth()
  {
    ParmsReportingPeriod month = new ParmsReportingPeriod(11, 2010);

    DateMidnight expMonthStart = new DateMidnight(2010, 11, 1);
    DateMidnight expMonthEnd = new DateMidnight(2010, 12, 1);

    assertEquals(expMonthStart, month.getStartOfFirstMonthInPeriod());
    assertEquals(expMonthEnd, month.getStartOfNextMonthInPeriod());

    assertEquals(expMonthStart.plusMonths(1), month.getNextReportingPeriod().getStartOfFirstMonthInPeriod());
    assertEquals(expMonthStart.minusMonths(1), month.getPreviousReportingPeriod().getStartOfFirstMonthInPeriod());
  }

  @Test
  public void testGetDpiRowDateFormat() throws Exception
  {
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    assertEquals("20101130", period.getDpiRowDateFormat());
  }
  
  @Test
  public void testEquals() throws Exception
  { 
    ParmsReportingPeriod p1 = new ParmsReportingPeriod(11, 2010);
    ParmsReportingPeriod p2 = new ParmsReportingPeriod(11, 2010);
    assertTrue(p1.equals(p2));
  }
  

  @Test
  public void testNotEquals() throws Exception
  { 
    ParmsReportingPeriod p1 = new ParmsReportingPeriod(11, 2010);
    ParmsReportingPeriod p2 = new ParmsReportingPeriod(12, 2010);
    assertFalse(p1.equals(p2));
  }
}

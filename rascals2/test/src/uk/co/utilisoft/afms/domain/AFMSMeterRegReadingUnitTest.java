package uk.co.utilisoft.afms.domain;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.parms.validation.WithdrawnReadingMatcher;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class AFMSMeterRegReadingUnitTest
{
  @Test
  public void filterForNoneWithdrawnRecords()
  {
    List<AFMSMeterRegReading> readings =  new ArrayList<AFMSMeterRegReading>();
    Freeze.freeze(15, 12, 2010);
    DateTime now = new DateTime();

    AFMSMeterRegReading read1 = new AFMSMeterRegReading();
    read1.setMeterReadingDate(now);
    read1.setReadingType("R");
    read1.setRegisterReading(100F);

    AFMSMeterRegReading read2 = new AFMSMeterRegReading();
    read2.setMeterReadingDate(now);
    read2.setReadingType("W");
    read2.setRegisterReading(2222F);
    read2.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());

    AFMSMeterRegReading read3 = new AFMSMeterRegReading();
    read3.setMeterReadingDate(now.plusDays(5));
    read3.setReadingType("W");
    read3.setRegisterReading(5555F);

    AFMSMeterRegReading read4 = new AFMSMeterRegReading();
    read4.setMeterReadingDate(now);
    read4.setReadingType("R");
    read4.setRegisterReading(2222F);
    read4.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());

    readings.add(read1);
    readings.add(read2);
    readings.add(read3);
    readings.add(read4);

    List<AFMSMeterRegReading> withdrawns = select(readings,
                                                  having(on(AFMSMeterRegReading.class).getReadingType(),
                                                         equalTo(AFMSMeterRegReading.READING_TYPE.W.getValue())));
    List<AFMSMeterRegReading> notWithdrawns = select(readings,
                                                     having(on(AFMSMeterRegReading.class).getReadingType(),
                                                            not(equalTo(AFMSMeterRegReading.READING_TYPE.W.getValue()))));

    Map<DateTime, AFMSMeterRegReading> withdrawnReadDatesValues
      = index(withdrawns, on(AFMSMeterRegReading.class).getMeterReadingDate());

    Iterator<DateTime> withdrawnReadDatesIt = withdrawnReadDatesValues.keySet().iterator();
    while (withdrawnReadDatesIt.hasNext())
    {
      DateTime withdrawnReadDate = withdrawnReadDatesIt.next();

      List<AFMSMeterRegReading> withdrawnReadings = select(notWithdrawns,
        new WithdrawnReadingMatcher(withdrawnReadDate, withdrawnReadDatesValues.get(withdrawnReadDate)
                                    .getRegisterReading(), withdrawnReadDatesValues.get(withdrawnReadDate)
                                    .getBSCValidationStatus()));
      notWithdrawns.removeAll(withdrawnReadings);
    }

    assertEquals(1, notWithdrawns.size());

    for (AFMSMeterRegReading afmsMeterRegRead : notWithdrawns)
    {
      assertEquals(true, !afmsMeterRegRead.getReadingType().equals(AFMSMeterRegReading.READING_TYPE.W.getValue()));
      assertEquals(true, afmsMeterRegRead.getRegisterReading().equals(100F));
    }

    Freeze.thaw();
  }

  /**
   * AFMSMpan meter register readings with J0171 Reading Type 'W'(withdrawn) are to be excluded
   * when calculating Maximum Demand value.
   */
  @Test
  public void checkReadingIsWithdrawn()
  {
    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setReadingType(AFMSMeterRegReading.READING_TYPE.W.getValue());
    assertEquals(true, reading.isReadingType(AFMSMeterRegReading.READING_TYPE.W));
  }
}

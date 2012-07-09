package uk.co.utilisoft.parms.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.file.sp04.HalfHourlySettlementDate;
import uk.co.utilisoft.parms.file.sp04.ThreeHighestReadingsAndAverage;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
public class Sp04AfmsMpanValidatorUnitTest
{
  @Test
  public void testRemoveWithdrawnMeterReadings()
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

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public boolean isReadingValid(AFMSMeterRegReading reading,
                                    DateTime validEffectiveFromDate,
                                    DateTime validEffectiveToDate)
      {
        return true;
      }
    };

    List<AFMSMeterRegReading> notWithdrawnMeterReads = sp04AfmsMpanValidator.removeWithdrawnReadings(readings);

    assertEquals(1, notWithdrawnMeterReads.size());
    assertEquals(notWithdrawnMeterReads.get(0).getRegisterReading(), new Float(100F));

    Freeze.thaw();
  }

  @Test
  public void testRecalculateMaxDemandIgnoreWithdrawnMeterReadings()
  {
    AFMSMeterRegister register = new AFMSMeterRegister();

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

    AFMSMeterRegReading read5 = new AFMSMeterRegReading();
    read5.setMeterReadingDate(now);
    read5.setReadingType("R");
    read5.setRegisterReading(150F);

    AFMSMeterRegReading read6 = new AFMSMeterRegReading();
    read6.setMeterReadingDate(now);
    read6.setReadingType("R");
    read6.setRegisterReading(180F);

    readings.add(read1);
    readings.add(read2);
    readings.add(read3);
    readings.add(read4);
    readings.add(read5);
    readings.add(read6);

    register.setMeterRegReadings(readings);

    Sp04AfmsMpanValidatorImpl sp04AfmsMpanValidator = new Sp04AfmsMpanValidatorImpl()
    {
      @Override
      public boolean isReadingValid(AFMSMeterRegReading reading,
                                    DateTime validEffectiveFromDate,
                                    DateTime validEffectiveToDate)
      {
        return true;
      }
    };

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();
    msidReadings.put("meter_serial_id_1", readings);

    ThreeHighestReadingsAndAverage threeHighestReadsAndAvg
      = sp04AfmsMpanValidator.getAverageOfThreeHighest(new DateTime(), new DateTime(), msidReadings, 3, true);

    assertEquals(new Float(143.33333F), threeHighestReadsAndAvg.avgThreeHighest);

    Freeze.thaw();
  }

  /**
   * Check p0028 data mpan meter last updated is greater than latest D0268 last updated.
   */
  @Test
  public void getSettlementDateMeterLastUpdGreaterThanLatestD0268LastUpd()
  {
    AFMSMeter meter = new AFMSMeter();
    DateTime meterLastUpd = new DateTime(2011, 1, 14, 12, 12, 12, 0);
    meter.setLastUpdated(meterLastUpd);
    meter.setOutstationId("dummyOutstationId");
    AFMSMpan mpan = new AFMSMpan();

    List<AFMSAregiProcess> aregiProcs = new ArrayList<AFMSAregiProcess>();
    AFMSAregiProcess aregiProc = new AFMSAregiProcess();
    DateTime d0268Received = new DateTime(2011, 1, 1, 0, 0, 0, 0);
    aregiProc.setD0268DupReceived(d0268Received);
    aregiProcs.add(aregiProc);
    mpan.setAregiProcesses(aregiProcs);
    meter.setMpan(mpan);
    meter.setSettlementDate(new DateTime(2011, 1, 2, 0, 0, 0, 0));

    mpan.getMeters().add(meter);

    Collection<AFMSAregiProcess> aregiProcesses = new ArrayList<AFMSAregiProcess>();
    aregiProcesses.add(aregiProc);


    HalfHourlySettlementDate halfHourlySettlementDate = new HalfHourlySettlementDate();
    DateTime settDate = halfHourlySettlementDate.getHalfHourlySettlementDate(mpan);

    assertNotNull(settDate);
  }

  /**
   * Average of three highest readings has not exceeded maximum demand of 100KW.
   */
  @Test
  public void averageThreeHighestReadingsHasNotExceededMaximumDemand()
  {
    ThreeHighestReadingsAndAverage threeHighestReadingsAndAvg = new ThreeHighestReadingsAndAverage();
    threeHighestReadingsAndAvg.avgThreeHighest = 100F;
    assertEquals(Boolean.FALSE, new Sp04AfmsMpanValidatorImpl()
      .hasExceededMaximumDemand(threeHighestReadingsAndAvg.avgThreeHighest));
  }

  /**
   * Average of three highest readings has exceeded maximum demand of 100KW.
   */
  @Test
  public void averageThreeHighestReadingsHasExceededMaximumDemand()
  {
    ThreeHighestReadingsAndAverage threeHighestReadingsAndAvg = new ThreeHighestReadingsAndAverage();
    threeHighestReadingsAndAvg.avgThreeHighest = 111F;
    assertEquals(Boolean.TRUE, new Sp04AfmsMpanValidatorImpl()
      .hasExceededMaximumDemand(threeHighestReadingsAndAvg.avgThreeHighest));
  }

  /**
   * Check for valid Meter Register Id(J0010) = 'MD', Meter Register Type(J0474) = 'M',
   * Measurement Quantity Id(J0103) = 'KW', BSC Validation Status(J0022) = 'V', J0040 = 'Register readings'
   */
  @Test
  public void isValidMeterRegisterForSp04InclusionValidMeterRegisterValidReadingsValidDates()
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    register.setEffectiveFromDate(validEffectiveFromDate.minusDays(1));
    register.setEffectiveToDate(validEffectiveToDate.plusDays(1));
    register.setMeterRegisterId("MD");
    register.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());

    List<AFMSMeterRegReading> meterRegReadings = new ArrayList<AFMSMeterRegReading>();
    AFMSMeterRegReading reading1 = new AFMSMeterRegReading();
    reading1.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    reading1.setRegisterReading(10.9F);
    reading1.setDateReceived(validEffectiveFromDate.plusDays(1));
    register.setMeterRegReadings(meterRegReadings);

    assertTrue(register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_regTypeisM_validDates() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    register.setEffectiveFromDate(validEffectiveFromDate.minusDays(1));
    register.setEffectiveToDate(validEffectiveToDate.plusDays(1));
    register.setMeterRegisterId("MD");
    register.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());

    List<AFMSMeterRegReading> meterRegReadings = new ArrayList<AFMSMeterRegReading>();
    // TODO add valid meter reg readings
    register.setMeterRegReadings(meterRegReadings);

    //test method
    assertTrue(register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_regTypeisM_validDate_no_registerETD() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    register.setMeterRegisterId("MD");
    register.setEffectiveFromDate(validEffectiveFromDate.minusDays(1));
    register.setMeasurementQuantityId(AFMSMeterRegister.MEASUREMENT_QUANTITY_ID.KW.getValue());

    //test method
    assertTrue(register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_regTypeisNotM_validDates() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("a");
    register.setEffectiveFromDate(validEffectiveFromDate.minusDays(1));
    register.setEffectiveToDate(validEffectiveToDate.plusDays(1));

    //test method
    assertFalse(register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_Invalid_effectiveToDate() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    register.setEffectiveFromDate(validEffectiveFromDate.minusDays(1));
    register.setEffectiveToDate(validEffectiveToDate.minusYears(1));  //this is one year to old so should fail

    //test method
    assertFalse("effectiveTodate is too old",
                register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_Invalid_effectiveFromDate() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    register.setEffectiveFromDate(new DateTime());  //today is too new a date for effectiveFromeDate
    register.setEffectiveToDate(validEffectiveToDate.plusDays(1));

    //test method
    assertFalse("today is too new a date for effectiveFromeDate",
                register.isValidMeterRegisterForSp04Report(validEffectiveFromDate,
                                                           validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_NullMeterRegType() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType(null);
    register.setEffectiveFromDate(validEffectiveFromDate.minusDays(1));
    register.setEffectiveToDate(validEffectiveToDate.plusDays(1));

    //test method
    assertFalse("null MeterRegType should be handled & return false",
                register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isValidMeterRegisterForSp04Inclusion_NullEffectiveFromDate() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegister register = new AFMSMeterRegister();
    register.setMeterRegType("M");
    register.setEffectiveFromDate(null);
    register.setEffectiveToDate(validEffectiveToDate.plusDays(1));

    //test method
    assertFalse("null EffectiveFromDate should be handled & return false",
                register.isValidMeterRegisterForSp04Report(validEffectiveFromDate, validEffectiveToDate));
  }

  @Test
  public void isReadingDateValid_validDate() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setDateReceived(validEffectiveToDate.plusDays(1));

    //test method
    assertTrue(new Sp04AfmsMpanValidatorImpl().isReadingDateValid(reading, validEffectiveFromDate,
                                                                  validEffectiveToDate));
  }

  @Test
  public void isReadingDateValid_ReadingdateTooRecent() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setDateReceived(new DateTime());   //reading date of now is too recent

    //test method
    assertFalse("reading date of now is too recent",
                new Sp04AfmsMpanValidatorImpl().isReadingDateValid(reading, validEffectiveFromDate,
                                                                   validEffectiveToDate));
  }

  @Test
  public void isReadingDateValid_ReadingdateTooOld() throws Exception
  {
    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setDateReceived(new DateTime().minusYears(2));   //reading date of now is too old

    //test method
    assertFalse("reading date of now is too old",
                new Sp04AfmsMpanValidatorImpl().isReadingDateValid(reading, validEffectiveFromDate,
                                                                   validEffectiveToDate));
  }

  @Test
  public void getAverageOfThreeHighestInvalidReadings() throws Exception
  {
    Freeze.freeze(new DateTime());

    AFMSMeterRegister register = new AFMSMeterRegister();
    List<AFMSMeterRegReading> meterReadings = new ArrayList<AFMSMeterRegReading>();

    // not setting BSC validation status = invalid
    AFMSMeterRegReading invalidReading1 = new AFMSMeterRegReading();
    invalidReading1.setRegisterReading(99.0F);
    invalidReading1.setMeterReadingDate(new DateTime().minusDays(1));
    invalidReading1.setMeterRegister(register);
    meterReadings.add(invalidReading1);

    register.setMeterRegReadings(meterReadings);
    AFMSMeter meter = new AFMSMeter();
    meter.setMeterSerialId("msid");
    meter.setPk(1L);
    register.setMeter(meter);

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();
    msidReadings.put(register.getMeter().getMeterSerialId(), meterReadings);

    //test method
    ThreeHighestReadingsAndAverage result = new Sp04AfmsMpanValidatorImpl()
      .getAverageOfThreeHighest(null, null, msidReadings, 3);

    assertNotNull(result);
    assertEquals(0, result.threeHighestReadings.size());
    assertNull(result.mostRecent);
    assertEquals(Boolean.TRUE, Float.isNaN(result.avgThreeHighest));

    Freeze.thaw();
  }

  @Test
  public void getAverageOfThreeHighest_allInOrder() throws Exception
  {
    Freeze.freeze(new DateTime());

    AFMSMeterRegister register = new AFMSMeterRegister();
    List<AFMSMeterRegReading> meterReadings = new ArrayList<AFMSMeterRegReading>();

    AFMSMeterRegReading reading1 = new AFMSMeterRegReading();
    reading1.setRegisterReading(1.0F);
    reading1.setMeterReadingDate(new DateTime());
    meterReadings.add(reading1);

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setRegisterReading(2.0F);
    reading2.setMeterReadingDate(new DateTime());
    meterReadings.add(reading2);

    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setRegisterReading(3.0F);
    reading3.setMeterReadingDate(new DateTime());
    meterReadings.add(reading3);

    AFMSMeterRegReading reading4 = new AFMSMeterRegReading();
    reading4.setRegisterReading(4.0F);
    reading4.setMeterReadingDate(new DateTime().plusDays(1));
    meterReadings.add(reading4);

    AFMSMeterRegReading reading5 = new AFMSMeterRegReading();
    reading5.setRegisterReading(5.0F);
    reading5.setMeterReadingDate(new DateTime().minusYears(1));
    meterReadings.add(reading5);

    AFMSMeterRegReading reading6 = new AFMSMeterRegReading();
    reading6.setRegisterReading(6.0F);
    reading6.setMeterReadingDate(new DateTime());
    meterReadings.add(reading6);

    AFMSMeterRegReading reading7 = new AFMSMeterRegReading();
    reading7.setRegisterReading(7.0F);
    reading7.setMeterReadingDate(new DateTime());
    meterReadings.add(reading7);

    AFMSMeterRegReading reading8 = new AFMSMeterRegReading();
    reading8.setRegisterReading(8.0F);
    reading8.setMeterReadingDate(new DateTime().plusDays(2));
    reading8.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading8);

    AFMSMeterRegReading reading9 = new AFMSMeterRegReading();
    reading9.setRegisterReading(9.0F);
    reading9.setMeterReadingDate(new DateTime());
    reading9.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading9);

    AFMSMeterRegReading reading10 = new AFMSMeterRegReading();
    reading10.setRegisterReading(10.0F);
    reading10.setMeterReadingDate(new DateTime());
    reading10.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading10);

    register.setMeterRegReadings(meterReadings);

    //test method
    Sp04AfmsMpanValidator validator = new Sp04AfmsMpanValidatorImpl()
    {
      public boolean isReadingValid(AFMSMeterRegReading reading, DateTime validEffectiveFromDate,
                                    DateTime validEffectiveToDate)
      {
        return true;
      };
    };

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();
    msidReadings.put("msid", meterReadings);

    ThreeHighestReadingsAndAverage result = validator.getAverageOfThreeHighest(null, null, msidReadings, 3);

    assertNotNull(result);
    assertEquals("avg of (10+9+8)/3=9", new Float(9.0F), result.avgThreeHighest);
    assertEquals(3, result.threeHighestReadings.size());

    assertEquals(new DateTime().plusDays(2), result.mostRecent.getMeterReadingDate());

    Freeze.thaw();
  }


  @Test
  public void getAverageOfThreeHighest_randomOrder() throws Exception
  {
    AFMSMeterRegister register = new AFMSMeterRegister();
    List<AFMSMeterRegReading> meterReadings = new ArrayList<AFMSMeterRegReading>();

    AFMSMeterRegReading reading1 = new AFMSMeterRegReading();
    reading1.setRegisterReading(1.0F);
    reading1.setMeterReadingDate(new DateTime());
    meterReadings.add(reading1);

    AFMSMeterRegReading reading10 = new AFMSMeterRegReading();
    reading10.setRegisterReading(10.0F);
    reading10.setMeterReadingDate(new DateTime());
    reading10.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading10);

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setRegisterReading(2.0F);
    reading2.setMeterReadingDate(new DateTime());
    meterReadings.add(reading2);
    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setRegisterReading(3.0F);
    reading3.setMeterReadingDate(new DateTime());
    meterReadings.add(reading3);
    AFMSMeterRegReading reading5 = new AFMSMeterRegReading();
    reading5.setRegisterReading(5.0F);
    reading5.setMeterReadingDate(new DateTime());
    meterReadings.add(reading5);
    AFMSMeterRegReading reading6 = new AFMSMeterRegReading();
    reading6.setRegisterReading(6.0F);
    reading6.setMeterReadingDate(new DateTime());
    meterReadings.add(reading6);
    AFMSMeterRegReading reading7 = new AFMSMeterRegReading();
    reading7.setRegisterReading(7.0F);
    reading7.setMeterReadingDate(new DateTime());
    meterReadings.add(reading7);
    AFMSMeterRegReading reading8 = new AFMSMeterRegReading();
    reading8.setRegisterReading(8.0F);
    reading8.setMeterReadingDate(new DateTime());
    reading8.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading8);
    AFMSMeterRegReading reading9 = new AFMSMeterRegReading();
    reading9.setRegisterReading(9.0F);
    reading9.setMeterReadingDate(new DateTime());
    reading9.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading9);
    AFMSMeterRegReading reading4 = new AFMSMeterRegReading();
    reading4.setRegisterReading(4.0F);
    reading4.setMeterReadingDate(new DateTime());
    meterReadings.add(reading4);

    register.setMeterRegReadings(meterReadings);

    //test method
    Sp04AfmsMpanValidator validator = new Sp04AfmsMpanValidatorImpl()
    {
      public boolean isReadingValid(AFMSMeterRegReading reading, DateTime validEffectiveFromDate,
                                    DateTime validEffectiveToDate)
      {
        return true;
      };
    };

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();
    msidReadings.put("msid", meterReadings);

    ThreeHighestReadingsAndAverage result = validator.getAverageOfThreeHighest(null, null, msidReadings, 3);

    assertNotNull(result);
    assertEquals("avg of (10+9+8)/3=9", new Float(9.0F), result.avgThreeHighest);
    assertEquals(3, result.threeHighestReadings.size());
  }


  @Test
  public void getAverageOfThreeHighest_randomOrder_twoReadings() throws Exception
  {
    Freeze.freeze(new DateTime());

    AFMSMeterRegister register = new AFMSMeterRegister();
    List<AFMSMeterRegReading> meterReadings = new ArrayList<AFMSMeterRegReading>();

    AFMSMeterRegReading reading10 = new AFMSMeterRegReading();
    reading10.setRegisterReading(10.0F);
    reading10.setMeterReadingDate(new DateTime().minusYears(1));
    reading10.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading10);

    AFMSMeterRegReading reading2 = new AFMSMeterRegReading();
    reading2.setRegisterReading(2.0F);
    reading2.setMeterReadingDate(new DateTime().minusDays(1));
    reading2.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading2);

    // bug#5835 fix this test which must have at least 3 afmsmeterregreadings to calculate average
    AFMSMeterRegReading reading3 = new AFMSMeterRegReading();
    reading3.setRegisterReading(3.0F);
    reading3.setMeterReadingDate(new DateTime().minusDays(2));
    reading3.setBSCValidationStatus(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue());
    meterReadings.add(reading3);

    register.setMeterRegReadings(meterReadings);

    //test method
    Sp04AfmsMpanValidator validator = new Sp04AfmsMpanValidatorImpl()
    {
      public boolean isReadingValid(AFMSMeterRegReading reading, DateTime validEffectiveFromDate,
                                    DateTime validEffectiveToDate)
      {
        return true;
      };
    };

    Map<String, List<AFMSMeterRegReading>> msidReadings = new HashMap<String, List<AFMSMeterRegReading>>();
    msidReadings.put("msid", meterReadings);

    ThreeHighestReadingsAndAverage result = validator.getAverageOfThreeHighest(null, null, msidReadings, 3);

    assertNotNull(result);
    assertEquals("avg of (10+2+3)/3=5", new Float(5.0F), result.avgThreeHighest);
    assertEquals(3, result.threeHighestReadings.size());

    assertEquals(new DateTime().minusDays(1), result.mostRecent.getMeterReadingDate());


    Freeze.thaw();
  }

  /**
   * Check AFMSMeterRegReading.BSCValidationStatus is 'unknown' value.
   */
  @Test
  public void isReadingValidFailWithUnknownBSCValidationStatus()
  {
    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setDateReceived(new DateTime(2011, 2, 21, 0, 0, 0, 0));
    reading.setBSCValidationStatus("UNKNOWN");
    DateTime etd = new DateTime(2011, 1, 21, 0, 0, 0, 0);
    DateTime efd = new DateTime(2011, 3, 21, 0, 0, 0, 0);

    assertFalse("expected reading with BSC Validation Status of " + reading.getBSCValidationStatus()
                + " to be invalid", new Sp04AfmsMpanValidatorImpl().isReadingValid(reading, efd, etd));
  }

  /**
   * Check AFMSMeterRegReading.BSCValidationStatus is null.
   */
  @Test
  public void isReadingValidFailWithNull()
  {
    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setDateReceived(new DateTime(2011, 2, 21, 0, 0, 0, 0));
    reading.setBSCValidationStatus(null);
    DateTime etd = new DateTime(2011, 1, 21, 0, 0, 0, 0);
    DateTime efd = new DateTime(2011, 3, 21, 0, 0, 0, 0);

    assertFalse("expected reading with BSC Validation Status of " + reading.getBSCValidationStatus()
                + " to be invalid", new Sp04AfmsMpanValidatorImpl().isReadingValid(reading, efd, etd));
  }

  /**
   * Check AFMSMeterRegReading.BSCValidationStatus is 'V' if a reading exists.
   */
  @Test
  public void isReadingValidSuccess()
  {
    AFMSMeterRegReading reading = new AFMSMeterRegReading();
    reading.setDateReceived(new DateTime(2011, 2, 21, 0, 0, 0, 0));
    reading.setBSCValidationStatus("V");
    reading.setRegisterReading(144F);
    DateTime etd = new DateTime(2011, 1, 21, 0, 0, 0, 0);
    DateTime efd = new DateTime(2011, 3, 21, 0, 0, 0, 0);

    assertTrue("expected reading with BSC Validation Status of " + reading.getBSCValidationStatus()
               + " and reading of " + reading.getRegisterReading() + " to be valid", new Sp04AfmsMpanValidatorImpl()
               .isReadingValid(reading, efd, etd));
  }

  /**
   * Max demand exceed 100KW check.
   */
  @Test
  public void hasExceededMaxDemandSuccess()
  {
    Long maxDemand = 110L;
    assertTrue("expected " + maxDemand + " to have exceeded max demand of 100KW",
               new Sp04AfmsMpanValidatorImpl().hasExceededMaximumDemand(new Float(maxDemand)));
  }

  /**
   * Max demand to fail on exceed max demand check of 100KW.
   */
  @Test
  public void hasExceededMaxDemandFail()
  {
    Long maxDemand = 100L;
    assertFalse("expected " + maxDemand + " to not have exceeded max demand of 100KW",
                new Sp04AfmsMpanValidatorImpl().hasExceededMaximumDemand(new Float(maxDemand)));
  }
}
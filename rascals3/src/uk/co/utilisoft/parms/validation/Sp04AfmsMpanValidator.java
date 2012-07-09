package uk.co.utilisoft.parms.validation;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;
import uk.co.utilisoft.parms.file.sp04.ThreeHighestReadingsAndAverage;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface Sp04AfmsMpanValidator
{
  /**
   * @param aAfmsMpan the AFMSMpan
   * @return true if the mpan already has HH metering installed before start of reporting period
   */
  boolean hasMpanGotHalfHourlyMeteringBeforeReportMonth(AFMSMpan aAfmsMpan, DateTime aStartOfReportingPeriod);

  /**
   * bug#5791 - Check if a D0150 has been received for an mpan. This is required where mpans have not yet received a D0268,
   * but because a D0150 Non Half Hourly meter details update has been received, the mpan should still be reported
   * in the Sp04.
   *
   * @param aAFMSAregiProcess the AFMSAregiProcess
   * @return true if the mpan has received a D0150; otherwise false
   */
  boolean hasReceivedD0150(AFMSAregiProcess aAFMSAregiProcess);

  /**
   * @param aLastUpdated the time when the meter details was last updated
   * @param aLatestD0268ReceiptDate the most recent D0268 Receipt date
   * @return true if the most recent D0268 matches or is after the last updated date of a
   */
  boolean isD0268DateSameAsOrGreaterThanMeterLastUpd(DateTime aMeterLastUpdated,
                                                     DateTime aLatestD0268ReceiptDate);

  /**
   * @param aSupplier the supplier
   * @param aMpan the afms mpan
   * @param aEndOfMonthT the end of the reporting period
   * @return true if the supplier is current for the specified mpan
   */
  boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan aMpan, DateTime aEndOfMonthT);

  /**
   * bug#5835 - add check for settlement date prior to meter installation deadline
   *
   * @param aAfmsMeter the AFMS Meter
   * @param aMID the meter installation deadline
   * @return true if the settlement date is prior to the meter installation deadline
   */
  boolean isSettlementDateBeforeMID(AFMSMeter aAfmsMeter, DateTime aMID);

  /**
   * @param aMaxDemand the maximum demand of register readings
   * @return the Sp04FaultReasonType
   */
  Sp04FaultReasonType validateMaximumDemand(Float aMaxDemand);

  /**
   * @param aMaxDemand
   * @return
   */
  boolean isMaxDemandGreaterThanOrEqualToThreshold(Float aMaxDemand);

  /**
   * @param aMaxDemand the maximum demand
   * @return true if the maximum demand has exceeded 100KW; otherwise false
   */
  boolean hasExceededMaximumDemand(Float aMaxDemand);

  /**
   * @param aReading the AFMS Meter Register Reading
   * @param aValidEffectiveFromDate the EFD
   * @param aValidEffectiveToDate the ETD
   * @return true if the AFMS Meter Register Reading is valid for the EFD and ETD; otherwise, false
   */
  boolean isReadingDateValid(AFMSMeterRegReading aReading, DateTime aValidEffectiveFromDate,
                             DateTime aValidEffectiveToDate);

  /**
   * bug#5682 - additional check for is AFMSMeterRegReading valid
   *
   * @param aReading the AFMS Meter Register Reading
   * @param aValidEffectiveFromDate the EFD
   * @param aValidEffectiveToDate the ETD
   * @return true if the AFMS Meter Register Reading is valid for BSC Validation Status 'V'; otherwise, false
   */
  boolean isReadingValid(AFMSMeterRegReading aReading, DateTime aValidEffectiveFromDate,
                         DateTime aValidEffectiveToDate);

  /**
   * gets the average of the (upto) three highest readings
   * @param aValidEffectiveFromDate the effective from date
   * @param aValidEffectiveToDate the effective to date
   * @param aMsidReadings the AFMSMeterRegReading records identified by Meter Serial Id
   * @param aMinimumNoOfMeterReadingsRequired the minimum number of afms meter readings required before an average is taken
   * @return the ThreeHighestReadingsAndAverage data
   */
  ThreeHighestReadingsAndAverage getAverageOfThreeHighest(DateTime aValidEffectiveFromDate, DateTime aValidEffectiveToDate,
                                                          Map<String, List<AFMSMeterRegReading>> aMsidReadings,
                                                          Integer aMinimumNoOfMeterReadingsRequired);


  /**
   * gets the average of the (upto) three highest readings
   * @param aValidEffectiveFromDate a valid effective from date
   * @param aValidEffectiveToDate a valid effective to date
   * @param aMsidValidReadings the valid AFMSMeterRegReadings
   * @param aMinimumNoOfMeterReadingsRequired the minimum number of afms meter readings required before an average is taken
   * @param aCheckForWithdrawnReadings check for withdrawn readings that should be removed before calculating the max demand
   * @return the ThreeHighestReadingsAndAverage data
   */
  ThreeHighestReadingsAndAverage getAverageOfThreeHighest(DateTime aValidEffectiveFromDate, DateTime aValidEffectiveToDate,
                                                          Map<String, List<AFMSMeterRegReading>> aMsidValidReadings, Integer aMinimumNoOfMeterReadingsRequired,
                                                          Boolean aCheckForWithdrawnReadings);

  /**
   * Note: Withdrawn readings are handled by adding a new AFMSMeterRegReading with J0171 = 'W',
   * and the original record remains in the database. Both the original and newly created withdrawn reading records
   * must be ignored when using the Meter Register's readings to calculate the maximum demand value.
   * Readings with J0171 = 'W' are only considered if the mpan's max demand value matches or exceeds the configured
   * max demand threshold
   *
   * @param aReadings the AFMSMeterRegReadings
   * @return the AFMSMeterRegReadings with withdrawn readings removed
   */
  List<AFMSMeterRegReading> removeWithdrawnReadings(List<AFMSMeterRegReading> aReadings);

  /**
   * bug#6466 check max demand does not match or exceed configured max demand threshold
   * if max demand exceeds configured threshold need to check for readings which have be withdrawn,
   * that need to be ignored and max demand re-calculated
   *
   * @param aValidEffectiveFromDate  the effective from date
   * @param aValidEffectiveToDate the effective to date
   * @param aMsidReadings the afms meter register readings
   * @param aMinimumNoOfMeterReadingsRequired the number of afms meter register readings used to calculate
   *        the max demand value
   * @param aMaxDemand the maximum demand
   * @return the ThreeHighestReadingsAndAverage with the maximum demand re-cacluated if the readings matches or exceeds the MD threshold configured
   */
  ThreeHighestReadingsAndAverage recalculateMaxDemandIgnoreWithrawnReadings(DateTime aValidEffectiveFromDate,
                                                                            DateTime aValidEffectiveToDate,
                                                                            Map<String, List<AFMSMeterRegReading>> aMsidReadings,
                                                                            Integer aMinimumNoOfMeterReadingsRequired,
                                                                            Float aMaxDemand);

  /**
   * A custom configured maximum demand threshold value.
   *
   * @return the configured maximum demand threshold from a resource
   */
  Float getMaxDemandThreshold();
}

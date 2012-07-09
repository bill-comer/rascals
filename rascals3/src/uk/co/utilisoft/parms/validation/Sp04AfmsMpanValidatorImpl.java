package uk.co.utilisoft.parms.validation;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import uk.co.utilisoft.afms.dao.AFMSAregiProcessDao;
import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.AFMSMeterRegReadingSortByReadingDate;
import uk.co.utilisoft.parms.file.sp04.AFMSMeterRegReadingSortByReadingReverse;
import uk.co.utilisoft.parms.file.sp04.HalfHourlySettlementDate;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;
import uk.co.utilisoft.parms.file.sp04.ThreeHighestReadingsAndAverage;
import uk.co.utilisoft.parms.util.PropertyLoader;

/**
 * Provide validation for active AFMS mpan(s)
 *
 * @author Philip Lau
 * @version 1.0
 */
@Service("parms.sp04AfmsMpanValidator")
public class Sp04AfmsMpanValidatorImpl implements Sp04AfmsMpanValidator
{
  public static final Float MAX_DEMAND_THRESHOLD_DEFAULT = 100.0F;
  public static final String MAX_DEMAND_THRESHOLD_PROPERTY_NAME = "sp04.exception.report.max.demand.threshold";
  public static final String ROOT_PATH_PROPERTY_VALUE_PROPERTY_NAME = "uk.co.utilisoft.parms.path";

  @Autowired(required = true)
  @Qualifier("parms.afmsAregiProcessDao")
  AFMSAregiProcessDao mAFMSAregiProcessDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  AFMSMeterDao mAFMSMeterDao;

  @Autowired(required = true)
  @Qualifier("parms.halfHourlySettlementDate")
  private HalfHourlySettlementDate mHalfHourlySettlementDate;

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#hasMpanGotHalfHourlyMeteringBeforeReportMonth(
   * uk.co.utilisoft.afms.domain.AFMSMpan)
   */
  @Override
  public boolean hasMpanGotHalfHourlyMeteringBeforeReportMonth(AFMSMpan aAfmsMpan, DateTime aStartOfReportingPeriod)
  {
    for (AFMSMeter meter : aAfmsMpan.getMeters())
    {
      if (meter.isHalfHourlyMeter())
      {
        if (meter.isThisMeterActiveNow())
        {
          return meter.getSettlementDate().isBefore(aStartOfReportingPeriod);
        }
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#isSupplierCurrent(uk.co.utilisoft.parms.domain
   * .Supplier, uk.co.utilisoft.afms.domain.AFMSMpan, org.joda.time.DateTime)
   */
  public boolean isSupplierCurrent(Supplier aSupplier, AFMSMpan mpan, DateTime endOfMonthT)
  {
    boolean isSupplierEffectiveToDateOK = true; /// assume OK

    if (mpan.getSupplierEffectiveToDate() != null)
    {
      isSupplierEffectiveToDateOK = mpan.getSupplierEffectiveToDate().isAfter(endOfMonthT);
    }

    return mpan.getSupplierId().equalsIgnoreCase(aSupplier.getSupplierId()) && isSupplierEffectiveToDateOK;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#validateMaximumDemand(java.lang.Float)
   */
  @Override
  public Sp04FaultReasonType validateMaximumDemand(Float aMaxDemand)
  {
    if (aMaxDemand != null && !aMaxDemand.isNaN())
    {
      if (isMaxDemandGreaterThanOrEqualToThreshold(aMaxDemand))
      {
        return Sp04FaultReasonType.getMDMatchesOrExceedsThreshold();
      }
      else
      {
        // check MD <= 100kw
        if (!hasExceededMaximumDemand(aMaxDemand))
        {
          return Sp04FaultReasonType.getMDLessThanOneHundred();
        }
      }
    }

    return null;
  }


  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#isMaxDemandGreaterThanOrEqualToThreshold(java.lang.Float,
   * java.lang.Float)
   */
  public boolean isMaxDemandGreaterThanOrEqualToThreshold(Float aMaxDemand)
  {
    if (aMaxDemand != null && !aMaxDemand.isNaN())
    {
      Float configuredMDThreshold = getMaxDemandThreshold();

      if (configuredMDThreshold != null)
      {
        if (Float.valueOf(aMaxDemand).compareTo(configuredMDThreshold) >= 0)
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#hasExceededMaximumDemand(java.lang.Long)
   */
  public boolean hasExceededMaximumDemand(Float aMaxDemand)
  {
    if (aMaxDemand != null && !aMaxDemand.isNaN())
    {
      if (aMaxDemand.compareTo(MAX_DEMAND_THRESHOLD_DEFAULT) > 0)
      {
        return true;
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#isReadingValid(uk.co.utilisoft.afms.domain
   * .AFMSMeterRegReading, org.joda.time.DateTime, org.joda.time.DateTime)
   */
  public boolean isReadingValid(AFMSMeterRegReading reading, DateTime validEffectiveFromDate,
                                DateTime validEffectiveToDate)
  {
    if (reading.isReadingBscValidated())
    {
      if (reading.getRegisterReading() != null)
      {
        return isReadingDateValid(reading, validEffectiveFromDate, validEffectiveToDate);
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#isReadingDateValid(uk.co.utilisoft.afms.domain
   * .AFMSMeterRegReading, org.joda.time.DateTime, org.joda.time.DateTime)
   */
  public boolean isReadingDateValid(AFMSMeterRegReading reading, DateTime validEffectiveFromDate,
                                    DateTime validEffectiveToDate)
  {
    return reading.getDateReceived().isAfter(validEffectiveToDate)
      && reading.getDateReceived().isBefore(validEffectiveFromDate);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#getAverageOfThreeHighest(org.joda.time.DateTime,
   * org.joda.time.DateTime, java.util.Map, java.lang.Integer, Boolean)
   */
  public ThreeHighestReadingsAndAverage getAverageOfThreeHighest(DateTime aValidBeforeEffectiveFromDate,
                                                                 DateTime aValidAfterEffectiveToDate,
                                                                 Map<String, List<AFMSMeterRegReading>> aMsidReadings,
                                                                 Integer aMinimumNoOfMeterReadingsRequired,
                                                                 Boolean aCheckForWithdrawnReadings)
  {
    ThreeHighestReadingsAndAverage results = new ThreeHighestReadingsAndAverage();

    List<AFMSMeterRegReading> allReadings = new ArrayList<AFMSMeterRegReading>();
    if (aMsidReadings != null && !aMsidReadings.isEmpty())
    {
      allReadings = aMsidReadings.get(aMsidReadings.keySet().iterator().next());
    }

    // bug#6466 remove withdrawn readings
    List<AFMSMeterRegReading> notWithdrawnReadings = new ArrayList<AFMSMeterRegReading>();

    if (!allReadings.isEmpty())
    {
      notWithdrawnReadings = select(allReadings,
        having(on(AFMSMeterRegReading.class).getReadingType(),
               not(equalTo(AFMSMeterRegReading.READING_TYPE.W.getValue()))));

      if (aCheckForWithdrawnReadings)
      {
        notWithdrawnReadings = removeWithdrawnReadings(allReadings);
      }

      Collections.sort(notWithdrawnReadings, new AFMSMeterRegReadingSortByReadingReverse());
    }

    Float total = 0.0F;
    int numFound = 0;

    // bug#5835 - defaults to minimum number of meter readings required to 3
    if (aMinimumNoOfMeterReadingsRequired != null)
    {
      int minNoMeterReadingsRequired = aMinimumNoOfMeterReadingsRequired > 0
        ? aMinimumNoOfMeterReadingsRequired : 3;

      if (notWithdrawnReadings.size() >= minNoMeterReadingsRequired)
      {
        for (AFMSMeterRegReading value : notWithdrawnReadings)
        {
          numFound++;
          total+= value.getRegisterReading();
          results.threeHighestReadings.add(value);

          if (numFound >= minNoMeterReadingsRequired)
          {
            break;
          }
        }
      }
    }

    results.avgThreeHighest = total / numFound;

    //find the latest of these three
    Collections.sort(results.threeHighestReadings, new AFMSMeterRegReadingSortByReadingDate());
    Iterator<AFMSMeterRegReading> it = results.threeHighestReadings.iterator();
    if (it.hasNext())
    {
      results.mostRecent = it.next();
    }

    return results;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#getAverageOfThreeHighest(org.joda.time.DateTime,
   * org.joda.time.DateTime, java.util.Map, java.lang.Integer)
   */
  @Override
  public ThreeHighestReadingsAndAverage getAverageOfThreeHighest(DateTime aValidBeforeEffectiveFromDate,
                                                                 DateTime aValidAfterEffectiveToDate,
                                                                 Map<String, List<AFMSMeterRegReading>> aMsidReadings,
                                                                 Integer aMinimumNoOfMeterReadingsRequired)
  {
    return getAverageOfThreeHighest(aValidBeforeEffectiveFromDate, aValidAfterEffectiveToDate, aMsidReadings,
                                    aMinimumNoOfMeterReadingsRequired, false);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#hasReceivedD0150(
   * uk.co.utilisoft.afms.domain.AFMSAregiProcess)
   */
  @Override
  public boolean hasReceivedD0150(AFMSAregiProcess aAFMSAregiProcess)
  {
    if (aAFMSAregiProcess != null
        && aAFMSAregiProcess.getD0150Received() != null)
    {
      return true;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#isD0268DateSameAsOrGreaterThanMeterLastUpd(
   * org.joda.time.DateTime, org.joda.time.DateTime)
   */
  public boolean isD0268DateSameAsOrGreaterThanMeterLastUpd(DateTime aMeterLastUpdated,
                                                            DateTime aLatestD0268ReceiptDate)
  {
    if (aMeterLastUpdated != null && aLatestD0268ReceiptDate != null)
    {
      DateMidnight meterLastUpdated = new DateMidnight(aMeterLastUpdated);
      DateMidnight latestD0268Receipt = new DateMidnight(aLatestD0268ReceiptDate);

      if (meterLastUpdated.isEqual(latestD0268Receipt)
          || meterLastUpdated.isAfter(latestD0268Receipt))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#isSettlementDateBeforeMID(
   * uk.co.utilisoft.afms.domain.AFMSMeter, org.joda.time.DateTime)
   */
  public boolean isSettlementDateBeforeMID(AFMSMeter aAfmsMeter, DateTime aMID)
  {
    if (aAfmsMeter != null)
    {
      DateTime settlementDate = mHalfHourlySettlementDate.getHalfHourlySettlementDate(aAfmsMeter.getMpan());

      if (aMID != null)
      {
        if (settlementDate != null &&
            settlementDate.isBefore(aMID))
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#removeWithdrawnReadings(java.util.List)
   */
  @Override
  public List<AFMSMeterRegReading> removeWithdrawnReadings(List<AFMSMeterRegReading> aReadings)
  {
    List<AFMSMeterRegReading> withdrawns = select(aReadings, having(on(AFMSMeterRegReading.class).getReadingType(),
      equalTo(AFMSMeterRegReading.READING_TYPE.W.getValue())));

    if (withdrawns != null && !withdrawns.isEmpty())
    {
      List<AFMSMeterRegReading> notWithdrawns = select(aReadings, having(on(AFMSMeterRegReading.class).getReadingType(),
        not(equalTo(AFMSMeterRegReading.READING_TYPE.W.getValue()))));

      Map<DateTime, AFMSMeterRegReading> withdrawnReadDatesValues
        = index(withdrawns, on(AFMSMeterRegReading.class).getMeterReadingDate());

      Iterator<DateTime> withdrawnReadDatesIt = withdrawnReadDatesValues.keySet().iterator();
      while (withdrawnReadDatesIt.hasNext())
      {
        DateTime withdrawnReadDate = withdrawnReadDatesIt.next();

        List<AFMSMeterRegReading> withdrawnReadingsNotOfReadTypeW = select(notWithdrawns,
          new WithdrawnReadingMatcher(withdrawnReadDate, withdrawnReadDatesValues.get(withdrawnReadDate)
                                      .getRegisterReading(), withdrawnReadDatesValues.get(withdrawnReadDate)
                                      .getBSCValidationStatus()));
        notWithdrawns.removeAll(withdrawnReadingsNotOfReadTypeW);
      }

      return notWithdrawns;
    }

    return aReadings;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#recalculateMaxDemandIgnoreWithrawnReadings(
   * org.joda.time.DateTime, org.joda.time.DateTime, java.util.Map, java.lang.Integer,
   * Float)
   */
  @Override
  public ThreeHighestReadingsAndAverage recalculateMaxDemandIgnoreWithrawnReadings(DateTime aValidEffectiveFromDate,
                                                                                   DateTime aValidEffectiveToDate,
                                                                                   Map<String, List<AFMSMeterRegReading>> aMsidReadings,
                                                                                   Integer aMinimumNoOfMeterReadingsRequired,
                                                                                   Float aMaxDemand)
  {
    if (isMaxDemandGreaterThanOrEqualToThreshold(aMaxDemand))
    {
      return getAverageOfThreeHighest(aValidEffectiveFromDate, aValidEffectiveToDate, aMsidReadings,
                                      aMinimumNoOfMeterReadingsRequired, true);
    }

    return null;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator#getMaxDemandThreshold()
   */
  @Override
  public Float getMaxDemandThreshold()
  {
    String propertyPath = System.getProperty(ROOT_PATH_PROPERTY_VALUE_PROPERTY_NAME);
    Assert.notNull(propertyPath, "System property with name RootPathPropertyValue not found");
    Properties props = PropertyLoader.loadProperties(propertyPath + "/config/rascals.properties");

    String configuredMaxDemandThreshold = props != null ? props.getProperty(MAX_DEMAND_THRESHOLD_PROPERTY_NAME) : null;

    try
    {
      if (configuredMaxDemandThreshold != null)
      {
        return Float.valueOf(configuredMaxDemandThreshold);
      }
    }
    catch (NumberFormatException nfe)
    {
      // do nothing
    }

    return null;
  }

  /**
   * @param aAFMSAregiProcessDao the AFMSAregiProcessDao
   */
  public void setAFMSAregiProcessDao(AFMSAregiProcessDao aAFMSAregiProcessDao)
  {
    mAFMSAregiProcessDao = aAFMSAregiProcessDao;
  }

  /**
   * @param aAFMSMeterDao the AFMSMeterDao
   */
  public void setAFMSMeterDao(AFMSMeterDao aAFMSMeterDao)
  {
    mAFMSMeterDao = aAFMSMeterDao;
  }

  /**
   * @param aHalfHourlySettlementDate the HalfHourlySettlementDate
   */
  public void setHalfHourlySettlementDate(HalfHourlySettlementDate aHalfHourlySettlementDate)
  {
    mHalfHourlySettlementDate = aHalfHourlySettlementDate;
  }
}
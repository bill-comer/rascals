package uk.co.utilisoft.parms.file.sp04;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import uk.co.utilisoft.afms.dao.AFMSAregiProcessDao;
import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.afms.domain.MeasurementClassification;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator;


@Service("parms.sp04RowCalculator")
public class Sp04RowCalculator
{
  public static final Float MAX_DEMAND_THRESHOLD_DEFAULT = 100.0F;

  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  AFMSMeterDao mAFMSMeterDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsAregiProcessDao")
  AFMSAregiProcessDao mAFMSAregiProcessDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  AFMSMpanDao mAFMSMpanDao;

  @Autowired(required = true)
  @Qualifier("parms.sp04AfmsMpanValidator")
  private Sp04AfmsMpanValidator mSp04AfmsMpanValidator;

  @Autowired(required = true)
  @Qualifier("parms.halfHourlySettlementDate")
  private HalfHourlySettlementDate mHalfHourlySettlementDate;

  public void setAFMSMpanDao(AFMSMpanDao aAFMSMpanDao)
  {
    this.mAFMSMpanDao = aAFMSMpanDao;
  }

  private ParmsReportingPeriod mParmsReportingPeriod;

  public void setParmsReportingPeriod(ParmsReportingPeriod mParmsReportingPeriod)
  {
    this.mParmsReportingPeriod = mParmsReportingPeriod;
  }

  public ParmsReportingPeriod getParmsReportingPeriod()
  {
    return mParmsReportingPeriod;
  }

  /**
   * bug#5835 - Calculate the standards for eligible afms mpans and build sp04data record.
   *            business logic used here is copied from getRowsFromAfmsSp04Mpans(Long aSupplierId, List<String> aMpansToExclude)
   *            to build Sp04Data object
   *
   * @param aSp04FromAfmsMpan the Sp04FromAFMSMpan record
   * @param aLastMonthPrp the reporting period (Note: for calculating afms mpan
   *        validity is calculated as the previous month from the current date)
   * @return the Sp04Data
   */
  @Transactional(propagation=Propagation.REQUIRED, readOnly = true)
  public Sp04Data buildAfmsSp04DataRecord(Sp04FromAFMSMpan aSp04FromAfmsMpan, ParmsReportingPeriod aLastMonthPrp,
                                          Supplier aSupplier)
  {
    Sp04Data afmsMpanSp04Data = null;

    if (aSp04FromAfmsMpan != null)
    {
      // use standards 1,2,3 calculated in the PARMS_SP04_FROM_AFMS_MPANS table if they are available
      afmsMpanSp04Data = new Sp04Data();
      afmsMpanSp04Data.setStandard1(aSp04FromAfmsMpan.getCalculatedStandard1());
      afmsMpanSp04Data.setStandard2(aSp04FromAfmsMpan.getCalculatedStandard2());
      afmsMpanSp04Data.setStandard3(aSp04FromAfmsMpan.getCalculatedStandard3());
    }

    if (aSp04FromAfmsMpan != null && aSp04FromAfmsMpan.getMpanFk() > 0L)
    {
      AFMSMpan activeAfmsMpan = mAFMSMpanDao.getById(aSp04FromAfmsMpan.getMpanFk());

      if (activeAfmsMpan != null)
      {
        String[] metersWithReads = StringUtils.commaDelimitedListToStringArray(aSp04FromAfmsMpan.getMeterId()
                                                                               .replaceAll("\\s+", ""));

        afmsMpanSp04Data = buildSp04DataRecord(afmsMpanSp04Data, aLastMonthPrp, activeAfmsMpan, aSupplier,
          aSp04FromAfmsMpan.getMaxDemand(), aSp04FromAfmsMpan.getCalculatedMeterInstallationDeadline(),
          metersWithReads);
      }
    }

    return afmsMpanSp04Data;
  }

  /**
   * @param aPrp the reporting period
   * @param aAfmsMpan the AFMS mpan
   * @param aSupplier the Supplier
   * @param aMaxDemand the Maximum Demand
   * @param aMeterInstallationDeadline the Meter Installation Deadline
   * @return the mpan data in an Sp04Data record
   */
  public Sp04Data buildP0028Sp04DataRecord(ParmsReportingPeriod aPrp, AFMSMpan aAfmsMpan,
                                           Supplier aSupplier, Float aMaxDemand, DateTime aMeterInstallationDeadline,
                                           String aMeterSerialId)
  {
    return buildSp04DataRecord(null, aPrp, aAfmsMpan, aSupplier, aMaxDemand, aMeterInstallationDeadline, aMeterSerialId);
  }

  /**
   * Build Sp04Data record and calculate standards for currently active P0028Active or AFMS Mpan(s).
   *
   * @param aSp04Data the Sp04Data record
   * @param aPrp the reporting period
   * @param aAfmsMpan the AFMS mpan
   * @param aSupplier the Supplier
   * @param aMaxDemand the Maximum Demand
   * @param aMeterInstallationDeadline the Meter Installation Deadline
   * @param aMeterSerialids the the meter serial ids for meter register readings used to calculate the max demand
   * @return the mpan data in an Sp04Data record
   */
  public Sp04Data buildSp04DataRecord(Sp04Data aSp04Data, ParmsReportingPeriod aPrp, AFMSMpan aAfmsMpan,
                                      Supplier aSupplier, Float aMaxDemand, DateTime aMeterInstallationDeadline, String ... aMeterSerialIds)
  {
    setParmsReportingPeriod(aPrp);
    Sp04Data sp04Data = aSp04Data != null ? aSp04Data : new Sp04Data();

    // bug#5622 on demand load of mpan.meters
    loadAFMSMeters(aAfmsMpan);

    if (aAfmsMpan == null)
    {
      sp04Data.setSp04FaultReason(Sp04FaultReasonType.getNoMpanForSearchCriteria());
      return sp04Data;
    }

    if(doesMpanHaveNoMeters(aAfmsMpan))
    {
      sp04Data.setSp04FaultReason(Sp04FaultReasonType.getNoAfmsMeterForMeterSerialId());
      return sp04Data;
    }


    if (MeasurementClassification.isMeasurementClassificationHalfHourly(
        aAfmsMpan.getMeasurementClassification()))
    {
      DateTime settlementDate = mHalfHourlySettlementDate.getHalfHourlySettlementDate(aAfmsMpan);

      if (settlementDate != null && settlementDate.isBefore(aMeterInstallationDeadline))
      {
        //The meter has been converted to HH before the |MID - that is good so no need to raise an Exception

        // bug#6583 null pointer generating sp04 file. Note: this change affects sp04 exception report and sp04 file generation
        return null;
      }

      // bug#6339 - ignore mpans already converted to HH metering & the settlement date is before the reporting month
      if (mSp04AfmsMpanValidator.hasMpanGotHalfHourlyMeteringBeforeReportMonth(aAfmsMpan,
                                                                               aPrp.getStartOfMonth(false).toDateTime()))
      {
        return null;
      }
    }

    if (!mSp04AfmsMpanValidator.isSupplierCurrent(aSupplier, aAfmsMpan, getParmsReportingPeriod().getEndOfMonth(true)))
    {
      sp04Data.setSp04FaultReason(Sp04FaultReasonType.getSupplierIsNotCorrect());
      return sp04Data;
    }

    // check for max demand value of latest record of half hourly maximum demand > 100KW
    // bugg#5744 - if a custom MD threshold proeprty is configured that is used instead of default check MD > 100KW
    Float mdThreshold = mSp04AfmsMpanValidator.getMaxDemandThreshold();
    Sp04FaultReasonType mdErrReason = mSp04AfmsMpanValidator.validateMaximumDemand(aMaxDemand);

    if (mdErrReason != null)
    {
      if (mdErrReason.equals(Sp04FaultReasonType.getMDMatchesOrExceedsThreshold()))
      {
        sp04Data.setSp04FaultReason(Sp04FaultReasonType.getMDMatchesOrExceedsThreshold());
        sp04Data.setMaxDemandThreshold(mdThreshold);
      }
      else if (mdErrReason.equals(Sp04FaultReasonType.getMDLessThanOneHundred()))
      {
        sp04Data.setSp04FaultReason(Sp04FaultReasonType.getMDLessThanOneHundred());
        sp04Data.setMaxDemandThreshold(MAX_DEMAND_THRESHOLD_DEFAULT);
      }

      return sp04Data;
    }

    if (isMIDAfterEndOfMonthT(aMeterInstallationDeadline, getParmsReportingPeriod().getStartOfNextMonthInPeriod()))
    {
      //MID after month T is not an error.
      //It just means it is not passed its sell by date yet.
      return null;
    }

    //bug 6320/6334
    AFMSMeter meter = getMeterForMeterSerialId(aAfmsMpan.getMeters(), aMeterSerialIds);

    if (meter == null)
    {
      sp04Data.setSp04FaultReason(Sp04FaultReasonType.getNoActiveAfmsMeterForMeterSerialId());
      return sp04Data;
    }

    // bug#5954 & bug#5992 - add check to only consider non half hourly afms meters.
    if (meter.isHalfHourlyMeter())
    {
      if (mSp04AfmsMpanValidator.isSettlementDateBeforeMID(meter, aMeterInstallationDeadline))
      {
        //D0268 occurred in time
        return null;
      }

      DateTime settlementDate = mHalfHourlySettlementDate.getHalfHourlySettlementDate(aAfmsMpan);

      if (isD0268BeforeMonthT(settlementDate, getParmsReportingPeriod().getStartOfFirstMonthInPeriod()))
      {
        return null;
      }
    }

    // bug#5791 - no receipt of D0268, and a receipt of D0150 should be reported in Sp04
    if (!isReceiptD0150Valid(getReceiptD0150RemovalNonHalfHourlyMeterTechnicalDetails(meter.getMpan()),
                             getParmsReportingPeriod().getStartOfFirstMonthInPeriod().toDateTime()))
    {
      return null;
    }

    //I believe this is already coverred by lines 161 to 171

    // bug#6174 - if meter.settlementDate(j1254) is within reporting month
    /*if (MeasurementClassification.isMeasurementClassificationHalfHourly(aAfmsMpan.getMeasurementClassification())
                &&  !mSp04AfmsMpanValidator.isMeterSettlementDateInReportPeriod(meter.getSettlementDate() , aPrp))
    {
      return null;
    }*/

    // calculate standards 1, 2, 3 for eligible mpans screen if they do not already exist
    if (sp04Data.getStandard1() == null)
    {
      sp04Data.setStandard1(calculateStandard1(aMeterInstallationDeadline, getParmsReportingPeriod()));
    }

    if (sp04Data.getStandard2() == null)
    {
      sp04Data.setStandard2(calculateStandard2(aMeterInstallationDeadline, meter.getMpan(), getParmsReportingPeriod()));
    }

    if (sp04Data.getStandard3() == null)
    {
      sp04Data.setStandard3(calculateStandard3(sp04Data.getStandard1(), sp04Data.getStandard2()));
    }

    sp04Data.setMpanCore(new MPANCore(aAfmsMpan.getMpanCore()));
    sp04Data.setGspGroupId(aAfmsMpan.getGridSupplyPoint());

    return sp04Data;
  }

  private AFMSMeter getMeterForMeterSerialId(Collection<AFMSMeter> aMeters, String ... aMeterSerialIds)
  {
    if (aMeters != null)
    {
      List<AFMSMeter> metersOrderedBySettDateDesc = sort(aMeters, on(AFMSMeter.class).getSettlementDate(), Collections.reverseOrder());
      List<String> msids = aMeterSerialIds != null ? Arrays.asList(aMeterSerialIds) : null;

      for (AFMSMeter afmsMeter : metersOrderedBySettDateDesc)
      {
        if (msids.contains(afmsMeter.getMeterSerialId()))
        {
          return afmsMeter;
        }
      }
    }

    return null;
  }

  /**
   * @param aAfmsMpan the AFMSMpan
   * @param aMeterSerialIds the meter serial id(s)
   * @return the meter currently active
   */
  AFMSMeter getMetersForMeterSerialIds(AFMSMpan aAfmsMpan, String ... aMeterSerialIds)
  {
    if (aAfmsMpan != null)
    {
      List<AFMSMeter> meters = sort(aAfmsMpan.getMeters(),
                                    on(AFMSMeter.class).getSettlementDate(), Collections.reverseOrder());

      if (meters != null && !meters.isEmpty())
      {
        List<String> msidList = Arrays.asList(aMeterSerialIds);

        for (AFMSMeter meter : meters)
        {
          // is change of nhh meter due to expiry
          if (msidList.contains(meter.getMeterSerialId()))
          {
            return meter;
          }
        }
      }
    }

    return null;
  }

  private boolean doesMpanHaveNoMeters(AFMSMpan mpan)
  {
    return mpan.getMeters() == null || mpan.getMeters().size() == 0;
  }

  // bug#5622 changes on demand load of mpan.meters
  private void loadAFMSMeters(AFMSMpan aMpan)
  {
    if (aMpan != null)
    {
      aMpan.setMeters(mAFMSMeterDao.getByMpanUniqueId(aMpan.getPk()));
    }
  }

  // TODO already refactored into common code for use by Sp04FromAfmsMpan and P0028Active sp04 report validation - FOR REFERENCE ONLY
  private AFMSMpan getAFMSMpan(P0028Active aP0028Active, ParmsReportingPeriod parmsReportingPeriod)
  {
    return mAFMSMpanDao.getAfmsMpan(aP0028Active.getMpanCore(), aP0028Active.getSupplier(), parmsReportingPeriod);
  }

  // TODO already refactored into common code for use by Sp04FromAfmsMpan and P0028Active sp04 report validation - FOR REFERENCE ONLY
  private AFMSMpan getMpan(P0028Active aP0028Active)
  {
    return mAFMSMpanDao.getById(aP0028Active.getLatestP0028Data().getMpanUniqId());
  }

  // TODO already refactored into common code for use by Sp04FromAfmsMpan and P0028Active sp04 report validation - FOR REFERENCE ONLY
  private Long calculateStandard1ForParmsReportingPeriod(ParmsReportingPeriod aPrp, P0028Active aP0028Active)
  {
    if (aP0028Active != null)
    {
      return getNumDaysInMonthT(aPrp) - getDayOfMID(aP0028Active);
    }

    return getNumDaysInMonthT(aPrp);
  }

  /*
   * @param aReportingPeriod the parms reporting period
   * @param aMeterInstallationDeadline the meter installation deadline
   * @return standard 2
   */
  private Long calculateStandard2ForParmsReportingPeriod(ParmsReportingPeriod aReportingPeriod, DateTime aMeterInstallationDeadline)
  {
    if (aReportingPeriod != null && aMeterInstallationDeadline != null)
    {
      return getNumDaysInMonthT(aReportingPeriod) - getDayOfMID(aMeterInstallationDeadline);
    }

    return null;
  }


  /*
   * @param aMeter the AFMS Meter
   * @param aMeterInstallationDeadline the meter installation deadline
   * @param aReportingPeriod the parms reporting period
   * @return standard 2
   */
  private Long calculateStandard2ForAFMSMeter(AFMSMpan aMpan, DateTime aMeterInstallationDeadline, ParmsReportingPeriod aReportingPeriod)
  {
    // bug#5291- ref BSCP533 parms calculations v17 page 17 - examples in BSCP533 does not include the following
    // case where an AFMSMeter.settlementDate equals Meter Installation Date. Meters installed on the MID are
    // aggregated not installed by 1 MSID -Day in month HH 100kW Site.
    if (aMpan != null)
    {
      /*
       * As this is used for calc of standard2 it needs to be the mpan.getSettlementDate
       */
      DateTime meterSettlementDate = mHalfHourlySettlementDate.getHalfHourlySettlementDate(aMpan);

      if (aMeterInstallationDeadline != null)
      {
        if (meterSettlementDate != null)
        {
          if (meterSettlementDate.equals(aMeterInstallationDeadline))
          {
            return new Long(1);
          }

          if (aReportingPeriod != null)
          {
            // bug#5291 - fix calculated standard 2 can be a negative value.
            // And case where meter.settlementDate is not within parmsreportingmonth T.
            if (meterSettlementDate.compareTo(aReportingPeriod.getStartOfFirstMonthInPeriod().toDateTime()) >= 0)
            {
              if (meterSettlementDate.compareTo(aReportingPeriod.getStartOfNextMonthInPeriod().toDateTime()) >= 0)
              {
                meterSettlementDate = aReportingPeriod.getStartOfNextMonthInPeriod().minusDays(1).toDateTime();
              }
            }

            return new Long(Days.daysBetween(aMeterInstallationDeadline, meterSettlementDate).getDays());
          }
        }
      }
    }

    return null;
  }

  // TODO already refactored into common code for use by Sp04FromAfmsMpan and P0028Active sp04 report validation - FOR REFERENCE ONLY
  private Float calculateStandard3(Sp04Data aSp04Data)
  {
    Float standard3 = (new Float(aSp04Data.getStandard2())/aSp04Data.getStandard1() * 100F);
    if (standard3 > 100F)
    {
      standard3 = 100F;
    }
    standard3 = (Math.round(standard3*10.0f)/10.0f);  // round to 2 decimal places
    return standard3;
  }

  /**
   * @param aMeter the AFMS Meter
   * @param aReportingPeriod the parms reporting period
   * @return true or false
   */
  boolean isHalfHourlySettlementDateAfterEndOfMonthOrNotHappenedOrBeforeMonthT(AFMSMpan aMpan, ParmsReportingPeriod aReportingPeriod)
  {
    DateTime settlementDate = mHalfHourlySettlementDate.getHalfHourlySettlementDate(aMpan);

    if (aReportingPeriod != null)
    {
      if (settlementDate == null)
      {
        //not happened
        return true;
      }
      else
      {
        if (settlementDate.isAfter(aReportingPeriod.getStartOfNextMonthInPeriod().minusDays(1)))
        {
          //is after
          return true;
        }

        if (settlementDate.isBefore(aReportingPeriod.getStartOfFirstMonthInPeriod()))
        {
          //is before
          return true;
        }
      }
    }

    return false;
  }

  private Long getDayOfMID(P0028Active p0028Active)
  {
    return new Long(p0028Active.getMeterInstallationDeadline().getDayOfMonth());
  }

  /*
   * @param aMeterInstallationDeadline the meter installation deadline
   * @return the day of month in the Meter Installation Deadline date
   */
  private Long getDayOfMID(DateTime aMeterInstallationDeadline)
  {
    if (aMeterInstallationDeadline != null)
    {
      return new Long(aMeterInstallationDeadline.getDayOfMonth());
    }

    return null;
  }

  private boolean isD0268BeforeMonthT(DateTime settlementDate, DateMidnight startOfFirstMonthInPeriod)
  {
    if (settlementDate != null &&
        settlementDate.isBefore(startOfFirstMonthInPeriod))
    {
      return true;
    }
    return false;
  }

  /**
   * @param aP0028Active the P0028Active record
   * @return the AFMSMpan
   */
  public AFMSMpan getAFMSMpan(P0028Active aP0028Active)
  {
    return mAFMSMpanDao.getAfmsMpan(aP0028Active.getMpanCore(), aP0028Active.getSupplier(), getParmsReportingPeriod());
  }

  /**
   * TODO bug#5746 - this is a duplicate of getAFMSMpan(P0028Active), but uses mpancore, supplier,
   *      parameters instead of P0028Active object.
   *
   * @param aMpanCore the MPANCcore
   * @param aSupplier the Supplier
   * @param aReportingPeriod the ParmsReportingPeriod
   * @return the AFMS Mpan
   */
  public AFMSMpan getAFMSMpan(MPANCore aMpanCore, Supplier aSupplier, ParmsReportingPeriod aReportingPeriod)
  {
    return mAFMSMpanDao.getAfmsMpan(aMpanCore, aSupplier, aReportingPeriod);
  }


  /*
   * As this is used for calc of standard2 it needs to be the mpan.getSettlementDate
   */
  private Long getSettlementDayOfMonth(AFMSMpan aMpan)
  {
    DateTime meterSettlementDate = mHalfHourlySettlementDate.getHalfHourlySettlementDate(aMpan);

    if (meterSettlementDate != null)
    {
      return new Long(meterSettlementDate.getDayOfMonth());
    }

    return null;
  }

  public DateTime getSettlementDate(P0028Active aP0028Active)
  {
    AFMSMpan mpan = getAFMSMpan(aP0028Active);
    return mHalfHourlySettlementDate.getHalfHourlySettlementDate(mpan);
  }

  // bug#5791 - no valid afms meter, but in receipt of D0150
  boolean isReceiptD0150Valid(DateTime aD0150Received, DateTime aStartOf1stMonthInPeriod)
  {
    boolean isValid = aD0150Received != null;

    if (aD0150Received != null)
    {
      // check D0150 date is valid
      if (aStartOf1stMonthInPeriod != null)
      {
        isValid = true; // any D0150 date is considered valid
      }
    }

    return isValid;
  }

  // bug#5791 - no valid afms meter, but maybe in receipt of D0150
  DateTime getReceiptD0150RemovalNonHalfHourlyMeterTechnicalDetails(AFMSMpan aAfmsMpan)
  {
    if (aAfmsMpan != null)
    {
      aAfmsMpan.setAregiProcesses(mAFMSAregiProcessDao.getByMpanUniqueId(aAfmsMpan.getPk()));
      AFMSAregiProcess latestAregiProc = aAfmsMpan.getLatestAregiProcess();

      if (latestAregiProc != null)
      {
        return latestAregiProc.getD0150Received();
      }
    }

    return null;
  }

  /**
   * returns true if both these dates are for the same day
   * @param lastUpdated
   * @param latestD0268ReceiptDate
   * @return
   */
  boolean isD0268DateSameAsMeterLastUpd(DateTime lastUpdated, DateTime latestD0268ReceiptDate) {

    DateMidnight meterLastUpdated = new DateMidnight(lastUpdated);
    DateMidnight latestD0268Receipt = new DateMidnight(latestD0268ReceiptDate);

    if (meterLastUpdated.isEqual(latestD0268Receipt)){
      return true;
    }

    return false;
  }

  /**
   * bug#5835 - add check for MID > end of Month T
   *
   * @param aMID the Meter installation date
   * @param aEndOf1stMonthInPeriod the reporting period end of 1st month in period
   * @return true if the Meter Installation Deadline is after end of month T
   */
  boolean isMIDAfterEndOfMonthT(DateTime aMID, DateMidnight aEndOf1stMonthInPeriod)
  {
    if (aMID.isAfter(aEndOf1stMonthInPeriod))
    {
      return true;
    }

    return false;
  }

  boolean isMIDAfterEndOfMonthT(P0028Active aP0028Active)
  {
    return isMIDAfterEndOfMonthT(aP0028Active.getMeterInstallationDeadline(),
                                 getParmsReportingPeriod().getStartOfNextMonthInPeriod());
  }

  Long getNumDaysInMonthT(ParmsReportingPeriod prp)
  {
    // bug#5373 problem parsing a reporting period to a valid datetime object, throws jodatime exception when generating an sp04 in the first month of any year
    DateTime previousMonthTime = prp.getStartOfNextMonthInPeriod().toDateTime().minusMonths(1);
    int year = previousMonthTime.getYear();
    int month = previousMonthTime.getMonthOfYear();

    DateTime dateTime = new DateTime(year, month, 14, 12, 0, 0, 000);
    return new Long(dateTime.dayOfMonth().getMaximumValue());
  }

  boolean isMIDBeforeStartOfMonthT(P0028Active aP0028Active)
  {
    if (aP0028Active.getMeterInstallationDeadline().isBefore(getParmsReportingPeriod().getStartOfFirstMonthInPeriod()))
    {
      return true;
    }

    return false;
  }

  /**
   * @param aMeterInstallationDeadline the meter installation deadline
   * @param aReportingPeriod the reporting period
   * @return standard 1
   */
  public Long calculateStandard1(DateTime aMeterInstallationDeadline, ParmsReportingPeriod aReportingPeriod)
  {
    if (aMeterInstallationDeadline != null)
    {
      if (aMeterInstallationDeadline.isBefore(aReportingPeriod.getStartOfFirstMonthInPeriod()))
      {
        return getNumDaysInMonthT(aReportingPeriod);
      }

      return getNumDaysInMonthT(aReportingPeriod) - new Long(aMeterInstallationDeadline.getDayOfMonth());
    }

    return null;
  }

  /**
   * @param aMeterInstallationDeadline the AFMS Meter Installation Deadline
   * @param aMpan the AFMS Mpan record
   * @param aReportingPeriod the parms reporting period
   * @return standard 2
   */
  public Long calculateStandard2(DateTime aMeterInstallationDeadline, AFMSMpan aMpan,
                                 ParmsReportingPeriod aReportingPeriod)
  {
    DateTime calcMID = aMeterInstallationDeadline;
    boolean isSettlementDateAfterEndOfMonthOrNotHappendOrBeforeMonthT
      = isHalfHourlySettlementDateAfterEndOfMonthOrNotHappenedOrBeforeMonthT(aMpan, aReportingPeriod);

    if (calcMID != null)
    {
      if (calcMID.isBefore(aReportingPeriod.getStartOfFirstMonthInPeriod()))
      {
        return isSettlementDateAfterEndOfMonthOrNotHappendOrBeforeMonthT ?
          getNumDaysInMonthT(aReportingPeriod) : getSettlementDayOfMonth(aMpan);
      }

      return isSettlementDateAfterEndOfMonthOrNotHappendOrBeforeMonthT
        ? calculateStandard2ForParmsReportingPeriod(aReportingPeriod, calcMID)
        : calculateStandard2ForAFMSMeter(aMpan, aMeterInstallationDeadline, aReportingPeriod);
    }

    return null;
  }

  /**
   * @param aMeterInstallationDeadline the AFMS Meter Installation Deadline
   * @param aAfmsMeter the AFMS Meter
   * @param aReportingPeriod the parms reporting period
   * @return standard 3
   */
  public Float calculateStandard3(Long aStandard1, Long aStandard2)
  {
    Float standard3 = null;

    if (aStandard1 != null && aStandard2 != null)
    {
      standard3 = (new Float(aStandard2) / aStandard1 * 100F);

      if (standard3 > 100F)
      {
        standard3 = 100F;
      }

      standard3 = (Math.round(standard3*10.0f)/10.0f);  // round to 2 decimal places
    }

    return standard3;
  }

  /**
   * @param aAFMSMeterDao the AFMS Meter dao
   */
  public void setAFMSMeterDao(AFMSMeterDao aAFMSMeterDao)
  {
    mAFMSMeterDao = aAFMSMeterDao;
  }

  /**
   * @param aSp04AfmsMpanValidator the Sp04AfmsMpanValidator
   */
  public void setSp04AfmsMpanValidator(Sp04AfmsMpanValidator aSp04AfmsMpanValidator)
  {
    mSp04AfmsMpanValidator = aSp04AfmsMpanValidator;
  }

  /**
   * @param aAFMSAregiProcessDao the AFMSAregiProcessDao
   */
  public void setAFMSAregiProcessDao(AFMSAregiProcessDao aAFMSAregiProcessDao)
  {
    mAFMSAregiProcessDao = aAFMSAregiProcessDao;
  }

  /**
   * @param aHalfHourlySettlementDate the HalfHourlySettlementDate
   */
  public void setHalfHourlySettlementDate(HalfHourlySettlementDate aHalfHourlySettlementDate)
  {
    mHalfHourlySettlementDate = aHalfHourlySettlementDate;
  }
}
package uk.co.utilisoft.parms.file.sp04;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.domain.Audit.TYPE;
import uk.co.utilisoft.parms.validation.Sp04AfmsMpanValidator;
import uk.co.utilisoft.parms.web.service.sp04.Sp04Service;

@ManagedResource(objectName = "parms:name=Sp04FromAfmsMpanBuilder",
                 description = "JMX Managed Sp04FromAfmsMpanBuilder service")
@Service("parms.sp04FromAfmsMpanBuilder")
public class Sp04FromAfmsMpanBuilder
{
  private static final Logger LOGGER = Logger.getLogger(Sp04FromAfmsMpanBuilder.class);

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDao mAFMSMPanDao;

  @Autowired(required=true)
  @Qualifier("parms.sp04FromAFMSMpanDao")
  private Sp04FromAFMSMpanDao mSp04FromAFMSMpanDao;

  @Autowired(required=true)
  @Qualifier("parms.sp04Service")
  private Sp04Service mSp04Service;

  @Autowired(required = true)
  @Qualifier("parms.sp04AfmsMpanValidator")
  private Sp04AfmsMpanValidator mSp04AfmsMpanValidator;

  @Autowired(required = true)
  @Qualifier("parms.halfHourlySettlementDate")
  private HalfHourlySettlementDate mHalfHourlySettlementDate;

  @Autowired(required=true)
  @Qualifier("parms.sp04Calculator")
  public Sp04Calculator mSp04Calculator;

  /**
   * Get MPANS for all Suppliers
   */
  @ManagedOperation(description = "Execute Sp04FromAfmsMpanBuilder.getAfmsMpansForSp04Inclusion() Process")
  @Transactional(rollbackFor = Exception.class)
  @ParmsAudit(auditType = TYPE.SP04_AFMS_DATA_RUN)
  public void getAfmsMpansForSp04Inclusion()
  {
    List<Supplier> suppliers = mSupplierDao.getAll();
    for (Supplier supplier : suppliers)
    {
      getAfmsMpansForSp04Inclusion(supplier.getPk());
    }
  }

  /**
   * Gets rows from the AFMS database for inclusion in the SP04
   * & insert into Sp04FromAFMSMpan table
   * @param aSupplierId
   * @return
   */
  public void getAfmsMpansForSp04Inclusion(Long aSupplierId)
  {
    long count = 0;

    Supplier supplier = getTheSupplier(aSupplierId);
    if (supplier == null)
    {
      return;
    }

    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveToDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);

    // bug#5722 - get afmsmpans to be considered for writing into PARMS_SP04_FROM_AFMS_MPANS db table that have been
    // supplied for more than the last 12 months.
    DateTime validStartOf12MonthMonitoringDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    // bug5817 - consider mpans which have been in supply for at least 3 months
    List<AFMSMpan> activeMpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, validEffectiveToDate, validStartOf12MonthMonitoringDate, 3);

    // bug#5776 - store activeMpans saved to sp04FromAFMSMpans table
    Set<MPANCore> sp04IncludedMpans = new HashSet<MPANCore>();

    if (LOGGER.isDebugEnabled())
    {
      DateTimeFormatter dateTimeFmt = DateTimeFormat.forPattern("yyyy-MM-dd HHmmss");
      LOGGER.debug("Sp04FromAfmsMpanBuilder Valid EFD:" + dateTimeFmt.print(validEffectiveToDate));
      LOGGER.debug("Sp04FromAfmsMpanBuilder Valid start of 12 month monitoring:"
        + dateTimeFmt.print(validStartOf12MonthMonitoringDate));
      LOGGER.debug("*** Sp04FromAfmsMpanBuilder - Mpans for consideration - log start ***");

      for (AFMSMpan afmsMpan : activeMpans)
      {
        LOGGER.debug("AFMSMpan[mpan:" + afmsMpan.getMpanCore() + ", mpan_pk:" + afmsMpan.getPk() + "]");
        for (AFMSMeter afmsMeter : afmsMpan.getMeters())
        {
          Boolean hasAfmsMeterRegister = !afmsMeter.getMeterRegisters().isEmpty() ? true : false;

          LOGGER.debug("AFMSMeter[meter_pk:" + afmsMeter.getPk() + ", meter_serial:" + afmsMeter.getMeterSerialId()
                       + ", has_meter_register:" + hasAfmsMeterRegister.toString() + "]");
          if (hasAfmsMeterRegister)
          {
            for (AFMSMeterRegister register : afmsMeter.getMeterRegisters())
            {
              LOGGER.debug("AFMS" + ", meter_register_pk:" + register.getPk()
                           + ", meter_register_id:" + register.getMeterRegisterId()
                           + ", measurenement_qty:" + register.getMeasurementQuantityId() + "]");
              for (AFMSMeterRegReading meterRegReading : register.getMeterRegReadings())
              {
                LOGGER.debug("AFMSMeterRegReading[pk:" + meterRegReading.getPk() + ", bsc_validation_status:"
                             + meterRegReading.getBSCValidationStatus() + ", date_received:"
                             + dateTimeFmt.print(meterRegReading.getDateReceived())
                             + ", meter_reading_date:" + dateTimeFmt.print(meterRegReading.getMeterReadingDate())
                             + ", meter_reading:" + meterRegReading.getRegisterReading().toString()
                             + "]");
              }
            }
          }
        }
      }
      LOGGER.debug("*** Sp04FromAfmsMpanBuilder - Mpans for consideration - log finish ***");
    }

    for (AFMSMpan afmsMpan : activeMpans)
    {
      //get the meters for that mpan
      // bug#6375 now includes meters which have been changed due to life expectancy expiration
      List<AFMSMeter> meters = afmsMpan.getNonHalfHourlyMeters(validStartOf12MonthMonitoringDate,
                                                               validEffectiveToDate);

      //6415 - take off 2 days as effToDate can be the end of the month. so we need to say effToDate > startOfMonth - 2 days
      List<AFMSMeterRegister> registers
        = afmsMpan.getNonHalfHourlyMeterRegisters(validStartOf12MonthMonitoringDate.minusDays(2),
                                                  validEffectiveToDate);

        if (registers != null && !registers.isEmpty())
        {
          Map<String, List<AFMSMeterRegReading>> msidValidReadings
            = afmsMpan.getNonHalfHourlyMeterRegReadings(validStartOf12MonthMonitoringDate, validEffectiveToDate);

          //6415 - subtract 1 day as validStartOf12MonthMonitoringDate is the start of the month & getAverageOfThreeHighest() needs the end of ther previous month
          ThreeHighestReadingsAndAverage threeHighestReadingsAndAvg = mSp04AfmsMpanValidator
            .getAverageOfThreeHighest(validEffectiveToDate, validStartOf12MonthMonitoringDate.minusDays(1),
                                      msidValidReadings, 3);

          // bug#6466 re-caculate max demand for readings if the max demand has exceeded the configured max demand threshold
          ThreeHighestReadingsAndAverage threeHighestReadingsAndAvgIgnoreWithdrawnReadings
            = mSp04AfmsMpanValidator.recalculateMaxDemandIgnoreWithrawnReadings(validEffectiveToDate,
            validStartOf12MonthMonitoringDate.minusDays(1), msidValidReadings, 3,
            threeHighestReadingsAndAvg.avgThreeHighest);

          threeHighestReadingsAndAvg = threeHighestReadingsAndAvgIgnoreWithdrawnReadings != null
            ? threeHighestReadingsAndAvgIgnoreWithdrawnReadings : threeHighestReadingsAndAvg;

          if (mSp04AfmsMpanValidator.hasExceededMaximumDemand(threeHighestReadingsAndAvg.avgThreeHighest))
          {
            MPANCore mpanValue = new MPANCore(afmsMpan.getMpanCore());
            Sp04FromAFMSMpan sp04FromAFMSMpan = mSp04FromAFMSMpanDao.getMpan(mpanValue);

            if (sp04FromAFMSMpan == null)
            {
              sp04FromAFMSMpan = createNewSp04FromAFMSMpan(afmsMpan, threeHighestReadingsAndAvg, supplier, 3);
              count++;
            }

            // bug#6375 - to calculate standard 2 we need the unique mpan for any qualifying NHH meter
            // bug#5954 and 5992 - always recalculate standards
            // bug#5746 - calculate standards 1, 2, 3
            calculateStandards(sp04FromAFMSMpan, (meters.size() > 0 ? meters.get(0).getMpan() : null), lastMonthPrp);

            // bug#6038 mpans considered for sp04 reporting may have no associated meter registers with
            // J0010 = 'MD' and J0474 = 'M' but still need to be reported in sp04
            sp04FromAFMSMpan.setMaxDemand(threeHighestReadingsAndAvg.avgThreeHighest);

            sp04FromAFMSMpan.setD0268SettlementDate(mHalfHourlySettlementDate.getHalfHourlySettlementDate(afmsMpan));

            mSp04Service.saveSp04FromAFMSMpan(sp04FromAFMSMpan);

            // bug#5776 - change to clear out all mpans not included in this run we need to store reference to
            // mpans saved to PARMS_SP04_FROM_AFMS_MPANS
            sp04IncludedMpans.add(mpanValue);
          }
        }
    }

    System.out.println("Number of AFMS Mpan(s) Found " + count + " for inclusion");

    // bug#5776 - change to remove only mpans from PARMS_SP04_FROM_AFMS_MPANS table if they are not elligible for sp04 reporting
    removeFromSp04FromAFMSMpans(sp04IncludedMpans);
  }

  /*
   * bug#5746 - calculate standards 1, 2, 3 for Sp04FromAFMSMpan records
   */
  private void calculateStandards(Sp04FromAFMSMpan aSp04FromAfmsMpan, AFMSMpan aMpan,
                                  ParmsReportingPeriod aLastMonthPrp)
  {
    if (aSp04FromAfmsMpan != null)
    {
      aSp04FromAfmsMpan.setCalculatedStandard1(mSp04Calculator.mSp04RowCalculator.calculateStandard1(aSp04FromAfmsMpan
        .getCalculatedMeterInstallationDeadline(), aLastMonthPrp));
      aSp04FromAfmsMpan.setCalculatedStandard2(mSp04Calculator.mSp04RowCalculator.calculateStandard2(aSp04FromAfmsMpan
        .getCalculatedMeterInstallationDeadline(), aMpan, aLastMonthPrp));
      aSp04FromAfmsMpan.setCalculatedStandard3(mSp04Calculator.mSp04RowCalculator.calculateStandard3(aSp04FromAfmsMpan
        .getCalculatedStandard1(), aSp04FromAfmsMpan.getCalculatedStandard2()));
    }
  }

  /*
   * @param aExcludedSp04Mpans the mpans excluded from Sp04 reporting
   */
  private void removeFromSp04FromAFMSMpans(Set<MPANCore> aExcludedSp04Mpans)
  {
    removeFromSp04FromAFMSMpan(aExcludedSp04Mpans);
  }

  Sp04FromAFMSMpan createNewSp04FromAFMSMpan(AFMSMpan aAfmsMpan,
                                             ThreeHighestReadingsAndAverage aThreeHighestResults,
                                             Supplier aSupplier, Integer aNoMonthsToCalcMID)
  {
    Sp04FromAFMSMpan sp04FromAFMSMpan = new Sp04FromAFMSMpan();
    sp04FromAFMSMpan.setMpan(new MPANCore(aAfmsMpan.getMpanCore()));
    sp04FromAFMSMpan.setMpanFk(aAfmsMpan.getPk());

    Set<AFMSMeterRegister> readingRegisters = new HashSet<AFMSMeterRegister>(extract(aThreeHighestResults
      .threeHighestReadings, on(AFMSMeterRegReading.class).getMeterRegister()));
    Set<Long> registerPks = new HashSet<Long>(extract(readingRegisters, on(AFMSMeterRegister.class).getPk()));
    String registerPksTxt = Arrays.toString(registerPks.toArray(new Long[] {})).replace("[", "").replace("]", "");
    sp04FromAFMSMpan.setMeterRegisterFks(registerPksTxt);

    Set<AFMSMeter> metersWithReads
      = new HashSet<AFMSMeter>(extract(readingRegisters, on(AFMSMeterRegister.class).getMeter()));
    String meterSerialIds = arrayToCommaDelimitedString(extract(metersWithReads, on(AFMSMeter.class).getMeterSerialId())
                                                        .toArray(new String[] {})).replaceAll("\\s+", "");
    sp04FromAFMSMpan.setMeterId(meterSerialIds);

    sp04FromAFMSMpan.setSupplierFk(aSupplier.getPk());

    sp04FromAFMSMpan.setDataCollector(aAfmsMpan.getAgent().getDataCollector());
    sp04FromAFMSMpan.setDataCollectorFk(aAfmsMpan.getAgent().getPk());

    // bug#5927 - calculation of Sp04FromAFMSMpan.calculatedMeterInstallationDeadline modified to use the most recent of the 3
    // highest meter register readings DATE_RECEIVED values to calculate the MID

    //this is set to the most recent of the three highest
    //is this correct ?
    //perhaps it should be the most recent of the highest over 100 ????
    sp04FromAFMSMpan.setCalculatedMeterInstallationDeadline(aThreeHighestResults.mostRecent.getDateReceived()
                                                            .plusMonths(aNoMonthsToCalcMID));

    sp04FromAFMSMpan.setD0268SettlementDate(mHalfHourlySettlementDate.getHalfHourlySettlementDate(aAfmsMpan));

    int num = 1;
    for (AFMSMeterRegReading reading : aThreeHighestResults.threeHighestReadings)
    {
      if (num == 1)
      {
        sp04FromAFMSMpan.setMeterReadingDate1(reading.getDateReceived());
        sp04FromAFMSMpan.setMeterRegisterReading1(reading.getRegisterReading());
      }
      else if (num == 2)
      {
        sp04FromAFMSMpan.setMeterReadingDate2(reading.getDateReceived());
        sp04FromAFMSMpan.setMeterRegisterReading2(reading.getRegisterReading());
      }
      else if (num == 3)
      {
        sp04FromAFMSMpan.setMeterReadingDate3(reading.getDateReceived());
        sp04FromAFMSMpan.setMeterRegisterReading3(reading.getRegisterReading());
      }
      num++;
    }

    sp04FromAFMSMpan.setEffectiveFromDate(aAfmsMpan.getEffectiveFromDate());
    sp04FromAFMSMpan.setEffectiveToDate(aAfmsMpan.getEffectiveToDate());

    return sp04FromAFMSMpan;
  }

  private boolean existsInSp04FromAFMSMpan(MPANCore mpan)
  {
    return mSp04FromAFMSMpanDao.exists(mpan);
  }


  private void removeFromSp04FromAFMSMpan(Set<MPANCore> aMpans)
  {
    mSp04Service.deleteSp04FromAFMSMpan(aMpans);
  }


  private Supplier getTheSupplier(Long aSupplierId)
  {
    return mSupplierDao.getById(aSupplierId);
  }

  /**
   * for test injection
   * @param aSupplierDao
   */
  public void setSupplierDao(SupplierDao aSupplierDao)
  {
    mSupplierDao = aSupplierDao;

  }

  /**
   * for test injection
   * @param aAFMSMPanDao
   */
  public void setAFMSMPanDao(AFMSMpanDao aAFMSMPanDao)
  {
    mAFMSMPanDao = aAFMSMPanDao;
  }

  /**
   * for test injection
   * @param aSp04FromAFMSMpanDao
   */
  public void setSp04FromAFMSMpanDao(Sp04FromAFMSMpanDao aSp04FromAFMSMpanDao)
  {
    mSp04FromAFMSMpanDao = aSp04FromAFMSMpanDao;
  }

  /**
   * @return the Sp04 Service
   */
  public Sp04Service getSp04Service()
  {
    return mSp04Service;
  }

  /**
   * for test injection
   * @param aSp04Service
   */
  public void setSp04Service(Sp04Service aSp04Service)
  {
    mSp04Service = aSp04Service;
  }

  /**
   * for test injection
   * @param aSp04AfmsMpanValidator the sp04 afms mpan validator
   */
  public void setSp04AfmsMpanValidator(Sp04AfmsMpanValidator aSp04AfmsMpanValidator)
  {
    mSp04AfmsMpanValidator = aSp04AfmsMpanValidator;
  }

  /**
   * @param aHalfHourlySettlementDate the Half Hourly Settlement Date
   */
  public void setHalfHourlySettlementDate(HalfHourlySettlementDate aHalfHourlySettlementDate)
  {
    mHalfHourlySettlementDate = aHalfHourlySettlementDate;
  }
}
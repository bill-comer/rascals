package uk.co.utilisoft.parms.web.service.sp04;

import static uk.co.utilisoft.parms.domain.ConfigurationParameter.NAME.PARMS_SP04_FILE_LOCATION;
import static uk.co.utilisoft.parms.util.DateUtil.formatLongDate;
import static uk.co.utilisoft.parms.web.controller.WebConstants.DISPLAY_DAY_MONTH_YEAR_DATE_FORMAT;
import static uk.co.utilisoft.parms.web.controller.WebConstants.DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT;
import static uk.co.utilisoft.parms.web.controller.WebConstants.MONTH_YEAR_DATE_FORMAT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.map.HashedMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.ConfigurationParameterDao;
import uk.co.utilisoft.parms.dao.P0028ActiveDao;
import uk.co.utilisoft.parms.dao.Sp04FileDao;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Audit.TYPE;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04Calculator;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;
import uk.co.utilisoft.parms.file.sp04.Sp04FileBuilder;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;
import uk.co.utilisoft.parms.web.util.ResponseOutputStreamWriter;

/**
 * @version 1.0
 */
@Service("parms.sp04Service")
public class Sp04ServiceImpl implements Sp04Service
{
  public static final String SP04_UPDATE_REPORT_HEADER_MPAN = "MPAN";
  public static final String SP04_UPDATE_REPORT_HEADER_METER_INSTALLATION_DEADLINE = "Meter Installation Deadline";
  public static final String SP04_UPDATE_REPORT_HEADER_METER_SERIAL_ID = "Meter Serial ID";
  public static final String SP04_UPDATE_REPORT_HEADER_D0268_RECEIVED_DATE = "D0268 Rx Date";
  public static final String SP04_UPDATE_REPORT_HEADER_ERROR_REASON = "Error Reason";

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  AFMSMpanDao mAFMSMpanDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  AFMSMeterDao mAFMSMeterDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028ActiveDao")
  private P0028ActiveDao mP0028ActiveDao;


  @Autowired(required = true)
  @Qualifier("parms.sp04Calculator")
  private Sp04Calculator mSp04Calculator;


  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;


  @Autowired(required = true)
  @Qualifier("parms.sp04FileBuilder")
  private Sp04FileBuilder mSp04FileBuilder;


  @Autowired(required = true)
  @Qualifier("parms.sp04FileDao")
  private Sp04FileDao mSp04FileDao;

  @Autowired(required = true)
  @Qualifier("parms.configurationParameterDao")
  private ConfigurationParameterDao mConfigurationParameterDao;

  @Autowired(required=true)
  @Qualifier("parms.sp04FromAFMSMpanDao")
  private Sp04FromAFMSMpanDao mSp04FromAFMSMpanDao;

  /**
   * @return the ConfigurationParameterDao
   */
  public ConfigurationParameterDao getConfigurationParameterDao()
  {
    return mConfigurationParameterDao;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllSortedSupplierRecords()
   */
  @Override
  public List<AdminListDTO> getAllSortedSupplierRecords()
  {
    List<AdminListDTO> listItems = new ArrayList<AdminListDTO>();

    List<Supplier> suppliers = mSupplierDao.getAll();
    for (Supplier supplier : suppliers)
    {
      List<Object> list = new ArrayList<Object>();
      List<Object> currentObjectList = new ArrayList<Object>();

      currentObjectList.add(supplier.getSupplierId());

      list.add(supplier.getPk());
      listItems.add(new AdminListDTO(list, currentObjectList));
    }

    return listItems;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllP0028Active(java.lang.Long)
   */
  @Override
  public IterableMap<String, P0028Active> getAllP0028Active(Long aSupplierId)
  {
    Supplier supplier = getSupplier(aSupplierId);
    return mP0028ActiveDao.getAllForSupplier(supplier);
  }

  public AFMSMpan getAFMSMpan(P0028Active aP0028Active, ParmsReportingPeriod parmsReportingPeriod)
  {
    return mAFMSMpanDao.getAfmsMpan(aP0028Active.getMpanCore(), aP0028Active.getSupplier(), parmsReportingPeriod);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllSortedRecords(java.lang.Long,
   * java.util.List, uk.co.utilisoft.parms.ParmsReportingPeriod)
   */
  @ParmsAudit(auditType = TYPE.SP04_GENERATE_EXCLUDE_MPANS)
  @Override
  public List<AdminListDTO> getAllSortedRecords(Long aSupplierId, List<String> aMpansToExclude, ParmsReportingPeriod aLastMonthPrp)
  {
    MultiHashMap<String, AdminListDTO> allSp04AfmsData = getRowsFromAfmsSp04Mpans(aSupplierId, aMpansToExclude, aLastMonthPrp);
    MultiHashMap<String, AdminListDTO> allParmsData =  getAllP0028ForSp04SortedRecords(aSupplierId, aMpansToExclude, aLastMonthPrp);
    return combineTwoSourcesOfSp04Mpans(allSp04AfmsData, allParmsData);
  }

  private boolean ignoreSp04DataOfType(Sp04Data aSp04Data, Sp04FaultReasonType ... aSp04FaultReasonTypesToIgnore)
  {
    if (aSp04Data == null)
    {
      return true;
    }

    if (aSp04Data != null && aSp04Data.getSp04FaultReason() != null)
    {
      for (Sp04FaultReasonType sp04FaultReasonType : aSp04FaultReasonTypesToIgnore)
      {
        if (aSp04Data.getSp04FaultReason().equals(sp04FaultReasonType))
        {
          return true;
        }
      }

      return false;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllSortedRecords(java.lang.Long)
   */
  @Override
  public List<AdminListDTO> getAllSortedRecords(Long aSupplierId)
  {
    //PRP for previous month
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    MultiHashMap<String, AdminListDTO> allSp04AfmsData = getRowsFromAfmsSp04Mpans(aSupplierId, null, lastMonthPrp); // from PARMS_SP04_FROM_AFMSM_MPANS table
    MultiHashMap<String, AdminListDTO> allParmsData =  getAllP0028ForSp04SortedRecords(aSupplierId, null, lastMonthPrp); // from PARMS_P0028_ACTIVE table
    return combineTwoSourcesOfSp04Mpans(allSp04AfmsData, allParmsData);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#combineP0028AndAfmsMpans(
   * org.apache.commons.collections15.multimap.MultiHashMap, org.apache.commons.collections15.multimap.MultiHashMap)
   */
  public IterableMap<String, Object> combineP0028AndAfmsMpans(MultiHashMap<String, Sp04FromAFMSMpan> aAfmsMpanDatas,
                                                              MultiHashMap<String, P0028Active> aP0028MpanDatas)
  {
    IterableMap<String, Object> p28AndAfmsMpans = new HashedMap<String, Object>();

    // add all p28 active mpans
    for (P0028Active p28Mpan : aP0028MpanDatas.values())
    {
      p28AndAfmsMpans.put(p28Mpan.getMpanCore().getValue(), p28Mpan);
    }

    for (Sp04FromAFMSMpan afmsMpan : aAfmsMpanDatas.values())
    {
      // add only Sp04FromAFMSMpan records with mpan not already in list
      if (!p28AndAfmsMpans.containsKey(afmsMpan.getMpan().getValue()))
      {
        p28AndAfmsMpans.put(afmsMpan.getMpan().getValue(), aAfmsMpanDatas.get(afmsMpan.getMpan().getValue()).iterator().next());
      }
    }

    return p28AndAfmsMpans;
  }

  /**
   * Combine both Lists
   * If in both lists then both booleans in the list of values in the DTO, isParms & inAFmsDB want to be true.
   * @param allSp04AfmsData
   * @param allParmsData
   */
  List<AdminListDTO> combineTwoSourcesOfSp04Mpans(MultiHashMap<String, AdminListDTO> allSp04AfmsData, MultiHashMap<String,
                                                  AdminListDTO> allParmsData)
  {
    List<AdminListDTO> results = new ArrayList<AdminListDTO>();

    Iterator<String> parmsIt =   allParmsData.keySet().iterator();

    while (parmsIt.hasNext())
    {
      String mpan = parmsIt.next();
      Collection<AdminListDTO> fromParms = allParmsData.get(mpan);

      if (allSp04AfmsData.containsKey(mpan))
      {
        // this entry is in both lists so set AFMS flag

        // set inAfms flag TODO
        AdminListDTO adminListDTO = fromParms.iterator().next();
        if (adminListDTO != null)
        {
          adminListDTO.getValues().set(2, true);
        }
        //remove this from AFms list as it has been added from the parms list
        allSp04AfmsData.remove(mpan);
      }

      results.addAll(fromParms);

    }

    // now add all the remaining Afms list
    Collection<AdminListDTO> allAfms = allSp04AfmsData.values();
    results.addAll(allAfms);

    return results;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getEligibleMpansForP0028Active(java.lang.Long,
   * uk.co.utilisoft.parms.ParmsReportingPeriod, org.joda.time.DateTime)
   */
  @Override
  public MultiHashMap<String, P0028Active> getEligibleMpansForP0028Active(Long aSupplierId,
                                                                          ParmsReportingPeriod aLastMonthPrp,
                                                                          DateTime aCurrentDate)
  {
    if (aCurrentDate != null)
    {
      Supplier supplier = getTheSupplier(aSupplierId);
      IterableMap<String, P0028Active> allP0028ActivesForSp04Report = mP0028ActiveDao.get(supplier, aCurrentDate);
      return filterValidP0028Actives(allP0028ActivesForSp04Report, aLastMonthPrp, supplier);
    }

    return getEligibleMpansForP0028Active(aSupplierId, aLastMonthPrp);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getEligibleMpansForP0028Active(java.lang.Long,
   * uk.co.utilisoft.parms.ParmsReportingPeriod)
   */
  public MultiHashMap<String, P0028Active> getEligibleMpansForP0028Active(Long aSupplierId, ParmsReportingPeriod aLastMonthPrp)
  {
    Supplier supplier = getTheSupplier(aSupplierId);
    IterableMap<String, P0028Active> allP0028Actives = mP0028ActiveDao.getAllForSupplier(supplier);
    return filterValidP0028Actives(allP0028Actives, aLastMonthPrp, supplier);
  }

  protected MultiHashMap<String, P0028Active> filterValidP0028Actives(IterableMap<String, P0028Active> aP0028Actives,
                                                                      ParmsReportingPeriod aLastMonthPrp, Supplier aSupplier)
  {
    MultiHashMap<String, P0028Active> records = new MultiHashMap<String, P0028Active>();

    // filter for elligible p0028actives only
    mSp04Calculator.setParmsReportingPeriod(aLastMonthPrp);

    MapIterator<String, P0028Active> it = aP0028Actives.mapIterator();

    while (it.hasNext())
    {
      it.next();
      P0028Active p0028Active = it.getValue();

      //test
      AFMSMpan afmsMpan = getAFMSMpan(p0028Active, aLastMonthPrp);
      if (afmsMpan == null)
      {
        // afms mpan is not valid for this time period either cos of supplier or effective To & from dates
        continue;
      }

      Sp04Data sp04Data = null;

      // bug#5954 and 5992 - add check for a valid afms mpan currently active meter settlement date < meter installation deadline

      // bug#6038 - this check for meter.register.J0010=MD, J0474=M is not valid because a new Half hourly meter may have been added for this mpan
//      if (mSp04AfmsMpanValidator.isValidAFMSMeterForSp04inclusion(afmsMpan, p0028Active.getMeterInstallationDeadline()))
//      {
        // load meters for validation
        afmsMpan.setMeters(mAFMSMeterDao.getByMpanUniqueId(afmsMpan.getPk()));
        sp04Data = mSp04Calculator.calculate(p0028Active, aSupplier);
//      }

      if (ignoreSp04DataOfType(sp04Data, Sp04FaultReasonType.getMIDAfterMonthT()))
      {
        // do not include this MPAN as it is not ready to be included in this
        // SP04 report
        // or it is OK - perhaps as D0268 rxd in time
        continue;
      }

      boolean isSp04Valid = isSp04Valid(sp04Data);

      if (isSp04Valid)
      {
        records.put(p0028Active.getMpanCore().getValue(), p0028Active);
      }
    }

    return records;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllP0028ForSp04SortedRecords(java.lang.Long,
   * java.util.List, uk.co.utilisoft.parms.ParmsReportingPeriod)
   */
  @Override
  public MultiHashMap<String, AdminListDTO> getAllP0028ForSp04SortedRecords(Long aSupplierId, List<String> aMpansToExclude, ParmsReportingPeriod aLastMonthPrp)
  {
    MultiHashMap<String, AdminListDTO> records = new MultiHashMap<String, AdminListDTO>();
    Supplier supplier = getTheSupplier(aSupplierId);
    MultiHashMap<String, P0028Active> allP0028Actives
      = getEligibleMpansForP0028Active(aSupplierId, aLastMonthPrp, new DateMidnight().toDateTime());
    Set<String> activeP28Mpans = allP0028Actives.keySet();

    for(String activeP28Mpan : activeP28Mpans)
    {
      P0028Active p28Active = allP0028Actives.iterator(activeP28Mpan).hasNext()
        ? allP0028Actives.iterator(activeP28Mpan).next() : null;

      if (p28Active != null)
      {
        mSp04Calculator.setParmsReportingPeriod(aLastMonthPrp);
        Sp04Data sp04Data = mSp04Calculator.calculate(p28Active, supplier);
        boolean isSp04Valid = true; // always true because validated in getEligibleMpansForP0028Active(aSupplierId) above

        /*
         * Check if sp04 data is valid, and return an Array[isSpo4Valid, isCheckBoxDisabled]
         */
        // only include if box ticked and is valid sp04
        boolean[] includeInSp04Array = includeInSp04Report(aMpansToExclude, p28Active.getMpanCore().getValue(),
                                                           isSp04Valid);

        List<Object> pk = new ArrayList<Object>();
        pk.add(p28Active.getMpanCore().getValue());

        Sp04DtoData sp04DtoData = new Sp04DtoData(
          includeInSp04Array,
          p28Active.getMpanCore(),
          p28Active.getMeterInstallationDeadline(),
          p28Active.getMeterSerialId(),
          getLatestD0268ReceiptDate(p28Active),
          (hasValidSp04Data(sp04Data) ? sp04Data.getStandard1() : "No SP04 row will be created"),
          (hasValidSp04Data(sp04Data) ? sp04Data.getStandard2() : "-"),
          (hasValidSp04Data(sp04Data) ? sp04Data.getStandard3() : "-"),
          getHiddenText(p28Active, sp04Data, aLastMonthPrp)
        );

        sp04DtoData.inParms = true;
        List<Object> values = createAdminListDTO(sp04DtoData);

        records.put(p28Active.getMpanCore().getValue(), new AdminListDTO(pk, values));
      }
    }

    return records;
  }

  /**
   * Note: values array for Sp04Data
   *
   * [0]=include in sp04 report (tick|untick)
   * [1]=in P0028Active (tick|untick)
   * [2]=in afms database (tick|untick)
   * [3]=mpan
   * [4]=Meter installation deadline (dd/MM/yyyy hh:mm:ss)
   * [5]=Meter serial id
   * [6]=D0268 receipt date (dd/MM/yyyy hh:mm:ss)
   * [7]=calculated standard 1 for (P0028Active|Sp04FromAFMSMpan record)
   * [8]=calculated standard 2 for (P0028Active|Sp04FromAFMSMpan record)
   * [9]=calculated standard 3 for (P0028Active|Sp04FromAFMSMpan record)
   * [10]=additional mpan data such as is valid and meter register readings.
   *
   * @param sp04DtoData the mpan data
   * @return the table column data from the Sp04Data
   */
  List<Object> createAdminListDTO(Sp04DtoData sp04DtoData)
  {
    List<Object> values = new ArrayList<Object>();

    values.add(sp04DtoData.includeInSp04);
    values.add(sp04DtoData.inParms);
    values.add(sp04DtoData.inAfmsDb);
    values.add(sp04DtoData.mpanCore.getValue());
    values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_DATE_FORMAT,sp04DtoData.meterInstallationDeadline));
    values.add(sp04DtoData.meterSerialId);
    values.add(getLatestD0268ReceiptDate(sp04DtoData.d0268ReceiptDate));
    values.add(sp04DtoData.standard1);
    values.add(sp04DtoData.standard2);
    values.add(sp04DtoData.standard3);
    values.add(sp04DtoData.hiddenText);

    return values;
  }



  class Sp04DtoData
  {
    boolean[] includeInSp04;
    boolean inParms;
    boolean inAfmsDb;
    MPANCore mpanCore;
    DateTime meterInstallationDeadline;
    String meterSerialId;
    String d0268ReceiptDate;
    Object standard1;
    Object standard2;
    Object standard3;
    List<AdminListDTO> hiddenText;


    public Sp04DtoData(boolean[] includeInSp04, MPANCore mpanCore, DateTime meterInstallationDeadline,
                String meterSerialId, String d0268ReceiptDate, Object standard1, Object standard2, Object standard3,
                List<AdminListDTO> hiddenText)
    {
      super();
      this.includeInSp04 = includeInSp04;
      this.mpanCore = mpanCore;
      this.meterInstallationDeadline = meterInstallationDeadline;
      this.meterSerialId = meterSerialId;
      this.d0268ReceiptDate = d0268ReceiptDate;
      this.standard1 = standard1;
      this.standard2 = standard2;
      this.standard3 = standard3;
      this.hiddenText = hiddenText;
      this.inParms = false;
      this.inAfmsDb = false;
    }



  }



  private Supplier getTheSupplier(Long aSupplierId)
  {
    return mSupplierDao.getById(aSupplierId);
  }




  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllErroredRecords(java.lang.Long)
   */
  @Override
  public List<AdminListDTO> getAllErroredRecords(Long aSupplierId)
  {
    List<AdminListDTO> records = new ArrayList<AdminListDTO>();

    Supplier supplier = getTheSupplier(aSupplierId);

    // bug#5470 fix to only include only P0028Actives which are within the P0028File.receiptDate
//    IterableMap<String, P0028Active> allP0028Actives = mP0028ActiveDao.getAllForSupplier(supplier);
    IterableMap<String, P0028Active> allP0028Actives = mP0028ActiveDao.getForSupplierWithinP28UploadAndMID(supplier);

    // check p28 mpans for errors
    MapIterator<String, P0028Active> it = allP0028Actives.mapIterator();
    while (it.hasNext())
    {
      it.next();
      P0028Active p0028Active = it.getValue();
      Sp04Data sp04Data = mSp04Calculator.calculate(p0028Active, supplier);

      // bug#5744 - check for max demand matches or exceeds configured threshold
//      sp04Data = mSp04AfmsMpanValidator.isValidMaximumDemand(sp04Data, Float.valueOf(p0028Active.getMaxDemand()), getMaxDemandThreshold());
      populateSp04ExceptionData(p0028Active, records, sp04Data, null);
    }

    // bug#5744 - include afms mpans in sp04 exception report
//    List<Sp04FromAFMSMpan> allSp04Mpans = mSp04FromAFMSMpanDao.get(supplier.getPk(), new DateMidnight().toDateTime());

    // bug#6467
    DateTime currentDayStart = new DateMidnight().toDateTime();
    List<Sp04FromAFMSMpan> allSp04Mpans = mSp04FromAFMSMpanDao.getInReportingPeriod(supplier.getPk(), currentDayStart);

    // bug#6467
    // filter for only lost supply mpans
    // It has been agreed with PP that instead of reporting once, it will be continued to be reported during the first
    // month after the change of supplier but no longer after that.
    // EG it is Feb 14, and we are running the report for January.
    // If the Change of Supplier happens before 1st Jan then it will not appear in the Exception report.
    // If the COS happens on or after 1st Jan it will be in the report.
    // In Sp04ServiceImpl.populateSp04ExceptionData() need to see if the fault reason is SUPPLIER_IS_NOT_CORRECT
    // If it is then check when the supplier was changed and if it was before start of previous month then do not
    // add to Exception Report.
    Map<Sp04FromAFMSMpan, Boolean> sp04MpansIncLostSupply = includeLostSupplyMpans(allSp04Mpans, currentDayStart);


    // check afms mpans for errors
    // PRP is always for the previous month
    ParmsReportingPeriod prpLastMonth = new ParmsReportingPeriod(currentDayStart.toDateMidnight().minusMonths(1));

    Iterator<Sp04FromAFMSMpan> sp04MpansincListSupplyIter = sp04MpansIncLostSupply.keySet().iterator();

    while (sp04MpansincListSupplyIter.hasNext())
    {
      Sp04FromAFMSMpan sp04FromAfmsMpan = sp04MpansincListSupplyIter.next();

      if (sp04FromAfmsMpan != null)
      {
        Sp04Data sp04Data = mSp04Calculator.calculate(sp04FromAfmsMpan, supplier, prpLastMonth);
        boolean hasLostSupplyInReportPeriod = sp04MpansIncLostSupply.get(sp04FromAfmsMpan);
        populateSp04ExceptionData(sp04FromAfmsMpan, records, sp04Data, hasLostSupplyInReportPeriod);
      }
    }

    return records;
  }

  /**
   * @param aSp04Mpans the Sp04FromAFMSMpan records inclusive of mpans with lost supply within the reporting period
   * @param aCurrentDayStart the start of the current day
   * @return the Sp04FromAFMSMpan records with loss of supply indication
   */
  Map<Sp04FromAFMSMpan, Boolean> includeLostSupplyMpans(List<Sp04FromAFMSMpan> aSp04Mpans, DateTime aCurrentDayStart)
  {
    Map<Sp04FromAFMSMpan, Boolean> sp04MpansIncLostSupply = new HashMap<Sp04FromAFMSMpan, Boolean>();
    ParmsReportingPeriod currentReportTime = new ParmsReportingPeriod(aCurrentDayStart.toDateMidnight());
    DateTime reportMid = currentReportTime.getStartOfMonth(false).toDateTime();
    DateTime reportStartDay = currentReportTime.getStartOfMonth(false).minusMonths(1);
    DateTime reportEndDay = currentReportTime.getEndOfMonth(false).minusMonths(1).minusDays(1);

    for (Sp04FromAFMSMpan sp04Mpan : aSp04Mpans)
    {
      if (sp04Mpan.getCalculatedMeterInstallationDeadline() != null)
      {
        // check is a loss of supply within report period, and report in sp04 exception
        if (hasLostSupply(sp04Mpan, reportStartDay, reportEndDay))
        {
          sp04MpansIncLostSupply.put(sp04Mpan, true);
          continue;
        }

        if (sp04Mpan.getCalculatedMeterInstallationDeadline().isBefore(aCurrentDayStart)
            || sp04Mpan.getCalculatedMeterInstallationDeadline().equals(aCurrentDayStart))
        {
          if (sp04Mpan.getCalculatedMeterInstallationDeadline().isBefore(reportMid))
          {
            // include as mpan calculated mid is within the reporting period
            sp04MpansIncLostSupply.put(sp04Mpan, false);
            continue;
          }
        }
      }
    }

    return sp04MpansIncLostSupply;
  }

  /**
   * @param aSp04Mpan the Sp04FromAFMSMpan
   * @param aReportPeriodStartDay the report period start
   * @param aReportPeriodEndDay the report period end
   * @return true if the afmsMpan has lost supply within the reporting period
   */
  boolean hasLostSupply(Sp04FromAFMSMpan aSp04Mpan, DateTime aReportPeriodStartDay, DateTime aReportPeriodEndDay)
  {
    if (aSp04Mpan != null)
    {
      AFMSMpan afmsMpan = mAFMSMpanDao.getById(aSp04Mpan.getMpanFk());

      if (afmsMpan != null)
      {
        DateTime supplierEtd = afmsMpan.getSupplierEffectiveToDate();

        // has mpan lost supply
        if (supplierEtd != null)
        {
          // has mpan lost supply within reporting period
          if (supplierEtd.equals(aReportPeriodStartDay)
              || supplierEtd.isAfter(aReportPeriodStartDay))
          {
            if (supplierEtd.isBefore(aReportPeriodEndDay.plusDays(1)))
            {
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  /**
   * @param aMpanObject the AFMS Mpan object. Can be aP0028Active or Sp04FromAFMSMpan object
   * @param aAdminListDTOs the Collection of AdminListDTO for display
   * @param aSp04Data the validated AFMS Mpan data for a P0028Active or Sp04FromAFMSMpan object
   * @param aHasLostSupplyInReportPeriod true if lost supply within report period
   */
  void populateSp04ExceptionData(Object aMpanObject, List<AdminListDTO> aAdminListDTOs, Sp04Data aSp04Data,
                                 Boolean aHasLostSupplyInReportPeriod)
  {
    List<AdminListDTO> records = aAdminListDTOs;

    if (aAdminListDTOs != null)
    {
      boolean isSp04Valid = isSp04Valid(aSp04Data);

      List<Object> values = null;
      List<Object> pk = null;

      if (aSp04Data != null)
      {
        // bug#6467 - exclude sp04 mpans from exception report which have lost supply outside of the reporting period
        if (aSp04Data.getSp04FaultReason() != null)
        {
          if (aSp04Data.getSp04FaultReason().equals(Sp04FaultReasonType.getSupplierIsNotCorrect()))
          {
            if (aHasLostSupplyInReportPeriod != null)
            {
              if (!aHasLostSupplyInReportPeriod)
              {
                isSp04Valid = true;
              }
            }
          }
        }

        if (!isSp04Valid)
        {
          //invalid sp04

          if (aMpanObject != null)
          {
            if (aMpanObject instanceof P0028Active)
            {
              P0028Active p0028Active = (P0028Active) aMpanObject;
              values = new ArrayList<Object>();
              pk = new ArrayList<Object>();
              values.add(p0028Active.getMpanCore().getValue());
              values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT,
                  p0028Active.getMeterInstallationDeadline()));
              values.add(p0028Active.getMeterSerialId());
              values.add(getLatestD0268ReceiptDate(p0028Active));
              pk.add(p0028Active.getMpanCore().getValue());
            }
            else if (aMpanObject instanceof Sp04FromAFMSMpan)
            {
              Sp04FromAFMSMpan sp04FromAfmsMpan = (Sp04FromAFMSMpan) aMpanObject;
              values = new ArrayList<Object>();
              pk = new ArrayList<Object>();
              values.add(sp04FromAfmsMpan.getMpan().getValue());
              values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT,
                sp04FromAfmsMpan.getCalculatedMeterInstallationDeadline()));
              values.add(sp04FromAfmsMpan.getMeterId());

              // bug#6582 - cast from datetime to formatted date time issue
              values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT,
                                        sp04FromAfmsMpan.getD0268SettlementDate()));

              pk.add(sp04FromAfmsMpan.getMpan().getValue());
            }

            // bug#5744 - add maximum demand data to sp04 information
            if (aSp04Data.getSp04FaultReason() != null)
            {
              if (aSp04Data.getSp04FaultReason().equals(Sp04FaultReasonType.getMDMatchesOrExceedsThreshold()))
              {
                String mdThresholdConfigured = aSp04Data.getMaxDemandThreshold() != null
                  ? new BigDecimal(aSp04Data.getMaxDemandThreshold()).stripTrailingZeros().toPlainString() : "";
                values.add(aSp04Data.getSp04FaultReason().getDescription() + mdThresholdConfigured + " Please Investigate further");
              }
              else if (aSp04Data.getSp04FaultReason().equals(Sp04FaultReasonType.getMDLessThanOneHundred()))
              {
                values.add(aSp04Data.getSp04FaultReason().getDescription() + aSp04Data.getMaxDemandThreshold());
              }
              else
              {
                values.add(aSp04Data.getSp04FaultReason().getDescription());
              }
            }

            records.add(new AdminListDTO(pk, values));
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#downloadSp04ErrorReport(java.lang.Long)
   */
  @Override
  public String downloadSp04ErrorReport(Long aSupplierId, HttpServletResponse aResponse) throws Exception
  {
    DateTime now = new DateTime();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd.HHmmss");
    String filename =  "sp04UpdateReport." + now.toString(fmt) + ".csv";

    StringBuffer data = new StringBuffer();

    //header
    data.append(SP04_UPDATE_REPORT_HEADER_MPAN).append(',')
      .append(SP04_UPDATE_REPORT_HEADER_METER_INSTALLATION_DEADLINE)
      .append(',')
      .append(SP04_UPDATE_REPORT_HEADER_METER_SERIAL_ID)
      .append(',')
      .append(SP04_UPDATE_REPORT_HEADER_D0268_RECEIVED_DATE)
      .append(',')
      .append(SP04_UPDATE_REPORT_HEADER_ERROR_REASON)
      .append('\n');

    List<AdminListDTO> allRecords = getAllErroredRecords(aSupplierId);
    for (AdminListDTO record : allRecords)
    {
      List<Object> values = record.getValues();
      boolean first = true;
      for (Object value : values)
      {
        if (!first)
        {
          data.append(",");
        }

        // bug#6582 - cast from datetime to formatted date time issue
        String sValue = value instanceof String ? (String) value : value.toString();

        data.append(sValue);
        first = false;
      }

      data.append("\n");
    }

    ResponseOutputStreamWriter writer = new ResponseOutputStreamWriter();
    writer.writeCsvFileToResponseOutputStream(aResponse, data.toString(), filename);

    return filename;
  }

  /**
   * @return the ConfigurationParameter enum name
   */
  public ConfigurationParameter.NAME getConfigParamName()
  {
    return PARMS_SP04_FILE_LOCATION;
  }

  /**
   * @param sp04Data the Sp04Data
   * @return true if the Sp04Data is valid; otherwise false
   */
  boolean isSp04Valid(Sp04Data sp04Data)
  {
    if (sp04Data == null)
    {
      return false;
    }

    if (sp04Data.getMpanCore() == null || sp04Data.getGspGroupId().equals("err")
        ||  sp04Data.getStandard1() == null || sp04Data.getStandard2() == null || sp04Data.getStandard3() == null)
    {
      return false;
    }

    if (sp04Data.getSp04FaultReason() != null)
    {
      return false;
    }

    return true;
  }

  private List<AdminListDTO> getHiddenText(P0028Active p0028Active, Sp04Data sp04Data, ParmsReportingPeriod aLastMonthPrp)
  {
    List<AdminListDTO> hiddenText = new ArrayList<AdminListDTO>();
    List<Object> values = new ArrayList<Object>();
    List<Object> pk = new ArrayList<Object>();
    pk.add(p0028Active.getPk());

    if (sp04Data != null && sp04Data.getSp04FaultReason() != null)
    {
      values.add(sp04Data.getSp04FaultReason().getDescription());
    }
    else
    {
      // bug#6334 - meter for a meter_serial_id
      AFMSMpan afmsMpan = getAFMSMpan(p0028Active, aLastMonthPrp);
      AFMSMeter meter = mAFMSMeterDao.getLatestMeterForMeterSerialIdAndMpanUniqId(p0028Active.getMeterSerialId(), afmsMpan.getPk());

      values.add("Meter Serial ID:" + meter.getMeterSerialId());

      // bug#5682 - change to afmsmeter - afmsmeterregister jpa mappings relationship.
      // Note: ONLY get register readings for an afmsmeterregister which has exceeded maximum demand (J0010=MD)
      AFMSMeterRegister meterRegister = meter.getMaxDemandMeterRegister();

      Collection<AFMSMeterRegReading> readings = meterRegister != null ? meterRegister.getMeterRegReadings() : null;

      if (readings != null)
      {
        for (AFMSMeterRegReading reading : readings)
        {
          //Only display validated readings
          if (reading.isReadingBscValidated())
          {
            values.add("Reading Date:"
                        + formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, reading.getMeterReadingDate())
                        + ", Value:" + reading.getRegisterReading());
          }
        }
      }

      if (readings == null || readings.size() == 0)
      {
        values.add("No Meter readings available");
      }

    }
    hiddenText.add(new AdminListDTO(pk, values));
    return hiddenText;
  }

  /*
   * @param aMeterInstallationDeadline the Meter Installation Deadline
   * @param sp04Data the sp04Data
   * @param aSupplier the supplier
   * @param aReportingPeriod the ParmsReportingPeriod
   * @return the AdmintListDTOs
   */
  private List<AdminListDTO> getHiddenText(Sp04FromAFMSMpan aSp04FromAfmsMpan, Sp04Data sp04Data, Supplier aSupplier,
                                           ParmsReportingPeriod aReportingPeriod)
  {
    List<AdminListDTO> hiddenText = new ArrayList<AdminListDTO>();
    List<Object> values = new ArrayList<Object>();
    List<Object> pk = new ArrayList<Object>();
    pk.add(aSp04FromAfmsMpan.getPk());

    if (sp04Data != null && sp04Data.getSp04FaultReason() != null)
    {
      values.add(sp04Data.getSp04FaultReason().getDescription());
    }
    else
    {
      // bug#6334 - meter for a meter_serial_id
      AFMSMeter meter = mAFMSMeterDao.getLatestMeterForMeterSerialIdAndMpanUniqId(aSp04FromAfmsMpan.getMeterId(), aSp04FromAfmsMpan.getMpanFk());

      // bug5810 - fix null pointer for case where sp04fromafmsmpan record's afmsmpan is not valid for a parms reporting period
      if (meter != null)
      {
        values.add("Meter Serial ID:" + meter.getMeterSerialId());

        // bug#5682 - change to afmsmeter - afmsmeterregister jpa mappings relationship.
        // Note: ONLY get register readings for an afmsmeterregister which has exceeded maximum demand (J0010=MD)
        AFMSMeterRegister meterRegister = meter.getMaxDemandMeterRegister();

        Collection<AFMSMeterRegReading> readings = meterRegister != null ? meterRegister.getMeterRegReadings() : null;

        if (readings != null)
        {
          for (AFMSMeterRegReading reading : readings)
          {
            //Only display validated readings
            if (reading.isReadingBscValidated())
            {
              values.add("Reading Date:"
                          + formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, reading.getMeterReadingDate())
                          + ", Value:" + reading.getRegisterReading());
            }
          }
        }

        if (readings == null || readings.size() == 0)
        {
          values.add("No Meter readings available");
        }

      }
    }
    hiddenText.add(new AdminListDTO(pk, values));
    return hiddenText;
  }

  private boolean hasValidSp04Data(Sp04Data data)
  {
    return data != null && data.getSp04FaultReason() == null;
  }

  private String getLatestD0268ReceiptDate(P0028Active p0028Active)
  {
    DateTime settlementDate = mSp04Calculator.getSettlementDate(p0028Active);
    if (settlementDate == null)
    {
      return "NOT Rxd";
    }
    else
    {
      return formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, settlementDate);
    }
  }

  private String getLatestD0268ReceiptDate(String d0268ReceiptDate)
  {
    if (StringUtils.isEmpty(d0268ReceiptDate))
    {
      return "NOT Rxd";
    }
    else
    {
      return d0268ReceiptDate;
    }
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getSupplier(java.lang.Long)
   */
  @Override
  public Supplier getSupplier(Long aSupplierId)
  {
    return getTheSupplier(aSupplierId);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#buildFile(uk.co.utilisoft.parms.domain.Supplier,
   * org.apache.commons.collections15.IterableMap)
   */
  @Override
  @ParmsAudit(auditType = TYPE.SP04_GENERATE)
  public Long buildFile(Supplier aSupplier,
      IterableMap<String, Object> aActiveMapToUse)
  {
    return mSp04FileBuilder.buildFile(aSupplier, aActiveMapToUse);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getAllSp04Records()
   */
  @Override
  public List<AdminListDTO> getAllSp04Records()
  {
    List<AdminListDTO> listItems = new ArrayList<AdminListDTO>();

    List<Sp04File> allSp04s = mSp04FileDao.getAll();

    for (Sp04File sp04File : allSp04s)
    {
      List<Object> list = new ArrayList<Object>();
      List<Object> values = new ArrayList<Object>();

      values.add(sp04File.getSupplier().getSupplierId());
      values.add(sp04File.getFilename());
      values.add(formatLongDate(MONTH_YEAR_DATE_FORMAT, sp04File.getReportingPeriod().getStartOfMonth(true)));
      values.add(formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, sp04File.getDateCreated()));


      list.add(sp04File.getPk());
      listItems.add(new AdminListDTO(list, values));
    }

    return listItems;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getSp04File(java.lang.Long)
   */
  @Override
  public Sp04File getSp04File(Long aFilePk)
  {
    return mSp04FileDao.getById(aFilePk);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#downloadSp04FileReport(java.lang.Long)
   */
  @Override
  @ParmsAudit(auditType = TYPE.SP04_DOWNLOAD_REPORTS)
  public String downloadSp04FileReport(Long aSp04FilePk, HttpServletResponse aResponse, boolean aDownloadAsCsv) throws Exception
  {

    Sp04File sp04File = mSp04FileDao.getById(aSp04FilePk);
    String sp04FileDataRec = StringUtils.isBlank(sp04File.getData()) ? null : sp04File.getData();
    String filename =  sp04File.getFilename();

    if (aDownloadAsCsv)
    {
      sp04FileDataRec = sp04FileDataRec.replace("|", ",");
      filename += ".csv";
    }

    ResponseOutputStreamWriter writer = new ResponseOutputStreamWriter();
    writer.writePlainFileToResponseOutputStream(aResponse, sp04FileDataRec, filename);

    return filename;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getEligibleMpansForSp04FromAfmsMpans(java.lang.Long,
   * org.joda.time.DateTime)
   */
  @Override
  public MultiHashMap<String, Sp04FromAFMSMpan> getEligibleMpansForSp04FromAfmsMpans(Long aSupplierId,
                                                                                     DateTime aCurrentDate)
  {
    if (aCurrentDate != null)
    {
      List<Sp04FromAFMSMpan> allForSp04Report = mSp04FromAFMSMpanDao.get(aSupplierId, aCurrentDate);

      // PRP is always for the previous month
      ParmsReportingPeriod prpLastMonth = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));

      return getValidForSp04Reporting(allForSp04Report, aSupplierId, prpLastMonth);
    }

    return getEligibleMpansForSp04FromAfmsMpans(aSupplierId);
  }

  private MultiHashMap<String, Sp04FromAFMSMpan> getValidForSp04Reporting(List<Sp04FromAFMSMpan> aSp04AfmsMpans, Long aSupplierId, ParmsReportingPeriod aLastMonthPrp)
  {
    MultiHashMap<String, Sp04FromAFMSMpan> validAfmsMpansForSp04Reporting = new MultiHashMap<String, Sp04FromAFMSMpan>();

    for (Sp04FromAFMSMpan sp04AfmsMpan : aSp04AfmsMpans)
    {
      Sp04Data sp04AfmsData = mSp04Calculator.calculate(sp04AfmsMpan, mSupplierDao.getById(aSupplierId), aLastMonthPrp);

      if (sp04AfmsData != null && sp04AfmsData.getSp04FaultReason() == null)
      {
        validAfmsMpansForSp04Reporting.put(sp04AfmsMpan.getMpan().getValue(), sp04AfmsMpan);
      }
    }

    return validAfmsMpansForSp04Reporting;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getEligibleMpansForSp04FromAfmsMpans(java.lang.Long)
   */
  public MultiHashMap<String, Sp04FromAFMSMpan> getEligibleMpansForSp04FromAfmsMpans(Long aSupplierId)
  {
    // note: currently PARMS_SP04_FROM_AFMS_MPAN records written represent elligible afms mpans
    List<Sp04FromAFMSMpan> allForSupplier = mSp04FromAFMSMpanDao.getAll(aSupplierId);

    // PRP is always for the previous month
    ParmsReportingPeriod prpLastMonth = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));

    return getValidForSp04Reporting(allForSupplier, aSupplierId, prpLastMonth);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getRowsFromAfmsSp04Mpans(java.lang.Long, java.util.List, uk.co.utilisoft.parms.ParmsReportingPeriod)
   */
  @Override
  public MultiHashMap<String, AdminListDTO> getRowsFromAfmsSp04Mpans(Long aSupplierId, List<String> aMpansToExclude, ParmsReportingPeriod aLastMonthPrp)
  {
    MultiHashMap<String, AdminListDTO> records = new MultiHashMap<String, AdminListDTO>();

    MultiHashMap<String, Sp04FromAFMSMpan> allForSupplier
      = getEligibleMpansForSp04FromAfmsMpans(aSupplierId, new DateMidnight().toDateTime());
    Set<String> activeAfmsMpans = allForSupplier.keySet();

    for (String activeAfmsMpan : activeAfmsMpans)
    {
      Sp04FromAFMSMpan sp04FromAfmsMpan = allForSupplier.iterator(activeAfmsMpan).hasNext()
        ? allForSupplier.iterator(activeAfmsMpan).next() : null;

        // all Sp04FromAFMSMpan records for a supplier are assumed to be valid
        // as the have been filtered in getEligibleMpansForSp04FromAfmsMpans(...)
        boolean sp04IsValid = true;

      Sp04DtoData sp04DtoData = sp04FromAfmsMpan != null ? buildSp04AfmsDtoDataRecord(sp04FromAfmsMpan,
        sp04IsValid, aMpansToExclude, mSupplierDao.getById(aSupplierId), aLastMonthPrp) : null;

      // manually add in trunk bug#2439 - do not show mpans on sp04 elligible screen that are not to be considered for sp04 generation
      if (sp04IsValid && sp04DtoData != null)
      {
        List<Object> pk = new ArrayList<Object>();
        pk.add(sp04FromAfmsMpan.getMpan().getValue());
        sp04DtoData.inAfmsDb = true;
        List<Object> values = createAdminListDTO(sp04DtoData);

        records.put(sp04FromAfmsMpan.getMpan().getValue(), new AdminListDTO(pk, values));
      }
    }

    return records;
  }

  // bug#5746 - add calculated standards from Sp04FromAfmsMpan records
  private Sp04Data getCalculatedStandards(Sp04FromAFMSMpan aSp04FromAfmsMpan, Sp04Data aSp04DataRecord)
  {
    Sp04Data sp04Data = aSp04DataRecord != null ? aSp04DataRecord : new Sp04Data();

    if (aSp04FromAfmsMpan != null)
    {
      sp04Data.setStandard1(aSp04FromAfmsMpan.getCalculatedStandard1());
      sp04Data.setStandard2(aSp04FromAfmsMpan.getCalculatedStandard2());
      sp04Data.setStandard3(aSp04FromAfmsMpan.getCalculatedStandard3());
    }

    return sp04Data;
  }

  /*
   * Check if sp04 data is valid, and return an Array[isSpo4Valid, isCheckBoxDisabled]
   */
  private boolean[] includeInSp04Report(List<String> aMpansToIgnore, String aMpan, boolean aIsSp04Valid)
  {
    if (aIsSp04Valid && aMpansToIgnore != null)
    {
      if (aMpansToIgnore.contains(aMpan))
      {
        return new boolean[] { false, false };
      }

      return new boolean[] { aIsSp04Valid, false };
    }

    if (aIsSp04Valid)
    {
      return new boolean[] { aIsSp04Valid, false };
    }
    else
    {
      return new boolean[] { aIsSp04Valid, true };
    }
  }

  /**
   * It would appear more logical to have this method purely in Sp04FromAFMSMpanDao.
   * However this causes the Exception Caused by: java.lang.IllegalStateException: Unable to locate bridged method for bridge method...
   * in BridgeMethodResolver.
   * See http://forum.springsource.org/showthread.php?37218-BridgeMethodResolver-Unable-to-locate-bridged-method
   * This bug is not fixed.
   */
  @Override
  @ParmsAudit(auditType = TYPE.SP04_AFMS_DATA_REMOVE_MPAN)
  public void deleteSp04FromAFMSMpan(Set<MPANCore> aMpanToExcludeFromDeletion)
  {
    mSp04FromAFMSMpanDao.delete(aMpanToExcludeFromDeletion);
  }

  /**
   * It would appear more logical to have this method purely in Sp04FromAFMSMpanDao.
   * However this causes the Exception Caused by: java.lang.IllegalStateException: Unable to locate bridged method for bridge method...
   * in BridgeMethodResolver.
   * See http://forum.springsource.org/showthread.php?37218-BridgeMethodResolver-Unable-to-locate-bridged-method
   * This bug is not fixed.
   */
  @Override
  @ParmsAudit(auditType = TYPE.SP04_AFMS_DATA_ADD_MPAN)
  public Sp04FromAFMSMpan saveSp04FromAFMSMpan(Sp04FromAFMSMpan sp04FromAFMSMpan)
  {
    return mSp04FromAFMSMpanDao.makePersistent(sp04FromAFMSMpan);
  }

  /**
   * @param aSp04FromAfmsMpan the Sp04FromAFMSMpan
   * @param isSp04Valid true if the Sp04FromAFMSMpan is considered valid
   * @param aMpansToExclude the Mpans to exclude
   * @param aSupplier
   * @param aLastMonthPrp
   * @return the Sp04DtoData records
   */
  protected Sp04DtoData buildSp04AfmsDtoDataRecord(Sp04FromAFMSMpan aSp04FromAfmsMpan, boolean isSp04Valid,
                                                   List<String> aMpansToExclude, Supplier aSupplier,
                                                   ParmsReportingPeriod aLastMonthPrp)
  {
    // manually add in trunk bug#2439 - do not show mpans on sp04 elligible screen that are not to be considered for sp04 generation
    if (isSp04Valid)
    {
      // bug#5746 - add calculated standards from Sp04FromAfmsMpan records
      Sp04Data sp04Data = getCalculatedStandards(aSp04FromAfmsMpan, null);

      if (sp04Data != null)
      {
        /*
         * Check if sp04 data is valid, and return an Array[isSpo4Valid, isCheckBoxDisabled]
         */
        // only include if box ticked and is valid sp04
        boolean[] includeInSp04Array
          = includeInSp04Report(aMpansToExclude, aSp04FromAfmsMpan.getMpan().getValue(), isSp04Valid);

        Sp04DtoData sp04DtoData = new Sp04DtoData(
          includeInSp04Array,
          aSp04FromAfmsMpan.getMpan(),
          aSp04FromAfmsMpan.getCalculatedMeterInstallationDeadline(),
          aSp04FromAfmsMpan.getMeterId(),
          formatLongDate(DISPLAY_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND_DATE_FORMAT, aSp04FromAfmsMpan.getD0268SettlementDate()),
          (true ? sp04Data.getStandard1() : "SP04 Calc for rows from AFMS NOT DONE YET-ask Bill"),
          (true ? sp04Data.getStandard2() : "-"),
          (true ? sp04Data.getStandard3() : "-"),
          getHiddenText(aSp04FromAfmsMpan, sp04Data, aSupplier, aLastMonthPrp)
        );

        sp04DtoData.inAfmsDb = true;
        return sp04DtoData;
      }
    }

    return null;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.service.sp04.Sp04Service#getCombinedSortedRecords(java.lang.Long)
   */
  @Override
  public IterableMap<String, AdminListDTO> getCombinedSortedRecords(Long aSupplierId)
  {
    IterableMap<String, AdminListDTO> sortedRecords = new HashedMap<String, AdminListDTO>();
    List<AdminListDTO> records = getAllSortedRecords(aSupplierId);

    for (AdminListDTO record : records)
    {
      // position of mpan value in AdminListDTO
      int mpanIdx = 3;
      String mpan = record.getValues().get(mpanIdx) instanceof String ? ((String) record.getValues().get(mpanIdx)) : null;

      if (mpan != null)
      {
        sortedRecords.put(mpan, record);
      }
    }

    return sortedRecords;
  }

  /**
   * For tests.
   *
   * @param aSupplierDao the supplier dao
   */
  void setSupplierDao(SupplierDao aSupplierDao)
  {
    mSupplierDao = aSupplierDao;
  }

  /**
   * For tests.
   *
   * @param aP0028ActiveDao the p0028 active dao
   */
  void setP0028ActiveDao(P0028ActiveDao aP0028ActiveDao)
  {
    mP0028ActiveDao = aP0028ActiveDao;
  }

  /**
   * @param aSp04Calculator the sp04 calculator
   */
  public void setSp04Calculator(Sp04Calculator aSp04Calculator)
  {
    mSp04Calculator = aSp04Calculator;
  }

  /**
   * @param aSp04FromAFMSMpanDao the Sp04FromAFMSMpanDao
   */
  public void setSp04FromAFMSMpanDao(Sp04FromAFMSMpanDao aSp04FromAFMSMpanDao)
  {
    mSp04FromAFMSMpanDao = aSp04FromAFMSMpanDao;
  }

  /**
   * @param aAFMSMpanDao the AFMSMpanDao
   */
  public void setAFMSMpanDao(AFMSMpanDao aAFMSMpanDao)
  {
    mAFMSMpanDao = aAFMSMpanDao;
  }

  /**
   * @param aAFMSMeterDao the AFMSMeterDao
   */
  public void setAFMSMeterDao(AFMSMeterDao aAFMSMeterDao)
  {
    mAFMSMeterDao = aAFMSMeterDao;
  }
}
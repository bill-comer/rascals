package uk.co.utilisoft.parms.file.sp04;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.afms.dao.AFMSMeterDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;


@Service("parms.sp04Calculator")
public class Sp04Calculator
{
  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  AFMSMpanDao mAFMSMpanDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  AFMSMeterDao mAFMSMeterDao;

  @Autowired(required=true)
  @Qualifier("parms.sp04RowCalculator")
  Sp04RowCalculator mSp04RowCalculator;

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
   * Notes: bug#5835 - added to calculate single Sp04Data for a give P0028Active record, supplier, sp04 file,
   * and Sp04Data list. calculate(Sp04File sp04File, IterableMap<String, P0028Active> aActiveList, Supplier aSupplier)
   * method now uses
   *
   * @param sp04File the Sp04 File
   * @param aActiveP0028 the currently active P0028
   * @param aSupplier the supplier
   * @param aSp04Datas the Sp04Data records
   */
  void calculate(Sp04File sp04File, P0028Active aActiveP0028, Supplier aSupplier, List<Sp04Data> aSp04Datas)
  {
    mParmsReportingPeriod = sp04File.getReportingPeriod();
    Sp04Data data = calculate(aActiveP0028, aSupplier);
    if (hasNoValidSp04Data(data))
    {
      data.setSp04File(sp04File);
      aSp04Datas.add(data);
    }
  }

  /**
   * @param aSp04File the Sp04 File
   * @param aActiveList the currently active P0028Active records
   * @param aSupplier the supplier
   * @return the Sp04Data records
   */
  List<Sp04Data> calculate(Sp04File aSp04File, IterableMap<String, P0028Active> aActiveList, Supplier aSupplier)
  {
    mParmsReportingPeriod = aSp04File.getReportingPeriod();

    List<Sp04Data> allSp04Data = new ArrayList<Sp04Data>();

    MapIterator<String, P0028Active> it = aActiveList.mapIterator();

    while (it.hasNext())
    {
      it.next();
      calculate(aSp04File, it.getValue(), aSupplier, allSp04Data);
    }

    return allSp04Data;
  }

  private boolean hasNoValidSp04Data(Sp04Data data)
  {
    return data != null && data.getSp04FaultReason() == null;
  }

  public Sp04Data calculate(P0028Active aP0028Active, Supplier aSupplier)
  {
    AFMSMpan afmsMpan = mAFMSMpanDao.getAfmsMpan(aP0028Active.getMpanCore(), aP0028Active.getSupplier(),
                                                 getParmsReportingPeriod());

    return mSp04RowCalculator.buildP0028Sp04DataRecord(getParmsReportingPeriod(), afmsMpan, aSupplier,
      new Float(aP0028Active.getMaxDemand()), aP0028Active.getMeterInstallationDeadline(), aP0028Active.getMeterSerialId());
  }

  /**
   * @param aSp04AfmsMpan the Sp04FromAfmsMpan record
   * @param aSupplier the supplier
   * @param aLastMonthPrp the reporting period for previous month
   * @return the Sp04Data record generated for an Afms mpan considered for sp04 reporting
   */
  public Sp04Data calculate(Sp04FromAFMSMpan aSp04AfmsMpan, Supplier aSupplier, ParmsReportingPeriod aLastMonthPrp)
  {
    return mSp04RowCalculator.buildAfmsSp04DataRecord(aSp04AfmsMpan, aLastMonthPrp, aSupplier);
  }

  public DateTime getSettlementDate(P0028Active aP0028Active)
  {
    return mSp04RowCalculator.getSettlementDate(aP0028Active);
  }

  /**
   * @param aSp04RowCalculator the sp04 row calculator
   */
  public void setSp04RowCalculator(Sp04RowCalculator aSp04RowCalculator)
  {
    mSp04RowCalculator = aSp04RowCalculator;
  }

  /**
   * @param aAFMSMpanDao the AFMS Mpan Dao
   */
  public void setAFMSMpanDao(AFMSMpanDao aAFMSMpanDao)
  {
    mAFMSMpanDao = aAFMSMpanDao;
  }
}

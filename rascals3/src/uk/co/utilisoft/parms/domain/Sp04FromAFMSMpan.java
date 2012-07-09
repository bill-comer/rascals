
package uk.co.utilisoft.parms.domain;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@AccessType(value="property")
@Table(name = "PARMS_SP04_FROM_AFMS_MPANS")
@SuppressWarnings("serial")
public class Sp04FromAFMSMpan extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private MPANCore mMpan;
  private Long mMpanFk;
  private Long mSupplierFk;
  private String mMeterId;
  private String mMeterRegisterFks;
  private String mDataCollector;
  private Long mDataCollectorFk;
  private DateTime mCalculatedMeterInstallationDeadline;
  private DateTime mD0268SettlementDate;
  private Float mMeterRegisterReading1;
  private Float mMeterRegisterReading2;
  private Float mMeterRegisterReading3;
  private DateTime mMeterReadingDate1;
  private DateTime mMeterReadingDate2;
  private DateTime mMeterReadingDate3;
  private Long mCalculatedStandard1;
  private Long mCalculatedStandard2;
  private Float mCalculatedStandard3;
  private DateTime mEffectiveFromDate;
  private DateTime mEffectiveToDate;
  private Float mMaxDemand;

  /**
   * @return the Mpan.
   */
  @Column(name = "MPAN")
  @Type(type="uk.co.utilisoft.parms.MPANCore")
  @NotNull
  public MPANCore getMpan()
  {
    return mMpan;
  }

  /**
   * @param aMpan The Mpan to set.
   */
  public void setMpan(MPANCore aMpan)
  {
    mMpan = aMpan;
  }

  /**
   * @return the MpanFk.
   */
  @Column(name = "MPAN_FK", nullable = false)
  @NotNull
  public Long getMpanFk()
  {
    return mMpanFk;
  }

  /**
   * @param aMpanFk The MpanFk to set.
   */
  public void setMpanFk(Long aMpanFk)
  {
    mMpanFk = aMpanFk;
  }


  /**
   * @return the SupplierFk.
   */
  @Column(name = "SUPPLIER_FK", nullable = false)
  @NotNull
  public Long getSupplierFk()
  {
    return mSupplierFk;
  }

  /**
   * @param aSupplierFk The SupplierFk to set.
   */
  public void setSupplierFk(Long aSupplierFk)
  {
    mSupplierFk = aSupplierFk;
  }

  /**
   * @return the MeterId.
   */
  @Column(name="METER_ID", nullable = false) // J0004
  @NotEmpty
  @Length (max = 255)
  public String getMeterId()
  {
    return mMeterId;
  }

  /**
   * @param aMeterId The MeterId to set.
   */
  public void setMeterId(String aMeterId)
  {
    mMeterId = aMeterId;
  }

  /**
   * @return the MeterRegisterFks.
   */
  @Column(name = "METER_REGISTER_FKS", nullable = false) //METER_REG_PK(S)
  @NotEmpty
  @Length (max = 255)
  public String getMeterRegisterFks()
  {
    return mMeterRegisterFks;
  }

  /**
   * @param aMeterRegisterFks The MeterRegisterFks to set.
   */
  public void setMeterRegisterFks(String aMeterRegisterFks)
  {
    mMeterRegisterFks = aMeterRegisterFks;
  }

  /**
   * @return the DataCollector.
   */
  @Column(name = "DC", nullable = false) // J0205
  @NotNull
  public String getDataCollector()
  {
    return mDataCollector;
  }

  /**
   * @param aDataCollector The DataCollector to set.
   */
  public void setDataCollector(String aDataCollector)
  {
    mDataCollector = aDataCollector;
  }

  /**
   * @return the DataCollectorFk.
   */
  @Column(name = "DC_FK", nullable = false)
  @NotNull
  public Long getDataCollectorFk()
  {
    return mDataCollectorFk;
  }

  /**
   * @param aDataCollectorFk The DataCollectorFk to set.
   */
  public void setDataCollectorFk(Long aDataCollectorFk)
  {
    mDataCollectorFk = aDataCollectorFk;
  }

  /**
   * @return the CalculatedMeterInstallationDeadline.
   */
  @Column(name = "CALCULATED_MID", nullable = false)
  @NotNull
  public DateTime getCalculatedMeterInstallationDeadline()
  {
    return mCalculatedMeterInstallationDeadline;
  }

  /**
   * @param aCalculatedMeterInstallationDeadline The CalculatedMeterInstallationDeadline to set.
   */
  public void setCalculatedMeterInstallationDeadline(DateTime aCalculatedMeterInstallationDeadline)
  {
    mCalculatedMeterInstallationDeadline = aCalculatedMeterInstallationDeadline;
  }

  /**
   * @return the D0268SettlementDate.
   */
  public DateTime getD0268SettlementDate()
  {
    return mD0268SettlementDate;
  }

  /**
   * @param aD0268SettlementDate The D0268SettlementDate to set.
   */
  public void setD0268SettlementDate(DateTime aD0268SettlementDate)
  {
    mD0268SettlementDate = aD0268SettlementDate;
  }

  /**
   * @return the MeterRegisterReading1.
   */
  public Float getMeterRegisterReading1()
  {
    return mMeterRegisterReading1;
  }

  /**
   * @param aMeterRegisterReading1 The MeterRegisterReading1 to set.
   */
  public void setMeterRegisterReading1(Float aMeterRegisterReading1)
  {
    mMeterRegisterReading1 = aMeterRegisterReading1;
  }

  /**
   * @return the MeterRegisterReading2.
   */
  public Float getMeterRegisterReading2()
  {
    return mMeterRegisterReading2;
  }

  /**
   * @param aMeterRegisterReading2 The MeterRegisterReading2 to set.
   */
  public void setMeterRegisterReading2(Float aMeterRegisterReading2)
  {
    mMeterRegisterReading2 = aMeterRegisterReading2;
  }

  /**
   * @return the MeterRegisterReading3.
   */
  public Float getMeterRegisterReading3()
  {
    return mMeterRegisterReading3;
  }

  /**
   * @param aMeterRegisterReading3 The MeterRegisterReading3 to set.
   */
  public void setMeterRegisterReading3(Float aMeterRegisterReading3)
  {
    mMeterRegisterReading3 = aMeterRegisterReading3;
  }

  /**
   * @return the MeterReadingDate1.
   */
  public DateTime getMeterReadingDate1()
  {
    return mMeterReadingDate1;
  }

  /**
   * @param aMeterReadingDate1 The mMeterReadingDate1 to set.
   */
  public void setMeterReadingDate1(DateTime aMeterReadingDate1)
  {
    mMeterReadingDate1 = aMeterReadingDate1;
  }

  /**
   * @return the MeterReadingDate2.
   */
  public DateTime getMeterReadingDate2()
  {
    return mMeterReadingDate2;
  }

  /**
   * @param aMeterReadingDate2 The MeterReadingDate2 to set.
   */
  public void setMeterReadingDate2(DateTime aMeterReadingDate2)
  {
    mMeterReadingDate2 = aMeterReadingDate2;
  }

  /**
   * @return the MeterReadingDate3.
   */
  public DateTime getMeterReadingDate3()
  {
    return mMeterReadingDate3;
  }

  /**
   * @param aMeterReadingDate3 The MeterReadingDate3 to set.
   */
  public void setMeterReadingDate3(DateTime aMeterReadingDate3)
  {
    mMeterReadingDate3 = aMeterReadingDate3;
  }

  /**
   * @return the calculated standard 1
   */
  @Column(name = "CALCULATED_STANDARD_1", nullable = true)
  public Long getCalculatedStandard1()
  {
    return mCalculatedStandard1;
  }

  /**
   * @param mCalculatedStandard the calculated standard 1
   */
  public void setCalculatedStandard1(Long aCalculatedStandard1)
  {
    this.mCalculatedStandard1 = aCalculatedStandard1;
  }

  /**
   * @return the calculated standard 2
   */
  @Column(name = "CALCULATED_STANDARD_2", nullable = true)
  public Long getCalculatedStandard2()
  {
    return mCalculatedStandard2;
  }

  /**
   * @param aCalculatedStandard2 the calculated standard 2
   */
  public void setCalculatedStandard2(Long aCalculatedStandard2)
  {
    this.mCalculatedStandard2 = aCalculatedStandard2;
  }

  /**
   * @return the calculated standard 3
   */
  @Column(name = "CALCULATED_STANDARD_3", nullable = true)
  public Float getCalculatedStandard3()
  {
    return mCalculatedStandard3;
  }

  /**
   * @param aCalculatedStandard3 the calculated standard 3
   */
  public void setCalculatedStandard3(Float aCalculatedStandard3)
  {
    this.mCalculatedStandard3 = aCalculatedStandard3;
  }

  /**
   * @return the effective from date(J0049)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="EFFECTIVE_FROM_DATE", nullable=true)
  public DateTime getEffectiveFromDate()
  {
    return mEffectiveFromDate;
  }

  /**
   * @param aEffectiveFromDate the effective from date(J0049)
   */
  public void setEffectiveFromDate(DateTime aEffectiveFromDate)
  {
    mEffectiveFromDate = aEffectiveFromDate;
  }

  /**
   * @return the effective to date(J0928)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="EFFECTIVE_TO_DATE", nullable=true)
  public DateTime getEffectiveToDate()
  {
    return mEffectiveToDate;
  }

  /**
   * @param aEffectiveToDate the effective to date(J0928)
   */
  public void setEffectiveToDate(DateTime aEffectiveToDate)
  {
    mEffectiveToDate = aEffectiveToDate;
  }

  /**
   * @return the maximum demand
   */
  @Column(name = "MAX_DEMAND", nullable = true)
  public Float getMaxDemand()
  {
    return mMaxDemand;
  }

  /**
   * @param aMaxDemand the maximum demand
   */
  public void setMaxDemand(Float aMaxDemand)
  {
    mMaxDemand = aMaxDemand;
  }

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof Sp04FromAFMSMpan))
      return false;

    Sp04FromAFMSMpan ga = (Sp04FromAFMSMpan)o;
    return ga.getMpan().equals(getMpan())
      && ga.getMpanFk().equals(getMpanFk())
      && ga.getMeterId().equals(getMeterId())
      && ga.getSupplierFk().equals(getSupplierFk())
      && ga.getMeterRegisterFks().equals(getMeterRegisterFks())
      && ga.getDataCollector().equals(getDataCollector())
      && ga.getDataCollectorFk().equals(getDataCollectorFk())

      && ga.getCalculatedMeterInstallationDeadline().equals(getCalculatedMeterInstallationDeadline())
      && ga.getD0268SettlementDate().equals(getD0268SettlementDate())

      && ga.getMeterRegisterReading1().equals(getMeterRegisterReading1())
      && ga.getMeterRegisterReading2().equals(getMeterRegisterReading2())
      && ga.getMeterRegisterReading3().equals(getMeterRegisterReading3())
      && ga.getMeterReadingDate1().equals(getMeterReadingDate1())
      && ga.getMeterReadingDate2().equals(getMeterReadingDate2())
      && ga.getMeterReadingDate3().equals(getMeterReadingDate3())
    ;
  }

  @Override
  public int hashCode()
  {
    int result = 17;

    result = 31 * result + (null == getMpan() ? 0 : getMpan().hashCode());
    result = 31 * result + (null == getMpanFk() ? 0 : getMpanFk().hashCode());
    result = 31 * result + (null == getMeterId() ? 0 : getMeterId().hashCode());
    result = 31 * result + (null == getSupplierFk() ? 0 : getSupplierFk().hashCode());
    result = 31 * result + (null == getMeterRegisterFks() ? 0 : getMeterRegisterFks().hashCode());
    result = 31 * result + (null == getDataCollector() ? 0 : getDataCollector().hashCode());
    result = 31 * result + (null == getDataCollectorFk() ? 0 : getDataCollectorFk().hashCode());

    result = 31 * result + (null == getCalculatedMeterInstallationDeadline() ? 0 : getCalculatedMeterInstallationDeadline().hashCode());
    result = 31 * result + (null == getD0268SettlementDate() ? 0 : getD0268SettlementDate().hashCode());

    result = 31 * result + (null == getMeterRegisterReading1() ? 0 : getMeterRegisterReading1().hashCode());
    result = 31 * result + (null == getMeterRegisterReading2() ? 0 : getMeterRegisterReading2().hashCode());
    result = 31 * result + (null == getMeterRegisterReading3() ? 0 : getMeterRegisterReading3().hashCode());
    result = 31 * result + (null == getMeterReadingDate1() ? 0 : getMeterReadingDate1().hashCode());
    result = 31 * result + (null == getMeterReadingDate2() ? 0 : getMeterReadingDate2().hashCode());
    result = 31 * result + (null == getMeterReadingDate3() ? 0 : getMeterReadingDate3().hashCode());

    return result;
  }

  @Override
  public String toString()
  {
    return "mpan[" + getMpan() + "], settlementDate[" + getD0268SettlementDate() + "]";
  }
}

package uk.co.utilisoft.afms.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name="METER")
@SuppressWarnings("serial")
public class AFMSMeter extends AFMSDomainObject
{

  private Long mMeterPk;

  private AFMSMpan mMpan;
  private String mMeterSerialId;

  private DateTime mSettlementDate;   //J1254
  private DateTime mEffectiveToDateMSID;   //etd_msid

  private Collection<AFMSMeterRegister> mAfmsMeterRegisters;

  private String mOutstationID;   //J0428

  private Long mMpanLinkId;

  private String mMeterType;  //J0483



  /**
   * @return the primary key
   */
  @Id
  @Column(name="METER_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mMeterPk;
  }

  /**
   * @param aPk the primary key
   */
  public void setPk(Long aPk)
  {
    mMeterPk = aPk;
  }

  /**
   * @return the AFMS Mpan
   */
  @ManyToOne(fetch=FetchType.EAGER, optional=false)
  @JoinColumn(name="MPAN_LNK", nullable=true)
  public AFMSMpan getMpan()
  {
    return mMpan;
  }

  /**
   * @param aMpan the AFMS Mpan
   */
  public void setMpan(AFMSMpan aMpan)
  {
    mMpan = aMpan;
  }

  @Column(name="J0004", nullable=false)
  public String getMeterSerialId()
  {
    return mMeterSerialId;
  }

  public void setMeterSerialId(String aMeterSerialId)
  {
    this.mMeterSerialId = aMeterSerialId;
  }


  /**
   * This will only be populated fore meters that are already HH or a smart meter
   * @return
   */
  @Column(name="J0428 ", nullable=false)
  public String getOutstationId()
  {
    return mOutstationID;
  }

  public void setOutstationId(String aOutstationID)
  {
    this.mOutstationID = aOutstationID;
  }


  /**
   * Confusingly, a HH meter can be null or H
   * NB as this can be null or H we do not use these as identifiers as 'null' is not very indicative.
   * instead use method isMeterHalfHourly()
   * @return
   */
  @Column(name="J0483 ", nullable=true)
  public String getMeterType()
  {
    return mMeterType;
  }

  public void setMeterType(String aMeterType)
  {
    this.mMeterType = aMeterType;
  }



  @OneToMany(mappedBy="meter", fetch=FetchType.EAGER)
  public Collection<AFMSMeterRegister> getMeterRegisters()
  {
    if (mAfmsMeterRegisters == null)
    {
      mAfmsMeterRegisters = new ArrayList<AFMSMeterRegister>();
    }
    return mAfmsMeterRegisters;
  }

  public void setMeterRegisters(Collection<AFMSMeterRegister> aAfmsMeterRegisters)
  {
    mAfmsMeterRegisters = aAfmsMeterRegisters;
  }


  /**
   * @return aSettlementDate the settlement date(J1254)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J1254", nullable=true)
  public DateTime getSettlementDate()
  {
    return mSettlementDate;
  }

  /**
   * @param aSettlementDate the settlement date(J1254)
   */
  public void setSettlementDate(DateTime aSettlementDate)
  {
    mSettlementDate = aSettlementDate;
  }


  /**
   * @return EffectiveToDateMSID the Effective To Date (etd_msid)
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="etd_msid", nullable=true)
  public DateTime getEffectiveToDateMSID()
  {
    return mEffectiveToDateMSID;
  }

  /**
   * @param EffectiveToDateMSID the Effective To Date (etd_msid)
   */
  public void setEffectiveToDateMSID(DateTime aEffectiveToDateMSID)
  {
    mEffectiveToDateMSID = aEffectiveToDateMSID;
  }

  /**
   * @return the mpan link id
   */
  @Column(name = "MPAN_LNK", nullable = true, insertable = false, updatable = false)
  @NotNull
  public Long getMpanLinkId()
  {
    return mMpanLinkId;
  }

  /**
   * @param aMpanLinkId the mpan link id
   */
  public void setMpanLinkId(Long aMpanLinkId)
  {
    mMpanLinkId = aMpanLinkId;
  }

  @Transient
  public boolean isThisMeterActiveNow()
  {
    if (isTimeAfterSettlementDate() && isNowBeforeEffectiveToDate())
    {
      return true;
    }
    return false;
  }

  @Transient
  private boolean isTimeAfterSettlementDate()
  {
    if (getSettlementDate() != null)
    {
      return getSettlementDate().isBeforeNow();
    }

    return false;
  }

  @Transient
  private boolean isNowBeforeEffectiveToDate()
  {
    if (getEffectiveToDateMSID() != null)
    {
      if (getEffectiveToDateMSID().isAfterNow())
      {
        //is set but in future so is true
        return true;
      }
      else
      {
        return false;
      }
    }
    else
    {
      //not set so default to true
      return true;
    }
  }

  @Transient
  public boolean isNonHalfHourlyMeter()
  {
    // a smart meter is a non HH meter
    if (isThisASmartMeter())
    {
      return true;
    }

    if (StringUtils.isBlank(getOutstationId()))
    {
      return true;
    }
    else
    {
      return false;
    }

  }

  @Transient
  public boolean isHalfHourlyMeter()
  {
    return !isNonHalfHourlyMeter();
  }

  /**
   * a Smart meter can have outstaion ID populated but must have meterType == NCAMR, RCAMY or RCAMR.
   * a smart meter is a NON HH meter.
   * @return
   */
  @Transient
  public boolean isThisASmartMeter()
  {
    if (StringUtils.isNotBlank(getMeterType()))
    {
      String meterType = getMeterType().toUpperCase();

      if (meterType.equals(METER_TYPE.NCAMR.getValue())
          || meterType.equals(METER_TYPE.RCAMY.getValue())
          || meterType.equals(METER_TYPE.RCAMR.getValue()))
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    else
    {
      return false;
    }

  }

  /**
   * AFMS Meter Register for the given meter register id, and meter register type.
   * @param aMeterRegisterId the AFMS Meter Register Id
   * @return the AFMS Meter Register with the given id
   */
  @Transient
  public AFMSMeterRegister getMaxDemandMeterRegister()
  {
    if (mAfmsMeterRegisters != null && !mAfmsMeterRegisters.isEmpty())
    {
      for (AFMSMeterRegister meterRegister : mAfmsMeterRegisters)
      {
        // case insensitive meter register type, and id match
        if (meterRegister.isMeterRegisterTypeMaxDemand() && meterRegister.isMeterRegisterIdMaxDemand())
        {
          return meterRegister;
        }
      }
    }

    return null;
  }

  public static enum METER_TYPE
  {
    NCAMR("NCAMR", "NCAMR"), RCAMY("RCAMY", "RCAMY"), RCAMR("RCAMR", "RCAMR");

    private String mValue;
    private String mDescription;

    private METER_TYPE(String aValue, String aDescription)
    {
      mValue = aValue;
      mDescription = aDescription;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
      return mValue;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
      return mDescription;
    }
  }
}
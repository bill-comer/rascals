package uk.co.utilisoft.afms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;


@Entity
@Table(name="METER_REG_READING")
@SuppressWarnings("serial")
public class AFMSMeterRegReading extends AFMSDomainObject
{

  private Long mMeterRegReadPk;
  private AFMSMeterRegister mMeterRegister;
  private DateTime mMeterReadingDate;
  private DateTime mDateReceived;
  private Float mRegisterReading;
  private String mFlowReceived;
  private String mBSCValidationStatus;
  private String mReadingType;

  /**
   * @return the primary key
   */
  @Id
  @Column(name="MET_REG_READ", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mMeterRegReadPk;
  }

  /**
   * @param aPk the primary key
   */
  public void setPk(Long aPk)
  {
    mMeterRegReadPk = aPk;
  }

  /**
   * @return the AFMS Mpan
   */
  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="METER_REG_FK", nullable=true)
  public AFMSMeterRegister getMeterRegister()
  {
    return mMeterRegister;
  }

  /**
   * @param aMpan the AFMS Mpan
   */
  public void setMeterRegister(AFMSMeterRegister aMeterRegister)
  {
    mMeterRegister = aMeterRegister;
  }


  @Type(type="org.joda.time.DateTime")
  @Column(name="J0016", nullable=false)
  public DateTime getMeterReadingDate()
  {
    return mMeterReadingDate;
  }

  public void setMeterReadingDate(DateTime mMeterReadingDate)
  {
    this.mMeterReadingDate = mMeterReadingDate;
  }


  @Type(type="org.joda.time.DateTime")
  @Column(name="DATE_RECEIVED", nullable=false)
  public DateTime getDateReceived()
  {
    return mDateReceived;
  }

  public void setDateReceived(DateTime aDateReceived)
  {
    this.mDateReceived = aDateReceived;
  }

  @Column(name="J0040", nullable=false)
  public Float getRegisterReading()
  {
    return mRegisterReading;
  }

  public void setRegisterReading(Float aRegisterReading)
  {
    this.mRegisterReading = aRegisterReading;
  }


  @Column(name="FLOW_RECEIVED", nullable=false)
  public String getFlowReceived()
  {
    return mFlowReceived;
  }

  public void setFlowReceived(String aFlowReceived)
  {
    this.mFlowReceived = aFlowReceived;
  }

  /**
   * @return the BSC Validation Status
   */
  @Column(name="J0022", nullable = true)
  public String getBSCValidationStatus()
  {
    return mBSCValidationStatus;
  }

  /**
   * @param aBSCValidationStatus the BSC Validation Status
   */
  public void setBSCValidationStatus(String aBSCValidationStatus)
  {
    mBSCValidationStatus = aBSCValidationStatus;
  }

  @Column(name="J0171", nullable = true)
  public String getReadingType()
  {
    return mReadingType;
  }

  public void setReadingType(String aReadingType)
  {
    mReadingType = aReadingType;
  }

  @Transient
  public boolean isReadingBscValidated()
  {
    if (StringUtils.isNotBlank(getBSCValidationStatus())
                && getBSCValidationStatus().toUpperCase().equals(AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue()))
    {
      return true;
    }
    return false;

  }

  @Transient
  public boolean isReadingType(READING_TYPE aReadingType)
  {
    if (StringUtils.isNotBlank(getReadingType())
        && getReadingType().toUpperCase().equals(aReadingType.getValue()))
    {
      return true;
    }

    return false;
  }

  public static enum BSC_VALIDATION_STATUS
  {
    V("V", "BSC Validated For Settlement");

    private String mValue;
    private String mDescription;

    private BSC_VALIDATION_STATUS(String aValue, String aDescription)
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

  public static enum READING_TYPE
  {
    W("W", "Reading Withdrawn");

    private String mValue;
    private String mDescription;

    private READING_TYPE(String aValue, String aDescription)
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

package uk.co.utilisoft.afms.domain;

import java.util.ArrayList;
import java.util.List;

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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MeterRegisterType;


@Entity
@Table(name="METER_REGISTER")
@SuppressWarnings("serial")
public class AFMSMeterRegister extends AFMSDomainObject
{
  private Long mMeterRegPk;
  private AFMSMeter mAfmsMeter;
  private DateTime mEffectiveFromDate;
  private DateTime mEffectiveToDate;
  private String mMeterRegisterId;
  private String mMeasurementQuantityId;
  private String mMeterRegType;



  private List<AFMSMeterRegReading> mMeterRegReadings = new ArrayList<AFMSMeterRegReading>();

  /**
   * @return the primary key
   */
  @Id
  @Column(name="METER_REG_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mMeterRegPk;
  }

  /**
   * @param aPk the primary key
   */
  public void setPk(Long aPk)
  {
    mMeterRegPk = aPk;
  }


  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="METER_FK", nullable=true)
  public AFMSMeter getMeter()
  {
    return mAfmsMeter;
  }

  public void setMeter(AFMSMeter aAfmsMeter)
  {
    this.mAfmsMeter = aAfmsMeter;
  }

  @Type(type="org.joda.time.DateTime")
  @Column(name="EFD_ID", nullable=false)
  public DateTime getEffectiveFromDate()
  {
    return mEffectiveFromDate;
  }
  public void setEffectiveFromDate(DateTime effectiveFromDate)
  {
    this.mEffectiveFromDate = effectiveFromDate;
  }


  @Type(type="org.joda.time.DateTime")
  @Column(name="ETD_ID", nullable=false)
  public DateTime getEffectiveToDate()
  {
    return mEffectiveToDate;
  }

  public void setEffectiveToDate(DateTime effectiveToDate)
  {
    this.mEffectiveToDate = effectiveToDate;
  }


  @Column(name="J0010", nullable=false)
  public String getMeterRegisterId()
  {
    return mMeterRegisterId;
  }

  public void setMeterRegisterId(String aMeterRegisterId)
  {
    this.mMeterRegisterId = aMeterRegisterId;
  }

  @Column(name="J0103", nullable=false)
  public String getMeasurementQuantityId()
  {
    return mMeasurementQuantityId;
  }

  public void setMeasurementQuantityId(String measurementQuantityId)
  {
    this.mMeasurementQuantityId = measurementQuantityId;
  }

  /**
   * see MeterRegisterType
   * @return
   */
  @Column(name="J0474", nullable=false)
  public String getMeterRegType()
  {
    return mMeterRegType;
  }

  public void setMeterRegType(String meterRegType)
  {
    this.mMeterRegType = meterRegType;
  }


  @OneToMany(mappedBy="meterRegister", fetch=FetchType.EAGER)
  @Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
  public List<AFMSMeterRegReading> getMeterRegReadings()
  {
    return mMeterRegReadings;
  }

  public void setMeterRegReadings(List<AFMSMeterRegReading> aMeterRegReadings)
  {
    mMeterRegReadings = aMeterRegReadings;
  }

  @Transient
  public boolean isMeterRegisterTypeMaxDemand()
  {
    String meterRegisterType = this.getMeterRegType();
    if (StringUtils.isBlank(meterRegisterType))
    {
      return false;
    }

    meterRegisterType = this.getMeterRegType().toUpperCase();
    if (meterRegisterType.equals(MeterRegisterType.M.getValue()))
    {
      return true;
    }


    if (meterRegisterType.equals(MeterRegisterType.THREE.getValue()))
    {
      return true;
    }

    return false;

  }

  @Transient
  public boolean isMeasurementQuantityKW()
  {
    String measQuantity = this.getMeasurementQuantityId();
    if (StringUtils.isBlank(measQuantity))
    {
      return false;
    }
    measQuantity = this.getMeasurementQuantityId().toUpperCase();
    if (measQuantity.equals(MEASUREMENT_QUANTITY_ID.KW.getValue()))
    {
      return true;
    }

    return false;
  }


  @Transient
  public boolean isMeterRegisterIdMaxDemand()
  {
    String meterRegisterId = this.getMeterRegisterId();
    if (StringUtils.isBlank(meterRegisterId))
    {
      return false;
    }
    meterRegisterId = this.getMeterRegisterId().toUpperCase();
    if (meterRegisterId.equals(METER_REGISTER_ID.MD.getValue()))
    {
      return true;
    }

    return false;
  }

  /**
   * @param aValidEffectiveFromDate the effective from date
   * @param aValidEffectiveToDate the effective to date
   * @return true if the AFMSMeterRegister is valid for inclusion in an Sp04 report
   */
  @Transient
  public boolean isValidMeterRegisterForSp04Report(DateTime aValidEffectiveFromDate,
                                                   DateTime aValidEffectiveToDate)
  {
    return this.getEffectiveFromDate() != null
      && this.getEffectiveFromDate().isBefore(aValidEffectiveFromDate)
      &&  (this.getEffectiveToDate() == null || this.getEffectiveToDate().isAfter(aValidEffectiveToDate))

      // bug#5682 - additional check for is AFMSMeterRegReading valid
      && this.isMeasurementQuantityKW()
      && this.isMeterRegisterIdMaxDemand()
      && this.isMeterRegisterTypeMaxDemand();
  }

  public static enum MEASUREMENT_QUANTITY_ID
  {
    KW("KW", "KILOWATT");

    private String mValue;
    private String mDescription;

    private MEASUREMENT_QUANTITY_ID(String aValue, String aDescription)
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

  public static enum METER_REGISTER_ID
  {
    MD("MD", "Has exceeded MAXIMUM DEMAND");

    private String mValue;
    private String mDescription;

    private METER_REGISTER_ID(String aValue, String aDescription)
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
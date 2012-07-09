package uk.co.utilisoft.parms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;

@Entity
@AccessType(value="property")
@Table(name="PARMS_P0028_DATA")
@SuppressWarnings("serial")
public class P0028Data extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  // the meter max demand value
  private Long mMaxDemand;

  /**
   *  the meter register id
   *  This maps to the AFMSMeter.meterSerialId
   */
  private String mMeterSerialId;

  // the mpan for this reading
  private MPANCore mMpan;

  // the datetime of this reading
  private DateTime mReadingDate;

  // the parent file for this reading
  private P0028File mP0028File;

  // Agent DC Collector Id as it appears in the P0028 File
  private String mDcAgentName;

  private boolean mValidated;

  private List<P0028UploadError> mP0028UploadError;

  private Long mMpanUniqId;

  private String mCurrentMeasurementClassification;


  @Column(name="VALIDATED")
  @NotNull
  public boolean isValidated()
  {
    return mValidated;
  }

  public void setValidated(boolean aValid)
  {
    this.mValidated = aValid;
  }




  public P0028Data()
  {
    super();
    this.setLastUpdated(new DateTime());
    mP0028UploadError = new ArrayList<P0028UploadError>();
  }

  public P0028Data(Long aMaxDemand, String aMeterSerialId, MPANCore aMpan,
      DateTime aReadingDate, P0028File aP0028File)
  {
    this();
    this.mMaxDemand = aMaxDemand;
    this.mMeterSerialId = aMeterSerialId;
    this.mMpan = aMpan;
    this.mReadingDate = aReadingDate;
    this.mP0028File = aP0028File;
  }

  @Column(name="MAX_DEMAND")
  @NotNull
  public Long getMaxDemand()
  {
    return mMaxDemand;
  }

  public void setMaxDemand(Long maxDemand)
  {
    this.mMaxDemand = maxDemand;
  }

  @Column(name="METER_ID")
  @NotNull
  public String getMeterSerialId()
  {
    return mMeterSerialId;
  }

  public void setMeterSerialId(String meterSerialId)
  {
    this.mMeterSerialId = meterSerialId;
  }

  @Column(name="MPAN")
  @Type(type="uk.co.utilisoft.parms.MPANCore")
  @NotNull
  public MPANCore getMpan()
  {
    return mMpan;
  }

  public void setMpan(MPANCore mpan)
  {
    this.mMpan = mpan;
  }

  @Column(name="READING_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getReadingDate()
  {
    return mReadingDate;
  }

  public void setReadingDate(DateTime readingDate)
  {
    this.mReadingDate = readingDate;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "P0028_FILE_FK", nullable = false)
  @NotNull
  public P0028File getP0028File()
  {
    return mP0028File;
  }

  public void setP0028File(P0028File p0028File)
  {
    this.mP0028File = p0028File;
  }


  @OneToMany(mappedBy = "p0028Data")
  @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
  @LazyCollection(LazyCollectionOption.FALSE)
  public List<P0028UploadError> getP0028UploadError()
  {
    return mP0028UploadError;
  }
  /*
    */
  public void setP0028UploadError(List<P0028UploadError> aP0028UploadError)
  {
    mP0028UploadError = aP0028UploadError;
  }

  @Column(name = "MPAN_UNIQ_ID", nullable = true)
  public Long getMpanUniqId()
  {
    return mMpanUniqId;
  }

  public void setMpanUniqId(Long aMpanUniqId)
  {
    mMpanUniqId = aMpanUniqId;
  }

  @Column(name = "CURRENT_MEASURE_CLASS", nullable = true)
  public String getCurrentMeasurementClassification()
  {
    return mCurrentMeasurementClassification;
  }

  public void setCurrentMeasurementClassification(String aCurrentMeasurementClassification)
  {
    mCurrentMeasurementClassification = aCurrentMeasurementClassification;
  }

  @Transient
  public String getDcAgentName()
  {
    return mDcAgentName;
  }
  public void setDcAgentName(String dcAgentName)
  {
    this.mDcAgentName = dcAgentName;
  }

  @Transient
  public DateTime getMeterInstallationDeadline()
  {
    return getP0028File().getMeterInstallationDeadline();
  }

  @Transient
  public boolean hasErrors()
  {
    if (getP0028UploadError() != null && getP0028UploadError().size() > 0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

}
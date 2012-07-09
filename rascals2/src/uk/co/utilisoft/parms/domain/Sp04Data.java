package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;

@Entity
@AccessType(value="property")
@Table(name="PARMS_SP04_DATA")
@SuppressWarnings("serial")
public class Sp04Data extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String mGspGroupId;
  private MPANCore mMpanCore;

  private Long mStandard1;
  private Long mStandard2;
  private Float mStandard3;

  private Sp04File mSp04File;

  private Float mMaxDemandThreshold;

  //transcient
  private Sp04FaultReasonType mSp04FaultReason;


  public Sp04Data()
  {
    super();
    setLastUpdated(new DateTime());
  }

  public Sp04Data(String gspGroupId, MPANCore mpanCore, Long standard1,
      Long standard2, Float standard3, Sp04File sp04File)
  {
    this();
    this.mGspGroupId = gspGroupId;
    this.mMpanCore = mpanCore;
    this.mStandard1 = standard1;
    this.mStandard2 = standard2;
    this.mStandard3 = standard3;
    this.mSp04File = sp04File;
  }



  @Column(name="GSP_GROUP_ID")
  public String getGspGroupId()
  {
    return mGspGroupId;
  }

  public void setGspGroupId(String gspGroupId)
  {
    this.mGspGroupId = gspGroupId;
  }

  @Column(name="MPAN")
  @Type(type="uk.co.utilisoft.parms.MPANCore")
  @NotNull
  public MPANCore getMpanCore()
  {
    return mMpanCore;
  }

  public void setMpanCore(MPANCore mpanCore)
  {
    this.mMpanCore = mpanCore;
  }


  @Column(name="STANDARD_1")
  public Long getStandard1()
  {
    return mStandard1;
  }

  public void setStandard1(Long standard1)
  {
    this.mStandard1 = standard1;
  }

  @Column(name="STANDARD_2")
  public Long getStandard2()
  {
    return mStandard2;
  }

  public void setStandard2(Long standard2)
  {
    this.mStandard2 = standard2;
  }


  @Column(name="STANDARD_3")
  public Float getStandard3()
  {
    return mStandard3;
  }

  public void setStandard3(Float standard3)
  {
    this.mStandard3 = standard3;
  }

  @ManyToOne
  @JoinColumn(name = "SP04_FILE_FK")
  @NotNull
  public Sp04File getSp04File()
  {
    return mSp04File;
  }

  public void setSp04File(Sp04File sp04File)
  {
    this.mSp04File = sp04File;
  }

  @Transient
  public Sp04FaultReasonType getSp04FaultReason()
  {
    return mSp04FaultReason;
  }

  public void setSp04FaultReason(Sp04FaultReasonType sp04FaultReason)
  {
    this.mSp04FaultReason = sp04FaultReason;
  }

  @Transient
  public Float getMaxDemandThreshold()
  {
    return mMaxDemandThreshold;
  }

  public void setMaxDemandThreshold(Float aMaxDemandThreshold)
  {
    mMaxDemandThreshold = aMaxDemandThreshold;
  }

}

package uk.co.utilisoft.parmsmop.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;


@MappedSuperclass
public abstract class ParmsMopBaseData extends MOPDomainObject
{

  private MPANCore mMpan;
  private String mSerialType;
  private DateTime mStartDate;
  private DateTime mCompletedDate;
  
  private Float mStandard1;
  private Float mStandard2;
  private Float mStandard3;
  private Float mStandard4;
  private Float mStandard5;
  private Float mStandard6;
  private Float mStandard7;
  private Float mStandard8;

  private DateTime mGenDate1;
  private DateTime mGenDate2;
  private DateTime mGenDate3;
  private DateTime mGenDate4;

  private String mGenString1;
  private String mGenString2;
  private String mGenString3;
  private String mGenString4;
  
  private boolean mPendingIndicator;
  private Float mWdElapsed;
  private boolean mExcludeIndicator;
  private boolean mHalfHourlyIndicator;
  

  
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
  
  @Column(name="SERIAL_TYPE", nullable=false)
  @Length( max = 20 )
  @NotNull
  public String getSerialType()
  {
    return mSerialType;
  }
  public void setSerialType(String serialType)
  {
    this.mSerialType = serialType;
  }

  @Column(name="START_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getStartDate()
  {
    return mStartDate;
  }
  public void setStartDate(DateTime startDate)
  {
    this.mStartDate = startDate;
  }
  @Transient
  public void setStartDate(Timestamp startDate)
  {
    this.mStartDate = new DateTime(startDate);
  }
  
  @Column(name="COMPLETED_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getCompletedDate()
  {
    return mCompletedDate;
  }
  public void setCompletedDate(DateTime completedDate)
  {
    this.mCompletedDate = completedDate;
  }

  @Column(name="STANDARD_1")
  public Float getStandard1()
  {
    return mStandard1;
  }
  public void setStandard1(Float standard1)
  {
    this.mStandard1 = standard1;
  }

  @Column(name="STANDARD_2")
  public Float getStandard2()
  {
    return mStandard2;
  }
  public void setStandard2(Float standard2)
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

  @Column(name="STANDARD_4")
  public Float getStandard4()
  {
    return mStandard4;
  }
  public void setStandard4(Float standard4)
  {
    this.mStandard4 = standard4;
  }

  @Column(name="STANDARD_5")
  public Float getStandard5()
  {
    return mStandard5;
  }
  public void setStandard5(Float standard5)
  {
    this.mStandard5 = standard5;
  }

  @Column(name="STANDARD_6")
  public Float getStandard6()
  {
    return mStandard6;
  }
  public void setStandard6(Float standard6)
  {
    this.mStandard6 = standard6;
  }

  @Column(name="STANDARD_7")
  public Float getStandard7()
  {
    return mStandard7;
  }
  public void setStandard7(Float standard7)
  {
    this.mStandard7 = standard7;
  }

  @Column(name="STANDARD_8")
  public Float getStandard8()
  {
    return mStandard8;
  }
  public void setStandard8(Float standard8)
  {
    this.mStandard8 = standard8;
  }
  
  @Column(name="GEN_DATE_1")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getGenDate1()
  {
    return mGenDate1;
  }
  public void setGenDate1(DateTime genDate1)
  {
    this.mGenDate1 = genDate1;
  }
  
  @Column(name="GEN_DATE_2")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getGenDate2()
  {
    return mGenDate2;
  }
  public void setGenDate2(DateTime genDate2)
  {
    this.mGenDate2 = genDate2;
  }
  
  @Column(name="GEN_DATE_3")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getGenDate3()
  {
    return mGenDate3;
  }
  public void setGenDate3(DateTime genDate3)
  {
    this.mGenDate3 = genDate3;
  }
  
  @Column(name="GEN_DATE_4")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getGenDate4()
  {
    return mGenDate4;
  }
  public void setGenDate4(DateTime genDate4)
  {
    this.mGenDate4 = genDate4;
  }
  
  @Column(name="GEN_STR_1", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getGenString1()
  {
    return mGenString1;
  }
  public void setGenString1(String genString1)
  {
    this.mGenString1 = genString1;
  }
  
  @Column(name="GEN_STR_2", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getGenString2()
  {
    return mGenString2;
  }
  public void setGenString2(String genString2)
  {
    this.mGenString2 = genString2;
  }
  
  @Column(name="GEN_STR_3", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getGenString3()
  {
    return mGenString3;
  }
  public void setGenString3(String genString3)
  {
    this.mGenString3 = genString3;
  }
  
  @Column(name="GEN_STR_4", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getGenString4()
  {
    return mGenString4;
  }
  public void setGenString4(String genString4)
  {
    this.mGenString4 = genString4;
  }
  
  @Column(name="PENDING_INDICATOR", nullable=false)
  @NotNull
  public boolean isPendingIndicator()
  {
    return mPendingIndicator;
  }
  public void setPendingIndicator(boolean pendingIndicator)
  {
    this.mPendingIndicator = pendingIndicator;
  }

  @Column(name="WD_ELAPSED")
  public Float getWdElapsed()
  {
    return mWdElapsed;
  }
  public void setWdElapsed(Float wdElapsed)
  {
    this.mWdElapsed = wdElapsed;
  }
  
  @Column(name="EXCLUDE_IND", nullable=false)
  @NotNull
  public boolean isExcludeIndicator()
  {
    return mExcludeIndicator;
  }
  public void setExcludeIndicator(boolean excludeIndicator)
  {
    this.mExcludeIndicator = excludeIndicator;
  }
  
  @Column(name="NHH_HH_IND", nullable=false)
  @NotNull
  public boolean isHalfHourlyIndicator()
  {
    return mHalfHourlyIndicator;
  }
  public void setHalfHourlyIndicator(boolean aHalfHourlyIndicator)
  {
    this.mHalfHourlyIndicator = aHalfHourlyIndicator;
  }
  
  
}

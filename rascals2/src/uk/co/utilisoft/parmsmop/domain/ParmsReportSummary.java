package uk.co.utilisoft.parmsmop.domain;

import java.math.BigInteger;

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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

@Entity
@AccessType(value="property")
@Table(name="PARMS_REPORT_SUMMARY")
public class ParmsReportSummary  extends MOPDomainObject implements Comparable<ParmsReportSummary>
{

  public ParmsReportSummary()
  {
    super();
    setLastUpdated(new DateTime());
  }

  private BigInteger mPk;
  
  private String mParticipantId;
  private String mReportString2;
  private String mReportString3;
  private String mReportString4;
  private String mReportString5;
  private String mReportString6;
  private String mReportString7;
  private String mReportString8;
  private String mReportString9;
  private String mReportString10;
  private String mReportString11;
  private String mReportString12;
  
  private String mHalfHourlyIndicator;
  
  // the parent report for this reading
  private ParmsMopReport mParmsMopReport;

  @Id
  @Column(name="PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public BigInteger getPk()
  {
    return mPk;
  }

  /**
   * @param aPk the mpan primary key
   */
  public void setPk(BigInteger aMpanPk)
  {
    mPk = aMpanPk;
  }

  @Column(name="REP_STR_1", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getParticipantId()
  {
    return mParticipantId;
  }

  public void setParticipantId(String aParticipantId)
  {
    this.mParticipantId = aParticipantId;
  }

  @Column(name="REP_STR_2", nullable=false,  insertable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString2()
  {
    return StringUtils.isNotEmpty(mReportString2) ? mReportString2 : "UUUU";
  }

  public void setReportString2(String aReportString2)
  {
    this.mReportString2 = aReportString2;
  }

  @Column(name="REP_STR_3", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString3()
  {
    return StringUtils.isNotEmpty(mReportString3) ? mReportString3 : "0";
  }

  public void setReportString3(String aReportString3)
  {
    this.mReportString3 = aReportString3;
  }

  @Column(name="REP_STR_4", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString4()
  {
    return StringUtils.isNotEmpty(mReportString4) ? mReportString4 : "0";
  }

  public void setReportString4(String aReportString4)
  {
    this.mReportString4 = aReportString4;
  }

  @Column(name="REP_STR_5", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString5()
  {

    return StringUtils.isNotEmpty(mReportString5) ? mReportString5 : "0";
  }

  public void setReportString5(String aReportString5)
  {
    this.mReportString5 = aReportString5;
  }

  @Column(name="REP_STR_6", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString6()
  {
    return StringUtils.isNotEmpty(mReportString6) ? mReportString6 : "0";
  }

  public void setReportString6(String aReportString6)
  {
    this.mReportString6 = aReportString6;
  }

  @Column(name="REP_STR_7", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString7()
  {
    return StringUtils.isNotEmpty(mReportString7) ? mReportString7 : "0";
  }

  public void setReportString7(String aReportString7)
  {
    this.mReportString7 = aReportString7;
  }

  @Column(name="REP_STR_8", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString8()
  {
    return StringUtils.isNotEmpty(mReportString8) ? mReportString8 : "0";
  }

  public void setReportString8(String aReportString8)
  {
    this.mReportString8 = aReportString8;
  }

  @Column(name="REP_STR_9", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString9()
  {
    return StringUtils.isNotEmpty(mReportString9) ? mReportString9 : "0";
  }

  public void setReportString9(String aReportString9)
  {
    this.mReportString9 = aReportString9;
  }

  @Column(name="REP_STR_10", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString10()
  {
    return StringUtils.isNotEmpty(mReportString10) ? mReportString10 : "0";
  }

  public void setReportString10(String aReportString10)
  {
    this.mReportString10 = aReportString10;
  }

  @Column(name="REP_STR_11", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString11()
  {
    return StringUtils.isNotEmpty(mReportString11) ? mReportString11 : "0";
  }

  public void setReportString11(String aReportString11)
  {
    this.mReportString11 = aReportString11;
  }

  @Column(name="REP_STR_12", nullable=false)
  @Length( max = 30 )
  @NotNull
  public String getReportString12()
  {
    return StringUtils.isNotEmpty(mReportString12) ? mReportString12 : "0";
  }

  public void setReportString12(String aReportString12)
  {
    this.mReportString12 = aReportString12;
  }

  @Column(name="NHH_HH_IND", nullable=false)
  @Length( max = 1 )
  @NotNull
  @Type(type="java.lang.String")
  public String getHalfHourlyIndicator()
  {
    return mHalfHourlyIndicator;
  }

  public void setHalfHourlyIndicator(String aHalfHourlyIndicator)
  {
    //Integer y = new Integer((String)aHalfHourlyIndicator);
    this.mHalfHourlyIndicator = aHalfHourlyIndicator;
  }
  @Transient
  public boolean isHalfHourlyIndicatorAsBoolean()
  {
    if (getHalfHourlyIndicator().equals("H")) {
      return true;
    } else {
      return false;
    }
  }
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "REPORT_FK", nullable = false)
  @NotNull
  public ParmsMopReport getParmsMopReport()
  {
    return mParmsMopReport;
  }

  public void setParmsMopReport(ParmsMopReport aParmsMopReport)
  {
    this.mParmsMopReport = aParmsMopReport;
  }
  
  @Override
  public int hashCode()
  {
    int result = 17;

    result = 31 * result + (null == getParticipantId() ? 0 : getParticipantId().hashCode());
    result = 31 * result + (null == getHalfHourlyIndicator() ? 0 : getHalfHourlyIndicator().hashCode());

    return result;
  }
  
  /**
   * equals only tests ParticipantId & isHH as this is all that is needed
   * to order items in the PARMS MOP report
   */
  @Override
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    
    if (!(o instanceof ParmsReportSummary)) {
      return false;
    }
    

    ParmsReportSummary other = (ParmsReportSummary)o;
    
    //ToDo this is good for SP11 - Is it good for others
    return other.getParticipantId().equals(getParticipantId())
                && other.getReportString2().equals(getReportString2())
                && other.getHalfHourlyIndicator().equals(getHalfHourlyIndicator());
    
    /*boolean isHHStatus = true;  //default to true (if not required)
    ParmsReportSummary other = (ParmsReportSummary)o;
    // only check isHH if serial is SP11, SP14 or SP15
    if (isHHStatusRequired())
    {
      //if required use determined value
      isHHStatus = isHalfHourlyIndicatorAsBoolean() == other.isHalfHourlyIndicatorAsBoolean();
      return other.getParticipantId().equals(getParticipantId()) && isHHStatus;
    }
    else
    {
      return other.getReportString2().equals(getReportString2());
    }*/
    
  }

  @Transient
  public boolean isHHStatusRequired()
  {
    return this.getParmsMopReport().isSerialSP11() || this.getParmsMopReport().isSerialSP14() || this.getParmsMopReport().isSerialSP15();
  }

  /**
   * only uses mParticipantId & isHH as this is all that is needed for sorting
   * for the MOP PArms report
   */
  @Override
  public int compareTo(ParmsReportSummary o)
  {

    if (isHHStatusRequired() )
    {
      if (getParticipantId().compareTo(o.getParticipantId()) == -1) {
        return -1;
      }
      if (getParticipantId().compareTo(o.getParticipantId()) == 1) {
        return 1;
      }
      
      //only check HH status if serial is SP11, SP14 or SP15
      Boolean isHH = new Boolean(isHalfHourlyIndicatorAsBoolean());
      Boolean otherIsHH = new Boolean(o.isHalfHourlyIndicatorAsBoolean());
      if (isHH.compareTo(otherIsHH) == -1) {
        return -1;
      }
      if (isHH.compareTo(otherIsHH) == 1) {
        return 1;
      }
    }
    else
    {
      //orderring for HM12 & NM12
      if (getReportString2().compareTo(o.getReportString2()) == -1) {
        return -1;
      }
      if (getReportString2().compareTo(o.getReportString2()) == 1) {
        return 1;
      }
    }
    
    
    // All fields are equals return 0
    return 0;
  }
  
  
}

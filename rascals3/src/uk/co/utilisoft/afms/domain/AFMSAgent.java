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

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name="AGENT")
@SuppressWarnings("serial")
public class AFMSAgent extends AFMSDomainObject
{
  private Long mPk;
  private AFMSMpan mMPan;
  private String mDataCollector;
  private String mMeterOperator;
  private DateTime mMOEffectiveFromDate;
  private DateTime mMOEffectiveToDate;
  private DateTime mDCEffectiveFromDate;
  private DateTime mDCEffectiveToDate;

  /**
   * @return the mpan primary key
   */
  @Id
  @Column(name="AGENT_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mPk;
  }

  /**
   * @param aPk the mpan primary key
   */
  public void setPk(Long aMpanPk)
  {
    mPk = aMpanPk;
  }

  /**
   * @return the AFMS Mpan
   */
  @ManyToOne(fetch=FetchType.EAGER, optional=false)
  @JoinColumn(name="MPAN_LNK", nullable=false)
  public AFMSMpan getMpan()
  {
    return mMPan;
  }

  /**
   * @param aMpan the AFMS Mpan
   */
  public void setMpan(AFMSMpan aMpan)
  {
    mMPan = aMpan;
  }

  /**
   * @return the Data Collector agent name
   */
  @Column(name="J0205", nullable=true)
  public String getDataCollector()
  {
    return mDataCollector;
  }

  /**
   * @param aDataCollector the Data Collector agent name
   */
  public void setDataCollector(String aDataCollector)
  {
    mDataCollector = aDataCollector;
  }

  /**
   * @return the Meter Operator agent name
   */
  @Column(name="J0178", nullable=true)
  public String getMeterOperator()
  {
    return mMeterOperator;
  }

  /**
   * @param aMeterOperator the Meter Operator agent name
   */
  public void setMeterOperator(String aMeterOperator)
  {
    mMeterOperator = aMeterOperator;
  }

  /**
   * @return the Meter Operator Effective From Date
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0210", nullable=true)
  public DateTime getMOEffectiveFromDate()
  {
    return mMOEffectiveFromDate;
  }

  /**
   * @param aEffectiveFromDate the Meter Operator Effective From Date
   */
  public void setMOEffectiveFromDate(DateTime aEffectiveFromDate)
  {
    mMOEffectiveFromDate = aEffectiveFromDate;
  }
  

  /**
   * @return the Meter Operator Effective To Date
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0360", nullable=true)
  public DateTime getMOEffectiveToDate()
  {
    return mMOEffectiveToDate;
  }

  /**
   * @param aEffectiveFromDate the Meter Operator Effective To Date
   */
  public void setMOEffectiveToDate(DateTime aEffectiveToDate)
  {
    mMOEffectiveToDate = aEffectiveToDate;
  }

  /**
   * @return the Data Collector Effective From Date
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0219", nullable=true)
  public DateTime getDCEffectiveFromDate()
  {
    return mDCEffectiveFromDate;
  }

  /**
   * @param aEffectiveFromDate the Data Collector Effective From Date
   */
  public void setDCEffectiveFromDate(DateTime aEffectiveFromDate)
  {
    mDCEffectiveFromDate = aEffectiveFromDate;
  }
  

  /**
   * @return the Data Collector Effective From Date
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="J0399", nullable=true)
  public DateTime getDCEffectiveToDate()
  {
    return mDCEffectiveToDate;
  }

  /**
   * @param aEffectiveFromDate the Data Collector Effective To Date
   */
  public void setDCEffectiveToDate(DateTime aEffectiveToDate)
  {
    mDCEffectiveToDate = aEffectiveToDate;
  }
  
  @Transient
  public String toString()
  {
    return "mop[" + getMeterOperator() + "], dc[" + getDataCollector() + "], mpan[" + getMpan().getMpanCore() + "]";
  }
}

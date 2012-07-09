package uk.co.utilisoft.afms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.AgentRoleCodeType;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name="AGENT_HISTORY")
@SuppressWarnings("serial")
public class AFMSAgentHistory extends AFMSDomainObject
{
  private Long mPk;
  private AFMSMpan mMpan;
  private String mAgentId;
  private AgentRoleCodeType mAgentRoleCode;
  private DateTime mAgentEffectiveFromDate;
  private DateTime mAgentEffectiveToDate;
  private String mReasonForChange;

  /**
   * @return the primary key
   */
  @Id
  @Column(name="AGENT_HISTORY_PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_AFMS_FK")
  public Long getPk()
  {
    return mPk;
  }

  /**
   * @param aPk the primary key
   */
  public void setPk(Long aPk)
  {
    mPk = aPk;
  }

  /**
   * @return the AFMS Mpan
   */
  @ManyToOne(fetch=FetchType.EAGER, optional=false)
  @JoinColumn(name="MPAN_LNK", nullable=false)
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

  /**
   * @return the AFMS Agent Id
   */
  @Column(name="AGENT_ID", nullable=false)
  public String getAgentId()
  {
    return mAgentId;
  }

  /**
   * @param aAgentId the AFMS Agent Id
   */
  public void setAgentId(String aAgentId)
  {
    mAgentId = aAgentId;
  }

  /**
   * @return the AFMS Agent Role Code
   */
  @Enumerated(EnumType.STRING)
  @Column(name="AGENT_ROLE_CODE", nullable=false)
  public AgentRoleCodeType getAgentRoleCode()
  {
    return mAgentRoleCode;
  }

  /**
   * @param aAgentRoleCode the AFMS Agent Role Code
   */
  public void setAgentRoleCode(AgentRoleCodeType aAgentRoleCode)
  {
    mAgentRoleCode = aAgentRoleCode;
  }

  /**
   * @return the AFMS Agent Effective From Date
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="AGENT_EFD", nullable=false)
  public DateTime getAgentEffectiveFromDate()
  {
    return mAgentEffectiveFromDate;
  }

  /**
   * @param aAgentEffectiveFromDate the AFMS Agent Effective From Date
   */
  public void setAgentEffectiveFromDate(DateTime aAgentEffectiveFromDate)
  {
    mAgentEffectiveFromDate = aAgentEffectiveFromDate;
  }

  /**
   * @return the AFMS Agent Effective To Date
   */
  @Type(type="org.joda.time.DateTime")
  @Column(name="AGENT_ETD", nullable=false)
  public DateTime getAgentEffectiveToDate()
  {
    return mAgentEffectiveToDate;
  }

  /**
   * @param aEffectiveToDate the Agent Effective To Date
   */
  public void setAgentEffectiveToDate(DateTime aEffectiveToDate)
  {
    mAgentEffectiveToDate = aEffectiveToDate;
  }

  /**
   * @return the AFMS Agent's reason for change
   */
  @Column(name="REASON_FOR_CHANGE", nullable=false)
  public String getReasonForChange()
  {
    return mReasonForChange;
  }

  /**
   * @param aReasonForChange tje AFMS Agent's reason for change
   */
  public void setReasonForChange(String aReasonForChange)
  {
    mReasonForChange = aReasonForChange;
  }
  
  public String toString()
  {
    return "mpan[" + getMpan().getMpanCore() + "], agentId[" + getAgentId() + "], roleCode[" + getAgentRoleCode() + "], efd[" + getAgentEffectiveFromDate() + "], etd[" + getAgentEffectiveToDate() + "]";
  }
}

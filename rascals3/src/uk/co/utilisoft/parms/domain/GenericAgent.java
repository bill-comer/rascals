package uk.co.utilisoft.parms.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.AgentRoleCodeType;


@Entity
@Table(name="PARMS_AGENT")
@AccessType(value="property")
@SuppressWarnings("serial")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="IS_MOP", discriminatorType=DiscriminatorType.CHAR)
@DiscriminatorValue("0")
public class GenericAgent extends BaseVersionedDomainObject<Long, Long, DateTime> implements GenericAgentInterface
{
  private Collection<GridSupplyPoint> mGridSupplyPoints;

  /**
   * agents name
   */
  private String mName;


  /**
   * true if half hourly for first month
   * derived from AFMSMpan.J0082
   */
  private boolean mIsHalfHourMpansFirstMonth;

  /**
   * true if half hourly 2nd month
   * derived from AFMSMpan.J0082
   */
  private boolean mIsHalfHour2ndMonth;

  private boolean mIsNonHalfHourMpansFirstMonth;
  private boolean mIsNonHalfHourMpans2ndMonth;

  private DpiFile mDpiFile;

  @ManyToOne
  @JoinColumn(name = "DPI_FILE_FK")
  @NotNull
  public DpiFile getDpiFile()
  {
    return mDpiFile;
  }

  public void setDpiFile(DpiFile aDpiFile)
  {
    mDpiFile = aDpiFile;
  }

  @Column(name="NAME")
  @NotEmpty
  public String getName()
  {
    return mName;
  }

  public void setName(String aName)
  {
    this.mName = aName;
  }


  @Column(name="HAS_HALF_HOUR_MPANS_1ST_MONTH")
  @NotNull
  public boolean isHalfHourMpansFirstMonth()
  {
    return mIsHalfHourMpansFirstMonth;
  }

  public void setHalfHourMpansFirstMonth(boolean aIsHalfHourly)
  {
    mIsHalfHourMpansFirstMonth = aIsHalfHourly;
  }

  @Column(name="HAS_HALF_HOUR_MPANS_2ND_MONTH")
  @NotNull
  public boolean isHalfHourMpans2ndMonth()
  {
    return mIsHalfHour2ndMonth;
  }

  public void setHalfHourMpans2ndMonth(boolean aIsHalfHourly)
  {
    mIsHalfHour2ndMonth = aIsHalfHourly;
  }


  @Column(name="HAS_NON_HALF_HR_MPANS_1STMONTH")
  @NotNull
  public boolean isNonHalfHourMpansFirstMonth()
  {
    return mIsNonHalfHourMpansFirstMonth;
  }

  public void setNonHalfHourMpansFirstMonth(boolean hasNonHalfHourMpans)
  {
    this.mIsNonHalfHourMpansFirstMonth = hasNonHalfHourMpans;
  }

  @Column(name="HAS_NON_HALF_HR_MPANS_2NDMONTH")
  @NotNull
  public boolean isNonHalfHourMpans2ndMonth()
  {
    return mIsNonHalfHourMpans2ndMonth;
  }

  public void setNonHalfHourMpans2ndMonth(boolean hasNonHalfHourMpans)
  {
    this.mIsNonHalfHourMpans2ndMonth = hasNonHalfHourMpans;
  }

  /**
   * sets the HalfHourly flag for the correct month
   * NB monthT is second Month
   *
   * @param isMonthT
   * @param halfHourlyValue
   */
  @Transient
  public void setAppropiateHalfHourlyFlag(boolean isMonthT, boolean halfHourlyValue)
  {
    if (isMonthT)
    {
      if (halfHourlyValue)
      {
        setHalfHourMpans2ndMonth(true);
      }
      else
      {
        setNonHalfHourMpans2ndMonth(true);
      }
    }
    else
    {
      if (halfHourlyValue)
      {
        setHalfHourMpansFirstMonth(true);
      }
      else
      {
        setNonHalfHourMpansFirstMonth(true);
      }
    }
  }

  /**
   * gets the isHH for the appropiateMonth
   *
   * @param isMonthT
   * @param halfHourlyValue
   */
  @Transient
  public boolean isHalfHourlyForAppropiateMonth(boolean isMonthT, boolean isHalfHourlyValue)
  {
    if (isMonthT)
    {
      if (isHalfHourlyValue)
      {
        return isHalfHourMpans2ndMonth();
      }
      else
      {
        return isNonHalfHourMpans2ndMonth();
      }
    }
    else
    {
      if (isHalfHourlyValue)
      {
        return isHalfHourMpansFirstMonth();
      }
      else
      {
        return isNonHalfHourMpansFirstMonth();
      }
    }
  }

  /**
   * Dummy setter as getter is defined in child class specific to being a DC or a MOP
   * @param aMOP
   */
  private Boolean mMop;

  public void setMop(boolean aIsMop)
  {
    mMop = aIsMop;
  }

  @Column(name="IS_MOP", nullable=false, updatable=false, insertable=false)
  @NotNull
  public boolean isMop()
  {
    return mMop;
  }


  @OneToMany(
      targetEntity=GridSupplyPoint.class
  )
  @Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
  @JoinTable(
      name="PARMS_AGENT_GSP_LINK",
      joinColumns=@JoinColumn(name="AGENT_ID"),
      inverseJoinColumns=@JoinColumn(name="GSP_ID")
  )
  @OrderBy("name ASC")
  public Collection<GridSupplyPoint> getGridSupplyPoints()
  {
    return mGridSupplyPoints;
  }

  public void setGridSupplyPoints(Collection<GridSupplyPoint> aGridSupplyPoints)
  {
    this.mGridSupplyPoints = aGridSupplyPoints;
  }

  @Transient
  public AgentRoleCodeType getRoleCode(boolean isMonthT, boolean isHalfHourlyValue)
  {
    if (isMop())
    {
      return AgentRoleCodeType.getMOPRoleCodeType();
    }
    else
    {
      if (isHalfHourlyValue)
      {
        return AgentRoleCodeType.getHalfHourlyDCRoleCodeType();
      }
      else
      {
        return AgentRoleCodeType.getNonHalfHourlyDCRoleCodeType();
      }
    }
  }

  public GenericAgent()
  {
    this.mGridSupplyPoints = new ArrayList<GridSupplyPoint>();
  }

  public GenericAgent(String aName, boolean aHalfHourly, DpiFile aDpiFile, boolean isMonthT)
  {
    this.mName = aName;
    this.mDpiFile = aDpiFile;
    this.setLastUpdated(new DateTime());
    this.mGridSupplyPoints = new ArrayList<GridSupplyPoint>();
    setAppropiateHalfHourlyFlag(isMonthT, aHalfHourly);
  }

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof GenericAgent))
      return false;

    GenericAgent ga = (GenericAgent)o;

    if (!ga.getName().equals(getName())) {
      System.out.println("this name[" + getName() + "], is dufft to [" + ga.getName() + "]");
    }

    if (!ga.isHalfHourMpans2ndMonth() == isHalfHourMpans2ndMonth()) {
      System.out.println("this isHalfHourly2ndMonth[" + isHalfHourMpans2ndMonth() + "], is difft to [" + ga.isHalfHourMpans2ndMonth() + "]");
    }

    if (!ga.isHalfHourMpansFirstMonth() == isHalfHourMpansFirstMonth()) {
      System.out.println("this isHalfHourlyFirstMonth[" + isHalfHourMpansFirstMonth() + "], is difft to [" + ga.isHalfHourMpansFirstMonth() + "]");
    }
    

    if (!ga.isNonHalfHourMpans2ndMonth() == isNonHalfHourMpans2ndMonth()) {
      System.out.println("this isNonHalfHourly2ndMonth[" + isNonHalfHourMpans2ndMonth() + "], is difft to [" + ga.isNonHalfHourMpans2ndMonth() + "]");
    }

    if (!ga.isNonHalfHourMpansFirstMonth() == isNonHalfHourMpansFirstMonth()) {
      System.out.println("this isNonHalfHourlyFirstMonth[" + isNonHalfHourMpansFirstMonth() + "], is difft to [" + ga.isNonHalfHourMpansFirstMonth() + "]");
    }

    if (!ga.getDpiFile().equals(getDpiFile())) {
      System.out.println("this getDpiFile[" + getDpiFile() + "], is difft to [" + ga.getDpiFile() + "]");
    }

    return ga.getName().equals(getName())
      && ga.isHalfHourMpansFirstMonth() == isHalfHourMpansFirstMonth()
      && ga.isHalfHourMpans2ndMonth() == isHalfHourMpans2ndMonth()
      && ga.isNonHalfHourMpansFirstMonth() == isNonHalfHourMpansFirstMonth()
      && ga.isNonHalfHourMpans2ndMonth() == isNonHalfHourMpans2ndMonth()
      && ga.getDpiFile().equals(getDpiFile())
    ;
  }

  @Override
  public int hashCode()
  {
    int result = 17;

    result = 31 * result + (null == getName() ? 0 : getName().hashCode());
    result = 31 * result + (Boolean.valueOf(isHalfHourMpans2ndMonth()).hashCode());
    result = 31 * result + (Boolean.valueOf(isHalfHourMpansFirstMonth()).hashCode());
    result = 31 * result + (Boolean.valueOf(isNonHalfHourMpans2ndMonth()).hashCode());
    result = 31 * result + (Boolean.valueOf(isNonHalfHourMpansFirstMonth()).hashCode());
    result = 31 * result + (null == getDpiFile() ? 0 : getDpiFile().hashCode());

    return result;
  }

  public GenericAgent replicate(DpiFile replicatedDpiFile, Map<String, GridSupplyPoint> replicatedGSPs)
  {
    GenericAgent replicatedAgent = null;

    if (this.isMop())
    {
      replicatedAgent = new MOP();
    }
    else
    {
      replicatedAgent = new DataCollector();
    }

    //replicatedAgent.setMop(this.isMop());
    replicatedAgent.setName(this.getName());
    replicatedAgent.setHalfHourMpansFirstMonth(this.isHalfHourMpansFirstMonth());
    replicatedAgent.setHalfHourMpans2ndMonth(this.isHalfHourMpans2ndMonth());
    replicatedAgent.setNonHalfHourMpansFirstMonth(this.isNonHalfHourMpansFirstMonth());
    replicatedAgent.setNonHalfHourMpans2ndMonth(this.isNonHalfHourMpans2ndMonth());
    replicatedAgent.setDpiFile(replicatedDpiFile);

    for (GridSupplyPoint gsp : this.getGridSupplyPoints())
    {
/*      GridSupplyPoint replicarGSP = null;
      if (replicatedGSPs.containsKey(gsp.getName()))
      {
        replicarGSP = replicatedGSPs.get(gsp.getName());
      }
      else
      {
        replicatedGSPs.put(replicarGSP.getName(), replicarGSP);
      }*/

      GridSupplyPoint replicarGSP = gsp.replicate(replicatedDpiFile);
      replicatedAgent.getGridSupplyPoints().add(replicarGSP);
    }

    return replicatedAgent;
  }



}

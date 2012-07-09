package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;


@Entity
@Table(name="PARMS_GRID_SUPPLY_POINT")
@SuppressWarnings("serial")
public class GridSupplyPoint extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String mName;
  private boolean mIsHalfHourMpansFirstMonth;
  private boolean mIsHalfHourMpans2ndMonth;

  boolean mIsNonHalfHourMpansFirstMonth;
  boolean mIsNonHalfHourMpans2ndMonth;
  
  private DpiFile mDpiFile;
  private GenericAgent mAgent;

  /**
   * For Spring only
   */
  public GridSupplyPoint() {}

  public GridSupplyPoint(String aName, DpiFile aDpiFile)
  {
    mName = aName;
    mDpiFile = aDpiFile;
    mIsHalfHourMpans2ndMonth = false;
    mIsHalfHourMpansFirstMonth = false;
    mIsNonHalfHourMpans2ndMonth = false;
    mIsNonHalfHourMpansFirstMonth = false;
    setLastUpdated(new DateTime());
  }

  @Column(name="NAME")
  @NotEmpty
  @Length( max = 100 )
  public String getName()
  {
    return mName;
  }

  public void setName(String aName)
  {
    mName = aName;
  }

  @Column(name="HAS_HALF_HOUR_MPANS_1ST_MONTH")
  @NotNull
  public boolean isHalfHourMpansFirstMonth()
  {
    return mIsHalfHourMpansFirstMonth;
  }

  public void setHalfHourMpansFirstMonth(boolean hasHalfHourMpans)
  {
    this.mIsHalfHourMpansFirstMonth = hasHalfHourMpans;
  }

  @Column(name="HAS_HALF_HOUR_MPANS_2ND_MONTH")
  @NotNull
  public boolean isHalfHourMpans2ndMonth()
  {
    return mIsHalfHourMpans2ndMonth;
  }

  public void setHalfHourMpans2ndMonth(boolean hasHalfHourMpans)
  {
    this.mIsHalfHourMpans2ndMonth = hasHalfHourMpans;
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
  
  @Transient
  public boolean hasHalfHourlyMpansForMonth(boolean isMonthT)
  {
    if (isMonthT)
    {
      return isHalfHourMpans2ndMonth();
    }
    else
    {
      return isHalfHourMpansFirstMonth();
    }
  }
  
  @Transient
  public boolean hasNonHalfHourlyMpansForMonth(boolean isMonthT)
  {
    //throw new UnsupportedOperationException("ooops - some coding to do");
    if (isMonthT)
    {
      return isNonHalfHourMpans2ndMonth();
    }
    else
    {
      return isNonHalfHourMpansFirstMonth();
    }
  }

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

  @ManyToOne(
      targetEntity = GenericAgent.class
  )
  @Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
  @JoinTable(
      name="PARMS_AGENT_GSP_LINK",
      joinColumns=@JoinColumn(name="GSP_ID", insertable=false, updatable=false),
      inverseJoinColumns=@JoinColumn(name="AGENT_ID")
  )
  public GenericAgent getAgent()

  {
    return mAgent;
  }

  public void setAgent(GenericAgent aAgent)
  {
    this.mAgent = aAgent;
  }

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof GridSupplyPoint))
      return false;

    GridSupplyPoint ga = (GridSupplyPoint)o;
    return ga.getDpiFile().equals(getDpiFile())
      && ga.isHalfHourMpans2ndMonth() == isHalfHourMpans2ndMonth()
      && ga.isHalfHourMpansFirstMonth() == isHalfHourMpansFirstMonth()
      && ga.isNonHalfHourMpans2ndMonth() == isNonHalfHourMpans2ndMonth()
      && ga.isNonHalfHourMpansFirstMonth() == isNonHalfHourMpansFirstMonth()
      && ga.getName().equals(getName())
    ;
  }

  @Override
  public int hashCode()
  {
    int result = 17;

    result = 31 * result + (null == getDpiFile() ? 0 : getDpiFile().hashCode());
    result = 31 * result + (Boolean.valueOf(isHalfHourMpansFirstMonth()).hashCode());
    result = 31 * result + (Boolean.valueOf(isHalfHourMpans2ndMonth()).hashCode());
    result = 31 * result + (Boolean.valueOf(isNonHalfHourMpansFirstMonth()).hashCode());
    result = 31 * result + (Boolean.valueOf(isNonHalfHourMpans2ndMonth()).hashCode());
    result = 31 * result + (null == getName() ? 0 : getName().hashCode());

    return result;
  }

  public GridSupplyPoint replicate(DpiFile replicatedDpiFile)
  {
    GridSupplyPoint replicatedGridSupplyPoint = new GridSupplyPoint(this.getName(), replicatedDpiFile);
    replicatedGridSupplyPoint.setHalfHourMpans2ndMonth(this.isHalfHourMpans2ndMonth());
    replicatedGridSupplyPoint.setHalfHourMpansFirstMonth(this.isHalfHourMpansFirstMonth());
    replicatedGridSupplyPoint.setNonHalfHourMpans2ndMonth(this.isNonHalfHourMpans2ndMonth());
    replicatedGridSupplyPoint.setNonHalfHourMpansFirstMonth(this.isNonHalfHourMpansFirstMonth());

    return replicatedGridSupplyPoint;
  }
}

package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

@Entity
@AccessType(value="property")
@Table(name="PARMS_SERIAL_CONFIG")
@SuppressWarnings("serial")
public class SerialConfiguration extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String mName;
  private boolean isEnabled;
  private boolean isHalfHourly;
  private boolean isMop;
  private boolean isDataCollector;
  private boolean monthT;

  /**
   * @return the name
   */
  @Column(name="NAME")
  @NotEmpty
  public String getName()
  {
    return mName;
  }

  public void setName(String name)
  {
    this.mName = name;
  }

  /**
   * @return true if this serial is enabled
   */
  @Column(name="IS_ENABLED")
  @NotNull
  public boolean isEnabled()
  {
    return isEnabled;
  }

  public void setEnabled(boolean aIsEnabled)
  {
    this.isEnabled = aIsEnabled;
  }

  /**
   * @return true if this is for half hourly
   */
  @Column(name="IS_HALF_HOURLY")
  @NotNull
  public boolean isHalfHourly()
  {
    return isHalfHourly;
  }

  public void setHalfHourly(boolean aIsHalfHourly)
  {
    this.isHalfHourly = aIsHalfHourly;
  }

  /**
   * @return true if this is for a mop
   */
  @Column(name="IS_MOP")
  @NotNull
  public boolean isMop()
  {
    return isMop;
  }

  public void setMop(boolean aIsMop)
  {
    this.isMop = aIsMop;
  }

  /**
   * @return true if this is for a dc
   */
  @Column(name="IS_DC")
  @NotNull
  public boolean isDataCollector()
  {
    return isDataCollector;
  }

  public void setDataCollector(boolean aIsDataCollector)
  {
    this.isDataCollector = aIsDataCollector;
  }

  /**
   * @return true if this is for month T
   */
  @Column(name="IS_MONTH_T")
  @NotNull
  public boolean isMonthT()
  {
    return monthT;
  }

  public void setMonthT(boolean aIsMonthT)
  {
    this.monthT = aIsMonthT;
  }
  
  public String toString()
  {
    return "name[" + getName() + "],isHH[" + isHalfHourly + "],isMonthT[" + isMonthT() + "], isDC[" + isDataCollector() + "], isMOP[" + isMop() + "]";
  }
}

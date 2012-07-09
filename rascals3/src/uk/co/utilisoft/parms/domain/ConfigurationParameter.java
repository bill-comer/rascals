package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Entity
@Table(name="PARMS_CONFIGURATION_PARAMETER")
@SuppressWarnings("serial")
public class ConfigurationParameter extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private ConfigurationParameter.NAME mName;
  private String mValue;
  private String mDescription;

  public ConfigurationParameter() { }

  public ConfigurationParameter(ConfigurationParameter.NAME aName, String aValue, String aDescription)
  {
    mName = aName;
    mValue = aValue;
    mDescription = aDescription;
    setLastUpdated(new DateTime());
  }

  @Column(name="NAME",unique=true, length=255)
  @Enumerated(EnumType.STRING)
  @NotNull
  public ConfigurationParameter.NAME getName()
  {
    return mName;
  }

  public void setName(ConfigurationParameter.NAME aName)
  {
    mName = aName;
  }

  @Column(name="VALUE",length=255)
  @NotEmpty
  @Length (max = 255)
  public String getValue()
  {
    return mValue;
  }

  public void setValue(String aValue)
  {
    mValue = aValue;
  }

  @Column(name="DESCRIPTION",length=255)
  @NotEmpty
  @Length( max = 255 )
  public String getDescription()
  {
    return mDescription;
  }

  public void setDescription(String aDescription)
  {
    mDescription = aDescription;
  }

  public static enum NAME
  {
    PARMS_DPI_FILE_LOCATION, PARMS_SP04_FILE_LOCATION, P0028_UPLOAD_ERROR_FILE_LOCATION, NOT_DEFINED;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof ConfigurationParameter))
      return false;

    ConfigurationParameter cp = (ConfigurationParameter) o;
    return cp.getName().equals(getName());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    int result = 17;
    byte[] data = getName() != null ? getName().name().getBytes() : new byte[0];
    for (byte b : data)
    {
      result = 31 * result + b;
    }

    result = 31 * result + (null == getName() ? 0 : getName().hashCode());
    return result;
  }

  public static NAME getName(String aValue)
  {
    try
    {
      return NAME.valueOf(aValue);
    }
    catch (Exception e)
    {
      return null;
    }
  }
}

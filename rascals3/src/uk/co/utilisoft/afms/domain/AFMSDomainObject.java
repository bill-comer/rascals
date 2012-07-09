package uk.co.utilisoft.afms.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.domain.DomainObject;

/**
 * @author Philip Lau
 * @version 1.0
 */
@MappedSuperclass
@AccessType(value="property")
@SuppressWarnings("serial")
public abstract class AFMSDomainObject implements Serializable, DomainObject<Integer, DateTime>
{
  @Transient
  public abstract Long getPk();
  
  private DateTime mLastUpdated;

  /**
   * @see uk.co.formfill.hibernateutils.domain.DomainObject#getLastUpdated()
   */
  @Override
  @Type(type="org.joda.time.DateTime")
  @Column(name="LAST_UPD", nullable=false)
  public DateTime getLastUpdated()
  {
    return mLastUpdated;
  }

  /**
   * @see uk.co.formfill.hibernateutils.domain.DomainObject#setLastUpdated(java.lang.Object)
   */
  @Override
  public void setLastUpdated(DateTime aLastUpdated)
  {
    mLastUpdated = aLastUpdated;
  }

  /**
   * (Versioning not supported by this domain object marked as @Transient)
   *
   * @see uk.co.formfill.hibernateutils.domain.DomainObject#getVersion()
   */
  @Override
  @Transient
  public Integer getVersion()
  {
    return null;
  }

  /**
   * (Versioning not supported by this domain object)
   *
   * @param aVersion the version
   */
  public void setVersion(Integer aVersion)
  {

  }
}

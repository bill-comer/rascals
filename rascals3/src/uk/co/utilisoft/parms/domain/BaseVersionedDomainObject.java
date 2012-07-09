package uk.co.utilisoft.parms.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
//import javax.persistence.PrePersist;
//import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;

import uk.co.formfill.hibernateutils.domain.DomainObject;

/**
 * @author Philip Lau
 * @version 1.0
 */
@MappedSuperclass
@AccessType(value="property")
@SuppressWarnings("serial")
public abstract class BaseVersionedDomainObject<IdType extends Number, VersionType extends Number, LastUpdType>
    implements Serializable, DomainObject<VersionType, LastUpdType>
{
  private IdType mPk;
  private VersionType mVersion;
  private LastUpdType mLastUpdated;

  @Id
  @Column(name="PK", nullable=false)
  @GeneratedValue(strategy=GenerationType.AUTO, generator="generate_seq")
  @SequenceGenerator(name="generate_seq", sequenceName = "SEQ_PARMS_FK")
  public IdType getPk()
  {
    return mPk;
  }

  public void setPk(IdType aPk)
  {
    this.mPk = aPk;
  }

  /**
   * This field is maintained by hibernate for versioning.
   *
   * @see uk.co.formfill.hibernateutils.domain.DomainObject#getVersion()
   */
  @Column(name="VERSION")
  @Version
  public VersionType getVersion()
  {
    return mVersion;
  }
/*
  @PrePersist
  @PreUpdate
  public void sortlastupdate()
  {
    setLastUpdated(new DateTime());
  }*/


  /**
   * @param aVersion the version
   */
  public void setVersion(VersionType aVersion)
  {
    this.mVersion = aVersion;
  }

  /**
   * @see uk.co.formfill.hibernateutils.domain.DomainObject#getLastUpdated()
   */
  @Column(name="LAST_UPDATED")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public LastUpdType getLastUpdated()
  {
    return mLastUpdated;
  }

  /**
   * @see uk.co.formfill.hibernateutils.domain.DomainObject#setLastUpdated(java.lang.Object)
   */
  public void setLastUpdated(LastUpdType aLastUpdated)
  {
    this.mLastUpdated = aLastUpdated;
  }
}

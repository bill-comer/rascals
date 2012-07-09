package uk.co.utilisoft.rascals.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;

@Entity
@AccessType(value="property")
@Table(name="STROKE")
@SuppressWarnings("serial")
public class Stroke extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String name;

  @Column(name="name")
  @NotNull

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}

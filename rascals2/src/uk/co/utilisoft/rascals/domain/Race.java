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
@Table(name="RACE")
@SuppressWarnings("serial")
public class Race extends BaseVersionedDomainObject<Long, Long, DateTime>
{

  private long distance;
  private boolean male;
  
  @Column(name = "distance")
  @NotNull
  public long getDistance()
  {
    return distance;
  }
  public void setDistance(long distance)
  {
    this.distance = distance;
  }
  
  @Column(name="male")
  @NotNull
  public boolean isMale()
  {
    return male;
  }
  public void setMale(boolean male)
  {
    this.male = male;
  }

  
  
}

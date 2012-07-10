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
@Table(name="INDIVIDUAL_RACE")
@SuppressWarnings("serial")
public class IndividualRace extends BaseVersionedDomainObject<Long, Long, DateTime>
{

  private long distance;
  private boolean male;
  private String stroke;
  private long age;
  
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
  

  @Column(name="stroke")
  @NotNull
  public String getStroke()
  {
    return stroke;
  }
  public void setStroke(String stroke)
  {
    this.stroke = stroke;
  }

  @Column(name="age")
  @NotNull
  public long getAge()
  {
    return age;
  }
  public void setAge(long age)
  {
    this.age = age;
  }

  
  
}

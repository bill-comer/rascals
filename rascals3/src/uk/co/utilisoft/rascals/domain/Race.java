package uk.co.utilisoft.rascals.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.OptimisticLock;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;

@Entity
@AccessType(value="property")
@Table(name="RACE")
public class Race extends BaseVersionedDomainObject<Long, Long, DateTime>
{

  private String raceType;
  private long distance;
  private boolean male;
  private String stroke;
  private long age;
  
  private Gala gala;
  @ManyToOne(fetch = FetchType.EAGER)
  @OptimisticLock(excluded=true)
  @JoinColumn(name = "GALA_FK", nullable = false)
  @NotNull
  public Gala getGala()
  {
    return gala;
  }
  public void setGala(Gala aGala)
  {
    this.gala = aGala;
  }
  
  
  @Column(name = "RACE_TYPE")
  @NotNull
  public String getRaceType()
  {
    return raceType;
  }
  public void setRaceType(String raceType)
  {
    this.raceType = raceType;
  }
  
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

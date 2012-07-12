package uk.co.utilisoft.rascals.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;

@Entity
@AccessType(value="property")
@Table(name="GALA")
@SuppressWarnings("serial")
public class Gala extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  
  private DateTime eventDate;
  private boolean atHome;
  private String postcode;

  private String name;
  private String league;
  
  private DateTime eventDateOfBirthDate;
  
  @Column(name="EVENT_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getEventDate()
  {
    return eventDate;
  }
  public void setEventDate(DateTime when)
  {
    this.eventDate = when;
  }
  
  @Column(name="at_home")
  @NotNull
  public boolean isAtHome()
  {
    return atHome;
  }
  public void setAtHome(boolean atHome)
  {
    this.atHome = atHome;
  }
  
  @Column(name="postcode")
  @NotNull
  public String getPostcode()
  {
    return postcode;
  }
  public void setPostcode(String postcode)
  {
    this.postcode = postcode;
  }
  

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
  

  @Column(name="league")
  @NotNull
  public String getLeague()
  {
    return league;
  }
  public void setLeague(String league)
  {
    this.league = league;
  }
  
  @Column(name="EVENT_DATE_OF_BIRTH_DATE")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getEventDateOfBirthDate()
  {
    return eventDateOfBirthDate;
  }
  public void setEventDateOfBirthDate(DateTime eventDateOfBirthDate)
  {
    this.eventDateOfBirthDate = eventDateOfBirthDate;
  }
  

  private List<Race> races;
  @OneToMany(mappedBy = "gala")
  @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
  @LazyCollection(LazyCollectionOption.TRUE)
  public List<Race> getRaces()
  {
    return races;
  }
  public void setRaces(List<Race> aRaces)
  {
    this.races = aRaces;
  }
  
}

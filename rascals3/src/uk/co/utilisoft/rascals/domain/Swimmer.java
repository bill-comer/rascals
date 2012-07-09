package uk.co.utilisoft.rascals.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;

@Entity
@AccessType(value="property")
@Table(name="SWIMMER")
@SuppressWarnings("serial")
public class Swimmer extends BaseVersionedDomainObject<Long, Long, DateTime>
{

  private String surname;
  private String firstname;
  private DateTime dateOfBirth;
  private boolean male;
  
  
  @Column(name="surname")
  @NotNull
  public String getSurname()
  {
    return surname;
  }
  public void setSurname(String surname)
  {
    this.surname = surname;
  }
  

  @Column(name="firstname")
  @NotNull
  public String getFirstname()
  {
    return firstname;
  }
  public void setFirstname(String firstName)
  {
    this.firstname = firstName;
  }
  
  @Column(name="date_of_birth")
  @Type(type="org.joda.time.DateTime")
  @NotNull
  public DateTime getDateOfBirth()
  {
    return dateOfBirth;
  }
  public void setDateOfBirth(DateTime dateOfBirth)
  {
    this.dateOfBirth = dateOfBirth;
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

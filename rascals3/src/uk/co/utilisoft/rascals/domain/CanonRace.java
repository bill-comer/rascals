package uk.co.utilisoft.rascals.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.AccessType;
import org.joda.time.DateTime;

import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;

@Entity
@AccessType(value="property")
@Table(name="CANON_RACE")
@SuppressWarnings("serial")
public class CanonRace extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private long boy1;
  private long boy2;
  private long girl1;
  private long girl2;
  

  @Column(name="boy1")
  @NotNull
  public long getBoy1()
  {
    return boy1;
  }
  public void setBoy1(long boy1)
  {
    this.boy1 = boy1;
  }
  
  @Column(name="boy2")
  @NotNull
  public long getBoy2()
  {
    return boy2;
  }
  public void setBoy2(long boy2)
  {
    this.boy2 = boy2;
  }
  
  @Column(name="girl1")
  @NotNull
  public long getGirl1()
  {
    return girl1;
  }
  public void setGirl1(long girl1)
  {
    this.girl1 = girl1;
  }
  
  @Column(name="girl2")
  @NotNull
  public long getGirl2()
  {
    return girl2;
  }
  public void setGirl2(long girl2)
  {
    this.girl2 = girl2;
  }
  
  
}

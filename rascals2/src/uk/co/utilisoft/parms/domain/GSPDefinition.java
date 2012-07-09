package uk.co.utilisoft.parms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;


@Entity
@Table(name="PARMS_GSP_DEFINITION")
@SuppressWarnings("serial")
public class GSPDefinition extends BaseVersionedDomainObject<Long, Long, DateTime>
{
  private String mName;
  
  
  
  public GSPDefinition()
  {
  }
  
  

  public GSPDefinition(String aName)
  {
    this.mName = aName;
  }



  @Column(name="NAME")
  @NotEmpty
  @Length( max = 2 )
  public String getName()
  {
    return mName;
  }

  public void setName(String aName)
  {
    mName = aName;
  }
}

package uk.co.utilisoft.parms.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@SuppressWarnings("serial")
@DiscriminatorValue("0")
public class DataCollector extends GenericAgent
{

  public DataCollector()
  {
    super();
    setMop(false);
  }

  public DataCollector(String aName, boolean aHalfHourly, DpiFile aDpiFile, boolean isMonthT)
  {
    super(aName, aHalfHourly, aDpiFile, isMonthT);
    setMop(false);
  }


  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if (o instanceof MOP)
    {
      // can be a DC or a GA BUT not a MOP
      return false;
    }
    if(!(o instanceof GenericAgent))
    {
      return false;
    }

    GenericAgent agent = (GenericAgent)o;
    if (agent.isMop())
    {
      // this should be a DC
      return false;
    }

    return super.equals(o);
  }

  @Override
  public int hashCode()
  {
    return super.hashCode();
  }
}

package uk.co.utilisoft.parms.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@SuppressWarnings("serial")
@DiscriminatorValue("1")
public class MOP extends GenericAgent
{
  public MOP()
  {
    super();
    setMop(true);
  }

  public MOP(String aName, boolean aHalfHourly, DpiFile aDpiFile, boolean isMonthT)
  {
    super(aName, aHalfHourly, aDpiFile, isMonthT);
    setMop(true);
  }

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof GenericAgent))
    {
      return false;
    }
    if (o instanceof DataCollector)
    {
   // can be a MOP or a GA BUT not a DC
      return false;
    }
    GenericAgent agent = (GenericAgent)o;
    if (!agent.isMop())
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

package uk.co.utilisoft.parms.domain;

import java.util.Collection;

import org.joda.time.DateTime;


public interface GenericAgentInterface
{
  public String getName();

  public void setName(String aName);

  public boolean isHalfHourMpansFirstMonth();
  public boolean isHalfHourMpans2ndMonth();

  public void setHalfHourMpansFirstMonth(boolean aHalfHourly);
  public void setHalfHourMpans2ndMonth(boolean aHalfHourly);

  public void setMop(boolean aIsMOP);

  public boolean isMop();

  public Long getPk();
  public void setPk(Long aPk);

  public DateTime getLastUpdated();
  public void setLastUpdated(DateTime aLastUpdated);

  public Long getVersion();
  public void setVersion(Long aVersion);
  
  public Collection<GridSupplyPoint> getGridSupplyPoints();

  //public List<Mpan> getMpans();
  //public void setMpans(List<Mpan> aMpans);
}

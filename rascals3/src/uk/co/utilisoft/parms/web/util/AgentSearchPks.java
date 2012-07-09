package uk.co.utilisoft.parms.web.util;

import java.util.List;

public class AgentSearchPks
{
  private List<Long> mAgentPks;
  private List<Long> mGridSupplyPointPks;

  public AgentSearchPks(List<Long> aAgentPks, List<Long> aGridSupplyPointPks)
  {
    mAgentPks = aAgentPks;
    mGridSupplyPointPks = aGridSupplyPointPks;
  }

  public List<Long> getAgentPks()
  {
    return mAgentPks;
  }

  public void setAgentPks(List<Long> aAgentPks)
  {
    this.mAgentPks = aAgentPks;
  }

  public List<Long> getGridSupplyPointPks()
  {
    return mGridSupplyPointPks;
  }

  public void setmGridSupplyPointPks(List<Long> aGridSupplyPointPks)
  {
    this.mGridSupplyPointPks = aGridSupplyPointPks;
  }


}

package uk.co.utilisoft.afms.dao;

import uk.co.utilisoft.parms.AgentRoleCodeType;



public class AgentHistorySearch
{
  String mpanCore;
  AgentRoleCodeType mAgentRoleCodeType;

  public AgentRoleCodeType getAgentRoleCodeType()
  {
    return mAgentRoleCodeType;
  }
  

  public String getMpanCore()
  {
    return mpanCore;
  }

  
  public AgentHistorySearch(AgentRoleCodeType aAgentRoleCode, String aMpanCore)
  {
    super();
    this.mAgentRoleCodeType = aAgentRoleCode;
    this.mpanCore = aMpanCore;
  }
  
  public String toString()
  {
    return "AgentHistorySearch AgentRoleCode[" + mAgentRoleCodeType + "],mpan[" + mpanCore + "]";
  }
  

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if(!(o instanceof AgentHistorySearch))
      return false;

    AgentHistorySearch as = (AgentHistorySearch)o;
    return as.getMpanCore().equals(getMpanCore()) 
        && as.getAgentRoleCodeType().getValue().equals(getAgentRoleCodeType().getValue())
        ;
  }

  @Override
  public int hashCode()
  {
    int result = 17;
    
    result = 31 * result + (null == getMpanCore() ? 0 : getMpanCore().hashCode());
    result = 31 * result + (null == getAgentRoleCodeType().getValue() ? 0 : getAgentRoleCodeType().getValue().hashCode());
    return result;
  }
  
  
}

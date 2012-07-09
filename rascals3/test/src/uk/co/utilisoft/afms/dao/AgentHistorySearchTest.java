package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.utilisoft.parms.AgentRoleCodeType;


public class AgentHistorySearchTest
{

  @Test
  public void testSame() throws Exception
  { 
    AgentHistorySearch s1 = new AgentHistorySearch(AgentRoleCodeType.getHalfHourlyDCRoleCodeType(), "fred");
    AgentHistorySearch s2 = new AgentHistorySearch(AgentRoleCodeType.getHalfHourlyDCRoleCodeType(), "fred");
    
    assertTrue(s1.equals(s2));
  }
  

  @Test
  public void testDifftMpan() throws Exception
  { 
    AgentHistorySearch s1 = new AgentHistorySearch(AgentRoleCodeType.getHalfHourlyDCRoleCodeType(), "fred");
    AgentHistorySearch s2 = new AgentHistorySearch(AgentRoleCodeType.getHalfHourlyDCRoleCodeType(), "bert");
    
    assertFalse(s1.equals(s2));
  }
  

  @Test
  public void testDifftRCT() throws Exception
  { 
    AgentHistorySearch s1 = new AgentHistorySearch(AgentRoleCodeType.getHalfHourlyDCRoleCodeType(), "fred");
    AgentHistorySearch s2 = new AgentHistorySearch(AgentRoleCodeType.getMOPRoleCodeType(), "fred");
    
    assertFalse(s1.equals(s2));
  }
}

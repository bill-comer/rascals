package uk.co.utilisoft.parms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class AgentRoleCodeTypeTest
{

  @Test
  public void testTypeMOP() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getMOPRoleCodeType());
    assertEquals("M", type.getValue());
  }
  
  

  @Test
  public void testTypeHHDC() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getHalfHourlyDCRoleCodeType());
    assertEquals("C", type.getValue());
  }

  @Test
  public void testTypeNONHHDC() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getNonHalfHourlyDCRoleCodeType());
    assertEquals("D", type.getValue());
  }
  

  @Test
  public void testTypeSupplier() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getSupplierType());
    assertEquals("X", type.getValue());
  }
  
  @Test
  public void testCheckisSupplierType() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getSupplierType());
    assertTrue(type.isSupplierType());
    assertFalse(type.isHHDCType());
    assertFalse(type.isNonHHDCType());
    assertFalse(type.isMOPType());
  }
  

  @Test
  public void testCheckisHHDCType() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getHalfHourlyDCRoleCodeType());
    assertTrue(type.isHHDCType());
    assertFalse(type.isSupplierType());
    assertFalse(type.isNonHHDCType());
    assertFalse(type.isMOPType());
  }
  

  @Test
  public void testCheckisNonHHDCType() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getNonHalfHourlyDCRoleCodeType());
    assertTrue(type.isNonHHDCType());
    assertFalse(type.isHHDCType());
    assertFalse(type.isSupplierType());
    assertFalse(type.isMOPType());
  }
  

  @Test
  public void testCheckisMOPType() throws Exception
  { 
    AgentRoleCodeType type = (AgentRoleCodeType.getMOPRoleCodeType());
    assertTrue(type.isMOPType());
    assertFalse(type.isNonHHDCType());
    assertFalse(type.isHHDCType());
    assertFalse(type.isSupplierType());
  }
}

package uk.co.utilisoft.parms;

/**
 * @author Philip Lau
 * @version 1.0
 */
public enum AgentRoleCodeType
{
  X("Supplier", "X"), C("HH DATA COLLECTOR", "C"), D("NHH DATA COLLECTOR", "D"), M("METER OPERATOR", "M"), A("UNKNOWN - NOT USED", "A"), B("DATA AGGREGATOR - NOT USED", "B");

  private String mDescription;
  private String mValue;

  private AgentRoleCodeType(String aDescription, String aValue)
  {
    mDescription = aDescription;
    mValue = aValue;
  }

  public String getDescription()
  {
    return mDescription;
  }
  
  public String getValue()
  {
    return mValue;
  }
  
  public static AgentRoleCodeType getMOPRoleCodeType()
  {
    return AgentRoleCodeType.M;
  }
  
  public static AgentRoleCodeType getHalfHourlyDCRoleCodeType()
  {
    return AgentRoleCodeType.C;
  }

  public static AgentRoleCodeType getNonHalfHourlyDCRoleCodeType()
  {
    return AgentRoleCodeType.D;
  }

  public static AgentRoleCodeType getSupplierType()
  {
    return AgentRoleCodeType.X;
  }

  public boolean isSupplierType()
  {
    if (mValue.equals(getSupplierType().getValue()))
      return true;
    return false;
  }

  public boolean isHHDCType()
  {
    if (mValue.equals(getHalfHourlyDCRoleCodeType().getValue()))
      return true;
    return false;
  }

  public boolean isNonHHDCType()
  {
    if (mValue.equals(getNonHalfHourlyDCRoleCodeType().getValue()))
      return true;
    return false;
  }

  public boolean isMOPType()
  {
    if (mValue.equals(getMOPRoleCodeType().getValue()))
      return true;
    return false;
  }
}

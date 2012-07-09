package uk.co.utilisoft.parms;

/**
 * @author Bill Comer
 * @version 1.0
 */
public enum MeterRegisterType
{
  C("Cumulative", "C"), 
  M("Maximum Demand", "M"), 
  ONE("Cumulative Maximum Demand", "1"), 
  TWO("Month End Cumulative", "2"), 
  THREE("Month End Maximum Demand", "3"), 
  FOUR("Month End Cumulative Maximum Demand", "4");

  private String mDescription;
  private String mValue;

  private MeterRegisterType(String aDescription, String aValue)
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
  
  
  
  public static MeterRegisterType getCumulativeType()
  {
    return MeterRegisterType.C;
  }

  

  public boolean isCumulativeType()
  {
    if (mValue.equals(getCumulativeType().getValue()))
      return true;
    return false;
  }

 
}

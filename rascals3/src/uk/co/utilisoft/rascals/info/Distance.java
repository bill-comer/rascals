package uk.co.utilisoft.rascals.info;

public enum Distance
{
  TWENTY_FIVE(25),   // 25 m
  FIFTY(50),         // 50 m
  HUNDRED(100);      // 100m
  

  private long distance;
  
  private Distance(long aDistance)
  {
    distance = aDistance;
  }

  /**
   * @return the description of the Audit
   */
  public long getDescription()
  {
    return distance;
  }
 
}

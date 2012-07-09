package uk.co.utilisoft.parms.file;

public class PoolFooter
{
  public String createFooter(long aChecksum, int aRecordCount)
  {
    return getRecordType() + getSeperator() 
      + getCountPlus1(aRecordCount)  + getSeperator()
      + aChecksum;
  }
  
  /**
   * Needs to be count plus 1 as the recordCount does not include the footer
   * @param aCalculator
   * @return
   */
  private int getCountPlus1(int aRecordCount)
  {
    return aRecordCount + 1;
  }
  
  public String getRecordType()
  {
    return "ZPT";
  }

  private final String getSeperator()
  {
    return "|";
  }
}

package uk.co.utilisoft.parms.file;

public interface ChecksumCalculator
{

  public void addLineToCheckSum(String aData);
  
  public long getCheckSum();
  
  public int getRecordCount();
  
}

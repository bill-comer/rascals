package uk.co.utilisoft.parms.file;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("parms.poolCheckSumCalculator")
public class PoolChecksumCalculator implements ChecksumCalculator
{
  public PoolChecksumCalculator()
  {
    resetRecordCount();
  }

  private int mCheckSum;
  
  private int mRecordCount = 0;
  
  /**
   * Adds a line of data to the checksum calculation
   * @param aData the data to add
   */
  public void addLineToCheckSum(String aData)
  {
    if (aData == null)
    {
      return;
    }

    int len = aData.length();
    int pos = 0;

    while (pos < len)
    {
      int nextXOR = 0;
      int leftShift = 24;

      while (leftShift >= 0)
      {
        if (pos >= len)
        {
          break;
        }

        int nextChar = aData.charAt(pos++);
        nextChar <<= leftShift;
        nextXOR |= nextChar;
        leftShift -= 8;
      }
      mCheckSum ^= nextXOR;
    }
    mRecordCount++;
  }

  /**
   * Calculates the checksum
   * @return the checksum
   */
  public long getCheckSum()
  {
    if (mCheckSum >= 0)
    {
      return mCheckSum;
    }

    return (4294967296L + mCheckSum);  //4294967296 = 2^32
  }

  public int getRecordCount()
  {
    return mRecordCount;
  }

  private void resetRecordCount()
  {
    mRecordCount = 0;
    mCheckSum = 0;
  }
  
  
}

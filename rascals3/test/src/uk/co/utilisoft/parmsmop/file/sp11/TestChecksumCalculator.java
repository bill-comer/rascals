package uk.co.utilisoft.parmsmop.file.sp11;

import uk.co.utilisoft.parms.file.ChecksumCalculator;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;

class TestChecksumCalculator extends PoolChecksumCalculator implements ChecksumCalculator
{
  @Override
  public long getCheckSum()
  {
    return 998877;
  }
}

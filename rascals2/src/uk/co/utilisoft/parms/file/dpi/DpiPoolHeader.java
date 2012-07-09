package uk.co.utilisoft.parms.file.dpi;

import uk.co.utilisoft.parms.file.PoolHeader;

public class DpiPoolHeader extends PoolHeader
{
  @Override
  public String getFileType()
  {
    return "P0135001";
  }

  @Override
  public String getRecordType()
  {
    return "ZHD";
  }
}

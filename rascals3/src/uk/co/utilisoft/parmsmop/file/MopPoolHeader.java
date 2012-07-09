package uk.co.utilisoft.parmsmop.file;

import uk.co.utilisoft.parms.file.PoolHeader;

public abstract class MopPoolHeader extends PoolHeader
{

  @Override
  public String getFromRoleCode()
  {
    return "M";
  }
  

  @Override
  public String getRecordType()
  {
    return "ZHD";
  }
}

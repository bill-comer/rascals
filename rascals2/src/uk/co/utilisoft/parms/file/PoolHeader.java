package uk.co.utilisoft.parms.file;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public abstract class PoolHeader
{
  abstract public String getRecordType();
  
  abstract public String getFileType();
  
  
  public String createHeader(String aSupplierId)
  {
    return getRecordType() + getSeperator() 
      + getFileType() + getSeperator() 
      + getFromRoleCode() + getSeperator()
      + aSupplierId.toUpperCase() + getSeperator()
      + getToRoleCode() + getSeperator()
      + getToParticipantId() + getSeperator()
      + getCreationDate();
      
  }
  
  final String getCreationDate()
  {
    DateTime now = new DateTime();
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    return now.toString(fmt);
    
  }
  
  private final String getSeperator()
  {
    return "|";
  }
  
  public String getFromRoleCode()
  {
    return "X";
  }

  
  private final String getToRoleCode()
  {
    return "Z";
  }
  

  private final String getToParticipantId()
  {
    return "POOL";
  }
}

package uk.co.utilisoft.parms.util.csv;

import org.joda.time.DateTime;

import uk.co.utilisoft.parms.MPANCore;


public class ClassToImport
{
  //the meter max demand value
  Long maxDemand;
  
  String meterRegisterId;

  // the mpan for this reading
  MPANCore mpan;

  // the datetime of this reading
  DateTime readingDate;

  // Agent DC Collector Id as it appears in the P0028 File
  String dcAgentName;

  public Long getMaxDemand()
  {
    return maxDemand;
  }

  public void setMaxDemand(Long maxDemand)
  {
    this.maxDemand = maxDemand;
  }

  public String getMeterRegisterId()
  {
    return meterRegisterId;
  }

  public void setMeterRegisterId(String meterRegisterId)
  {
    this.meterRegisterId = meterRegisterId;
  }

  public MPANCore getMpan()
  {
    return mpan;
  }

  public void setMpan(MPANCore mpan)
  {
    this.mpan = mpan;
  }

  public DateTime getReadingDate()
  {
    return readingDate;
  }

  public void setReadingDate(DateTime readingDate)
  {
    this.readingDate = readingDate;
  }

  public String getDcAgentName()
  {
    return dcAgentName;
  }

  public void setDcAgentName(String dcAgentName)
  {
    this.dcAgentName = dcAgentName;
  }
  
  
  
}

package uk.co.utilisoft.parms.util.csv;

/**
 * @author Philip Lau
 * @version 1.0
 */
public enum ColumnMappingName
{
  DC_AGENT_NAME("dcAgentName", "DC"),
  MAX_DEMAND("maxDemand", "MD"),
  METER_SERIAL_ID("meterSerialId", "Meter Serial Id"),
  MPAN("mpan", "MPAN"),
  READING_DATE("readingDate", "Reading Date"),
  METER_REGISTER_ID("meterRegisterId", "Meter Register Id");

  private String mName;
  private String mLabel;

  private ColumnMappingName(String aName, String aLabel)
  {
    mName = aName;
    mLabel = aLabel;
  }

  /**
   * @return the column mapping name
   */
  public String getName()
  {
    return mName;
  }

  /**
   * @return the column mapping name label
   */
  public String getLabel()
  {
    return mLabel;
  }
}

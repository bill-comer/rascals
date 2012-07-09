package uk.co.utilisoft.parms.file.sp04;


/**
 * Fault Reason Type
 */
public enum Sp04FaultReasonType
{
  MID_AFTER_T("Meter Installation Deadline is after Month T", "1"),
  NO_AFMS_METER("NO AFMSMeter for Meter Register ID", "2"),
  NO_MPAN_FOR_CRITERIA("NO MPAN for Search Criteria", "3"),
  NO_MPAN_FOR_METER("NO AFMS MPAN for Meter", "4"),
  MPAN_IS_ALREADY_HALF_HOURLY_METERING("MPAN Already has Half Hourly Metering", "5"),
  SUPPLIER_IS_NOT_CORRECT("No Longer the Supplier", "6"),
  INVALID_MPAN("MPAN is invalid", "7"),
  METER_MPAN_DOES_NOT_MATCH_P0028("MPAN for Meter does not match the P0028 row", "8"),
  DC_IS_NOT_CORRECT("Data Collector in P0028 does not match", "9"),
  MD_VALUE_LESS_100("MD value is <= 100", "10"),
  D0268_RECEIVED_IN_TIME("D0268 Received In Time", "11"),
  NO_ACTIVE_AFMS_METER("NO Active AFMSMeter for Meter Register ID", "12"),
  HH_MPAN_INVALID("Half Hourly Mpan Invalid", "13"),
  MD_VALUE_MATCHES_OR_EXCEEDS_THRESHOLD("MD value >= ", "14");

  private String mDescription;
  private String mValue;

  private Sp04FaultReasonType(String aDescription, String aValue)
  {
    mDescription = aDescription;
    mValue = aValue;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return mDescription;
  }

  /**
   * @return the value
   */
  public String getValue()
  {
    return mValue;
  }

  /**
   * @return fault reason for meter installation date after t month
   */
  public static Sp04FaultReasonType getMIDAfterMonthT()
  {
    return Sp04FaultReasonType.MID_AFTER_T;
  }

  /**
   * @return fault reason for no afms meter for a meter serial id
   */
  public static Sp04FaultReasonType getNoAfmsMeterForMeterSerialId()
  {
    return Sp04FaultReasonType.NO_AFMS_METER;
  }

  /**
   * @return fault reason for no currently active afms meter for a meter serial id
   */
  public static Sp04FaultReasonType getNoActiveAfmsMeterForMeterSerialId()
  {
    return Sp04FaultReasonType.NO_ACTIVE_AFMS_METER;

  }

  /**
   * @return fault reason for no mpan for a search criteria
   */
  public static Sp04FaultReasonType getNoMpanForSearchCriteria()
  {
    return Sp04FaultReasonType.NO_MPAN_FOR_CRITERIA;
  }

  /**
   * @return fault reason for no mpan for a meter
   */
  public static Sp04FaultReasonType getNoMpanForMeter()
  {
    return Sp04FaultReasonType.NO_MPAN_FOR_METER;
  }

  /**
   * @return fault reason for mpan is already on half hourly metering
   */
  public static Sp04FaultReasonType getMpanIsAlreadyHalfHourlyMetering()
  {
    return Sp04FaultReasonType.MPAN_IS_ALREADY_HALF_HOURLY_METERING;
  }

  public static Sp04FaultReasonType getHalfHourlyMpanInvalid()
  {
    return Sp04FaultReasonType.HH_MPAN_INVALID;
  }

  /**
   * @return fault reason for supplier is not currently active
   */
  public static Sp04FaultReasonType getSupplierIsNotCorrect()
  {
    return Sp04FaultReasonType.SUPPLIER_IS_NOT_CORRECT;
  }

  /**
   * @return fault reason for an invalid mpan
   */
  public static Sp04FaultReasonType getInvalidMpan()
  {
    return Sp04FaultReasonType.INVALID_MPAN;
  }

  /**
   * @return fault reason for meter mpan does not match row in P0028
   */
  public static Sp04FaultReasonType getMeterMpanDoesNotMatchP0028()
  {
    return Sp04FaultReasonType.METER_MPAN_DOES_NOT_MATCH_P0028;
  }

  /**
   * @return fault reason for data collector not correct
   */
  public static Sp04FaultReasonType getDCIsNotCorrect()
  {
    return Sp04FaultReasonType.DC_IS_NOT_CORRECT;
  }

  /**
   * @return fault reason for max demand less than 100kw
   */
  public static Sp04FaultReasonType getMDLessThanOneHundred()
  {
    return Sp04FaultReasonType.MD_VALUE_LESS_100;
  }

  /**
   * @return fault reason for D0268 received on time
   */
  public static Sp04FaultReasonType getD0268ReceivedInTime()
  {
    return Sp04FaultReasonType.D0268_RECEIVED_IN_TIME;
  }

  public static Sp04FaultReasonType getMDMatchesOrExceedsThreshold()
  {
    return Sp04FaultReasonType.MD_VALUE_MATCHES_OR_EXCEEDS_THRESHOLD;
  }

  public boolean isMIDAfterMonthT()
  {
    if (mValue.equals(getMIDAfterMonthT().getValue()))
      return true;
    return false;
  }


  public boolean isNoAfmsMeterForMeterRegister()
  {
    if (mValue.equals(getNoAfmsMeterForMeterSerialId().getValue()))
      return true;
    return false;
  }

  public boolean isNoMpanForSearchCriteria()
  {
    if (mValue.equals(getNoMpanForSearchCriteria().getValue()))
      return true;
    return false;
  }

  public boolean isNoMpanForMeter()
  {
    if (mValue.equals(getNoMpanForMeter().getValue()))
      return true;
    return false;
  }

  public boolean isMpanIsAlreadyHalfHourlyMetering()
  {
    if (mValue.equals(getMpanIsAlreadyHalfHourlyMetering().getValue()))
      return true;
    return false;
  }

  public boolean isSupplierIsNotCorrect()
  {
    if (mValue.equals(getSupplierIsNotCorrect().getValue()))
      return true;
    return false;
  }
}


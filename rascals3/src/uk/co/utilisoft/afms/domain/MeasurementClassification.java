package uk.co.utilisoft.afms.domain;

import org.apache.commons.lang.StringUtils;

/**
 * comes from AFMS Mpan.J0082
 * Values of A & B indicate Non HalfHourly
 * Values of C, D & E indicate HalfHourly
 *
 * @author comerb
 *
 */
public class MeasurementClassification
{
  private String mValue;

  /**
   * @param aValue the Measurement Classification Code
   */
  public MeasurementClassification(String aValue)
  {
    this.mValue = aValue.toUpperCase();
  }

  /**
   * @return true, if the mpan is half hourly.
   */
  public boolean isHalfHourly()
  {
    return isMeasurementClassificationHalfHourly(mValue);
  }

  /**
   * @param aMeasurementClassificationCode the Measurement Classification Code
   * @return true if the Measurement Classification is half hourly
   */
  public static boolean isMeasurementClassificationHalfHourly(String aMeasurementClassificationCode)
  {
    if (StringUtils.isNotBlank(aMeasurementClassificationCode))
    {
      if (aMeasurementClassificationCode.equals(MeasurementClassificationType.HHDC.getCode())
          || aMeasurementClassificationCode.equals(MeasurementClassificationType.NHHDC.getCode())
          || aMeasurementClassificationCode.equals(MeasurementClassificationType.E.getCode()))
      {
        return true;
      }
      else if (aMeasurementClassificationCode.equals(MeasurementClassificationType.A.getCode())
               || aMeasurementClassificationCode.equals(MeasurementClassificationType.B.getCode()))
     {
       return false;
     }
     else
     {
       throw new RuntimeException("MeasurementClassification value[" + aMeasurementClassificationCode
                                  + "] is not valid");
     }
    }

    return false;
  }

  public static enum MeasurementClassificationType
  {
    HHDC("C"),
    NHHDC("D"),
    MOP("M"),
    A("A"),
    B("B"),
    E("E");

    private String mCode;

    private MeasurementClassificationType(String aCode)
    {
      mCode = aCode;
    }

    /**
     * @return the Measurement Classification Code
     */
    public String getCode()
    {
      return mCode;
    }
  }
}

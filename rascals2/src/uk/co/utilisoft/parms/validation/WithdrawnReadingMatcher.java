package uk.co.utilisoft.parms.validation;

import org.joda.time.DateTime;

import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import ch.lambdaj.function.matcher.LambdaJMatcher;


/**
 * @author Philip Lau
 * @version 1.0
 */
public class WithdrawnReadingMatcher extends LambdaJMatcher<AFMSMeterRegReading>
{
  private final DateTime mReadingDate;
  private final Float mRegisterReading;
  private final String mBSCValidationStatus;

  public WithdrawnReadingMatcher(DateTime aReadingDate, Float aRegisterReading, String aBSCValidationStatus)
  {
     mReadingDate = aReadingDate;
     mRegisterReading = aRegisterReading;
     mBSCValidationStatus = aBSCValidationStatus;
  }

  /**
   * {@inheritDoc}
   * @see org.hamcrest.Matcher#matches(java.lang.Object)
   */
  @Override
  public boolean matches(Object aObj)
  {
    AFMSMeterRegReading reading = aObj != null ? (AFMSMeterRegReading) aObj : null;

    if (reading != null)
    {
      if (reading.getBSCValidationStatus() != null
          && reading.getMeterReadingDate() != null
          && reading.getRegisterReading() != null)
      {
        return reading.getBSCValidationStatus().equals(mBSCValidationStatus)
          && reading.getMeterReadingDate().equals(mReadingDate)
          && reading.getRegisterReading().equals(mRegisterReading);
      }
    }

    return false;
  }
}

package uk.co.utilisoft.parms.file.sp04;

import java.util.Comparator;

import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;

/**
 * compare but NB this is a natural reverse IE highest first
 * @author comerb
 *
 */
public class AFMSMeterRegReadingSortByReadingReverse implements Comparator<AFMSMeterRegReading>
{
  public int compare(AFMSMeterRegReading o1, AFMSMeterRegReading o2)
  {
    return o2.getRegisterReading().compareTo(o1.getRegisterReading());
  }
}

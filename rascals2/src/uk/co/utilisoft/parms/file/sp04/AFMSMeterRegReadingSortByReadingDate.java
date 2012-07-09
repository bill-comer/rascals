package uk.co.utilisoft.parms.file.sp04;

import java.util.Comparator;

import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;

public /**
 * compare but NB this is a natural reverse IE highest first
 * @author comerb
 *
 */
class AFMSMeterRegReadingSortByReadingDate implements Comparator<AFMSMeterRegReading>
{
  public int compare(AFMSMeterRegReading o1, AFMSMeterRegReading o2)
  {
    if (o1.getMeterReadingDate().isAfter(o2.getMeterReadingDate()))
    {
      return -1;
    }
    else if (o2.getMeterReadingDate().isAfter(o1.getMeterReadingDate()))
    {
      return 1;
    }
    else
    {
      return 0;
    }
  }
}

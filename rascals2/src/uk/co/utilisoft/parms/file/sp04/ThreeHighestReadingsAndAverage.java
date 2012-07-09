package uk.co.utilisoft.parms.file.sp04;

import java.util.ArrayList;

import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;


/**
 * utility class used by getAverageOfThreeHighest()
 * @author comerb
 *
 */
public class ThreeHighestReadingsAndAverage
{
  public Float avgThreeHighest = 0.0F;

  public AFMSMeterRegReading mostRecent;   // most recent of three highest

  public ArrayList<AFMSMeterRegReading> threeHighestReadings = new ArrayList<AFMSMeterRegReading>();
}

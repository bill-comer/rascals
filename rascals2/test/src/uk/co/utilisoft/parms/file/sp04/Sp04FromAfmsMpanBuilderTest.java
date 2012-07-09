package uk.co.utilisoft.parms.file.sp04;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.utils.Freeze;

public class Sp04FromAfmsMpanBuilderTest
{
  @Test
  public void AFMSMeterRegReadingSortByReading_compareOne() throws Exception
  {
    AFMSMeterRegReading big = new AFMSMeterRegReading();
    big.setRegisterReading(10.0F);

    AFMSMeterRegReading small = new AFMSMeterRegReading();
    small.setRegisterReading(1.0F);

    AFMSMeterRegReadingSortByReadingReverse comparator = new AFMSMeterRegReadingSortByReadingReverse();

    assertTrue(comparator.compare(big, small) < 0);

    assertTrue(comparator.compare( small, big) > 0);

    AFMSMeterRegReading big2 = new AFMSMeterRegReading();
    big2.setRegisterReading(10.0F);

    assertTrue(comparator.compare( big2, big) == 0);

    ArrayList<AFMSMeterRegReading> threeHighestReadings = new ArrayList<AFMSMeterRegReading>();
    threeHighestReadings.add(big);
  }

  @Test
  public void AFMSMeterRegReadingSortByReading_sortList() throws Exception
  {
    AFMSMeterRegReading ten = new AFMSMeterRegReading();
    ten.setRegisterReading(10.0F);

    AFMSMeterRegReading one = new AFMSMeterRegReading();
    one.setRegisterReading(1.0F);


    AFMSMeterRegReading three = new AFMSMeterRegReading();
    three.setRegisterReading(3.0F);


    ArrayList<AFMSMeterRegReading> threeReadings = new ArrayList<AFMSMeterRegReading>();
    threeReadings.add(one);
    threeReadings.add(ten);
    threeReadings.add(three);

    Collections.sort(threeReadings, new AFMSMeterRegReadingSortByReadingReverse());

    Iterator<AFMSMeterRegReading> it = threeReadings.iterator();
    AFMSMeterRegReading r1 = it.next();
    assertNotNull(r1);
    assertEquals("10 is biggest so should be first", new Float(10.0F), r1.getRegisterReading());

    AFMSMeterRegReading r2 = it.next();
    assertNotNull(r2);
    assertEquals("then 3 is the middle one", new Float(3.0F), r2.getRegisterReading());

    AFMSMeterRegReading r3 = it.next();
    assertNotNull(r3);
    assertEquals("lastly 1 is the smalest", new Float(1.0F), r3.getRegisterReading());

  }


  @Test
  public void AFMSMeterRegReadingSortByReadingDate_sortList() throws Exception
  {
    Freeze.freeze(new DateTime());
    AFMSMeterRegReading ten = new AFMSMeterRegReading();
    ten.setMeterReadingDate(new DateTime());

    AFMSMeterRegReading one = new AFMSMeterRegReading();
    one.setMeterReadingDate(new DateTime().minusYears(1));

    AFMSMeterRegReading three = new AFMSMeterRegReading();
    three.setMeterReadingDate(new DateTime().minusMonths(1));


    ArrayList<AFMSMeterRegReading> threeReadings = new ArrayList<AFMSMeterRegReading>();
    threeReadings.add(one);
    threeReadings.add(ten);
    threeReadings.add(three);

    Collections.sort(threeReadings, new AFMSMeterRegReadingSortByReadingDate());

    Iterator<AFMSMeterRegReading> it = threeReadings.iterator();
    AFMSMeterRegReading r1 = it.next();
    assertNotNull(r1);
    assertEquals("10 newest", new DateTime(), r1.getMeterReadingDate());

    AFMSMeterRegReading r2 = it.next();
    assertNotNull(r2);
    assertEquals("then 3 is the middle one", new DateTime().minusMonths(1), r2.getMeterReadingDate());

    AFMSMeterRegReading r3 = it.next();
    assertNotNull(r3);
    assertEquals("lastly 1 is the oldest", new DateTime().minusYears(1), r3.getMeterReadingDate());

    Freeze.thaw();
  }
}

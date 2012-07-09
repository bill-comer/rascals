package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateMidnight;
import org.junit.Test;

import uk.co.utilisoft.BaseTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;

public class DpiFileTest extends BaseTest
{
  @Test
  public void testHashCode()
  {
    try
    {
      new DpiFile().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }

  @Test
  public void testGetLastDigitOfYear() throws Exception
  {
    assertEquals(new String("1"), DpiFile.getLastDigitOfYear(4321));

    assertEquals(new String("0"), DpiFile.getLastDigitOfYear(3210));
  }

  @Test
  public void testCreateFileName() throws Exception
  {
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(new DateMidnight(2010, 11, 1));
    assertEquals("should be DEC as the date refers to month T, & the date passed in is T-1", "ABCD1350.DEC", DpiFile.createFileName("ABCD", parmsReportingPeriod));
  }

  @Test
  public void testEquals() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    ParmsReportingPeriod m1 = new ParmsReportingPeriod(11, 2010);
    d1.setReportingPeriod(m1);
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    ParmsReportingPeriod m2 = new ParmsReportingPeriod(11, 2010);
    d2.setReportingPeriod(m2);
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);

    assertTrue(d1.equals(d2));
  }

  @Test
  public void testNotEquals_difft_filename() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    ParmsReportingPeriod m1 = new ParmsReportingPeriod(11, 2010);
    d1.setReportingPeriod(m1);
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f2");
    ParmsReportingPeriod m2 = new ParmsReportingPeriod(11, 2010);
    d2.setReportingPeriod(m2);
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);

    assertFalse(d1.equals(d2));
  }

  @Test
  public void testNotEquals_difft_supplier() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    ParmsReportingPeriod m1 = new ParmsReportingPeriod(11, 2010);
    d1.setReportingPeriod(m1);
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    ParmsReportingPeriod m2 = new ParmsReportingPeriod(11, 2010);
    d2.setReportingPeriod(m2);
    Supplier s2 = new Supplier("s2");
    d2.setSupplier(s2);

    assertFalse(d1.equals(d2));
  }

  @Test
  public void testNotEquals_difft_month() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    ParmsReportingPeriod m1 = new ParmsReportingPeriod(11, 2010);
    d1.setReportingPeriod(m1);
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    ParmsReportingPeriod m2 = new ParmsReportingPeriod(12, 2010);
    d2.setReportingPeriod(m2);
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);

    assertFalse(d1.equals(d2));
  }

  @Test
  public void replicate() throws Exception
  {
    freezeTime();

    Supplier supplier = new Supplier("test");
    supplier.setPk(909L);
    DpiFile src = new DpiFile(new ParmsReportingPeriod(11, 2011), supplier);

    //test method
    DpiFile replicar = src.replicate();

    assertTrue(src.getReportingPeriod().equals(replicar.getReportingPeriod()));
    assertTrue(src.getSupplier().equals(replicar.getSupplier()));

    assertNull(replicar.getPk());
    assertEquals("", replicar.getFileName());
    assertNull(replicar.getLastUpdated());
  }
}
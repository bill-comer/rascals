package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.utilisoft.parms.ParmsReportingPeriod;


public class GridSupplyPointTest
{
  @Test
  public void testHashCode()
  {
    try
    {
      new GridSupplyPoint().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }

  @Test
  public void testEquals() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    GridSupplyPoint g1 = new GridSupplyPoint("g1", d1);
    g1.setHalfHourMpans2ndMonth(true);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    s2.setSupplierId("s1");
    d2.setSupplier(s2);
    GridSupplyPoint g2 = new GridSupplyPoint("g1", d2);
    g2.setHalfHourMpans2ndMonth(true);

    assertTrue(g1.equals(g2));
  }

  @Test
  public void testNotEquals_diff_name() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    GridSupplyPoint g1 = new GridSupplyPoint("g1", d1);
    g1.setHalfHourMpans2ndMonth(true);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    GridSupplyPoint g2 = new GridSupplyPoint("g2", d2);
    g2.setHalfHourMpans2ndMonth(true);

    assertFalse(g1.equals(g2));
  }

  @Test
  public void testNotEquals_diff_HH() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    GridSupplyPoint g1 = new GridSupplyPoint("g1", d1);
    g1.setHalfHourMpans2ndMonth(true);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    GridSupplyPoint g2 = new GridSupplyPoint("g2", d2);
    g2.setHalfHourMpans2ndMonth(true);

    assertFalse(g1.equals(g2));
  }

  @Test
  public void testNotEquals_diff_month() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("f1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    GridSupplyPoint g1 = new GridSupplyPoint("g1", d1);
    g1.setHalfHourMpans2ndMonth(true);

    DpiFile d2 = new DpiFile();
    d2.setFileName("f1");
    d2.setReportingPeriod(new ParmsReportingPeriod(12, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    GridSupplyPoint g2 = new GridSupplyPoint("g2", d2);
    g2.setHalfHourMpans2ndMonth(true);

    assertFalse(g1.equals(g2));
  }

  @Test
  public void replicateHH_GSP_HH_2nd_month_true() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1909L);

    GridSupplyPoint srcGSP = new GridSupplyPoint("fred", srcDpiFile);
    srcGSP.setHalfHourMpans2ndMonth(true);
    srcGSP.setHalfHourMpansFirstMonth(false);
    srcGSP.setNonHalfHourMpans2ndMonth(true);
    srcGSP.setNonHalfHourMpansFirstMonth(false);

    GridSupplyPoint replGridSupplyPoint = srcGSP.replicate(replicatedDpiFile);

    assertEquals(srcGSP.getName(), replGridSupplyPoint.getName());
    assertNull(srcGSP.getPk());
    assertEquals(replicatedDpiFile.getPk(), replGridSupplyPoint.getDpiFile().getPk());

    assertTrue(srcGSP.isHalfHourMpans2ndMonth() == replGridSupplyPoint.isHalfHourMpans2ndMonth());
    assertTrue(srcGSP.isHalfHourMpansFirstMonth() == replGridSupplyPoint.isHalfHourMpansFirstMonth());

    assertTrue(srcGSP.isNonHalfHourMpans2ndMonth() == replGridSupplyPoint.isNonHalfHourMpans2ndMonth());
    assertTrue(srcGSP.isNonHalfHourMpansFirstMonth() == replGridSupplyPoint.isNonHalfHourMpansFirstMonth());
  }

  @Test
  public void replicateHH_GSP_HH_first_month_true() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1909L);

    GridSupplyPoint srcGSP = new GridSupplyPoint("fred", srcDpiFile);
    srcGSP.setHalfHourMpansFirstMonth(false);
    srcGSP.setHalfHourMpansFirstMonth(true);
    srcGSP.setNonHalfHourMpansFirstMonth(false);
    srcGSP.setNonHalfHourMpansFirstMonth(true);

    GridSupplyPoint replGridSupplyPoint = srcGSP.replicate(replicatedDpiFile);

    assertEquals(srcGSP.getName(), replGridSupplyPoint.getName());
    assertNull(srcGSP.getPk());
    assertEquals(replicatedDpiFile.getPk(), replGridSupplyPoint.getDpiFile().getPk());

    assertTrue(srcGSP.isHalfHourMpans2ndMonth() == replGridSupplyPoint.isHalfHourMpans2ndMonth());
    assertTrue(srcGSP.isNonHalfHourMpans2ndMonth() == replGridSupplyPoint.isNonHalfHourMpans2ndMonth());

    assertTrue(srcGSP.isHalfHourMpansFirstMonth() == replGridSupplyPoint.isHalfHourMpansFirstMonth());
    assertTrue(srcGSP.isNonHalfHourMpansFirstMonth() == replGridSupplyPoint.isNonHalfHourMpansFirstMonth());
  }

  @Test
  public void replicate_nonHH_GSP() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setReportingPeriod(new ParmsReportingPeriod(10, 2011));
    srcDpiFile.setPk(909L);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1909L);

    GridSupplyPoint srcGSP = new GridSupplyPoint("fred", srcDpiFile);
    srcGSP.setHalfHourMpans2ndMonth(false);
    srcGSP.setNonHalfHourMpans2ndMonth(true);

    GridSupplyPoint replGridSupplyPoint = srcGSP.replicate(replicatedDpiFile);

    assertEquals(srcGSP.getName(), replGridSupplyPoint.getName());
    assertNull(srcGSP.getPk());
    assertEquals(replicatedDpiFile.getPk(), replGridSupplyPoint.getDpiFile().getPk());

    assertTrue(srcGSP.isHalfHourMpans2ndMonth() == replGridSupplyPoint.isHalfHourMpans2ndMonth());
    assertTrue(srcGSP.isNonHalfHourMpans2ndMonth() == replGridSupplyPoint.isNonHalfHourMpans2ndMonth());

    assertTrue(srcGSP.isHalfHourMpansFirstMonth() == replGridSupplyPoint.isHalfHourMpansFirstMonth());
    assertTrue(srcGSP.isNonHalfHourMpansFirstMonth() == replGridSupplyPoint.isNonHalfHourMpansFirstMonth());
  }
}
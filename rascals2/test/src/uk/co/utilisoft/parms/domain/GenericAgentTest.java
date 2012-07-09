package uk.co.utilisoft.parms.domain;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import uk.co.utilisoft.parms.AgentRoleCodeType;
import uk.co.utilisoft.parms.ParmsReportingPeriod;


public class GenericAgentTest
{

  /**
   * Check agent role code assigned for MOP agent.
   */
  @Test
  public void testGetRoleCodeForMOPAgent()
  {
    boolean isMop = true;
    assertEquals(AgentRoleCodeType.M, new MOP().getRoleCode(isMop, false));
  }

  /**
   * Check agent role code assigned for DC agent valid in month T with HH mpans.
   */
  @Test
  public void testGetRoleCodeForDCAgentValidInMonthTWithHHMpans()
  {
    boolean hasNHHMpans = true;
    assertEquals(AgentRoleCodeType.C, new DataCollector().getRoleCode(true, hasNHHMpans));
  }

  /**
   * Check agent role code assigned for DC agent valid in month T with NHH mpans.
   */
  @Test
  public void testGetRoleCodeForDCAgentValidInMonthTWithNHHMpans()
  {
    boolean hasNHHMpans = false;
    assertEquals(AgentRoleCodeType.D, new DataCollector().getRoleCode(true, hasNHHMpans));
  }

  @Test
  public void testDataCollectorHashCode()
  {
    try
    {
      new DataCollector().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }

  @Test
  public void testMOPHashCode()
  {
    try
    {
      new MOP().hashCode();
    }
    catch (NullPointerException npe)
    {
      fail("Unexpected NullPointerException");
    }
  }

  @Test
  public void mopCreation_HH_monthT() throws Exception
  {
    boolean isHH = true, isMonthT = true;

    DpiFile dpiFile = new DpiFile();
    MOP mop = new MOP("fred", isHH, dpiFile, isMonthT);
    assertTrue(mop.isHalfHourMpans2ndMonth());
    assertFalse(mop.isHalfHourMpansFirstMonth());
    assertTrue(mop.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(mop.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void mopCreation_notHH_monthT() throws Exception
  {
    boolean isHH = false, isMonthT = true;

    DpiFile dpiFile = new DpiFile();
    MOP mop = new MOP("fred", isHH, dpiFile, isMonthT);

    assertFalse(mop.isHalfHourMpans2ndMonth());
    assertFalse(mop.isHalfHourMpansFirstMonth());
    assertTrue(mop.isNonHalfHourMpans2ndMonth());
    assertFalse(mop.isNonHalfHourMpansFirstMonth());

    assertTrue(mop.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(mop.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void mopCreation_HH_monthTMinus1() throws Exception
  {
    boolean isHH = true, isMonthT = false;

    DpiFile dpiFile = new DpiFile();
    MOP mop = new MOP("fred", isHH, dpiFile, isMonthT);
    assertFalse(mop.isHalfHourMpans2ndMonth());
    assertTrue(mop.isHalfHourMpansFirstMonth());
    assertTrue(mop.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(mop.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void dcCreation_notHH_monthTMinus1() throws Exception
  {
    boolean isHH = false, isMonthT = false;

    DpiFile dpiFile = new DpiFile();
    DataCollector dc = new DataCollector("fred", isHH, dpiFile, isMonthT);
    assertFalse(dc.isHalfHourMpans2ndMonth());
    assertFalse(dc.isHalfHourMpansFirstMonth());
    assertFalse(dc.isNonHalfHourMpans2ndMonth());
    assertTrue(dc.isNonHalfHourMpansFirstMonth());

    assertTrue(dc.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(dc.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void dcCreation_HH_monthT() throws Exception
  {
    boolean isHH = true, isMonthT = true;

    DpiFile dpiFile = new DpiFile();
    DataCollector dc = new DataCollector("fred", isHH, dpiFile, isMonthT);
    assertTrue(dc.isHalfHourMpans2ndMonth());
    assertFalse(dc.isHalfHourMpansFirstMonth());
    assertTrue(dc.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(dc.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void dcCreation_notHH_monthT() throws Exception
  {
    boolean isHH = false, isMonthT = true;

    DpiFile dpiFile = new DpiFile();
    DataCollector dc = new DataCollector("fred", isHH, dpiFile, isMonthT);

    assertFalse(dc.isHalfHourMpans2ndMonth());
    assertFalse(dc.isHalfHourMpansFirstMonth());
    assertTrue(dc.isNonHalfHourMpans2ndMonth());
    assertFalse(dc.isNonHalfHourMpansFirstMonth());

    assertTrue(dc.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(dc.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void dcCreation_HH_monthTMinus1() throws Exception
  {
    boolean isHH = true, isMonthT = false;

    DpiFile dpiFile = new DpiFile();
    DataCollector dc = new DataCollector("fred", isHH, dpiFile, isMonthT);
    assertFalse(dc.isHalfHourMpans2ndMonth());
    assertTrue(dc.isHalfHourMpansFirstMonth());
    assertTrue(dc.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(dc.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void mopCreation_notHH_monthTMinus1() throws Exception
  {
    boolean isHH = false, isMonthT = false;

    DpiFile dpiFile = new DpiFile();
    MOP mop = new MOP("fred", isHH, dpiFile, isMonthT);

    assertFalse(mop.isHalfHourMpans2ndMonth());
    assertFalse(mop.isHalfHourMpansFirstMonth());
    assertFalse(mop.isNonHalfHourMpans2ndMonth());
    assertTrue(mop.isNonHalfHourMpansFirstMonth());

    assertTrue(mop.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertFalse(mop.isHalfHourlyForAppropiateMonth(!isMonthT, isHH));
  }

  @Test
  public void testEqualsForTwoTheSame() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("d1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    DataCollector dc1 = new DataCollector("fred", false, d1, true);


    DpiFile d2 = new DpiFile();
    d2.setFileName("d1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    DataCollector dc2 = new DataCollector("fred", false, d2, true);

    assertTrue(dc1.equals(dc2));
  }

  @Test
  public void testNotEqualsForTwoTheSameButFalseDifftMonths() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("d1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    DataCollector dc1 = new DataCollector("fred", false, d1, false);


    DpiFile d2 = new DpiFile();
    d2.setFileName("d1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    DataCollector dc2 = new DataCollector("fred", false, d2, true);

    assertFalse("false, one is for monthT and one for T-1", dc1.equals(dc2));
  }

  @Test
  public void testNotEqualsForTwoTheSameButTrueDifftMonths() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("d1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    DataCollector dc1 = new DataCollector("fred", true, d1, false);

    DpiFile d2 = new DpiFile();
    d2.setFileName("d1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    DataCollector dc2 = new DataCollector("fred", true, d2, true);

    assertFalse(dc1.equals(dc2));
  }

  @Test
  public void testEqualsForTwoDifferentNames() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("d1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    DataCollector dc1 = new DataCollector("fred", false, d1, false);

    DpiFile d2 = new DpiFile();
    d2.setFileName("d1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    DataCollector dc2 = new DataCollector("bert", false, d2, false);

    assertFalse(dc1.equals(dc2));
    ;
  }

  @Test
  public void testEqualsForTwoSameNameDifftHH() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("d1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    DataCollector dc1 = new DataCollector("fred", false, d1, true);

    DpiFile d2 = new DpiFile();
    d2.setFileName("d1");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    DataCollector dc2 = new DataCollector("fred", true, d2, true);

    assertFalse("difft HH should say they are not equal", dc1.equals(dc2));
    ;
  }

  @Test
  public void testEqualsForTwoSameNameDifftDpiFiles() throws Exception
  {
    DpiFile d1 = new DpiFile();
    d1.setFileName("d1");
    d1.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s1 = new Supplier("s1");
    d1.setSupplier(s1);
    DataCollector dc1 = new DataCollector("fred", false, d1, true);

    DpiFile d2 = new DpiFile();
    d2.setFileName("d2");
    d2.setReportingPeriod(new ParmsReportingPeriod(11, 2010));
    Supplier s2 = new Supplier("s1");
    d2.setSupplier(s2);
    DataCollector dc2 = new DataCollector("fred", false, d2, true);

    assertFalse("difft HH should say they are not equal", dc1.equals(dc2));
    ;
  }

  @Test
  public void testMOPsAreNotDCs() throws Exception
  {
    MOP mop = new MOP("fred", false, null, true);
    DataCollector dc = new DataCollector("fred", false, null, true);

    assertFalse("MOPs and DCs should not be the same", mop.equals(dc));
  }

  @Test
  public void replicateHHDC() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);
    srcDpiFile.setReportingPeriod(new ParmsReportingPeriod(9, 2009));
    Supplier supplier = new Supplier("asuppId");
    srcDpiFile.setSupplier(supplier);

    GridSupplyPoint srcGSP = new GridSupplyPoint("aGSP", srcDpiFile);
    srcGSP.setPk(9091L);
    srcGSP.setHalfHourMpans2ndMonth(true);
    srcGSP.setNonHalfHourMpans2ndMonth(true);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1000L);

    DataCollector srcDc = new DataCollector();
    srcDc.setDpiFile(replicatedDpiFile);
    srcDc.setHalfHourMpans2ndMonth(true);
    srcDc.setNonHalfHourMpans2ndMonth(true);
    srcDc.getGridSupplyPoints().add(srcGSP);

    Map<String, GridSupplyPoint> replicatedGSPs = new HashMap<String, GridSupplyPoint>();

    //test method
    GenericAgent replicatedAgent = srcDc.replicate(replicatedDpiFile, replicatedGSPs);

    assertNotNull("not replicated DpiFile ", replicatedAgent.getDpiFile());
    assertEquals("got incorrect DpiFile Pk", replicatedDpiFile.getPk(), replicatedAgent.getDpiFile().getPk());

    assertFalse("should be a DC", replicatedAgent.isMop());
    assertTrue("should be HH", replicatedAgent.isHalfHourMpans2ndMonth());
    assertTrue("should be Non HH 2nd month ", replicatedAgent.isNonHalfHourMpans2ndMonth());

    assertEquals("names different", srcDc.getName(), replicatedAgent.getName());

    assertTrue("replicatedAgent should have a GSP", replicatedAgent.getGridSupplyPoints().size() == 1);

    assertFalse("rep GSP will have a difft Pk",
        srcDc.getGridSupplyPoints().iterator().next().getPk()
           .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getPk()));
    assertTrue("GSPs are not equivalent", srcDc.getGridSupplyPoints().iterator().next().getName()
           .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getName()));
    assertTrue("GSPs are not equivalent", replicatedDpiFile.equals(replicatedAgent.getGridSupplyPoints().iterator().next().getDpiFile()));

    assertEquals(1, replicatedAgent.getGridSupplyPoints().size());

    for (GridSupplyPoint gsp : replicatedAgent.getGridSupplyPoints())
    {
      assertEquals("aGSP", gsp.getName());
    }

  }

  @Test
  public void replicate_non_HHDC() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);
    srcDpiFile.setReportingPeriod(new ParmsReportingPeriod(9, 2009));
    Supplier supplier = new Supplier("asuppId");
    srcDpiFile.setSupplier(supplier);

    GridSupplyPoint srcGSP = new GridSupplyPoint("aGSP", srcDpiFile);
    srcGSP.setPk(9091L);
    srcGSP.setHalfHourMpans2ndMonth(false);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1000L);

    DataCollector srcDc = new DataCollector();
    srcDc.setDpiFile(replicatedDpiFile);
    srcDc.setHalfHourMpans2ndMonth(false);
    srcDc.getGridSupplyPoints().add(srcGSP);

    Map<String, GridSupplyPoint> replicatedGSPs = new HashMap<String, GridSupplyPoint>();
    //test method
    GenericAgent replicatedAgent = srcDc.replicate(replicatedDpiFile, replicatedGSPs);

    assertNotNull("not replicated DpiFile ", replicatedAgent.getDpiFile());
    assertEquals("got incorrect DpiFile Pk", replicatedDpiFile.getPk(), replicatedAgent.getDpiFile().getPk());

    assertFalse("should be a DC", replicatedAgent.isMop());
    assertFalse("should NOT be HH", replicatedAgent.isHalfHourMpans2ndMonth());

    assertTrue("replicatedAgent should have a GSP", replicatedAgent.getGridSupplyPoints().size() == 1);

    assertEquals("names different", srcDc.getName(), replicatedAgent.getName());
    assertFalse("rep GSP will have a difft Pk", srcDc.getGridSupplyPoints().iterator().next().getPk().equals(replicatedAgent.getGridSupplyPoints().iterator().next().getPk()));
    assertTrue("GSPs are not equivalent", srcDc.getGridSupplyPoints().iterator().next().getName().equals(replicatedAgent.getGridSupplyPoints().iterator().next().getName()));
    assertTrue("GSPs are not equivalent", replicatedDpiFile.equals(replicatedAgent.getGridSupplyPoints().iterator().next().getDpiFile()));

 assertEquals(1, replicatedAgent.getGridSupplyPoints().size());

    for (GridSupplyPoint gsp : replicatedAgent.getGridSupplyPoints())
    {
      assertEquals("aGSP", gsp.getName());
    }
  }

  @Test
  public void replicateHH_MOP() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);
    srcDpiFile.setReportingPeriod(new ParmsReportingPeriod(9, 2009));
    Supplier supplier = new Supplier("asuppId");
    srcDpiFile.setSupplier(supplier);

    GridSupplyPoint srcGSP = new GridSupplyPoint("aGSP", srcDpiFile);
    srcGSP.setPk(9091L);
    srcGSP.setHalfHourMpans2ndMonth(true);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1000L);

    MOP srcMOP = new MOP();
    srcMOP.setDpiFile(replicatedDpiFile);
    srcMOP.setHalfHourMpans2ndMonth(true);
    srcMOP.getGridSupplyPoints().add(srcGSP);

    Map<String, GridSupplyPoint> replicatedGSPs = new HashMap<String, GridSupplyPoint>();
    //test method
    GenericAgent replicatedAgent = srcMOP.replicate(replicatedDpiFile, replicatedGSPs);

    assertNotNull("not replicated DpiFile ", replicatedAgent.getDpiFile());
    assertEquals("got incorrect DpiFile Pk", replicatedDpiFile.getPk(), replicatedAgent.getDpiFile().getPk());

    assertTrue("should be a MOP", replicatedAgent.isMop());
    assertTrue("should be HH", replicatedAgent.isHalfHourMpans2ndMonth());

    assertTrue("replicatedAgent should have a GSP", replicatedAgent.getGridSupplyPoints().size() == 1);

    assertEquals("names different", srcMOP.getName(), replicatedAgent.getName());
    assertFalse("rep GSP will have a difft Pk", srcMOP.getGridSupplyPoints().iterator().next().getPk()
        .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getPk()));
    assertTrue("GSPs are not equivalent", srcMOP.getGridSupplyPoints().iterator().next().getName()
        .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getName()));
    assertTrue("GSPs are not equivalent", replicatedDpiFile
        .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getDpiFile()));

    assertEquals(1, replicatedAgent.getGridSupplyPoints().size());

    for (GridSupplyPoint gsp : replicatedAgent.getGridSupplyPoints())
    {
      assertEquals("aGSP", gsp.getName());
    }
  }

  @Test
  public void replicate_non_HH_MOP() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);
    srcDpiFile.setReportingPeriod(new ParmsReportingPeriod(9, 2009));
    Supplier supplier = new Supplier("asuppId");
    srcDpiFile.setSupplier(supplier);

    GridSupplyPoint srcGSP = new GridSupplyPoint("aGSP", srcDpiFile);
    srcGSP.setPk(9091L);
    srcGSP.setHalfHourMpans2ndMonth(false);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1000L);

    MOP srcMOP = new MOP("MOP1", false, replicatedDpiFile, true);
    srcMOP.getGridSupplyPoints().add(srcGSP);

    Map<String, GridSupplyPoint> replicatedGSPs = new HashMap<String, GridSupplyPoint>();
    //test method
    GenericAgent replicatedAgent = srcMOP.replicate(replicatedDpiFile, replicatedGSPs);

    assertNotNull("not replicated DpiFile ", replicatedAgent.getDpiFile());
    assertEquals("got incorrect DpiFile Pk", replicatedDpiFile.getPk(), replicatedAgent.getDpiFile().getPk());

    assertTrue("should be a MOP", replicatedAgent.isMop());
    assertFalse("should NOT be HH", replicatedAgent.isHalfHourMpans2ndMonth());

    assertTrue("replicatedAgent should have a GSP", replicatedAgent.getGridSupplyPoints().size() == 1);

    assertEquals("names different", srcMOP.getName(), replicatedAgent.getName());
    assertFalse("rep GSP will have a difft Pk", srcMOP.getGridSupplyPoints().iterator().next().getPk()
        .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getPk()));
    assertTrue("GSPs are not equivalent", srcMOP.getGridSupplyPoints().iterator().next().getName()
        .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getName()));
    assertTrue("GSPs are not equivalent", replicatedDpiFile
        .equals(replicatedAgent.getGridSupplyPoints().iterator().next().getDpiFile()));

    assertEquals(1, replicatedAgent.getGridSupplyPoints().size());

    for (GridSupplyPoint gsp : replicatedAgent.getGridSupplyPoints())
    {
      assertEquals("aGSP", gsp.getName());
    }
  }

  @Test
  public void replicate_non_HH_MOP_with_2_GSPs() throws Exception
  {
    DpiFile srcDpiFile = new DpiFile();
    srcDpiFile.setPk(909L);
    srcDpiFile.setReportingPeriod(new ParmsReportingPeriod(9, 2009));
    Supplier supplier = new Supplier("asuppId");
    srcDpiFile.setSupplier(supplier);

    GridSupplyPoint srcGSP1 = new GridSupplyPoint("aGSP1", srcDpiFile);
    srcGSP1.setPk(9091L);
    srcGSP1.setHalfHourMpans2ndMonth(false);

    GridSupplyPoint srcGSP2 = new GridSupplyPoint("aGSP2", srcDpiFile);
    srcGSP2.setPk(9092L);
    srcGSP2.setHalfHourMpans2ndMonth(false);

    DpiFile replicatedDpiFile = new DpiFile();
    replicatedDpiFile.setPk(1000L);

    MOP srcMOP = new MOP("MOP1", false, replicatedDpiFile, true);
    srcMOP.getGridSupplyPoints().add(srcGSP1);
    srcMOP.getGridSupplyPoints().add(srcGSP2);

    Map<String, GridSupplyPoint> replicatedGSPs = new HashMap<String, GridSupplyPoint>();
    //test method
    GenericAgent replicatedAgent = srcMOP.replicate(replicatedDpiFile, replicatedGSPs);

    assertNotNull("not replicated DpiFile ", replicatedAgent.getDpiFile());
    assertEquals("got incorrect DpiFile Pk", replicatedDpiFile.getPk(), replicatedAgent.getDpiFile().getPk());

    assertTrue("should be a MOP", replicatedAgent.isMop());
    assertFalse("should NOT be HH", replicatedAgent.isHalfHourMpans2ndMonth());

    assertTrue("replicatedAgent should have a GSP", replicatedAgent.getGridSupplyPoints().size() == 2);

    assertEquals("names different", srcMOP.getName(), replicatedAgent.getName());

    boolean foundGsp1 = false, foundGsp2 = false;
    for (GridSupplyPoint repGSP : replicatedAgent.getGridSupplyPoints())
    {
      assertTrue(replicatedDpiFile.equals(repGSP.getDpiFile()));
      if (repGSP.getName().equals("aGSP1"))
      {
        foundGsp1 = true;
      }
      if (repGSP.getName().equals("aGSP2"))
      {
        foundGsp2 = true;
      }
    }
    assertTrue(foundGsp1);
    assertTrue(foundGsp2);

    assertEquals(2, replicatedAgent.getGridSupplyPoints().size());
  }
}
package uk.co.utilisoft.parms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.afms.domain.MeasurementClassification;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.utils.Freeze;


public class AgentDTOTest
{
  ParmsReportingPeriod parmsReportingPeriod;
  DpiFile dpiFile;
  Supplier supplier;

  @Before
  public void prep() throws Exception
  {
    //Test set up
    Freeze.freeze(new DateTime(2010, 11, 23,11, 11, 11, 11));
    parmsReportingPeriod = new ParmsReportingPeriod(12, 2010);

    supplier = new Supplier("testSupplier");

    dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(parmsReportingPeriod);
    dpiFile.setSupplier(supplier);
    dpiFile.setFileName("aTestFile");
  }

  @Test
  public void testBuildDataCollectorPartForDCThatCoversWholeMonth_monthT() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = true;

    //The input AFMSMpan object
    AFMSMpan afmsMpan = createTestAFMSMpan();

    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", null);
    gsps.put("_A", gsp);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();

    AFMSAgent agent = new AFMSAgent();
    agent.setDCEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    agent.setDataCollector("fred");

    //agentDao Mock
    DataCollector dataCollector = new DataCollector(agent.getDataCollector(), testIsHalfHourly, dpiFile, isMonthT);
    dataCollector.getGridSupplyPoints().add(gsp);

    Map<String, DataCollector> agentsMapToBuild = new HashMap<String, DataCollector>();

    //test method
    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    dto.buildDataCollectorsForMpan(afmsMpan, agentsMapToBuild, agent, isMonthT);

    assertEquals(1, agentsMapToBuild.size());
    assertEquals(dataCollector, agentsMapToBuild.get("fred"));

    GenericAgent createdAgent = agentsMapToBuild.get("fred");
    assertFalse(createdAgent.isMop());
    assertEquals("should be GSP", 1, createdAgent.getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : createdAgent.getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
  }

  @Test
  public void testBuildDataCollectorPartForDCThatCoversWholeMonth_2GSPs_forAgent_1GSP_added_already_monthT() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = true;

    //The input AFMSMpan object
    AFMSMpan afmsMpan = createTestAFMSMpan();

    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(9999L);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();

    AFMSAgent afmsAgent = new AFMSAgent();
    afmsAgent.setDCEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    afmsAgent.setDataCollector("fred");

    //agentDao Mock
    DataCollector expectedDC = new DataCollector(afmsAgent.getDataCollector(), testIsHalfHourly, dpiFile, isMonthT);
    expectedDC.getGridSupplyPoints().add(gsp1);
    expectedDC.getGridSupplyPoints().add(gsp2);

    DataCollector startingDC = new DataCollector(afmsAgent.getDataCollector(), testIsHalfHourly, dpiFile, isMonthT);
    startingDC.getGridSupplyPoints().add(gsp2);

    Map<String, DataCollector> agentsMapToBuild = new HashMap<String, DataCollector>();
    agentsMapToBuild.put(startingDC.getName(), startingDC);

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildDataCollectorsForMpan(afmsMpan, agentsMapToBuild, afmsAgent, isMonthT);

    assertEquals(1, agentsMapToBuild.size());
    assertEquals(expectedDC, agentsMapToBuild.get("fred"));

    GenericAgent createdAgent = agentsMapToBuild.get("fred");
    assertFalse(createdAgent.isMop());
    assertEquals("should be GSP", 2, createdAgent.getGridSupplyPoints().size());

    boolean foundGsp1 = false;
    boolean foundGsp2 = false;
    for (GridSupplyPoint createdGsp : createdAgent.getGridSupplyPoints())
    {
      if (createdGsp.getName().equals("_A"))
      {
        foundGsp1 = true;
      }
      if (createdGsp.getName().equals("_B"))
      {
        foundGsp2 = true;
      }
    }
    assertTrue(foundGsp1);
    assertTrue(foundGsp2);
  }

  @Test
  public void testBuildDataCollectorPartForDCThatCoversWholeMonth_2GSPs_forAgent_1GSP_added_already_second_gsp_isHH() throws Exception
  {
    boolean initiallyIsHalfHourly = false;
    boolean expectedIsHalfHourly = true;
    boolean isMonthT = true;

    //The input AFMSMpan object
    AFMSMpan afmsMpan = createTestAFMSMpan();
    afmsMpan.setMeasurementClassification("C"); // an HH MC

    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(9999L);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();

    AFMSAgent afmsAgent = new AFMSAgent();
    afmsAgent.setDCEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    afmsAgent.setDataCollector("fred");

    //agentDao Mock
    DataCollector expectedDC = new DataCollector(afmsAgent.getDataCollector(), initiallyIsHalfHourly, dpiFile, isMonthT);
    expectedDC.setAppropiateHalfHourlyFlag(isMonthT, expectedIsHalfHourly);
    expectedDC.getGridSupplyPoints().add(gsp1);
    expectedDC.getGridSupplyPoints().add(gsp2);

    DataCollector startingDC = new DataCollector(afmsAgent.getDataCollector(), initiallyIsHalfHourly, dpiFile, isMonthT);
    startingDC.getGridSupplyPoints().add(gsp2);

    Map<String, DataCollector> agentsMapToBuild = new HashMap<String, DataCollector>();
    agentsMapToBuild.put(startingDC.getName(), startingDC);

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildDataCollectorsForMpan(afmsMpan, agentsMapToBuild, afmsAgent, isMonthT);

    assertEquals(1, agentsMapToBuild.size());
    assertEquals(expectedDC, agentsMapToBuild.get("fred"));

    GenericAgent createdAgent = agentsMapToBuild.get("fred");
    assertTrue(createdAgent.isHalfHourMpans2ndMonth());
    assertFalse(createdAgent.isMop());
    assertEquals("should be GSP", 2, createdAgent.getGridSupplyPoints().size());

    boolean foundGsp1 = false;
    boolean foundGsp2 = false;
    for (GridSupplyPoint createdGsp : createdAgent.getGridSupplyPoints())
    {
      if (createdGsp.getName().equals("_A"))
      {
        foundGsp1 = true;
      }
      if (createdGsp.getName().equals("_B"))
      {
        foundGsp2 = true;
      }
    }
    assertTrue(foundGsp1);
    assertTrue(foundGsp2);
  }


  private AFMSMpan createTestAFMSMpan()
  {
    AFMSMpan afmsMpan = new AFMSMpan();
    afmsMpan.setPk(112233L);
    MPANCore mpanCore = new MPANCore("5555555555555");
    afmsMpan.setMpanCore(mpanCore.getValue());
    afmsMpan.setGridSupplyPoint("_A");
    afmsMpan.setMeasurementClassification("A");   // a NON HH MC
    afmsMpan.setEffectiveFromDate(new DateTime());
    afmsMpan.setSupplierId(supplier.getSupplierId());
    return afmsMpan;
  }

  @Test
  public void testBuildMOPPartForMOPThatCoversWholeMonth() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = true;

    AFMSMpan afmsMpan = createTestAFMSMpan();

    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", null);
    gsps.put("_A", gsp);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();
    AFMSAgent agent = new AFMSAgent();
    agent.setMOEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    agent.setMeterOperator("fred");

    MOP mop = new MOP(agent.getMeterOperator(), testIsHalfHourly, dpiFile, isMonthT);
    mop.getGridSupplyPoints().add(gsp);

    Map<String, MOP> agentsMapToBuild = new HashMap<String, MOP>();

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildMOPsForMpan(afmsMpan, agentsMapToBuild, agent, isMonthT);

    assertEquals(1, agentsMapToBuild.size());

    assertEquals(mop, agentsMapToBuild.get("fred"));
    GenericAgent createdAgent = agentsMapToBuild.get("fred");
    assertTrue(createdAgent.isMop());
    assertEquals("should be GSP", 1, createdAgent.getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : createdAgent.getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
  }

  @Test
  public void testBuildMOPPartForMOPThatCoversWholeMonth_2GSPs_forAgent_1GSP_added_already() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = true;

    AFMSMpan afmsMpan = createTestAFMSMpan();

    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(9999L);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();
    AFMSAgent agent = new AFMSAgent();
    agent.setMOEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    agent.setMeterOperator("fred");

    MOP expectedMop = new MOP(agent.getMeterOperator(), testIsHalfHourly, dpiFile, isMonthT);
    expectedMop.getGridSupplyPoints().add(gsp1);
    expectedMop.getGridSupplyPoints().add(gsp2);

    MOP startingMop = new MOP(agent.getMeterOperator(), testIsHalfHourly, dpiFile, isMonthT);
    startingMop.getGridSupplyPoints().add(gsp2);

    Map<String, MOP> agentsMapToBuild = new HashMap<String, MOP>();
    agentsMapToBuild.put(startingMop.getName(), startingMop);

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildMOPsForMpan(afmsMpan, agentsMapToBuild, agent, isMonthT);

    assertEquals(1, agentsMapToBuild.size());

    assertEquals(expectedMop, agentsMapToBuild.get("fred"));
    GenericAgent createdAgent = agentsMapToBuild.get("fred");
    assertTrue(createdAgent.isMop());
    assertFalse("both GSPs are NON HH so agent should be too", createdAgent.isHalfHourMpans2ndMonth());
    assertEquals("should be GSP", 2, createdAgent.getGridSupplyPoints().size());

    boolean foundGsp1 = false;
    boolean foundGsp2 = false;
    for (GridSupplyPoint createdGsp : createdAgent.getGridSupplyPoints())
    {
      if (createdGsp.getName().equals("_A"))
      {
        foundGsp1 = true;
      }
      if (createdGsp.getName().equals("_B"))
      {
        foundGsp2 = true;
      }
    }
    assertTrue(foundGsp1);
    assertTrue(foundGsp2);
  }

  @Test
  public void testBuildMOPPartForMOPThatCoversWholeMonth_2GSPs_forAgent_1GSP_added_already_second_isHH() throws Exception
  {
    boolean initiallyIsHalfHourly = false;
    boolean expectedIsHalfHourly = true;
    boolean isMonthT = true;

    AFMSMpan afmsMpan = createTestAFMSMpan();
    afmsMpan.setMeasurementClassification("C"); // an HH MC

    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(9999L);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();
    AFMSAgent agent = new AFMSAgent();
    agent.setMOEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    agent.setMeterOperator("fred");

    MOP expectedMop = new MOP(agent.getMeterOperator(), initiallyIsHalfHourly, dpiFile, isMonthT);
    expectedMop.setAppropiateHalfHourlyFlag(isMonthT, expectedIsHalfHourly);
    expectedMop.getGridSupplyPoints().add(gsp1);
    expectedMop.getGridSupplyPoints().add(gsp2);

    MOP startingMop = new MOP(agent.getMeterOperator(), initiallyIsHalfHourly, dpiFile, isMonthT);
    startingMop.getGridSupplyPoints().add(gsp2);

    Map<String, MOP> agentsMapToBuild = new HashMap<String, MOP>();
    agentsMapToBuild.put(startingMop.getName(), startingMop);

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildMOPsForMpan(afmsMpan, agentsMapToBuild, agent, isMonthT);

    assertEquals(1, agentsMapToBuild.size());

    assertTrue(expectedMop.equals(agentsMapToBuild.get("fred")));
    GenericAgent createdAgent = agentsMapToBuild.get("fred");
    assertTrue(createdAgent.isMop());

    assertTrue("2nd GSP is HH so agent should be too", createdAgent.isHalfHourMpans2ndMonth());

    assertEquals("should be GSP", 2, createdAgent.getGridSupplyPoints().size());

    boolean foundGsp1 = false;
    boolean foundGsp2 = false;
    for (GridSupplyPoint createdGsp : createdAgent.getGridSupplyPoints())
    {
      if (createdGsp.getName().equals("_A"))
      {
        foundGsp1 = true;
      }
      if (createdGsp.getName().equals("_B"))
      {
        foundGsp2 = true;
      }
    }
    assertTrue(foundGsp1);
    assertTrue(foundGsp2);
  }

  /**
   * BuildDataCollectorPartForDCThatCoversPartMonth with no entries in AgentHistory
   */
  @Test
  public void testBuildDataCollectorPartForDCThatCoversPartMonth() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = false;

    AFMSMpan afmsMpan = createTestAFMSMpan();
    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", null);
    gsps.put("_A", gsp);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();
    AFMSAgent agent = new AFMSAgent();
    agent.setDCEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusDays(10)));
    agent.setDataCollector("TESTAHIST1");

    //set up mock list of AFMSAgentHistory
    List<Long> aAFMSMpanPks = new ArrayList<Long>();
    aAFMSMpanPks.add(112233L);

    AFMSAgentHistory mockAFMSAgentHistory = new AFMSAgentHistory();
    mockAFMSAgentHistory.setAgentId("TESTAHIST2");
    mockAFMSAgentHistory.setAgentEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    mockAFMSAgentHistory.setAgentRoleCode(AgentRoleCodeType.getNonHalfHourlyDCRoleCodeType());

    MultiHashMap<String, AFMSAgentHistory> agentHistoryMap= new MultiHashMap<String, AFMSAgentHistory>();
    agentHistoryMap.put(afmsMpan.getMpanCore(), mockAFMSAgentHistory);

    // set up mock AgentDao mAgentDao to return null
    DataCollector createdDc = new DataCollector("TESTAHIST1", testIsHalfHourly, dpiFile, isMonthT);
    createdDc.getGridSupplyPoints().add(gsp);
    createdDc.setLastUpdated(new DateTime());

    DataCollector createdDc2 = new DataCollector("TESTAHIST2", testIsHalfHourly, dpiFile, isMonthT);
    createdDc2.getGridSupplyPoints().add(gsp);

    Map<String, DataCollector> agentsMapToBuild = new HashMap<String, DataCollector>();
    Collection<AFMSAgentHistory> possibleAgents = new ArrayList<AFMSAgentHistory>();
    possibleAgents.add(mockAFMSAgentHistory);

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildDataCollectorsForMpan(afmsMpan, agentsMapToBuild, agent, 
        isMonthT);
    dto.buildDCsFromAgentHistoriesForMpan(afmsMpan, agentsMapToBuild, possibleAgents, isMonthT);

    assertEquals(2, agentsMapToBuild.size());

    assertEquals(createdDc, agentsMapToBuild.get("TESTAHIST1"));
    assertEquals("should be GSP", 1, agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
    assertEquals(createdDc2, agentsMapToBuild.get("TESTAHIST2"));
    assertEquals("should be GSP", 1, agentsMapToBuild.get("TESTAHIST2").getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : agentsMapToBuild.get("TESTAHIST2").getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
  }

  /**
   * BuildDataCollectorPartForDCThatCoversPartMonth with no entries in AgentHistory
   */
  @Test
  public void testBuildDataCollectorPartForDCThatCoversPartMonth_butAgentHistEffDateIsInFuture() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = true;

    AFMSMpan afmsMpan = createTestAFMSMpan();
    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", null);
    gsps.put("_A", gsp);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();
    AFMSAgent agent = new AFMSAgent();
    agent.setDCEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusDays(10)));
    agent.setDataCollector("TESTAHIST1");

    //set up mock list of AFMSAgentHistory
    List<Long> aAFMSMpanPks = new ArrayList<Long>();
    aAFMSMpanPks.add(112233L);
    List<AFMSAgentHistory> listAgents = new ArrayList<AFMSAgentHistory>();
    AFMSAgentHistory mockAFMSAgentHistory = new AFMSAgentHistory();
    mockAFMSAgentHistory.setAgentId("TESTAHIST2");
    mockAFMSAgentHistory.setAgentEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().plusYears(1)));
    listAgents.add(mockAFMSAgentHistory);

    // set up mock AgentDao mAgentDao to return null
    DataCollector createdDc = new DataCollector("TESTAHIST1", testIsHalfHourly, dpiFile, isMonthT);
    createdDc.getGridSupplyPoints().add(gsp);
    createdDc.setLastUpdated(new DateTime());

    DataCollector createdDc2 = new DataCollector("TESTAHIST2", testIsHalfHourly, dpiFile, isMonthT);
    createdDc2.getGridSupplyPoints().add(gsp);

    Map<String, DataCollector> agentsMapToBuild = new HashMap<String, DataCollector>();

    Collection<AFMSAgentHistory> possibleAgents = new ArrayList<AFMSAgentHistory>();

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildDataCollectorsForMpan(afmsMpan, agentsMapToBuild, agent, 
        //possibleAgents, 
        isMonthT);

    //only first one added as one from agentHistory has effectiveFromDate in future
    assertEquals(1, agentsMapToBuild.size());
    assertEquals(createdDc, agentsMapToBuild.get("TESTAHIST1"));
    assertEquals("should be GSP", 1, agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
  }

  /**
   * BuildDataCollectorPartForMOPThatCoversPartMonth with no entries in AgentHistory
   */
  @Test
  public void testBuildMOPPartForMOPThatCoversPartMonth() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = false;

    AFMSMpan afmsMpan = createTestAFMSMpan();
    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", null);
    gsps.put("_A", gsp);

    // the DTO object to test
    AgentDTO dto = new AgentDTO()
    {
      boolean isMonthT = true;
      public void buildAgents(AFMSMpan aAfmsMpan, ParmsReportingPeriod aParmsReportingPeriod,
          DpiFile aDpiFile, GridSupplyPoint gsp, Map<String, MOP> mops, Map<String, DataCollector> dataCollectors,
          AFMSAgent afmsAgent,AFMSAgent afmsMOPAgent, Collection<AFMSAgentHistory> mpanHistories)
      {
        buildMOPsForMpan(aAfmsMpan, mops, afmsMOPAgent,
            //mpanHistories, 
            isMonthT);
      }
    };
    AFMSAgent agent = new AFMSAgent();
    agent.setMOEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusDays(10)));
    agent.setMeterOperator("TESTAHIST1");

    //set up mock list of AFMSAgentHistory
    List<Long> aAFMSMpanPks = new ArrayList<Long>();
    aAFMSMpanPks.add(112233L);
    List<AFMSAgentHistory> listAgents = new ArrayList<AFMSAgentHistory>();
    AFMSAgentHistory mockAFMSAgentHistory = new AFMSAgentHistory();
    mockAFMSAgentHistory.setAgentId("TESTAHIST2");
    mockAFMSAgentHistory.setAgentEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusYears(1)));
    mockAFMSAgentHistory.setAgentRoleCode(AgentRoleCodeType.getMOPRoleCodeType());
    listAgents.add(mockAFMSAgentHistory);

    // set up mock AgentDao mAgentDao to return null
    MOP createdMOP = new MOP("TESTAHIST1", testIsHalfHourly, null, isMonthT);
    createdMOP.getGridSupplyPoints().add(gsp);
    createdMOP.setDpiFile(dpiFile);
    createdMOP.setLastUpdated(new DateTime());

    MOP createdMOP2 = new MOP("TESTAHIST2", testIsHalfHourly, dpiFile, isMonthT);
    createdMOP2.getGridSupplyPoints().add(gsp);

    Map<String, MOP> agentsMapToBuild = new HashMap<String, MOP>();

    Collection<AFMSAgentHistory> possibleAgents = new ArrayList<AFMSAgentHistory>();
    possibleAgents.add(mockAFMSAgentHistory);

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //2 test methods
    dto.buildMOPsForMpan(afmsMpan, agentsMapToBuild, agent, 
        isMonthT);
    dto.buildMOPsFromAgentHistoriesForMpan(afmsMpan, agentsMapToBuild, possibleAgents, isMonthT);

    assertEquals(2, agentsMapToBuild.size());

    assertEquals(createdMOP, agentsMapToBuild.get("TESTAHIST1"));
    assertEquals("should be GSP", 1, agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
    assertEquals(createdMOP2, agentsMapToBuild.get("TESTAHIST2"));
    assertEquals("should be GSP", 1, agentsMapToBuild.get("TESTAHIST2").getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : agentsMapToBuild.get("TESTAHIST2").getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
  }

  /**
   * BuildDataCollectorPartForMOPThatCoversPartMonth with no entries in AgentHistory
   */
  @Test
  public void testBuildMOPPartForMOPThatCoversPartMonth_butAgentHistoryEffDateIsInvalid() throws Exception
  {
    boolean testIsHalfHourly = false;
    boolean isMonthT = true;

    AFMSMpan afmsMpan = createTestAFMSMpan();
    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("_A", null);
    gsps.put("_A", gsp);

    // the DTO object to test
    AgentDTO dto = new AgentDTO();
    AFMSAgent agent = new AFMSAgent();
    agent.setMOEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().minusDays(10)));
    agent.setMeterOperator("TESTAHIST1");

    //set up mock list of AFMSAgentHistory
    List<Long> aAFMSMpanPks = new ArrayList<Long>();
    aAFMSMpanPks.add(112233L);
    List<AFMSAgentHistory> listAgents = new ArrayList<AFMSAgentHistory>();
    AFMSAgentHistory mockAFMSAgentHistory = new AFMSAgentHistory();
    mockAFMSAgentHistory.setAgentId("TESTAHIST2");
    mockAFMSAgentHistory.setAgentEffectiveFromDate(new DateTime(parmsReportingPeriod.getStartOfNextMonthInPeriod().plusYears(1)));
    listAgents.add(mockAFMSAgentHistory);

    // set up mock AgentDao mAgentDao to return null
    MOP createdMOP = new MOP("TESTAHIST1", testIsHalfHourly, null, isMonthT);
    createdMOP.getGridSupplyPoints().add(gsp);
    createdMOP.setDpiFile(dpiFile);
    createdMOP.setLastUpdated(new DateTime());

    MOP createdMOP2 = new MOP("TESTAHIST2", testIsHalfHourly, dpiFile, isMonthT);
    createdMOP2.getGridSupplyPoints().add(gsp);

    Map<String, MOP> agentsMapToBuild = new HashMap<String, MOP>();

    dto.setBaseParams(parmsReportingPeriod, dpiFile);
    //test method
    dto.buildMOPsForMpan(afmsMpan, agentsMapToBuild, agent, isMonthT);

    //only first one added as one from agentHistory has effectiveFromDate in future
    assertEquals(1, agentsMapToBuild.size());
    assertEquals(createdMOP, agentsMapToBuild.get("TESTAHIST1"));
    assertEquals("should be GSP", 1, agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints().size());
    for (GridSupplyPoint createdGsp : agentsMapToBuild.get("TESTAHIST1").getGridSupplyPoints())
    {
      assertEquals("_A", createdGsp.getName());
    }
  }

  @Test
  public void isGspInList_gsp_but_empty_list() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    List<GridSupplyPoint> listofGSPs = new ArrayList<GridSupplyPoint>();

    //test method
    Assert.assertNull(agentDTO.getGspInList(listofGSPs, "fred"));
  }

  @Test
  public void isGspInList_gsp_not_in_list() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    List<GridSupplyPoint> listofGSPs = new ArrayList<GridSupplyPoint>();

    GridSupplyPoint anoGSP = new GridSupplyPoint("bert", null);
    listofGSPs.add(anoGSP);

    //test method
    Assert.assertNull(agentDTO.getGspInList(listofGSPs, "fred"));
  }

  @Test
  public void isGspInList_gsp__in_list() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    List<GridSupplyPoint> listofGSPs = new ArrayList<GridSupplyPoint>();

    GridSupplyPoint anoGSP = new GridSupplyPoint("bert", null);
    listofGSPs.add(anoGSP);
    GridSupplyPoint gspWithSameName = new GridSupplyPoint("fred", null);
    listofGSPs.add(gspWithSameName);

    //test method
    assertNotNull(agentDTO.getGspInList(listofGSPs, "fred"));
  }

  @Test
  public void addToAgentDCMapIfNotThereAlready_HH_monthT_DC_toEmptyMap() throws Exception
  {
    boolean isHH = true;
    
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    Map<String, DataCollector> agentsMap = new HashMap<String, DataCollector>();
    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    MeasurementClassification classification = new MeasurementClassification("C"); //HH

    //test method
    agentDTO.addToAgentDCMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    DataCollector createdDC = agentsMap.get(agentName);
    assertNotNull(createdDC);
    assertEquals(agentName, createdDC.getName());
    assertEquals(false, createdDC.isHalfHourMpansFirstMonth());
    assertEquals(true, createdDC.isHalfHourMpans2ndMonth());
    assertEquals(true, createdDC.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdDC.getDpiFile());
    assertEquals(new Long(999), createdDC.getDpiFile().getPk());
    assertNotNull(createdDC.getGridSupplyPoints());
    assertEquals(1, createdDC.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdDC.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  @Test
  public void addToAgentDCMapIfNotThereAlready_nonHH_monthT_DC_toEmptyMap() throws Exception
  {
    boolean isHH = false;
    
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    Map<String, DataCollector> agentsMap = new HashMap<String, DataCollector>();
    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    MeasurementClassification classification = new MeasurementClassification("A"); //non HH

    //test method
    agentDTO.addToAgentDCMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    DataCollector createdDC = agentsMap.get(agentName);
    assertNotNull(createdDC);
    assertEquals(agentName, createdDC.getName());
    
    assertEquals(false, createdDC.isHalfHourMpansFirstMonth());
    assertEquals(false, createdDC.isHalfHourMpans2ndMonth());
    assertEquals(true, createdDC.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertEquals(false, createdDC.isNonHalfHourMpansFirstMonth());
    assertEquals(true, createdDC.isNonHalfHourMpans2ndMonth());
    
    assertNotNull(createdDC.getDpiFile());
    assertEquals(new Long(999), createdDC.getDpiFile().getPk());
    assertNotNull(createdDC.getGridSupplyPoints());
    assertEquals(1, createdDC.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdDC.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(false, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_HH_monthT_MOP_toEmptyMap() throws Exception
  {
    boolean isHH = true;
    
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    MeasurementClassification classification = new MeasurementClassification("C"); // HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    MOP createdMOP = agentsMap.get(agentName);
    assertNotNull(createdMOP);
    assertEquals(agentName, createdMOP.getName());
    assertEquals(true, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(1, createdMOP.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_HH_monthT_MOP_MopAndGSP_alreadyInMap() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    boolean isHH = true;

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    MOP preMOP = new MOP(agentName, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName, dpiFile);
    preMOP.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preMOP);
    MeasurementClassification classification = new MeasurementClassification("C"); // HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    MOP createdMOP = agentsMap.get(agentName);
    assertNotNull(createdMOP);
    assertEquals(agentName, createdMOP.getName());
    assertEquals(true, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(1, createdMOP.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_HH_monthT_MOP_MopAndGSPAlreadyInMap_addNewMOP() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    String gspName1 = "gsp1", agentName1 = "newAgent1";
    String gspName2 = "gsp2", agentName2 = "newAgent2";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    boolean isHH = true;

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    MOP preMOP = new MOP(agentName1, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName1, dpiFile);
    preMOP.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName1, preMOP);
    MeasurementClassification classification = new MeasurementClassification("C"); // HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName2, agentsMap, classification.isHalfHourly(), agentName2, isMop, isMonthT);

    assertEquals(2, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName2));
    MOP createdMOP = agentsMap.get(agentName2);
    assertNotNull(createdMOP);
    assertEquals(agentName2, createdMOP.getName());
    assertEquals(true, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(1, createdMOP.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      assertEquals(gspName2, gsp.getName());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_HH_monthT_MOP_MopAndGSP_alreadyInMap_newGSP() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    String gspName1 = "gsp1", agentName = "newAgent";
    String gspName2 = "gsp2";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    boolean isHH = true;

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    MOP preMOP = new MOP(agentName, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName1, dpiFile);
    preMOP.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preMOP);
    MeasurementClassification classification = new MeasurementClassification("C"); // HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName2, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    MOP createdMOP = agentsMap.get(agentName);
    assertNotNull(createdMOP);
    assertEquals(agentName, createdMOP.getName());
    assertEquals(true, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(2, createdMOP.getGridSupplyPoints().size());

    boolean foundGsp2 = false;
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      if(gsp.getName().equals(gspName2))
      {
        foundGsp2 = true;
        assertEquals(gspName2, gsp.getName());
        assertEquals(true, gsp.isHalfHourMpans2ndMonth());
        assertEquals(false, gsp.isHalfHourMpansFirstMonth());

      }
    }
    assertTrue("did not find GSP2 in agent", foundGsp2);
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_HH_monthT_MOP_MopAndGSP_alreadyInMap_butWasNotHH() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    boolean isHH = false;

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    MOP preMOP = new MOP(agentName, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName, dpiFile);
    preGSP.setAppropiateHalfHourlyFlag(isMonthT, isHH);
    preMOP.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preMOP);
    MeasurementClassification classification = new MeasurementClassification("C"); // HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    MOP createdMOP = agentsMap.get(agentName);
    assertNotNull(createdMOP);
    assertEquals(agentName, createdMOP.getName());
    assertEquals(true, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(1, createdMOP.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_HH_monthT_MOP_MopAndGSP_alreadyInMap_butGSPWasNotHH() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    boolean isHH = false;

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    MOP preMOP = new MOP(agentName, isHH, dpiFile, isMonthT);
    
    assertEquals(false, preMOP.isHalfHourMpans2ndMonth());
    assertEquals(false, preMOP.isHalfHourMpansFirstMonth());
    assertEquals(true, preMOP.isNonHalfHourMpans2ndMonth());
    assertEquals(false, preMOP.isNonHalfHourMpansFirstMonth());
    
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName, dpiFile);
    preGSP.setAppropiateHalfHourlyFlag(isMonthT, isHH);
    preMOP.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preMOP);
    MeasurementClassification classification = new MeasurementClassification("C"); // HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    MOP createdMOP = agentsMap.get(agentName);
    assertNotNull(createdMOP);
    assertEquals(agentName, createdMOP.getName());
    
    assertEquals(true, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(false, createdMOP.isHalfHourMpansFirstMonth());
    assertEquals(true, createdMOP.isNonHalfHourMpans2ndMonth());
    assertEquals(false, createdMOP.isNonHalfHourMpansFirstMonth());
    
    //assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(1, createdMOP.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }

  /**
   * DC already in map & is already HH for monthT
   * so no DC should be added and should still be HH
   * @throws Exception
   */
  @Test
  public void addToAgentDCMapIfNotThereAlready_HH_monthT_DC_DCAndGSP_alreadyInMap() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);
    String gspName = "gsp", agentName = "newAgent";
    boolean isHH = true;
    boolean isMonthT = true;
    boolean isMop = false; //DC

    Map<String, DataCollector> agentsMap = new HashMap<String, DataCollector>();
    DataCollector preDC = new DataCollector(agentName, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName, dpiFile);
    preDC.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preDC);

    MeasurementClassification classification = new MeasurementClassification("C"); //HH

    //test method
    agentDTO.addToAgentDCMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    DataCollector createdDC = agentsMap.get(agentName);
    assertNotNull(createdDC);
    assertEquals(agentName, createdDC.getName());
    assertEquals(false, createdDC.isHalfHourMpansFirstMonth());
    assertEquals(true, createdDC.isHalfHourMpans2ndMonth());
    assertEquals(true, createdDC.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdDC.getDpiFile());
    assertEquals(new Long(999), createdDC.getDpiFile().getPk());
    assertNotNull(createdDC.getGridSupplyPoints());
    assertEquals(1, createdDC.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdDC.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
    }
  }

  /**
   * DC already in map & is already HH for monthT
   * so no DC should be added and should still be HH
   * @throws Exception
   */
  @Test
  public void addToAgentDCMapIfNotThereAlready_HH_monthT_DC_OneDCAndGSPAlreadyInMapAddNewDC() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);
    String gspName1 = "gsp1", agentName1 = "newAgent1";
    String gspName2 = "gsp2", agentName2 = "newAgent2";
    boolean isHH = true;
    boolean isMonthT = true;
    boolean isMop = false; //DC

    Map<String, DataCollector> agentsMap = new HashMap<String, DataCollector>();
    DataCollector preDC = new DataCollector(agentName1, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName1, dpiFile);
    preDC.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName1, preDC);

    MeasurementClassification classification = new MeasurementClassification("C"); //HH

    //test method
    agentDTO.addToAgentDCMapIfNotThereAlready(gspName2, agentsMap, classification.isHalfHourly(), agentName2, isMop, isMonthT);

    assertEquals(2, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName2));
    DataCollector createdDC = agentsMap.get(agentName2);
    assertNotNull(createdDC);
    assertEquals(agentName2, createdDC.getName());
    assertEquals(false, createdDC.isHalfHourMpansFirstMonth());
    assertEquals(true, createdDC.isHalfHourMpans2ndMonth());
    assertEquals(true, createdDC.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdDC.getDpiFile());
    assertEquals(new Long(999), createdDC.getDpiFile().getPk());
    assertNotNull(createdDC.getGridSupplyPoints());
    assertEquals(1, createdDC.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdDC.getGridSupplyPoints())
    {
      assertEquals(gspName2, gsp.getName());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
    }
  }

  /**
   * DC already in map & is already HH for monthT
   * so no DC should be added and should still be HH
   * @throws Exception
   */
  @Test
  public void addToAgentDCMapIfNotThereAlready_HH_monthT_DC_DCAndGSP_alreadyInMap_newGSP() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);
    String gspName1 = "gsp1", agentName = "newAgent";
    String gspName2 = "gsp2";
    boolean isHH = true;
    boolean isMonthT = true;
    boolean isMop = false; //DC

    Map<String, DataCollector> agentsMap = new HashMap<String, DataCollector>();
    DataCollector preDC = new DataCollector(agentName, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName1, dpiFile);
    preDC.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preDC);

    MeasurementClassification classification = new MeasurementClassification("C"); //HH

    //test method
    agentDTO.addToAgentDCMapIfNotThereAlready(gspName2, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    DataCollector createdDC = agentsMap.get(agentName);
    assertNotNull(createdDC);
    assertEquals(agentName, createdDC.getName());
    assertEquals(false, createdDC.isHalfHourMpansFirstMonth());
    assertEquals(true, createdDC.isHalfHourMpans2ndMonth());
    assertEquals(true, createdDC.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdDC.getDpiFile());
    assertEquals(new Long(999), createdDC.getDpiFile().getPk());
    assertNotNull(createdDC.getGridSupplyPoints());
    assertEquals(2, createdDC.getGridSupplyPoints().size());
    boolean foundGsp2 = false;
    for (GridSupplyPoint gsp : createdDC.getGridSupplyPoints())
    {
      if (gsp.getName().equals(gspName2))
      {
        foundGsp2 = true;
        assertEquals(gspName2, gsp.getName());
        assertEquals(false, gsp.isHalfHourMpansFirstMonth());
        assertEquals(true, gsp.isHalfHourMpans2ndMonth());
      }
    }
    assertTrue(foundGsp2);
  }

  /**
   * DC already in map & but is NOT HH for monthT
   * so no DC should be added but should now be HH as should GSP
   * @throws Exception
   */
  @Test
  public void addToAgentDCMapIfNotThereAlready_HH_monthT_DC_DCAndGSP_alreadyInMap_butWasNotHH() throws Exception
  {
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);
    String gspName = "gsp", agentName = "newAgent";
    boolean isHH = false;
    boolean isMonthT = true;
    boolean isMop = false; //DC

    Map<String, DataCollector> agentsMap = new HashMap<String, DataCollector>();
    DataCollector preDC = new DataCollector(agentName, isHH, dpiFile, isMonthT);
    GridSupplyPoint preGSP = new GridSupplyPoint(gspName, dpiFile);
    preDC.getGridSupplyPoints().add(preGSP);
    agentsMap.put(agentName, preDC);

    MeasurementClassification classification = new MeasurementClassification("C"); //HH

    //test method
    agentDTO.addToAgentDCMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    DataCollector createdDC = agentsMap.get(agentName);
    assertNotNull(createdDC);
    assertEquals(agentName, createdDC.getName());
    assertEquals(false, createdDC.isHalfHourMpansFirstMonth());
    assertEquals(true, createdDC.isHalfHourMpans2ndMonth());
    assertEquals(true, createdDC.isHalfHourlyForAppropiateMonth(isMonthT, isHH));
    assertNotNull(createdDC.getDpiFile());
    assertEquals(new Long(999), createdDC.getDpiFile().getPk());
    assertNotNull(createdDC.getGridSupplyPoints());
    assertEquals(1, createdDC.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdDC.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
      assertEquals(true, gsp.isHalfHourMpans2ndMonth());
    }
  }

  @Test
  public void addToAgentMOPMapIfNotThereAlready_nonHH_monthT_MOP_toEmptyMap() throws Exception
  {
    
    AgentDTO agentDTO = new AgentDTO();
    DpiFile dpiFile = new DpiFile();
    dpiFile.setPk(999L);
    agentDTO.setDpiFile(dpiFile);

    Map<String, MOP> agentsMap = new HashMap<String, MOP>();
    String gspName = "gsp", agentName = "newAgent";
    boolean isMop = false; //DC
    boolean isMonthT = true;
    MeasurementClassification classification = new MeasurementClassification("A"); //non HH

    //test method
    agentDTO.addToAgentMOPMapIfNotThereAlready(gspName, agentsMap, classification.isHalfHourly(), agentName, isMop, isMonthT);

    assertEquals(1, agentsMap.size());
    assertTrue(agentsMap.containsKey(agentName));
    MOP createdMOP = agentsMap.get(agentName);
    assertNotNull(createdMOP);
    assertEquals(agentName, createdMOP.getName());
    assertEquals(false, createdMOP.isHalfHourMpansFirstMonth());
    assertEquals(false, createdMOP.isHalfHourMpans2ndMonth());
    assertEquals(false, createdMOP.isNonHalfHourMpansFirstMonth());
    assertEquals(true, createdMOP.isNonHalfHourMpans2ndMonth());
    assertEquals(true, createdMOP.isHalfHourlyForAppropiateMonth(isMonthT, classification.isHalfHourly()));
    
    
    assertNotNull(createdMOP.getDpiFile());
    assertEquals(new Long(999), createdMOP.getDpiFile().getPk());
    assertNotNull(createdMOP.getGridSupplyPoints());
    assertEquals(1, createdMOP.getGridSupplyPoints().size());
    for (GridSupplyPoint gsp : createdMOP.getGridSupplyPoints())
    {
      assertEquals(gspName, gsp.getName());
      assertEquals(false, gsp.isHalfHourMpans2ndMonth());
      assertEquals(false, gsp.isHalfHourMpansFirstMonth());
    }
  }
}
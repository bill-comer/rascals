package uk.co.utilisoft.parms.file.dpi;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateTime;
import org.junit.Test;

import uk.co.utilisoft.afms.dao.AFMSAgentDao;
import uk.co.utilisoft.afms.dao.AFMSAgentHistoryDao;
import uk.co.utilisoft.afms.dao.AFMSMpanDao;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.AgentDTO;
import uk.co.utilisoft.parms.AgentRoleCodeType;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;


/**
 * Warning Note on unit tests using DateTimeUtils.setCurrentMillisFixed(Long) and DateTimeUtils.setCurrentMillisSystem():
 *
 * Some tests here have been fixed bug where junit tests using DateTimeUtils.setCurrentMillisFixed(Long) are not calling
 * DateTimeUtils.setCurrentMillisSystem() on finishing a test. This is potentially very nasty bug
 * waiting to happen. To make it worse, affected tests will only fail if run individually. If all
 * tests are run in the same jvm at once, they could all pass.
 * To fix affected tests, I suggest it is better to use a fixed DateTime(year, month, day, hour, min, sec, millis)
 * in tests, which will not then be affected by other tests using the above DateTimeUtils time freeze functions.
 */
public class AgentBuilderTest
{

  @Test
  public void test_buildAgentsForAfmsMpansForAMonth_onempan_nonHH_no_agentHistories_monthT() throws Exception
  {
    final String gspName = "_A";
    final String mpan1 = "mpan1";
    boolean isMonthT = true;

    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(period);
    Supplier supplier = new Supplier("testSup");
    //a HH mpan
    AFMSMpan afmsMpan1 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan1;
      }
      public String getGridSupplyPoint()
      {
        return gspName;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };
    afmsMpan1.setMeasurementClassification("A");
    List<AFMSMpan> expMpanList = new ArrayList<AFMSMpan>();
    expMpanList.add(afmsMpan1);

    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp1 = new GridSupplyPoint(gspName, dpiFile);
    gsps.put(gspName, gsp1);

    //get active mpans
    AFMSMpanDao mockAFMSMPanDao = createMock(AFMSMpanDao.class);
    expect(mockAFMSMPanDao.getActiveMpans(period, supplier, isMonthT)).andReturn(expMpanList);
    replay(mockAFMSMPanDao);

    //get DCagents
    MultiHashMap<String, AFMSAgent> afmsDCAgents = new MultiHashMap<String, AFMSAgent>();
    AFMSAgent agent1 = new AFMSAgent();
    agent1.setDataCollector("dc1");
    agent1.setMeterOperator("mop1");
    agent1.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0).minusYears(1));
    agent1.setMpan(afmsMpan1);
    afmsDCAgents.put(mpan1, agent1);
    AFMSAgentDao mockAfmsAgentDao = createMock(AFMSAgentDao.class);
    expect(mockAfmsAgentDao.getDataCollectorAgents(period, isMonthT)).andReturn(afmsDCAgents);

    afmsMpan1.setAgent(agent1);

    //get MOP agents
    MultiHashMap<String, AFMSAgent> afmsMOPAgents = new MultiHashMap<String, AFMSAgent>();
    afmsMOPAgents.put(mpan1, agent1);
    expect(mockAfmsAgentDao.getMOPAgents(period, isMonthT)).andReturn(afmsMOPAgents);
    replay(mockAfmsAgentDao);

    //getAgentHistories
    AFMSAgentHistoryDao mockAfmsAgentHistoryDao = createMock(AFMSAgentHistoryDao.class);
    MultiHashMap<String, AFMSAgentHistory> agentHistMap = new MultiHashMap<String, AFMSAgentHistory>();
    expect(mockAfmsAgentHistoryDao.getAllAgentHistoryScrollableResults(period, supplier, isMonthT)).andReturn(agentHistMap);
    replay(mockAfmsAgentHistoryDao);

    //Mock AgentDTO
    AgentDTO mockAgentDTO = new AgentDTO();

    AgentBuilder builder = new AgentBuilder();
    builder.setAfmsAgentDao(mockAfmsAgentDao);
    builder.setAfmsMpanDao(mockAFMSMPanDao);
    builder.setAFMSAgentHistoryDao(mockAfmsAgentHistoryDao);
    builder.setAgentDTO(mockAgentDTO);

    MOP expectedMop = new MOP();
    expectedMop.setDpiFile(dpiFile);
    expectedMop.getGridSupplyPoints().add(gsp1);
    expectedMop.setHalfHourMpans2ndMonth(false);
    expectedMop.setNonHalfHourMpans2ndMonth(true);
    expectedMop.setName("mop1");

    DataCollector expectedDC = new DataCollector();
    expectedDC.setDpiFile(dpiFile);
    expectedDC.getGridSupplyPoints().add(gsp1);
    expectedDC.setHalfHourMpans2ndMonth(false);
    expectedDC.setNonHalfHourMpans2ndMonth(true);
    expectedDC.setName("dc1");

    //test method
    builder.buildAgentsForAfmsMpansForAPeriod(period, supplier, dpiFile, isMonthT);

    //test results
    assertNotNull(builder.mDataCollectors);
    assertEquals(1, builder.mDataCollectors.size());
    assertTrue(builder.mDataCollectors.containsKey("dc1"));

    GenericAgent dcFound = builder.mDataCollectors.get("dc1");
    assertEquals(gspName, dcFound.getGridSupplyPoints().iterator().next().getName());
    assertEquals(period, dcFound.getDpiFile().getReportingPeriod());


    assertNotNull(builder.mMops);
    assertEquals(1, builder.mMops.size());
    assertTrue(builder.mMops.containsKey("mop1"));
    assertEquals(expectedDC, dcFound);

    verify(mockAFMSMPanDao, mockAfmsAgentDao, mockAfmsAgentHistoryDao);

    GenericAgent mopFound = builder.mMops.get("mop1");
    assertEquals(expectedMop, mopFound);

  }


  @Test
  public void test_buildAgentsForAfmsMpansForAMonth_onempan_HH_no_agentHistories_monthTMinusOne() throws Exception
  {
    final String gspName = "_A";
    final String mpan1 = "mpan1";
    boolean isMonthT = false;

    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(period);
    Supplier supplier = new Supplier("testSup");
    //a HH mpan
    AFMSMpan afmsMpan1 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan1;
      }
      public String getGridSupplyPoint()
      {
        return gspName;
      }

      public boolean isHalfHourly()
      {
        return true;
      }
    };
    afmsMpan1.setMeasurementClassification("C");  //is HH
    List<AFMSMpan> expMpanList = new ArrayList<AFMSMpan>();
    expMpanList.add(afmsMpan1);

    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp1 = new GridSupplyPoint(gspName, dpiFile);
    gsps.put(gspName, gsp1);

    //get active mpans
    AFMSMpanDao mockAFMSMPanDao = createMock(AFMSMpanDao.class);
    expect(mockAFMSMPanDao.getActiveMpans(period, supplier, isMonthT)).andReturn(expMpanList);
    replay(mockAFMSMPanDao);

    //get DCagents
    MultiHashMap<String, AFMSAgent> afmsDCAgents = new MultiHashMap<String, AFMSAgent>();
    AFMSAgent agent1 = new AFMSAgent();
    agent1.setDataCollector("dc1");
    DateTime efd20101130 = new DateTime(2010, 11, 30, 0, 0, 0, 0);
    agent1.setDCEffectiveFromDate(efd20101130); // check for dcEFD is not before start of month T fix this???
    agent1.setMeterOperator("mop1");
    agent1.setMOEffectiveFromDate(efd20101130); // check for mopEFD is not before start of month T fix this???
    agent1.setMpan(afmsMpan1);
    afmsDCAgents.put(mpan1, agent1);
    AFMSAgentDao mockAfmsAgentDao = createMock(AFMSAgentDao.class);
    expect(mockAfmsAgentDao.getDataCollectorAgents(period, isMonthT)).andReturn(afmsDCAgents);

    afmsMpan1.setAgent(agent1);

    //get MOP agents
    MultiHashMap<String, AFMSAgent> afmsMOPAgents = new MultiHashMap<String, AFMSAgent>();
    /*AFMSAgent mopagent1 = new AFMSAgent();
    mopagent1.setMeterOperator("mop1");
    mopagent1.setMOEffectiveFromDate(new DateTime().minusYears(1));*/
    afmsMOPAgents.put(mpan1, agent1);
    expect(mockAfmsAgentDao.getMOPAgents(period, isMonthT)).andReturn(afmsMOPAgents);
    replay(mockAfmsAgentDao);



    //getAgentHistories
    AFMSAgentHistoryDao mockAfmsAgentHistoryDao = createMock(AFMSAgentHistoryDao.class);
    MultiHashMap<String, AFMSAgentHistory> agentHistMap = new MultiHashMap<String, AFMSAgentHistory>();
    expect(mockAfmsAgentHistoryDao.getAllAgentHistoryScrollableResults(period, supplier, isMonthT)).andReturn(agentHistMap);
    replay(mockAfmsAgentHistoryDao);

    //Mock AgentDTO
    AgentDTO mockAgentDTO = new AgentDTO();

    AgentBuilder builder = new AgentBuilder();
    builder.setAfmsAgentDao(mockAfmsAgentDao);
    builder.setAfmsMpanDao(mockAFMSMPanDao);
    builder.setAFMSAgentHistoryDao(mockAfmsAgentHistoryDao);
    builder.setAgentDTO(mockAgentDTO);

    MOP expectedMop = new MOP();
    expectedMop.setDpiFile(dpiFile);
    expectedMop.getGridSupplyPoints().add(gsp1);
    expectedMop.setHalfHourMpans2ndMonth(false);
    expectedMop.setHalfHourMpansFirstMonth(true);
    expectedMop.setName("mop1");

    DataCollector expectedDC = new DataCollector();
    expectedDC.setDpiFile(dpiFile);
    expectedDC.getGridSupplyPoints().add(gsp1);
    expectedDC.setHalfHourMpans2ndMonth(false);
    expectedDC.setHalfHourMpansFirstMonth(true);
    expectedDC.setName("dc1");

    //test method
    builder.buildAgentsForAfmsMpansForAPeriod(period, supplier, dpiFile, isMonthT);

    //test results
    assertNotNull(builder.mDataCollectors);
    assertEquals(1, builder.mDataCollectors.size());
    assertTrue(builder.mDataCollectors.containsKey("dc1"));

    GenericAgent dcFound = builder.mDataCollectors.get("dc1");
    assertEquals(gspName, dcFound.getGridSupplyPoints().iterator().next().getName());
    assertEquals(period, dcFound.getDpiFile().getReportingPeriod());

    assertNotNull(builder.mMops);
    assertEquals(1, builder.mMops.size());
    assertTrue(builder.mMops.containsKey("mop1"));
    assertEquals(expectedDC, dcFound);

    verify(mockAFMSMPanDao, mockAfmsAgentDao, mockAfmsAgentHistoryDao);

    GenericAgent mopFound = builder.mMops.get("mop1");
    assertEquals(expectedMop, mopFound);

  }




  @Test
  public void test_buildAgentsForAfmsMpansForAMonth_twompans_nonHH_no_agentHistories_sameAgents() throws Exception
  {
    final String gspName = "_A";
    final String mpan1 = "mpan1";
    final String mpan2 = "mpan2";
    boolean isMonthT = true;

    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(period);
    Supplier supplier = new Supplier("testSup");
    //a HH mpan
    AFMSMpan afmsMpan1 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan1;
      }
      public String getGridSupplyPoint()
      {
        return gspName;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };
    afmsMpan1.setMeasurementClassification("A");

    //a HH mpan
    AFMSMpan afmsMpan2 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan2;
      }
      public String getGridSupplyPoint()
      {
        return gspName;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };
    afmsMpan2.setMeasurementClassification("A");

    List<AFMSMpan> expMpanList = new ArrayList<AFMSMpan>();
    expMpanList.add(afmsMpan1);
    expMpanList.add(afmsMpan2);

    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp1 = new GridSupplyPoint(gspName, dpiFile);
    gsps.put(gspName, gsp1);

    //get active mpans
    AFMSMpanDao mockAFMSMPanDao = createMock(AFMSMpanDao.class);
    expect(mockAFMSMPanDao.getActiveMpans(period, supplier, isMonthT)).andReturn(expMpanList);
    replay(mockAFMSMPanDao);

    //get DCagents
    MultiHashMap<String, AFMSAgent> afmsDCAgents = new MultiHashMap<String, AFMSAgent>();
    AFMSAgent agent1 = new AFMSAgent();
    agent1.setDataCollector("dc1");
    agent1.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMeterOperator("mop1");
    agent1.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMpan(afmsMpan1);
    afmsDCAgents.put(mpan1, agent1);

    AFMSAgent agent2 = new AFMSAgent();
    agent2.setDataCollector("dc1");
    agent2.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent2.setMeterOperator("mop1");
    agent2.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent2.setMpan(afmsMpan2);
    afmsDCAgents.put(mpan2, agent2);


    AFMSAgentDao mockAfmsAgentDao = createMock(AFMSAgentDao.class);
    expect(mockAfmsAgentDao.getDataCollectorAgents(period, isMonthT)).andReturn(afmsDCAgents);

    //get MOP agents
    MultiHashMap<String, AFMSAgent> afmsMOPAgents = new MultiHashMap<String, AFMSAgent>();
    /*AFMSAgent mopagent1 = new AFMSAgent();
    mopagent1.setMeterOperator("mop1");
    mopagent1.setMOEffectiveFromDate(new DateTime().minusYears(1));*/
    afmsMOPAgents.put(mpan1, agent1);
    expect(mockAfmsAgentDao.getMOPAgents(period, isMonthT)).andReturn(afmsMOPAgents);
    replay(mockAfmsAgentDao);

    afmsMpan1.setAgent(agent1);
    afmsMpan2.setAgent(agent2);


    //getAgentHistories
    AFMSAgentHistoryDao mockAfmsAgentHistoryDao = createMock(AFMSAgentHistoryDao.class);
    MultiHashMap<String, AFMSAgentHistory> agentHistMap = new MultiHashMap<String, AFMSAgentHistory>();
    expect(mockAfmsAgentHistoryDao.getAllAgentHistoryScrollableResults(period, supplier, isMonthT)).andReturn(agentHistMap);
    replay(mockAfmsAgentHistoryDao);

    //Mock AgentDTO
    AgentDTO mockAgentDTO = new AgentDTO();

    AgentBuilder builder = new AgentBuilder();
    builder.setAfmsAgentDao(mockAfmsAgentDao);
    builder.setAfmsMpanDao(mockAFMSMPanDao);
    builder.setAFMSAgentHistoryDao(mockAfmsAgentHistoryDao);
    builder.setAgentDTO(mockAgentDTO);

    MOP expectedMop = new MOP();
    expectedMop.setDpiFile(dpiFile);
    expectedMop.getGridSupplyPoints().add(gsp1);
    expectedMop.setHalfHourMpans2ndMonth(false);
    expectedMop.setNonHalfHourMpans2ndMonth(true);
    expectedMop.setName("mop1");

    DataCollector expectedDC = new DataCollector();
    expectedDC.setDpiFile(dpiFile);
    expectedDC.getGridSupplyPoints().add(gsp1);
    expectedDC.setHalfHourMpans2ndMonth(false);
    expectedDC.setNonHalfHourMpans2ndMonth(true);
    expectedDC.setName("dc1");

    //test method
    builder.buildAgentsForAfmsMpansForAPeriod(period, supplier, dpiFile, isMonthT);

    //test results
    assertNotNull(builder.mDataCollectors);
    assertEquals(1, builder.mDataCollectors.size());
    assertTrue(builder.mDataCollectors.containsKey("dc1"));

    GenericAgent dcFound = builder.mDataCollectors.get("dc1");
    assertEquals(gspName, dcFound.getGridSupplyPoints().iterator().next().getName());
    assertEquals(period, dcFound.getDpiFile().getReportingPeriod());


    assertNotNull(builder.mMops);
    assertEquals(1, builder.mMops.size());
    assertTrue(builder.mMops.containsKey("mop1"));
    assertEquals(expectedDC, dcFound);

    verify(mockAFMSMPanDao, mockAfmsAgentDao, mockAfmsAgentHistoryDao);

    GenericAgent mopFound = builder.mMops.get("mop1");
    assertEquals(expectedMop, mopFound);

  }



  @Test
  public void test_buildAgentsForAfmsMpansForAMonth_twompans_nonHH_no_agentHistories_difftAgents() throws Exception
  {
    final String gspName = "_A";
    final String gspName2 = "_B";
    final String mpan1 = "mpan1";
    final String mpan2 = "mpan2";
    boolean isMonthT = true;

    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(period);
    Supplier supplier = new Supplier("testSup");
    //a HH mpan
    AFMSMpan afmsMpan1 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan1;
      }
      public String getGridSupplyPoint()
      {
        return gspName;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };
    afmsMpan1.setMeasurementClassification("A");

    //a HH mpan
    AFMSMpan afmsMpan2 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan2;
      }
      public String getGridSupplyPoint()
      {
        return gspName2;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };
    afmsMpan2.setMeasurementClassification("A");

    List<AFMSMpan> expMpanList = new ArrayList<AFMSMpan>();
    expMpanList.add(afmsMpan1);
    expMpanList.add(afmsMpan2);

    Map<String, GridSupplyPoint> gsps = new HashMap<String, GridSupplyPoint>();
    GridSupplyPoint gsp1 = new GridSupplyPoint(gspName, dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint(gspName2, dpiFile);
    gsps.put(gspName, gsp1);
    gsps.put(gspName2, gsp2);

    //get active mpans
    AFMSMpanDao mockAFMSMPanDao = createMock(AFMSMpanDao.class);
    expect(mockAFMSMPanDao.getActiveMpans(period, supplier, isMonthT)).andReturn(expMpanList);
    replay(mockAFMSMPanDao);

    //get DCagents
    MultiHashMap<String, AFMSAgent> afmsDCAgents = new MultiHashMap<String, AFMSAgent>();
    AFMSAgent agent1 = new AFMSAgent();
    agent1.setDataCollector("dc1");
    agent1.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMeterOperator("mop1");
    agent1.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMpan(afmsMpan1);
    afmsDCAgents.put(mpan1, agent1);

    AFMSAgent agent2 = new AFMSAgent();
    agent2.setDataCollector("dc2");
    agent2.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent2.setMeterOperator("mop2");
    agent2.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent2.setMpan(afmsMpan2);
    afmsDCAgents.put(mpan2, agent2);
    AFMSAgentDao mockAfmsAgentDao = createMock(AFMSAgentDao.class);
    expect(mockAfmsAgentDao.getDataCollectorAgents(period, isMonthT)).andReturn(afmsDCAgents);

    //get MOP agents
    MultiHashMap<String, AFMSAgent> afmsMOPAgents = new MultiHashMap<String, AFMSAgent>();

    afmsMOPAgents.put(mpan1, agent1);
    afmsMOPAgents.put(mpan2, agent2);

    expect(mockAfmsAgentDao.getMOPAgents(period, isMonthT)).andReturn(afmsMOPAgents);
    replay(mockAfmsAgentDao);

    afmsMpan1.setAgent(agent1);
    afmsMpan2.setAgent(agent2);

    //getAgentHistories
    AFMSAgentHistoryDao mockAfmsAgentHistoryDao = createMock(AFMSAgentHistoryDao.class);
    MultiHashMap<String, AFMSAgentHistory> agentHistMap = new MultiHashMap<String, AFMSAgentHistory>();
    expect(mockAfmsAgentHistoryDao.getAllAgentHistoryScrollableResults(period, supplier, isMonthT)).andReturn(agentHistMap);
    replay(mockAfmsAgentHistoryDao);

    //Mock AgentDTO
    AgentDTO mockAgentDTO = new AgentDTO();

    AgentBuilder builder = new AgentBuilder();
    builder.setAfmsAgentDao(mockAfmsAgentDao);
    builder.setAfmsMpanDao(mockAFMSMPanDao);
    builder.setAFMSAgentHistoryDao(mockAfmsAgentHistoryDao);
    builder.setAgentDTO(mockAgentDTO);

    MOP expMop1 = new MOP();
    expMop1.setDpiFile(dpiFile);
    expMop1.getGridSupplyPoints().add(gsp1);
    expMop1.setHalfHourMpans2ndMonth(false);
    expMop1.setNonHalfHourMpans2ndMonth(true);
    expMop1.setName("mop1");

    MOP expMop2 = new MOP();
    expMop2.setDpiFile(dpiFile);
    expMop2.getGridSupplyPoints().add(gsp1);
    expMop2.setHalfHourMpans2ndMonth(false);
    expMop2.setNonHalfHourMpans2ndMonth(true);
    expMop2.setName("mop2");

    DataCollector expDC1 = new DataCollector();
    expDC1.setDpiFile(dpiFile);
    expDC1.getGridSupplyPoints().add(gsp1);
    expDC1.setHalfHourMpans2ndMonth(false);
    expDC1.setNonHalfHourMpans2ndMonth(true);
    expDC1.setName("dc1");

    DataCollector expDC2 = new DataCollector();
    expDC2.setDpiFile(dpiFile);
    expDC2.getGridSupplyPoints().add(gsp1);
    expDC2.setHalfHourMpans2ndMonth(false);
    expDC2.setNonHalfHourMpans2ndMonth(true);
    expDC2.setName("dc2");

    //test method
    builder.buildAgentsForAfmsMpansForAPeriod(period, supplier, dpiFile, isMonthT);

    //test results
    assertNotNull(builder.mDataCollectors);
    assertEquals(2, builder.mDataCollectors.size());
    assertTrue(builder.mDataCollectors.containsKey("dc1"));

    GenericAgent dcFound = builder.mDataCollectors.get("dc1");
    assertTrue(expDC1.equals(dcFound));

    GenericAgent dcFound2 = builder.mDataCollectors.get("dc2");
    assertTrue(expDC2.equals(dcFound2));

    assertNotNull(builder.mMops);
    assertEquals(2, builder.mMops.size());
    assertTrue(builder.mMops.containsKey("mop1"));
    assertEquals(expDC1, dcFound);

    verify(mockAFMSMPanDao, mockAfmsAgentDao, mockAfmsAgentHistoryDao);

    GenericAgent mopFound = builder.mMops.get("mop1");
    assertEquals(expMop1, mopFound);
    GenericAgent mopFound2 = builder.mMops.get("mop2");
    assertEquals(expMop2, mopFound2);

  }



  @Test
  public void test_buildAgentsForAfmsMpansForAMonth_twompans_oneHH_no_agentHistories_difftAgents() throws Exception
  {
    final String gspName = "_A";
    final String gspName2 = "_B";
    final String mpan1 = "mpan1";
    final String mpan2 = "mpan2";
    boolean isMonthT = true;

    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(period);
    Supplier supplier = new Supplier("testSup");
    //a HH mpan
    AFMSMpan afmsMpan1 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan1;
      }
      public String getGridSupplyPoint()
      {
        return gspName;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };
    afmsMpan1.setMeasurementClassification("A");  //non HH

    //a HH mpan
    AFMSMpan afmsMpan2 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return mpan2;
      }
      public String getGridSupplyPoint()
      {
        return gspName2;
      }

      public boolean isHalfHourly()
      {
        return true;
      }
    };
    afmsMpan2.setMeasurementClassification("C"); //HH

    List<AFMSMpan> expMpanList = new ArrayList<AFMSMpan>();
    expMpanList.add(afmsMpan1);
    expMpanList.add(afmsMpan2);

    GridSupplyPoint gsp1 = new GridSupplyPoint(gspName, dpiFile);

    //get active mpans
    AFMSMpanDao mockAFMSMPanDao = createMock(AFMSMpanDao.class);
    expect(mockAFMSMPanDao.getActiveMpans(period, supplier, isMonthT)).andReturn(expMpanList);
    replay(mockAFMSMPanDao);

    //get DCagents
    MultiHashMap<String, AFMSAgent> afmsDCAgents = new MultiHashMap<String, AFMSAgent>();
    AFMSAgent agent1 = new AFMSAgent();
    agent1.setDataCollector("dc1");
    agent1.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMeterOperator("mop1");
    agent1.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent1.setMpan(afmsMpan1);
    afmsDCAgents.put(mpan1, agent1);

    AFMSAgent agent2 = new AFMSAgent();
    agent2.setDataCollector("dc2");
    agent2.setDCEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent2.setMeterOperator("mop2");
    agent2.setMOEffectiveFromDate(new DateTime(2004, 3, 26, 0, 0, 0, 0));
    agent2.setMpan(afmsMpan2);
    afmsDCAgents.put(mpan2, agent2);
    AFMSAgentDao mockAfmsAgentDao = createMock(AFMSAgentDao.class);
    expect(mockAfmsAgentDao.getDataCollectorAgents(period, isMonthT)).andReturn(afmsDCAgents);

    //get MOP agents
    MultiHashMap<String, AFMSAgent> afmsMOPAgents = new MultiHashMap<String, AFMSAgent>();

    afmsMOPAgents.put(mpan1, agent1);
    afmsMOPAgents.put(mpan2, agent2);

    expect(mockAfmsAgentDao.getMOPAgents(period, isMonthT)).andReturn(afmsMOPAgents);
    replay(mockAfmsAgentDao);

    afmsMpan1.setAgent(agent1);
    afmsMpan2.setAgent(agent2);

    //getAgentHistories
    AFMSAgentHistoryDao mockAfmsAgentHistoryDao = createMock(AFMSAgentHistoryDao.class);
    MultiHashMap<String, AFMSAgentHistory> agentHistMap = new MultiHashMap<String, AFMSAgentHistory>();
    expect(mockAfmsAgentHistoryDao.getAllAgentHistoryScrollableResults(period, supplier, isMonthT)).andReturn(agentHistMap);
    replay(mockAfmsAgentHistoryDao);

    //Mock AgentDTO
    AgentDTO mockAgentDTO = new AgentDTO();

    AgentBuilder builder = new AgentBuilder();
    builder.setAfmsAgentDao(mockAfmsAgentDao);
    builder.setAfmsMpanDao(mockAFMSMPanDao);
    builder.setAFMSAgentHistoryDao(mockAfmsAgentHistoryDao);
    builder.setAgentDTO(mockAgentDTO);

    MOP expMop1 = new MOP();
    expMop1.setDpiFile(dpiFile);
    expMop1.getGridSupplyPoints().add(gsp1);
    expMop1.setHalfHourMpansFirstMonth(false);
    expMop1.setHalfHourMpans2ndMonth(false);
    expMop1.setNonHalfHourMpansFirstMonth(false);
    expMop1.setNonHalfHourMpans2ndMonth(true);
    expMop1.setName("mop1");

    MOP expMop2 = new MOP();
    expMop2.setDpiFile(dpiFile);
    expMop2.getGridSupplyPoints().add(gsp1);
    expMop2.setHalfHourMpansFirstMonth(false);
    expMop2.setHalfHourMpans2ndMonth(true);
    expMop2.setName("mop2");

    DataCollector expDC1 = new DataCollector();
    expDC1.setDpiFile(dpiFile);
    expDC1.getGridSupplyPoints().add(gsp1);
    expDC1.setHalfHourMpansFirstMonth(false);
    expDC1.setHalfHourMpans2ndMonth(false);
    expDC1.setNonHalfHourMpansFirstMonth(false);
    expDC1.setNonHalfHourMpans2ndMonth(true);
    expDC1.setName("dc1");

    DataCollector expDC2 = new DataCollector();
    expDC2.setDpiFile(dpiFile);
    expDC2.getGridSupplyPoints().add(gsp1);
    expDC2.setHalfHourMpansFirstMonth(false);
    expDC2.setHalfHourMpans2ndMonth(true);
    expDC2.setName("dc2");

    //test method
    builder.buildAgentsForAfmsMpansForAPeriod(period, supplier, dpiFile, isMonthT);

    //test results
    assertNotNull(builder.mDataCollectors);
    assertEquals(2, builder.mDataCollectors.size());
    assertTrue(builder.mDataCollectors.containsKey("dc1"));

    GenericAgent dcFound = builder.mDataCollectors.get("dc1");
    assertTrue(expDC1.equals(dcFound));

    GenericAgent dcFound2 = builder.mDataCollectors.get("dc2");
    assertTrue(expDC2.equals(dcFound2));

    assertNotNull(builder.mMops);
    assertEquals(2, builder.mMops.size());
    assertTrue(builder.mMops.containsKey("mop1"));
    assertEquals(expDC1, dcFound);

    verify(mockAFMSMPanDao, mockAfmsAgentDao, mockAfmsAgentHistoryDao);

    GenericAgent mopFound = builder.mMops.get("mop1");
    assertEquals(expMop1, mopFound);
    GenericAgent mopFound2 = builder.mMops.get("mop2");
    assertEquals(expMop2, mopFound2);

  }

  @Test
  public void getDCHistories_noneToFind() throws Exception
  {
    Collection<AFMSAgentHistory> collectionOfAllHistories = new ArrayList<AFMSAgentHistory>();

    AgentBuilder builder = new AgentBuilder();
    Collection<AFMSAgentHistory> found = builder.getDCHistories(collectionOfAllHistories);

    assertNotNull(found);
    assertEquals(0, found.size());
  }


  @Test
  public void getDCHistories_oneToFind() throws Exception
  {
    Collection<AFMSAgentHistory> collectionOfAllHistories = new ArrayList<AFMSAgentHistory>();
    AFMSAgentHistory afmsAgentHistory = new AFMSAgentHistory();
    afmsAgentHistory.setAgentRoleCode(AgentRoleCodeType.C);
    collectionOfAllHistories.add(afmsAgentHistory);

    AgentBuilder builder = new AgentBuilder();
    //test method
    Collection<AFMSAgentHistory> found = builder.getDCHistories(collectionOfAllHistories);

    assertNotNull(found);
    assertEquals(1, found.size());
  }


  @Test
  public void getDCHistories_twoToFind() throws Exception
  {
    Collection<AFMSAgentHistory> collectionOfAllHistories = new ArrayList<AFMSAgentHistory>();
    AFMSAgentHistory afmsAgentHistory1 = new AFMSAgentHistory();
    afmsAgentHistory1.setAgentRoleCode(AgentRoleCodeType.C);
    collectionOfAllHistories.add(afmsAgentHistory1);

    AFMSAgentHistory afmsAgentHistory2 = new AFMSAgentHistory();
    afmsAgentHistory2.setAgentRoleCode(AgentRoleCodeType.D);
    collectionOfAllHistories.add(afmsAgentHistory2);

    AgentBuilder builder = new AgentBuilder();
    //test method
    Collection<AFMSAgentHistory> found = builder.getDCHistories(collectionOfAllHistories);

    assertNotNull(found);
    assertEquals(2, found.size());
  }


  @Test
  public void getDCHistories_twoToFind_fromMultiHashMap() throws Exception
  {

    AFMSMpan afmsMpan = new AFMSMpan();
    afmsMpan.setMpanCore("1300000000007");

    AFMSAgentHistory afmsAgentHistory1 = new AFMSAgentHistory();
    afmsAgentHistory1.setAgentRoleCode(AgentRoleCodeType.C);
    afmsAgentHistory1.setMpan(afmsMpan);

    AFMSAgentHistory afmsAgentHistory2 = new AFMSAgentHistory();
    afmsAgentHistory2.setAgentRoleCode(AgentRoleCodeType.D);
    afmsAgentHistory1.setMpan(afmsMpan);

    MultiHashMap<String, AFMSAgentHistory> allAfmsAgentHistories = new MultiHashMap<String, AFMSAgentHistory>();
    allAfmsAgentHistories.put(afmsMpan.getMpanCore(), afmsAgentHistory1);
    allAfmsAgentHistories.put(afmsMpan.getMpanCore(), afmsAgentHistory2);

    Collection<AFMSAgentHistory> collectionOfAllHistories = allAfmsAgentHistories.getCollection(afmsMpan.getMpanCore());

    assertNotNull(collectionOfAllHistories);
    assertEquals(2, collectionOfAllHistories.size());

    AgentBuilder builder = new AgentBuilder();
    //test method
    Collection<AFMSAgentHistory> found = builder.getDCHistories(collectionOfAllHistories);

    assertNotNull(found);
    assertEquals(2, found.size());
  }
}

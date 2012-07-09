package uk.co.utilisoft.parms.file.dpi;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.afms.dao.AFMSAgentDao;
import uk.co.utilisoft.afms.dao.AFMSAgentHistoryDao;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.AgentDTO;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;

public class DpiFileProcessUnitTest
{
  // test values
  private String GSPName = "TEST_GSP1";
  private String MPAN_CORE1 = "MPAN_CORE1";
  private String AFMS_MOP = "AFMS_MOP";
  private String AFMS_DC = "AFMS_DC";

  private Map<String, MOP> mMopsCreatedByAgentBuilder;
  private Map<String, DataCollector> mDataCollectorsCreatedByAgentBuilder;

  private DpiFileBuilder mDpiFileBuilder;
  private DpiDataCreator mDpiDataCreator;
  private AgentBuilder mAgentBuilder;
  private AgentDTO mAgentDTO;
  private AFMSAgentDao mAFMSAgentDao; // mocked not stubbed
  private AFMSAgentHistoryDao mAFMSAgentHistoryDao; // mocked

  List<AFMSMpan> getTestActiveMpans(ParmsReportingPeriod aParmsReportingPeriod,
                                    Supplier supplier)
  {
    List<AFMSMpan> listOfMpans = new ArrayList<AFMSMpan>();
    AFMSMpan afmsMpan = new AFMSMpan();
    afmsMpan.setMpanCore(MPAN_CORE1);
    afmsMpan.setGridSupplyPoint(GSPName);
    afmsMpan.setMeasurementClassification("C"); // an HH value
    afmsMpan.setAgent(getTestAfmsAgent());

    listOfMpans.add(afmsMpan);
    return listOfMpans;
  }

  @Before
  public void setup()
  {
    mAFMSAgentDao = createMock(AFMSAgentDao.class);
    mAFMSAgentHistoryDao = createMock(AFMSAgentHistoryDao.class);

    mDpiDataCreator = new DpiDataCreator()
    {
      @Override
      public DpiFile makePersistent(DpiFile dpiFile)
      {
        dpiFile.setPk(999L);
        return dpiFile;
      }
    };

    /**
     * stubbed DpiFileBuilder that just sets the Pk to 999 instead of going to
     * the DB
     */
    mDpiFileBuilder = new DpiFileBuilder()
    {

      @Override
      public String[] buildFileData(DpiFile aDpiFile, Supplier aSupplier)
      {
        return null;
      }
    };

    mAgentBuilder = new AgentBuilder()
    {
      /**
       * change to return just one AFMSMpan
       *
       * @param aParmsReportingPeriod
       * @param supplier
       * @return
       */
      @Override
      public List<AFMSMpan> getActiveMpans(ParmsReportingPeriod aParmsReportingPeriod,
                                           Supplier supplier, boolean isMonthT)
      {
        return getTestActiveMpans(aParmsReportingPeriod, supplier);
      }

      @Override
      public void saveAllAgents()
      {
        Long pk = 2000L;
        // do not do save to DB
        mMopsCreatedByAgentBuilder = mMops;
        mDataCollectorsCreatedByAgentBuilder = mDataCollectors;
        for (GenericAgent mop : mMopsCreatedByAgentBuilder.values())
        {
          mop.setPk(pk++);
        }
        pk = 3000L;
        for (GenericAgent dc : mDataCollectorsCreatedByAgentBuilder.values())
        {
          dc.setPk(pk++);
        }
      }
    };

    mAgentDTO = new AgentDTO();

  }

  @Test
  public void buildFileForOneSupplier_one_mpan_oneHHDC_one_HHmop()
      throws Exception
  {
    AFMSMpan afmsMpan1 = new AFMSMpan()
    {
      public String getMpanCore()
      {
        return MPAN_CORE1;
      }

      public String getGridSupplyPoint()
      {
        return GSPName;
      }

      public boolean isHalfHourly()
      {
        return false;
      }
    };

    DateTime lastYear = new DateTime(2004, 3, 26, 0, 0, 0, 0);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    Supplier supplier = new Supplier("SUPP");

    DpiFileProcess dpiFileProcessSUT = new DpiFileProcess();
    dpiFileProcessSUT.setDpiFileBuilder(mDpiFileBuilder);
    dpiFileProcessSUT.setDpiDataCreator(mDpiDataCreator);
    mDpiDataCreator.setAgentBuilder(mAgentBuilder);

    mAgentBuilder.setAfmsAgentDao(mAFMSAgentDao);
    mAgentBuilder.setAFMSAgentHistoryDao(mAFMSAgentHistoryDao);
    mAgentBuilder.setAgentDTO(mAgentDTO);

    MultiHashMap<String, AFMSAgent> mockedAfmsDCAgentsThisMonth = new MultiHashMap<String, AFMSAgent>();
    AFMSAgent aFMSAgent = new AFMSAgent();
    aFMSAgent.setDCEffectiveFromDate(lastYear);
    aFMSAgent.setDataCollector(AFMS_DC);
    aFMSAgent.setMOEffectiveFromDate(lastYear);
    aFMSAgent.setMeterOperator(AFMS_MOP);
    aFMSAgent.setMpan(afmsMpan1);

    setTestAfmsAgent(aFMSAgent);

    afmsMpan1.setAgent(aFMSAgent);

    mockedAfmsDCAgentsThisMonth.put(MPAN_CORE1, aFMSAgent);
    expect(mAFMSAgentDao.getDataCollectorAgents(period, true))
        .andReturn(mockedAfmsDCAgentsThisMonth);
    expect(mAFMSAgentDao.getDataCollectorAgents(period, false))
        .andReturn(mockedAfmsDCAgentsThisMonth);

    MultiHashMap<String, AFMSAgent> mockedAfmsMOPAgentsThisMonth = new MultiHashMap<String, AFMSAgent>();

    mockedAfmsMOPAgentsThisMonth.put(MPAN_CORE1, aFMSAgent);
    expect(mAFMSAgentDao.getMOPAgents(period, true))
        .andReturn(mockedAfmsMOPAgentsThisMonth);
    expect(mAFMSAgentDao.getMOPAgents(period, false))
        .andReturn(mockedAfmsMOPAgentsThisMonth);

    MultiHashMap<String, AFMSAgentHistory> mockedAllAfmsAgentHistories = new MultiHashMap<String, AFMSAgentHistory>();
    expect(
           mAFMSAgentHistoryDao.getAllAgentHistoryScrollableResults(period,
                                                                    supplier,
                                                                    true))
        .andReturn(mockedAllAfmsAgentHistories);
    expect(
           mAFMSAgentHistoryDao.getAllAgentHistoryScrollableResults(period,
                                                                    supplier,
                                                                    false))
        .andReturn(mockedAllAfmsAgentHistories);

    replay(mAFMSAgentDao, mAFMSAgentHistoryDao);

    // test method
    dpiFileProcessSUT.buildFileForOneSupplier(period, supplier);

    verify(mAFMSAgentDao, mAFMSAgentHistoryDao);

    // test the GSPs that we created

    // test the DCs created by AgentBuilder
    assertEquals(1, mDataCollectorsCreatedByAgentBuilder.size());
    assertTrue(mDataCollectorsCreatedByAgentBuilder.containsKey(AFMS_DC));
    GenericAgent dc = mDataCollectorsCreatedByAgentBuilder.get(AFMS_DC);
    assertTrue(dc.isHalfHourMpans2ndMonth());
    assertTrue(dc.getName().equals(AFMS_DC));
    assertEquals(1, dc.getGridSupplyPoints().size());
    GridSupplyPoint gspFromDC = dc.getGridSupplyPoints().iterator().next();
    assertNotNull(gspFromDC);
    assertEquals(GSPName, gspFromDC.getName());

    assertTrue(gspFromDC.isHalfHourMpans2ndMonth());
    assertTrue(gspFromDC.getDpiFile().getPk().equals(new Long(999)));

    // test the Mops created by AgentBuilder
    assertEquals(1, mMopsCreatedByAgentBuilder.size());
    assertTrue(mMopsCreatedByAgentBuilder.containsKey(AFMS_MOP));
    GenericAgent mop = mMopsCreatedByAgentBuilder.get(AFMS_MOP);
    assertTrue(mop.isHalfHourMpans2ndMonth());
    assertTrue(mop.getName().equals(AFMS_MOP));
    assertEquals(1, mop.getGridSupplyPoints().size());
    GridSupplyPoint gspFromMOP = mop.getGridSupplyPoints().iterator().next();
    assertNotNull(gspFromMOP);
    assertEquals(GSPName, gspFromMOP.getName());

    assertTrue(gspFromMOP.isHalfHourMpans2ndMonth());
    assertTrue(gspFromMOP.getDpiFile().getPk().equals(new Long(999)));

  }

  private AFMSAgent mAFMSAgent;

  private AFMSAgent getTestAfmsAgent()
  {
    return mAFMSAgent;
  }

  private void setTestAfmsAgent(AFMSAgent aFMSAgent)
  {
    mAFMSAgent = aFMSAgent;

  }

}

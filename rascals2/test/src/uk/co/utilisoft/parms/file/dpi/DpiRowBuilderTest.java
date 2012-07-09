package uk.co.utilisoft.parms.file.dpi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.utilisoft.BaseTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.GridSupplyPointDao;
import uk.co.utilisoft.parms.dao.GspDefinitionDao;
import uk.co.utilisoft.parms.dao.SerialConfigDao;
import uk.co.utilisoft.parms.dao.SerialConfigDaoHibernate;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GSPDefinition;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.SerialConfiguration;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.ChecksumCalculator;
import uk.co.utilisoft.parms.file.PoolChecksumCalculator;
import uk.co.utilisoft.utils.Freeze;

public class DpiRowBuilderTest extends BaseTest
{

  @Before
  public void freezeTheTime()
  {
    super.freezeTime(new DateTime(2005, 3, 26, 11, 10, 50, 00));
  }
  
  @After
  public void thaw()
  {
    Freeze.thaw();
  }
  
  
  
  @Test
  public void getIsAgentHHSameAsrequested_isHH_isMonthT() throws Exception
  { 
    MOP mop = new MOP("fred", true, null, true);
    DpiRowBuilder dpiRowBuilder = new DpiRowBuilder();
    assertTrue(dpiRowBuilder.getIsAgentHHSameAsRequested(true, true, mop));
  }

  @Test
  public void getIsAgentHHSameAsrequested_isNOTHH_isMonthT() throws Exception
  { 
    boolean isHH = false;
    boolean isMonthT = true;
    
    MOP mop = new MOP("fred", isHH, null, isMonthT);
    
    DpiRowBuilder dpiRowBuilder = new DpiRowBuilder();
    assertTrue(dpiRowBuilder.getIsAgentHHSameAsRequested(isHH, isMonthT, mop));
  }
  

  @Test
  public void getIsAgentHHSameAsrequested_isHH_isMonthTMinus1() throws Exception
  { 
    MOP mop = new MOP("fred", true, null, false);
    DpiRowBuilder dpiRowBuilder = new DpiRowBuilder();
    assertTrue(dpiRowBuilder.getIsAgentHHSameAsRequested(true, false, mop));
  }

  @Test
  public void getIsAgentHHSameAsrequested_isNOTHH_isMonthTMinus1() throws Exception
  { 
    MOP mop = new MOP("fred", false, null, false);
    DpiRowBuilder dpiRowBuilder = new DpiRowBuilder();
    assertTrue(dpiRowBuilder.getIsAgentHHSameAsRequested(false, false, mop));
  }
  
  @Test
  public void test_addSP04Records_ANoRowsAdded_noGSPs() throws Exception
  { 
    Supplier supplier = new Supplier("A_SUPP");
    DpiFile dpiFile = new DpiFile();
    dpiFile.setSupplier(supplier);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    dpiFile.setReportingPeriod(period);
    List<String> rows = new ArrayList<String>();
    
    DpiRowBuilder builder = new DpiRowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        return gsps;
      }
    };
    
    //test method
    builder.addSP04Records(rows, dpiFile);
    
    assertEquals("no rows added as only one GSP & it gas no HH MPANS", 0, rows.size());
    
  }
  

  @Test
  public void test_addSP04Records_One_RowsAdded_monthT_false() throws Exception
  { 
    Supplier supplier = new Supplier("A_SUPP");
    DpiFile dpiFile = new DpiFile();
    dpiFile.setSupplier(supplier);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    dpiFile.setReportingPeriod(period);
    List<String> rows = new ArrayList<String>();
    
    DpiRowBuilder builder = new DpiRowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        GSPDefinition gsp = new GSPDefinition();
        gsp.setName("AGSP");
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        gsps.add(gsp);
        return gsps;
      }
    };
    
    //test method
    builder.addSP04Records(rows, dpiFile);
    
    assertEquals("no rows added as only one GSP & it gas no HH MPANS", 1, rows.size());

    String row = rows.get(0);
    assertTrue(row.length() > 0);
    assertEquals("DPI|AGSP|SP04|A_SUPP|X|20101231", row);
  }
  

  
  

  @Test
  public void test_addSP04Records_TwosRowAddedForTwoGSPs_both_gsps_false() throws Exception
  { 
    Supplier supplier = new Supplier("A_SUPP");
    DpiFile dpiFile = new DpiFile();
    dpiFile.setSupplier(supplier);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    dpiFile.setReportingPeriod(period);
    
    List<String> rows = new ArrayList<String>();
    
    DpiRowBuilder builder = new DpiRowBuilder()
    {
      public List<GSPDefinition> getAllGspDefinitions()
      {
        GSPDefinition gsp = new GSPDefinition();
        gsp.setName("AGSP");
        
        GSPDefinition gsp2 = new GSPDefinition();
        gsp2.setName("BGSP");
        
        List<GSPDefinition> gsps = new ArrayList<GSPDefinition>();
        gsps.add(gsp);
        gsps.add(gsp2);
        return gsps;
      }
    };
    
    //test method
    builder.addSP04Records(rows, dpiFile);
    
    assertEquals("one rows added as only one GSP & it gas  HH MPANS", 2, rows.size());
    assertEquals("DPI|AGSP|SP04|A_SUPP|X|20101231", rows.get(0));
    assertEquals("DPI|BGSP|SP04|A_SUPP|X|20101231", rows.get(1));
  }
  

  @Test
  public void testBasicRowBuildingForMOPMonthTMinus1() throws Exception
  {
    boolean isMonthT = false;
    boolean isHH = true;
    
    DpiRowBuilder builder = new DpiRowBuilder();
    GridSupplyPoint gsp = new GridSupplyPoint("_X", null);

    MOP mop = new MOP("FRED", isHH, null, isMonthT);
    mop.getGridSupplyPoints().add(gsp);
    SerialConfiguration serial = new SerialConfiguration();
    serial.setName("ASERIAL");
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    assertEquals("DPI|_X|ASERIAL|FRED|M|20101130", builder.createLineForSerial(gsp.getName(), 
        mop.getName(), mop.getRoleCode(isMonthT, isHH).getValue(), 
        serial.getName(), period));
  }
  

  @Test
  public void testBasicRowBuildingForMOPMonthT() throws Exception
  {
    boolean isMonthT = true;
    boolean isHH = true;
    
    DpiRowBuilder builder = new DpiRowBuilder();
    GridSupplyPoint gsp = new GridSupplyPoint("_X", null);

    MOP mop = new MOP("FRED", isHH, null, isMonthT);
    mop.getGridSupplyPoints().add(gsp);
    SerialConfiguration serial = new SerialConfiguration();
    serial.setName("ASERIAL");
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    assertEquals("DPI|_X|ASERIAL|FRED|M|20101130", builder.createLineForSerial(gsp.getName(), 
        mop.getName(), mop.getRoleCode(isMonthT, isHH).getValue(), 
        serial.getName(), period));
  }


  @Test
  public void testBasicRowBuildingForHHDCMonthT() throws Exception
  {
    boolean isMonthT = true;
    boolean isHH = true;
    
    DpiRowBuilder builder = new DpiRowBuilder();
    GridSupplyPoint gsp = new GridSupplyPoint("_X", null);

    DataCollector dc = new DataCollector("FRED", true, null, isMonthT);
    dc.getGridSupplyPoints().add(gsp);
    SerialConfiguration serial = new SerialConfiguration();
    serial.setName("ASERIAL");
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    
    assertEquals("DPI|_X|ASERIAL|FRED|C|20101130", builder.createLineForSerial(gsp.getName(), 
        dc.getName(), dc.getRoleCode(isMonthT, isHH).getValue(),
        serial.getName(), period));
  }
  


  @Test
  public void testBasicRowBuildingForHHDCMonthTMinus1() throws Exception
  {
    boolean isMonthT = false;
    boolean isHH = true;
    
    DpiRowBuilder builder = new DpiRowBuilder();
    GridSupplyPoint gsp = new GridSupplyPoint("_X", null);

    DataCollector dc = new DataCollector("FRED", isHH, null, isMonthT);
    dc.getGridSupplyPoints().add(gsp);
    SerialConfiguration serial = new SerialConfiguration();
    serial.setName("ASERIAL");
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    
    assertEquals("DPI|_X|ASERIAL|FRED|C|20101130", builder.createLineForSerial(gsp.getName(), 
        dc.getName(), dc.getRoleCode(isMonthT, isHH).getValue(),
        serial.getName(), period));
  }


  @Test
  public void testBasicRowBuildingFor_NONHHDC_MonthT() throws Exception
  {
    boolean isMonthT = true;
    boolean isHH = false;
    
    DpiRowBuilder builder = new DpiRowBuilder();
    GridSupplyPoint gsp = new GridSupplyPoint("_X", null);
    
    DataCollector dc = new DataCollector("FRED", isHH, null, isMonthT);
    dc.getGridSupplyPoints().add(gsp);
    
    SerialConfiguration serial = new SerialConfiguration();
    serial.setName("ASERIAL");
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    assertEquals("DPI|_X|ASERIAL|FRED|D|20101130", builder.createLineForSerial(gsp.getName(), 
        dc.getName(), dc.getRoleCode(isMonthT, isHH).getValue(), serial.getName(), period));
  }

  @Test
  public void testBasicRowBuildingForNONHHDCMonthTMinus1() throws Exception
  {
    boolean isMonthT = false;
    boolean isHH = false;
    
    DpiRowBuilder builder = new DpiRowBuilder();
    GridSupplyPoint gsp = new GridSupplyPoint("_X", null);
    
    DataCollector dc = new DataCollector("FRED", isHH, null, isMonthT);
    dc.getGridSupplyPoints().add(gsp);
    
    SerialConfiguration serial = new SerialConfiguration();
    serial.setName("ASERIAL");
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    assertEquals("DPI|_X|ASERIAL|FRED|D|20101130", builder.createLineForSerial(gsp.getName(), 
        dc.getName(), dc.getRoleCode(isMonthT, isHH).getValue(), 
        serial.getName(), period));
  }

  @Test
  public void testBuildRowsWithNoDPIrows() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    DpiRowBuilder builder = new DpiRowBuilder()
    {
      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksum();
      }
    };
    builder.setSupplier(supplier);

    GridSupplyPointDao mockGridSupplyPointDao = createMock(GridSupplyPointDao.class);
    DpiFile dpiFile = new DpiFile();
    dpiFile.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    List<GridSupplyPoint> gspsFromMockCall = new ArrayList<GridSupplyPoint>();
    expect(mockGridSupplyPointDao.getAllGSPsDpi(dpiFile)).andReturn(gspsFromMockCall);
    builder.setGridSupplyPointDao(mockGridSupplyPointDao);
    replay(mockGridSupplyPointDao);
    
    GspDefinitionDao mockGspDefinitionDao = createMock(GspDefinitionDao.class);
    List<GSPDefinition> gspDefsFromMockCall = new ArrayList<GSPDefinition>();
    expect(mockGspDefinitionDao.getAll()).andReturn(gspDefsFromMockCall);
    builder.setGspDefinitionDao(mockGspDefinitionDao);
    replay(mockGspDefinitionDao);
    
    
    //test method
    List<String> rows = builder.buildAllRows(dpiFile);

    assertNotNull(rows);
    assertEquals(2, rows.size());
    assertEquals("ZHD|P0135001|X|ATESTSUPPLIERID|Z|POOL|20050326111050", rows.get(0));
    assertEquals("ZPT|2|998877", rows.get(1));
    verify(mockGridSupplyPointDao, mockGspDefinitionDao);
  }

  @Test
  public void testBuildRows_WithOneMopRow_NonHH_MonthT() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    TempDpiRowBuilder builder = new TempDpiRowBuilder()
    {
      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksum();
      }
    };
    builder.setSupplier(supplier);

    GridSupplyPointDao mock = createMock(GridSupplyPointDao.class);
    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight());
    DpiFile dpiFile = new DpiFile(period, supplier);
    
    List<GridSupplyPoint> gspsFromMockCall = new ArrayList<GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("testGspFromMock", dpiFile);
    gsp.setHalfHourMpansFirstMonth(true);
    gsp.setHalfHourMpans2ndMonth(true);
    gsp.setNonHalfHourMpansFirstMonth(true);
    gsp.setNonHalfHourMpans2ndMonth(true);
    MOP m1 = new MOP("m1", false, dpiFile, true);
    gsp.setAgent(m1);
    gspsFromMockCall.add(gsp);

    expect(mock.getAllGSPsDpi(dpiFile)).andReturn(gspsFromMockCall);
    builder.setGridSupplyPointDao(mock);
    replay(mock);
    

    GspDefinitionDao mockGspDefinitionDao = createMock(GspDefinitionDao.class);
    List<GSPDefinition> gspDefsFromMockCall = new ArrayList<GSPDefinition>();
    expect(mockGspDefinitionDao.getAll()).andReturn(gspDefsFromMockCall);
    builder.setGspDefinitionDao(mockGspDefinitionDao);
    replay(mockGspDefinitionDao);

    SerialConfigDao mockSerialConfigDao = createMock(SerialConfigDao.class);
    expect(mockSerialConfigDao.getAllHHMopSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllHHMopSerials(false)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHMopSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHMopSerials(false)).andReturn(null);
    expect(mockSerialConfigDao.getAllHHDCSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllHHDCSerials(false)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHDCSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHDCSerials(false)).andReturn(null);
    builder.setSerialConfigDao(mockSerialConfigDao);
    replay(mockSerialConfigDao);

    //test method
    List<String> rows = builder.buildAllRows(dpiFile);

    assertNotNull(rows);
    assertEquals(10, rows.size());
    assertEquals("ZHD|P0135001|X|ATESTSUPPLIERID|Z|POOL|20050326111050", rows.get(0));
    assertEquals("ZPT|10|998877", rows.get(rows.size()-1));
    assertEquals("addRowsForGspAndMonth shud be called 8 times", 8, builder.numTimesCalled);
    assertEquals("date should be for month T", period.getNextReportingPeriod(), builder.periods.get(0));
    assertEquals(period.getNextReportingPeriod().getStartOfFirstMonthInPeriod(), builder.periods.get(4).getStartOfFirstMonthInPeriod());

    verify(mock,mockSerialConfigDao, mockGspDefinitionDao);
  }
  

  @Test
  public void testBuildRowsWithOneDcRowNonHHMonthT() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    TempDpiRowBuilder builder = new TempDpiRowBuilder()
    {
      protected PoolChecksumCalculator getChecksumCalculator()
      {
        return new TestChecksum();
      }
    };
    builder.setSupplier(supplier);

    GridSupplyPointDao mock = createMock(GridSupplyPointDao.class);
    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight());
    DpiFile dpiFile = new DpiFile(period, supplier);
    
    List<GridSupplyPoint> gspsFromMockCall = new ArrayList<GridSupplyPoint>();
    GridSupplyPoint gsp = new GridSupplyPoint("testGspFromMock", dpiFile);
    gsp.setHalfHourMpansFirstMonth(true);
    gsp.setHalfHourMpans2ndMonth(true);
    gsp.setNonHalfHourMpansFirstMonth(true);
    gsp.setNonHalfHourMpans2ndMonth(true);
    DataCollector dc1 = new DataCollector("dc1", false, dpiFile, true);
    gsp.setAgent(dc1);
    gspsFromMockCall.add(gsp);

    expect(mock.getAllGSPsDpi(dpiFile)).andReturn(gspsFromMockCall);
    builder.setGridSupplyPointDao(mock);
    replay(mock);
    


    GspDefinitionDao mockGspDefinitionDao = createMock(GspDefinitionDao.class);
    List<GSPDefinition> gspDefsFromMockCall = new ArrayList<GSPDefinition>();
    expect(mockGspDefinitionDao.getAll()).andReturn(gspDefsFromMockCall);
    builder.setGspDefinitionDao(mockGspDefinitionDao);
    replay(mockGspDefinitionDao);

    SerialConfigDao mockSerialConfigDao = createMock(SerialConfigDao.class);
    expect(mockSerialConfigDao.getAllHHMopSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllHHMopSerials(false)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHMopSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHMopSerials(false)).andReturn(null);
    expect(mockSerialConfigDao.getAllHHDCSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllHHDCSerials(false)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHDCSerials(true)).andReturn(null);
    expect(mockSerialConfigDao.getAllNonHHDCSerials(false)).andReturn(null);
    builder.setSerialConfigDao(mockSerialConfigDao);
    replay(mockSerialConfigDao);

    //test method
    List<String> rows = builder.buildAllRows(dpiFile);

    assertNotNull(rows);
    assertEquals(10, rows.size());
    assertEquals("ZHD|P0135001|X|ATESTSUPPLIERID|Z|POOL|20050326111050", rows.get(0));
    assertEquals("ZPT|10|998877", rows.get(rows.size()-1));
    assertEquals("addRowsForGspAndMonth shud be called 8 times", 8, builder.numTimesCalled);
    assertEquals(period.getNextReportingPeriod(), builder.periods.get(0));
    assertEquals(period.getNextReportingPeriod().getStartOfFirstMonthInPeriod(), builder.periods.get(4).getStartOfFirstMonthInPeriod());

    verify(mock,mockSerialConfigDao, mockGspDefinitionDao);
  }
  
  
  /**
   * MOP, isHH, monthT,
   * noth serials are for as above
   * @throws Exception
   */
  @Test
  public void testAddRowsForGspAndMonthForOneMOPAgentMonthT_isHH() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    DpiRowBuilder builder = new DpiRowBuilder();
    builder.setSupplier(supplier);

    boolean isHH = true;
    boolean isMOP = true;
    boolean isMonthT = true;
    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight());
    //Two serials to create
    List<SerialConfiguration> serials = new ArrayList<SerialConfiguration>();
    SerialConfiguration serial1 = new SerialConfiguration();
    serial1.setName("s1");
    serial1.setEnabled(true);
    serial1.setHalfHourly(isHH);
    serial1.setMop(isMOP);
    serial1.setMonthT(true);
    serials.add(serial1);
    
    SerialConfiguration serial2 = new SerialConfiguration();
    serial2.setName("s2");
    serial2.setEnabled(true);
    serial2.setHalfHourly(isHH);
    serial2.setMop(isMOP);
    serial2.setMonthT(true);
    serials.add(serial2);

    List<String> rows = new ArrayList<String>();
    rows.add("onePreExistingRow");
    rows.add("twoPreExistingRow");

    GridSupplyPoint gsp = new GridSupplyPoint("aGSPName", null);
    gsp.setHalfHourMpans2ndMonth(isMonthT);

    List<GenericAgent> agents = new ArrayList<GenericAgent>();
    MOP m1 = new MOP("m1", isHH, null, isMonthT);
    m1.getGridSupplyPoints().add(gsp);
    m1.setHalfHourMpans2ndMonth(gsp.isHalfHourMpans2ndMonth());
    
    agents.add(m1);
    
    gsp.setAgent(m1);

    //test method
    builder.addRowsForGspAndMonth(rows, gsp, isHH, isMOP, null, period, serials, isMonthT);

    assertEquals("started with 2 rows & added 4 so should be 6", 4, rows.size());
    assertEquals("DPI|aGSPName|s1|m1|M|20050331", rows.get(2));
    assertEquals("DPI|aGSPName|s2|m1|M|20050331", rows.get(3));
  }
  

  
  /**
   * DC, isHH, monthT,
   * noth serials are for as above
   * @throws Exception
   */  
  @Test
  public void testAddRowsForGspAndMonthForOneDCAgentMonthT_isHH() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    DpiRowBuilder builder = new DpiRowBuilder();
    builder.setSupplier(supplier);

    boolean isHH = true;
    boolean isMOP = false;
    boolean isMonthT = true;
    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight());
    //Two serials to create
    List<SerialConfiguration> serials = new ArrayList<SerialConfiguration>();
    SerialConfiguration serial1 = new SerialConfiguration();
    serial1.setName("s1");
    serial1.setEnabled(true);
    serial1.setHalfHourly(isHH);
    serial1.setMop(isMOP);
    serial1.setMonthT(true);
    serials.add(serial1);
    
    SerialConfiguration serial2 = new SerialConfiguration();
    serial2.setName("s2");
    serial2.setEnabled(true);
    serial2.setHalfHourly(isHH);
    serial2.setMop(isMOP);
    serial2.setMonthT(true);
    serials.add(serial2);

    List<String> rows = new ArrayList<String>();
    rows.add("onePreExistingRow");
    rows.add("twoPreExistingRow");

    GridSupplyPoint gsp = new GridSupplyPoint("aGSPName", null);
    gsp.setHalfHourMpans2ndMonth(isMonthT);

    List<GenericAgent> agents = new ArrayList<GenericAgent>();
    DataCollector agent = new DataCollector("m1", isHH, null, isMonthT);
    agent.getGridSupplyPoints().add(gsp);
    agent.setHalfHourMpans2ndMonth(gsp.isHalfHourMpans2ndMonth());
    
    agents.add(agent);
    
    gsp.setAgent(agent);

    //test method
    builder.addRowsForGspAndMonth(rows, gsp, isHH, isMOP, null, period, serials, isMonthT);

    assertEquals("started with 2 rows & added 4 so should be 6", 4, rows.size());
    assertEquals("DPI|aGSPName|s1|m1|C|20050331", rows.get(2));
    assertEquals("DPI|aGSPName|s2|m1|C|20050331", rows.get(3));
  }
  

  @Test
  public void test_addHalfHourlyMopDpiRecords_ForOneMOPAgent_HH_monthT() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    DpiRowBuilder builder = new DpiRowBuilder();
    builder.setSupplier(supplier);
    SerialConfigDao serialConfig = new SerialConfigDaoHibernate()
    {
      public List<SerialConfiguration> getAllHHMopSerials(boolean isForMonthT)
      {
        //Two serials to create
        boolean isHH = true;
        boolean isMOP = false;
        List<SerialConfiguration> serials = new ArrayList<SerialConfiguration>();
        SerialConfiguration serial1 = new SerialConfiguration();
        serial1.setName("s1");
        serial1.setEnabled(true);
        serial1.setHalfHourly(isHH);
        serial1.setMop(isMOP);
        serial1.setMonthT(true);
        
        SerialConfiguration serial2 = new SerialConfiguration();
        serial2.setName("s2");
        serial2.setEnabled(true);
        serial2.setHalfHourly(isHH);
        serial2.setMop(isMOP);
        serial2.setMonthT(true);
        
        serials.add(serial1);
        serials.add(serial2);
        return serials;
      }
    };
    builder.setSerialConfigDao(serialConfig);

    boolean isHH = true;
    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight());

    List<String> rows = new ArrayList<String>();
    rows.add("onePreExistingRow");
    rows.add("twoPreExistingRow");

    GridSupplyPoint gsp = new GridSupplyPoint("aGSPName", null);

    MOP m1 = new MOP("m1", isHH, null, true);
    m1.getGridSupplyPoints().add(gsp);
    
    gsp.setAgent(m1);

    boolean isMonthT = true;
    
    //test method
    builder.addHalfHourlyMopDpiRecords(rows, gsp, period, null, isMonthT);

    assertEquals("started with 2 rows & added 4 so should be 6", 4, rows.size());
    assertEquals("DPI|aGSPName|s1|m1|M|20050331", rows.get(2));
    assertEquals("DPI|aGSPName|s2|m1|M|20050331", rows.get(3));
  }
  


  @Test
  public void test_addHalfHourlyMopDpiRecords_ForOneDCAgent_no_rows_should_be_added_monthT() throws Exception
  {
    Supplier supplier = new Supplier("aTestSupplierId");
    DpiRowBuilder builder = new DpiRowBuilder();
    builder.setSupplier(supplier);
    SerialConfigDao serialConfig = new SerialConfigDaoHibernate()
    {
      public List<SerialConfiguration> getAllHHMopSerials(boolean isForMonthT)
      {
        //Two serials to create
        boolean isHH = true;
        boolean isMOP = false;
        List<SerialConfiguration> serials = new ArrayList<SerialConfiguration>();
        SerialConfiguration serial1 = new SerialConfiguration();
        serial1.setName("s1");
        serial1.setEnabled(true);
        serial1.setHalfHourly(isHH);
        serial1.setMop(isMOP);
        serial1.setMonthT(true);
        
        SerialConfiguration serial2 = new SerialConfiguration();
        serial2.setName("s2");
        serial2.setEnabled(true);
        serial2.setHalfHourly(isHH);
        serial2.setMop(isMOP);
        serial2.setMonthT(true);
        
        serials.add(serial1);
        serials.add(serial2);
        return serials;
      }
    };
    builder.setSerialConfigDao(serialConfig);

    boolean isHH = true;
    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight());

    List<String> rows = new ArrayList<String>();
    rows.add("onePreExistingRow");
    rows.add("twoPreExistingRow");

    GridSupplyPoint gsp = new GridSupplyPoint("aGSPName", null);

    DataCollector m1 = new DataCollector("m1", isHH, null, true);
    m1.getGridSupplyPoints().add(gsp);
    m1.setHalfHourMpans2ndMonth(false);
    
    gsp.setAgent(m1);

    boolean isMonthT = true;
    
    //test method
    builder.addHalfHourlyMopDpiRecords(rows, gsp, period, null, isMonthT);

    assertEquals("started with 2 rows, should still be 2 as agent is a DC", 2, rows.size());
  }


  class TestChecksum extends PoolChecksumCalculator implements ChecksumCalculator
  {
    @Override
    public long getCheckSum()
    {
      return 998877;
    }
  }

  class TempDpiRowBuilder extends DpiRowBuilder implements RowBuilder
  {
    public int numTimesCalled = 0;

    public List<ParmsReportingPeriod> periods = new ArrayList<ParmsReportingPeriod>();

    @Override
    public void addRowsForGspAndMonth(
        List<String> rows, GridSupplyPoint gsp,
        boolean isHH, boolean isMOP,
        DpiFile dpiFile, ParmsReportingPeriod parmsReportingPeriod, List<SerialConfiguration> aSerials, boolean isMonthT)
    {
      numTimesCalled++;
      periods.add(parmsReportingPeriod);
      rows.add("some text");
    }

    public TempDpiRowBuilder()
    {
      super();
    }

  }

}

package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.IterableMap;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.utils.Freeze;


/**
 *
 */
@TransactionConfiguration(defaultRollback = true, transactionManager = "test.parms.transactionManager")
@Transactional(rollbackFor = RuntimeException.class)
@SuppressWarnings("rawtypes")
public class P0028ActiveDaoIntegrationTest  extends BaseDaoIntegrationTest
{

  // Dao under Test
  @Autowired(required = true)
  @Qualifier("parms.p0028ActiveDao")
  private P0028ActiveDaoHibernate mP0028ActiveDao;

  /**
   * Get P0028Active mpans which qualify for sp04 reporting now.
   */
  @Test
  public void getP0028ActivesForSp04ReportNow()
  {
    Freeze.thaw();
    DateTime now = new DateTime(2011, 2, 15, 0, 0, 0, 0);
    Freeze.freeze(now);
    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DpiFile dpiFile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpiFile);

    DataCollector dc = new DataCollector("dc", true, dpiFile, true);
    mAgentDao.makePersistent(dc);

    P0028File p0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);

    MPANCore mpan = new MPANCore("1000000000003");
    DateTime meterReadingdate = now.minusDays(1);
    String meterRegisterId = "12345";

    P0028Data p0028Data = new P0028Data(new Long(121), meterRegisterId, mpan, meterReadingdate, p0028File);
    mP0028DataDao.makePersistent(p0028Data);

    IterableMap<String, P0028Active> p28Actives =  mP0028ActiveDao.get(supplier, null);

    assertNotNull(p28Actives);

    Freeze.thaw();
  }

  /**
   * Get all P0028Active records between P28 Upload and MID.
   */
  @Test
  public void getForSupplierBetweenP28UploadAndMID()
  {
    Freeze.thaw();
    Freeze.freeze(15, 8, 2011);
    DateTime now = new DateTime(2011, 9, 20, 12, 0, 0, 0);
    DateTime receiptTime = new DateTime(2011, 5, 15, 12, 0, 0, 0);
    ParmsReportingPeriod period = new ParmsReportingPeriod(1, 2011);

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    // create p0028active within p28 upload and MID = between 15/5/11 to 15/8/11
    DpiFile dpiFile = new DpiFile(period, supplier);
    dpiFile.setFileName("dpi_file_01");
    mDpiFileDao.makePersistent(dpiFile);
    DataCollector dc = new DataCollector("bert", true, dpiFile, true);
    mAgentDao.makePersistent(dc);
    P0028File p0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptTime, period);
    mP0028FileDao.makePersistent(p0028File);
    MPANCore mpan = new MPANCore("1000000000003");
    DateTime meterReadingdate = now.minusDays(1);
    String meterRegisterId = "12345";
    P0028Data p0028Data = new P0028Data(new Long(121), meterRegisterId, mpan, meterReadingdate, p0028File);
    mP0028DataDao.makePersistent(p0028Data);
    P0028Active p0028Active = new P0028Active(supplier, dc, dc.getName(), p0028Data, 101L,
        new DateTime().minusHours(22), "meterReg", mpan, now.minusDays(2));
    mP0028ActiveDao.makePersistent(p0028Active);

    // create p0028active outside p28 upload and MID = between 15/5/11 to 15/8/11
    DpiFile dpiFile2 = new DpiFile(period, supplier);
    dpiFile2.setFileName("dpi_file_02");
    mDpiFileDao.makePersistent(dpiFile2);
    DataCollector dc2 = new DataCollector("fred", true, dpiFile2, true);
    mAgentDao.makePersistent(dc2);
    DateTime receiptTime2 = new DateTime(2011, 5, 15, 12, 0, 0, 0);
    P0028File p0028File2 = new P0028File("b_filename", dc2.getName(), supplier, "fred", receiptTime2.minusMonths(6),
                                         period);
    mP0028FileDao.makePersistent(p0028File2);
    MPANCore mpan2 = new MPANCore("1000000000004");
    DateTime meterReadingdate2 = now.minusDays(1);
    String meterRegisterId2 = "55555";
    P0028Data p0028Data2 = new P0028Data(new Long(124), meterRegisterId2, mpan2, meterReadingdate2, p0028File2);
    mP0028DataDao.makePersistent(p0028Data2);
    P0028Active p0028Active2 = new P0028Active(supplier, dc2, dc2.getName(), p0028Data2, 101L,
        new DateTime().minusHours(19), "meterReg2", mpan2, now.minusDays(1));
    mP0028ActiveDao.makePersistent(p0028Active2);

    assertNotNull(p0028Active2.getPk());
    Long p0028Active2Pk = p0028Active2.getPk();
    assertTrue(p0028Active2Pk > 0);

    IterableMap<String, P0028Active> records =  mP0028ActiveDao.getForSupplierWithinP28UploadAndMID(supplier);

    assertNotNull(records);
    assertEquals("expected one P0028Active record", 1,  records.size());
  }

  /**
   * Get empty list for zero p0028Actives in db.
   *
   * @throws Exception
   */
  @Test
  public void getAll_noneToGet() throws Exception
  {
    List<P0028Active> all = mP0028ActiveDao.getAll();

    //test method
    assertTrue(all.size() == 0);
  }

  /**
   * Create and save a p0028Active.
   *
   * @throws Exception
   */
  @Test
  public void createAndSave() throws Exception
  {
    DateTime now = new DateTime();
    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DpiFile dpiFile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpiFile);

    DataCollector dc = new DataCollector("dc", true, dpiFile, true);
    mAgentDao.makePersistent(dc);

    P0028File p0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);

    MPANCore mpan = new MPANCore("1000000000003");
    DateTime meterReadingdate = now.minusDays(1);
    String meterRegisterId = "12345";

    P0028Data p0028Data = new P0028Data(new Long(121), meterRegisterId, mpan, meterReadingdate, p0028File);
    mP0028DataDao.makePersistent(p0028Data);




    //create P0028Active
    P0028Active p0028Active = new P0028Active(supplier, dc, dc.getName(), p0028Data, 101L,
        new DateTime().minusHours(22), "meterReg", mpan, now.minusMonths(1));

    //test method
    mP0028ActiveDao.makePersistent(p0028Active);

    assertNotNull(p0028Active.getPk());
    assertTrue(p0028Active.getPk() > 0);

    List<P0028Active> all = mP0028ActiveDao.getAll();

    assertTrue(all.size() == 1);

    P0028Active p0028ActiveCreated = all.get(0);

    assertNotNull(p0028ActiveCreated);
    assertEquals(p0028Active.getPk(), p0028ActiveCreated.getPk());
    assertEquals(supplier, p0028ActiveCreated.getSupplier());
    assertEquals(dc, p0028ActiveCreated.getDataCollector());
    assertEquals(p0028Data.getPk(), p0028ActiveCreated.getLatestP0028Data().getPk());
    assertEquals(new Long(101), p0028ActiveCreated.getMaxDemand());
    assertEquals(new DateTime().minusHours(22), p0028ActiveCreated.getP0028ReceivedDate());
    assertEquals("meterReg", p0028ActiveCreated.getMeterSerialId());
    assertEquals(mpan, p0028ActiveCreated.getMpanCore());
    assertEquals(now.minusMonths(1), p0028ActiveCreated.getMeterReadingDate());
  }

  /**
   *
   * @throws Exception
   */
  @Test
  public void update_noneForSupplierSoFar() throws Exception
  {
    DateTime now = new DateTime();

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = now.minusMonths(2);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    DpiFile dpiFile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpiFile);

    DataCollector dc = new DataCollector("dc", true, dpiFile, true);
    mAgentDao.makePersistent(dc);

    P0028File p0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);

    DateTime meterReadingdate = now.minusDays(1);
    MPANCore mpan = new MPANCore("1000000000004");
    String meterRegisterId = "12345";

    P0028Data p0028Data = new P0028Data(new Long(121), meterRegisterId, mpan, meterReadingdate, p0028File);
    p0028Data.setDcAgentName("dc");
    mP0028DataDao.makePersistent(p0028Data);

    //test method
    mP0028ActiveDao.storeNewP0028Active(p0028Data, dc, dc.getName());

    //lets see if it is there
    List<P0028Active> all = mP0028ActiveDao.getAll();

    assertTrue(all.size() == 1);

    P0028Active p0028ActiveCreated = all.get(0);

    assertNotNull(p0028ActiveCreated);
    assertEquals(supplier, p0028ActiveCreated.getSupplier());
    assertEquals(dc, p0028ActiveCreated.getDataCollector());
    assertEquals(p0028Data.getPk(), p0028ActiveCreated.getLatestP0028Data().getPk());
    assertEquals(new Long(121), p0028ActiveCreated.getMaxDemand());
    assertEquals(now.minusMonths(2), p0028ActiveCreated.getP0028ReceivedDate());
    assertEquals("12345", p0028ActiveCreated.getMeterSerialId());
    assertEquals(mpan, p0028ActiveCreated.getMpanCore());
    assertEquals(meterReadingdate, p0028ActiveCreated.getMeterReadingDate());
  }

  /**
   * @throws Exception
   */
  @Test
  public void update_oneExistsThatForEarlier_soShouldNotBeReplaced() throws Exception
  {
    DateTime now = new DateTime();
    DateTime receiptDate = now.minusMonths(2);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DpiFile dpiFile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpiFile);

    DataCollector dc = new DataCollector("dc", true, dpiFile, true);
    mAgentDao.makePersistent(dc);

    P0028File p0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);

    DateTime initialMeterReadingdate = now.minusDays(1);
    MPANCore mpan = new MPANCore("1000000000004");
    String meterRegisterId = "12345";

    P0028Data secondP0028DataWithLaterMeterReadDate = new P0028Data(new Long(121), meterRegisterId, mpan,
                                                                    initialMeterReadingdate, p0028File);
    mP0028DataDao.makePersistent(secondP0028DataWithLaterMeterReadDate);

    //create earlier data
    P0028File earlier0028File = new P0028File("a_filename", dc.getName(), supplier, "bert",
                                              receiptDate.minusYears(1), period);
    mP0028FileDao.makePersistent(earlier0028File);

    DateTime earlierMeterReadingdate = initialMeterReadingdate.minusYears(1);
    P0028Data firstP0028DataWithEarlerMeterReadingDate = new P0028Data(new Long(100), meterRegisterId, mpan,
                                                                       earlierMeterReadingdate, earlier0028File);
    secondP0028DataWithLaterMeterReadDate.setDcAgentName("dc");

    mP0028DataDao.makePersistent(firstP0028DataWithEarlerMeterReadingDate);
    mP0028ActiveDao.storeNewP0028Active(firstP0028DataWithEarlerMeterReadingDate, dc, dc.getName());

    //test method
    mP0028ActiveDao.storeNewP0028Active(secondP0028DataWithLaterMeterReadDate, dc, dc.getName());

    //lets see if it is there
    List<P0028Active> all = mP0028ActiveDao.getAll();

    assertEquals("should still be 1 as old is replaced by new", 1, all.size());

    P0028Active p0028ActiveCreated = all.get(0);

    assertNotNull(p0028ActiveCreated);
    assertEquals(supplier, p0028ActiveCreated.getSupplier());
    assertEquals(dc, p0028ActiveCreated.getDataCollector());
    assertEquals(firstP0028DataWithEarlerMeterReadingDate.getPk(), p0028ActiveCreated.getLatestP0028Data().getPk());
    assertEquals(new Long(100), p0028ActiveCreated.getMaxDemand());
    assertEquals(receiptDate.minusYears(1), p0028ActiveCreated.getP0028ReceivedDate());
    assertEquals("12345", p0028ActiveCreated.getMeterSerialId());
    assertEquals(mpan, p0028ActiveCreated.getMpanCore());
    assertEquals(earlierMeterReadingdate, p0028ActiveCreated.getMeterReadingDate());
  }


  /**
   * @throws Exception
   */
  @Test
  public void update_oneExistsThatIsForLaterMeterReading_soShouldBeReplaced() throws Exception
  {
    DateTime now = new DateTime();
    DateTime receiptDate = now.minusMonths(2);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DpiFile dpiFile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpiFile);

    DataCollector dc = new DataCollector("dc", true, dpiFile, true);
    mAgentDao.makePersistent(dc);

    P0028File p0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);

    DateTime initialMeterReadingdate = now.minusDays(1);
    MPANCore mpan = new MPANCore("1000000000004");
    String meterRegisterId = "12345";

    P0028Data firstP0028DataWithOlderMeterReadDate = new P0028Data(new Long(121), meterRegisterId, mpan,
                                                                   initialMeterReadingdate, p0028File);
    mP0028DataDao.makePersistent(firstP0028DataWithOlderMeterReadDate);

    //create earlier data
    P0028File earlier0028File = new P0028File("a_filename", dc.getName(), supplier, "bert", receiptDate.minusYears(1),
                                              period);
    mP0028FileDao.makePersistent(earlier0028File);

    DateTime earlierMeterReadingdate = initialMeterReadingdate.minusYears(1);
    P0028Data secondP0028DataWithOlderMeterReadDate = new P0028Data(new Long(100), meterRegisterId, mpan,
                                                                    earlierMeterReadingdate, earlier0028File);
    firstP0028DataWithOlderMeterReadDate.setDcAgentName("dc");

    mP0028DataDao.makePersistent(secondP0028DataWithOlderMeterReadDate);
    mP0028ActiveDao.storeNewP0028Active(firstP0028DataWithOlderMeterReadDate, dc, dc.getName());

    //test method
    mP0028ActiveDao.storeNewP0028Active(secondP0028DataWithOlderMeterReadDate, dc, dc.getName());

    //lets see if it is there
    List<P0028Active> all = mP0028ActiveDao.getAll();

    assertEquals("should still be 1 as old is replaced by new", 1, all.size());

    P0028Active p0028ActiveCreated = all.get(0);

    assertNotNull(p0028ActiveCreated);
    assertEquals(supplier, p0028ActiveCreated.getSupplier());
    assertEquals(dc, p0028ActiveCreated.getDataCollector());
    assertEquals(secondP0028DataWithOlderMeterReadDate.getPk(), p0028ActiveCreated.getLatestP0028Data().getPk());
    assertEquals(new Long(100), p0028ActiveCreated.getMaxDemand());
    assertEquals(receiptDate.minusYears(1), p0028ActiveCreated.getP0028ReceivedDate());
    assertEquals("12345", p0028ActiveCreated.getMeterSerialId());
    assertEquals(mpan, p0028ActiveCreated.getMpanCore());
    assertEquals(earlierMeterReadingdate, p0028ActiveCreated.getMeterReadingDate());
  }

  /**
   * @throws Exception
   */
  @Test
  public void EnsureMoreThanOneRowCanNotBeAddedForSameSupplierMpan() throws Exception
  {
    DateTime now = new DateTime();
    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DpiFile dpiFile1 = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpiFile1);

    DataCollector dc1 = new DataCollector("dc", true, dpiFile1, true);
    mAgentDao.makePersistent(dc1);


    P0028File p0028File = new P0028File("a_filename", dc1.getName(), supplier, "bert", receiptDate, period);
    mP0028FileDao.makePersistent(p0028File);

    MPANCore mpan = new MPANCore("1000000000003");
    DateTime meterReadingdate = now.minusDays(1);
    String meterRegisterId = "12345";

    P0028Data p0028Data = new P0028Data(new Long(121), meterRegisterId, mpan, meterReadingdate, p0028File);
    mP0028DataDao.makePersistent(p0028Data);




    //create P0028Active
    P0028Active p0028Active = new P0028Active(supplier, dc1, dc1.getName(), p0028Data, 101L,
        new DateTime().minusHours(22), "meterReg", mpan, now.minusMonths(1));

    mP0028ActiveDao.makePersistent(p0028Active);

    //create second row with same mpan & supplier
    P0028Active secondP0028Active = new P0028Active(supplier, dc1, dc1.getName(), p0028Data, 101L,
        new DateTime().minusHours(22), "meterReg", mpan, now.minusMonths(1));

    boolean exceptionThrown = false;
    try
    {
      //test method - create second row with same mpan & supplier
      mP0028ActiveDao.makePersistent(secondP0028Active);
      fail("Exception should have been thrown for second insert");
    }
    catch (Exception e)
    {
      exceptionThrown = true;
    }
    finally
    {
      assertTrue("ooops, Exception should have been thrown for second insert", exceptionThrown);
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void getForSupplierAndMpan_NoRowsReturnsNull() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    MPANCore mpan = new MPANCore("1000000000003");

    //test method
    assertNull("none there so should get NULL back", mP0028ActiveDao.getForSupplierAndMpan(supplier, mpan));
  }

  /**
   * @throws Exception
   */
  @Test
  public void getForSupplierAndMpan_TworowsButNoneCorrect() throws Exception
  {
    Supplier supplier1 = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier1);

    Supplier supplier2 = new Supplier("BERT");
    mSupplierDao.makePersistent(supplier2);


    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile1 = createDpiFile(supplier1, period);
    DpiFile dpiFile2 = createDpiFile(supplier2, period);

    createActiveOne(supplier1, "1000000000001", 121L, period, dpiFile1);
    createActiveOne(supplier2, "1000000000002", 133L, period, dpiFile2);

    MPANCore mpan = new MPANCore("1000000000099");

    //test method
    assertNull("none there so should get NULL back", mP0028ActiveDao.getForSupplierAndMpan(supplier1, mpan));

  }

  /**
   * @throws Exception
   */
  @Test
  public void getForSupplierAndMpan_TwoRowsOneCorrect() throws Exception
  {
    Supplier supplier1 = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier1);

    Supplier supplier2 = new Supplier("BERT");
    mSupplierDao.makePersistent(supplier2);

    MPANCore mpan = new MPANCore("1000000000001");


    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    DpiFile dpiFile1 = createDpiFile(supplier1, period);
    DpiFile dpiFile2 = createDpiFile(supplier2, period);

    createActiveOne(supplier1, mpan.getValue(), 121L, period, dpiFile1);
    createActiveOne(supplier2, "1000000000002", 123L, period, dpiFile2);

    //test method
    assertNotNull("none there so should get NULL back", mP0028ActiveDao.getForSupplierAndMpan(supplier1, mpan));

    P0028Active found = mP0028ActiveDao.getForSupplierAndMpan(supplier1, mpan);
    assertEquals(new Long(121), found.getMaxDemand());
  }

  /**
   * @throws Exception
   */
  @Test
  public void getAllForSupplier_noneToGet() throws Exception
  {
    Supplier supplier1 = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier1);

    //test method
    Map<String, P0028Active> results = mP0028ActiveDao.getAllForSupplier(supplier1);
    assertNotNull(results);
    assertTrue(results.size() == 0);
  }

  /**
   * @throws Exception
   */
  @Test
  public void getAllForSupplier_two_oneForCorrectSupplier() throws Exception
  {
    DateTime now = new DateTime();

    Supplier supplier1 = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier1);

    Supplier supplier2 = new Supplier("BERT");
    mSupplierDao.makePersistent(supplier2);

    MPANCore mpan = new MPANCore("1000000000001");


    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    DpiFile dpiFile1 = createDpiFile(supplier1, period);
    DpiFile dpiFile2 = createDpiFile(supplier2, period);

    createActiveOne(supplier1, mpan.getValue(), 121L, period, dpiFile1, now.minusMonths(2));
    createActiveOne(supplier2, "1000000000002", 123L, period, dpiFile2, now.minusMonths(2));

    //test method
    Map<String, P0028Active> results = mP0028ActiveDao.getAllForSupplier(supplier1);
    assertNotNull(results);
    assertTrue(results.size() == 1);
  }


  /**
   * @throws Exception
   */
  @Test
  public void getAllForSupplier_three_threeForCorrectSupplier_withOneAlotOlder() throws Exception
  {
    DateTime now = new DateTime();

    Supplier supplier1 = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier1);

    ParmsReportingPeriod period = new ParmsReportingPeriod(new DateMidnight().minusMonths(1));
    ParmsReportingPeriod olderiod = new ParmsReportingPeriod(new DateMidnight().minusYears(1));
    DpiFile dpiFile = createDpiFile(supplier1, period);

    MPANCore mpan = new MPANCore("1000000000001");

    createActiveOne(supplier1, mpan.getValue(), 121L, period, dpiFile, now.minusMonths(2));
    createActiveOne(supplier1, "1000000000002", 123L, period, dpiFile, now.minusMonths(2));
    createActiveOne(supplier1, "1000000000003", 123L, olderiod, dpiFile, now.minusYears(1));

    //test method
    Map<String, P0028Active> results = mP0028ActiveDao.getAllForSupplier(supplier1);
    assertNotNull(results);
    assertEquals("should still be all 3 as the age restriction on P0028 uploads is removed", 3, results.size());
  }

  /**
   * Freeze time for new instances of DateTime
   */
  @Before
  public void before()
  {
    Freeze.freeze(30, 11, 2010);
  }

  /**
   * Thaw time for new instances of DateTime
   */
  @After
  public void after()
  {
    Freeze.thaw();
  }

  //utility test methods
  private void createActiveOne(Supplier supplier, String mpanValue, Long maxDemand, ParmsReportingPeriod period,
                               DpiFile dpiFile)
  {
    DateTime now = new DateTime();
    DateTime meterReadingdate = now.minusDays(1);
    createActiveOne(supplier, mpanValue, maxDemand, period, dpiFile, meterReadingdate);
  }

  //utility test methods
  private void createActiveOne(Supplier supplier, String mpanValue, Long maxDemand, ParmsReportingPeriod period,
                               DpiFile dpiFile,
      DateTime meterReadingdate)
  {
    DateTime receiptDate = new DateTime();
    String meterRegisterId = "12345";

    DataCollector dc = new DataCollector("dc_" + supplier.getSupplierId() , true, dpiFile, true);
    mAgentDao.makePersistent(dc);


    P0028File p0028File1 = new P0028File(supplier.getSupplierId() + "_filename", dc.getName(),
        supplier, "agent_" + supplier.getSupplierId(), receiptDate, period);
    mP0028FileDao.makePersistent(p0028File1);

    MPANCore mpan = new MPANCore(mpanValue);

    P0028Data p0028Data = new P0028Data(maxDemand, meterRegisterId, mpan, meterReadingdate, p0028File1);
    mP0028DataDao.makePersistent(p0028Data);


    //create P0028Active1
    P0028Active p0028Active = new P0028Active(supplier, dc, dc.getName(), p0028Data, maxDemand,
        meterReadingdate, "meterReg1", mpan, meterReadingdate.minusMonths(1));
    mP0028ActiveDao.makePersistent(p0028Active);
  }

  private DpiFile createDpiFile(Supplier supplier, ParmsReportingPeriod period)
  {
    DpiFile dpiFile = new DpiFile(period, supplier);
    dpiFile.setFileName(supplier.getSupplierId() + "_filename");
    mDpiFileDao.makePersistent(dpiFile);
    return dpiFile;
  }

  //utility Daos

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDaoHibernate mP0028FileDao;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  @Autowired(required = true)
  @Qualifier("parms.agentDao")
  private AgentDaoHibernate mAgentDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028DataDao")
  private P0028DataDaoHibernate mP0028DataDao;
}

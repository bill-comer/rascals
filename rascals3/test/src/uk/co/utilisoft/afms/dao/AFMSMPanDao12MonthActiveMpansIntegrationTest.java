package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class AFMSMPanDao12MonthActiveMpansIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDaoHibernate mAFMSMPanDao;

  private String f001 = "f001";   // no regi status
  private String f002 = "f002";   // regi status 2


  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList()
  {
    Supplier supplier = new Supplier(f001);
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, new DateTime(),
                                                                      new DateTime(2011, 1, 1, 0, 0, 0, 0), 3);
    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }



  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_AllMpansEffectiveToDateTooOld()
  {
    // create test data
    DateTime now = new DateTime();
    DateTime TwoYearsAgo = new DateTime().minusYears(2);
    insertMpanWithEffectiveFromDate(11, now.minusMonths(2), TwoYearsAgo, "77777777777");

    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, validEffectiveFromDate,
                                                                      validEffectiveToDate, 3);
    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }

  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_AllMpansEffectiveFromDateTooNew()
  {
    // create test data
    DateTime now = new DateTime();
    insertMpanWithEffectiveFromDate(11, now, now, "77777777777");

    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, validEffectiveFromDate,
                                                                      validEffectiveToDate, 3);

    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }

  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_AllMpansOK_randomDate()
  {
    // create test data
    DateTime now = new DateTime();
    insertMpanWithEffectiveFromDate(11, now.minusMonths(13), now.minusMonths(12), "77777777777");

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validStartOf12MonthMonitoringDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, null,
                                                                      validStartOf12MonthMonitoringDate, 3);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());
    assertEquals("should get what we put in", 11, mpans.size());
  }


  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_AllMpansOK_EffToDateInFirstOKMonth()
  {
    // create test data
    DateTime now = new DateTime();
    insertMpanWithEffectiveFromDate(11, now.minusMonths(13), now.minusMonths(12), "77777777777");

    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, validEffectiveFromDate,
                                                                      validEffectiveToDate, 3);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());
    assertEquals("should get what we put in", 11, mpans.size());
  }


  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_AllMpansOK_EffFromDateInLastOKMonth()
  {
    // create test data
    DateTime now = new DateTime();
    insertMpanWithEffectiveFromDate(11, now.minusMonths(13), now.minusMonths(12), "77777777777");

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, null, validEffectiveToDate, 3);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());
    assertEquals("should get what we put in", 11, mpans.size());
  }


  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_AllMpansOK_NoEffectiveTodate()
  {
    // create test data
    DateTime now = new DateTime();
    insertMpanWithEffectiveFromDate(11, now.minusMonths(13), null, "77777777777");

    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, validEffectiveFromDate,
                                                                      validEffectiveToDate, 3);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());
    assertEquals("should get what we put in", 11, mpans.size());
  }



  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPansEmptyList_11Good()
  {
    // create test data
    DateTime now = new DateTime();
    //effTDate too Old
    DateTime TwoYearsAgo = new DateTime().minusYears(2);
    insertMpanWithEffectiveFromDate(11, now.minusMonths(2), TwoYearsAgo, "77777777771");

    //effFromDate Too New
    insertMpanWithEffectiveFromDate(11, now, now, "77777777772");

    insertMpanWithEffectiveFromDate(11, now.minusMonths(13), now, "77777777777");

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f001);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, null, validEffectiveToDate, 3);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());
    assertEquals("should get what we put in", 11, mpans.size());
  }



  /**
   * No mpans in system returns an empty collection.
   */
  @Test
  public void getActiveMPans_emptyList_As11Good_butWrongSupplier()
  {
    // create test data
    DateTime now = new DateTime();
    //effTDate too Old
    DateTime TwoYearsAgo = new DateTime().minusYears(2);
    insertMpanWithEffectiveFromDate(11, now.minusMonths(2), TwoYearsAgo, "77777777771");

    //effFromDate Too New
    insertMpanWithEffectiveFromDate(11, now, now, "77777777772");

    insertMpanWithEffectiveFromDate(11, now.minusMonths(2), now, "77777777777");

    DateMidnight lastMonth = new DateMidnight().minusMonths(1);
    ParmsReportingPeriod lastMonthPrp = new ParmsReportingPeriod(lastMonth);
    DateTime validEffectiveFromDate = lastMonthPrp.getEndOfMonth(false).toDateTime();

    DateMidnight lastYear = new DateMidnight().minusMonths(12);
    ParmsReportingPeriod lastYearPrp = new ParmsReportingPeriod(lastYear);
    DateTime validEffectiveToDate = lastYearPrp.getStartOfMonth(false).toDateTime();

    Supplier supplier = new Supplier(f002);
    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpansForLast12Months(supplier, validEffectiveFromDate, validEffectiveToDate, 3);

    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }



  /**
   * Insert test data.
   */
  @BeforeTransaction
  public void init()
  {
    Freeze.freeze(10, 8, 2000);
  }

  /**
   * Cleanup test data.
   */
  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mAFMSMPanDao, AFMSMpan.class);
    Freeze.thaw();
  }

  private void insertMpanWithEffectiveFromDate(int aNumberMpans, DateTime aEfd, DateTime aEtd, String startNumber)
  {
    List<AFMSMpan> mpansToSave = new ArrayList<AFMSMpan>();
    DateTime efd = aEfd;
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      mpan.setEffectiveToDate(aEtd);
      mpan.setMpanCore(i + startNumber);
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(f001);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);
  }
}

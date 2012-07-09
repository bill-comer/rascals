package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class AFMSMPanDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  private DateMidnight mFirstDateMidnight = new DateMidnight(2021, 12, 1);

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDaoHibernate mAFMSMPanDao;

  private String f001 = "f001";   // no regi status
  private String f002 = "f002";   // regi status 2
  private String f003 = "f003";   // regi status 3
  private String f004 = "f004";   // regi status 4
  private String b001 = "b001";
  private String j001 = "j001";
  private String a001 = "a001";

  private int mNumberMpansPerMonth = 25;

  @Test
  @SuppressWarnings("unchecked")
  public void getWildCardMpans()
  {
    final String mpanWildCardQry = "1%9";

    List<AFMSMpan> afmsMpans = (List<AFMSMpan>) mAFMSMPanDao.getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSMpan.class)
          .add(Restrictions.like("mpanCore", mpanWildCardQry))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .list();
      }
    });

    assertEquals(22, afmsMpans.size());
  }

  /**
   * Calculate the mpan's minimum start date(J0049) when Sp04 reporting monitoring should occur.
   */
  @Test
  public void getMinimumStartOfMonitoringPeriod()
  {
    DateTime startOfMonitorPeriod = new DateTime(2010, 9, 28, 0, 0, 0, 0);
    DateTime minStartOfMonitorPeriod = mAFMSMPanDao.getMinimumStartOfMonitoringPeriod(startOfMonitorPeriod, 12, 3);
    assertEquals(startOfMonitorPeriod.plusMonths(9), minStartOfMonitorPeriod);
  }

  @Test
  public void testGetSupplierIDs() throws Exception
  {
    //Feb & March
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(mFirstDateMidnight);
    List<String> supplierIDs = mAFMSMPanDao.getSupplierIdsForTwoMonths(parmsReportingPeriod);
    assertNotNull(supplierIDs);

    assertEquals(5, supplierIDs.size());   // must not inlude f002 & f003 as these are invalid mpans

    boolean foundF001 = false;
    boolean foundF003 = false;
    boolean foundJ001 = false;
    boolean foundA001 = false;
    boolean foundB001 = false;


    for (String id : supplierIDs)
    {
      if (id.equals(f001)) {
        foundF001 = true;
      }else  if (id.equals(j001)) {
        foundJ001 = true;
      }else  if (id.equals(a001)) {
        foundA001 = true;
      }else  if (id.equals(b001)) {
        foundB001 = true;
      }else  if (id.equals(f003)) {
        foundF003 = true;
      }
    }

    assertTrue("not found f001", foundF001);
    assertTrue("not found f003", foundF003);
    assertTrue("not found j001", foundJ001);
    assertTrue("not found a001", foundA001);
    assertTrue("not found b001", foundB001);
  }


  /**
   * No mpans with a specific month returns an empty collection.
   */
  @Test
  public void getActiveMPansFail()
  {
    Supplier supplier = new Supplier(f001);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(new DateMidnight(1999, 3, 25));
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, true);
    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }


  /**
   * No mpans with a specific month returns an empty collection.
   */
  @Test
  public void getActiveMPansFail_monthT_minus_1()
  {
    Supplier supplier = new Supplier(f001);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(new DateMidnight(1999, 3, 25));
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, false);
    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }

  /**
   * Get active mpans with effective from date (J0049) in year and month specified.
   */
  @Test
  public void getActiveMPansSuccessMonthTMinus1_shouldFind25_f001()
  {
    boolean isMonthT = false;
    Supplier supplier = new Supplier(f001);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(mFirstDateMidnight);

    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, isMonthT);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());

    assertEquals( mNumberMpansPerMonth, mpans.size());
  }


  /**
   * Get active mpans with effective from date (J0049) in year and month specified.
   */
  @Test
  public void getActiveMPansSuccessMonthTMinus1_shouldFind25_f003()
  {
    boolean isMonthT = false;
    Supplier supplier = new Supplier(f003);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(mFirstDateMidnight);

    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, isMonthT);

    assertNotNull(mpans);
    assertFalse(mpans.isEmpty());

    assertEquals( mNumberMpansPerMonth, mpans.size());
  }


  /**
   * Get active mpans with effective from date (J0049) in year and month specified.
   */
  @Test
  public void getActiveMPansSuccessMonthTMinus1_shouldFind25_f002()
  {
    boolean isMonthT = false;
    Supplier supplier = new Supplier(f002);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(mFirstDateMidnight);

    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, isMonthT);

    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());

  }


  /**
   * Get active mpans with effective from date (J0049) in year and month specified.
   */
  @Test
  public void getActiveMPansSuccessMonthTMinus1_shouldFind25_f004()
  {
    boolean isMonthT = false;
    Supplier supplier = new Supplier(f004);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(mFirstDateMidnight);

    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, isMonthT);

    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());

  }


  /**
   * Get active mpans with effective from date (J0049) in year and month specified.
   * No mpans in Jan from fred
   */
  @Test
  public void getActiveMPansSuccessMonthT()
  {
    boolean isMonthT = true;
    Supplier supplier = new Supplier(f001);
    ParmsReportingPeriod parmsReportingPeriod = new ParmsReportingPeriod(new DateMidnight(2021, 10, 25));

    //test method
    List<AFMSMpan> mpans = mAFMSMPanDao.getActiveMpans(parmsReportingPeriod, supplier, isMonthT);
    assertNotNull(mpans);
    assertTrue(mpans.isEmpty());
  }


  @Test
  @SuppressWarnings("unchecked")
  public void getAfmsMpan() throws Exception
  {
    Supplier supplier = new Supplier(j001);
    MPANCore desiredMpan = new MPANCore("1977777777779");  //1977777777779 - //efd - 2022-01-20T00:00:00.000Z

    mFirstDateMidnight.toDateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(mFirstDateMidnight.plusMonths(2));

    List<AFMSMpan> all = mAFMSMPanDao.getHibernateTemplate().loadAll(AFMSMpan.class);
    for (AFMSMpan afmsMpan : all)
    {
      System.out.println("mpan:" + afmsMpan.getMpanCore());
    }

    //test method
    AFMSMpan mpan = (AFMSMpan) mAFMSMPanDao.getAfmsMpan(desiredMpan, supplier, period);

    assertNotNull(mpan);
    assertEquals(desiredMpan.getValue(), mpan.getMpanCore());
    System.out.println("mFM:" + mFirstDateMidnight.plusMonths(2));
    System.out.println("efd:" + mpan.getEffectiveFromDate());
    System.out.println("etd:" + mpan.getEffectiveToDate());
  }

  /**
   * Fails for query for mpan which returns null.
   */
  @Test
  public void getMPanFail()
  {
    Long pk = new Long(0L);
    AFMSMpan mpan = (AFMSMpan) mAFMSMPanDao.getHibernateTemplate().get(AFMSMpan.class, pk);
    assertNull(mpan);
  }

  /**
   * Find an mpan record.
   */
  @Test
  public void getMPanSuccess()
  {
    Long pk =  (Long) getPersistedObjectPks().get(AFMSMpan.class).get(0);
    AFMSMpan mpan = (AFMSMpan) mAFMSMPanDao.getHibernateTemplate().get(AFMSMpan.class, pk);
    assertNotNull(mpan);
    assertNotNull(mpan.getPk());
    assertNotNull(mpan.getMpanCore());
    mpan.getMpanCore().matches("[0-4]888888888888");
  }



  /**
   * Insert test data.
   */
  @BeforeTransaction
  public void init()
  {
    // create test data
    insertMpanWithEffectiveFromDate(mNumberMpansPerMonth, mFirstDateMidnight.toDateTime());
  }

  /**
   * Cleanup test data.
   */
  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mAFMSMPanDao, AFMSMpan.class);
  }

  private void insertMpanWithEffectiveFromDate(int aNumberMpans, DateTime aEfd)
  {
    List<AFMSMpan> mpansToSave = new ArrayList<AFMSMpan>();
    DateTime efd = aEfd;
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      //mpan.setEffectiveToDate(efd.plusYears(1));
      mpan.setMpanCore(i + "77777777777");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(f001);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);

    efd = aEfd;
    mpansToSave = new ArrayList<AFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      //mpan.setEffectiveToDate(efd.plusYears(1));
      mpan.setMpanCore(i + "77777777002");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(f002);
      mpan.setRegiStatus(2L);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);

    efd = aEfd;
    mpansToSave = new ArrayList<AFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      //mpan.setEffectiveToDate(efd.plusYears(1));
      mpan.setMpanCore(i + "77777777003");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(f003);
      mpan.setRegiStatus(3L);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);

    efd = aEfd;
    mpansToSave = new ArrayList<AFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      //mpan.setEffectiveToDate(efd.plusYears(1));
      mpan.setMpanCore(i + "77777777004");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(f004);
      mpan.setRegiStatus(4L);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);



    //berts data is all for prev month
    efd = mFirstDateMidnight.minusMonths(1).toDateTime();
    mpansToSave = new ArrayList<AFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      mpan.setEffectiveToDate(efd.plusYears(1));
      mpan.setMpanCore(i + "77777777778");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(b001);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);

    //alf data is all for prev 2 month
    efd = mFirstDateMidnight.minusMonths(2).toDateTime();
    mpansToSave = new ArrayList<AFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      mpan.setEffectiveToDate(efd.plusYears(1));
      mpan.setMpanCore(i + "77777777779");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(a001);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);


    //john data is all for next month
    efd = mFirstDateMidnight.plusMonths(1).toDateTime();
    mpansToSave = new ArrayList<AFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      AFMSMpan mpan = new AFMSMpan();
      mpan.setEffectiveFromDate(efd);
      mpan.setEffectiveToDate(efd.plusMonths(6));
      mpan.setMpanCore(i + "77777777779");
      mpan.setLastUpdated(new DateTime());
      mpan.setSupplierId(j001);
      mpansToSave.add(mpan);
      efd = efd.plusDays(1);
    }
    insertTestData(mpansToSave, mAFMSMPanDao, AFMSMpan.class);
  }
}

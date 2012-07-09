package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class Sp04FromAFMSMpanDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required = true)
  @Qualifier("parms.sp04FromAFMSMpanDao")
  private Sp04FromAFMSMpanDaoHibernate mSp04FromAFMSMpanDao;

  /**
   * Get Sp04FromAFMSMpan records which qualify for sp04 reporting now.
   */
  @Test
  public void getSp04FromAFMSMpansForSp04ReportNow()
  {
    DateTime jan232011 = new DateTime(2011, 1, 23, 0, 0, 0, 0);
    DateTime reportDateFeb232011 = new DateTime(2011, 2, 23, 0, 0, 0, 0);
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    MPANCore mpanToIgnore = new MPANCore("5555555555555");
    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(mpanToIgnore);
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(jan232011);
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);
    newAfmsMpan1.setCalculatedStandard1(1111L);
    newAfmsMpan1.setCalculatedStandard2(2222L);
    newAfmsMpan1.setCalculatedStandard3(333.333F);
    newAfmsMpan1.setEffectiveFromDate(jan232011.minusMonths(3));
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);

    DateTime mar152012 = new DateTime(2011, 3, 15, 0, 0, 0, 0);
    MPANCore mpanToIgnore2 = new MPANCore("6666666666666");
    Sp04FromAFMSMpan newAfmsMpan2 = new Sp04FromAFMSMpan();
    newAfmsMpan2.setMpan(mpanToIgnore2);
    newAfmsMpan2.setMpanFk(666666L);
    newAfmsMpan2.setSupplierFk(supplier.getPk());
    newAfmsMpan2.setMeterId("666666");
    newAfmsMpan2.setMeterRegisterFks(new Long(6L).toString());
    newAfmsMpan2.setDataCollector(dcName1);
    newAfmsMpan2.setDataCollectorFk(1L);
    newAfmsMpan2.setCalculatedMeterInstallationDeadline(mar152012);
    newAfmsMpan2.setD0268SettlementDate(new DateTime());
    newAfmsMpan2.setMeterRegisterReading1(6F);
    newAfmsMpan2.setMeterRegisterReading2(6F);
    newAfmsMpan2.setMeterRegisterReading3(6F);
    newAfmsMpan2.setCalculatedStandard1(66L);
    newAfmsMpan2.setCalculatedStandard2(666L);
    newAfmsMpan2.setCalculatedStandard3(666.333F);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan2);

    mSp04FromAFMSMpanDao.getHibernateTemplate().flush(); // simulate real changes to a db within a tx

    String expMpan = "5555555555555";
    boolean exists = mSp04FromAFMSMpanDao.exists(new MPANCore(expMpan));
    assertEquals(Boolean.TRUE, exists);

    String expMpan2 = "6666666666666";
    boolean exists2 = mSp04FromAFMSMpanDao.exists(new MPANCore(expMpan2));
    assertEquals(Boolean.TRUE, exists2);

    List<Sp04FromAFMSMpan> sp04AfmsMpans = mSp04FromAFMSMpanDao.get(supplier.getPk(), reportDateFeb232011);
    assertNotNull(sp04AfmsMpans);
    assertEquals(1, sp04AfmsMpans.size());
    assertEquals(expMpan, sp04AfmsMpans.iterator().next().getMpan().getValue());
  }

  /**
   *
   */
  @Test
  public void testSp04FromAfmsMpanExistsSuccess()
  {
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    MPANCore mpanToIgnore = new MPANCore("5555555555555");
    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(mpanToIgnore);
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);
    newAfmsMpan1.setCalculatedStandard1(1111L);
    newAfmsMpan1.setCalculatedStandard2(2222L);
    newAfmsMpan1.setCalculatedStandard3(333.333F);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush(); // simulate real changes to a db within a tx
    String expMpan = "5555555555555";
    boolean exists = mSp04FromAFMSMpanDao.exists(new MPANCore(expMpan));
    assertEquals(Boolean.TRUE, exists);

    if (!exists)
    {
      System.out.println("Sp04FromAfmsMpan record with mpan " + expMpan + " does not exist");
    }
    else
    {
      System.out.println("Sp04FromAfmsMpan record with mpan " + expMpan + " exists");
    }
  }

  /**
   *
   */
  @Test
  public void testSp04FromAfmsMpanExistsFail()
  {
    String expMpan = "5555555555555";
    boolean exists = mSp04FromAFMSMpanDao.exists(new MPANCore(expMpan));
    assertEquals(Boolean.FALSE, mSp04FromAFMSMpanDao.exists(new MPANCore(expMpan)));

    if (!exists)
    {
      System.out.println("Sp04FromAfmsMpan record with mpan " + expMpan + " does not exist");
    }
    else
    {
      System.out.println("Sp04FromAfmsMpan record with mpan " + expMpan + " exists");
    }
  }

  /**
   * Delete all record but ignoring mpans provided using any empty ignore mpans set.
   */
  @Test
  public void testDeleteSp04FromAFMSMpanTableWithEmptyIgnoreMpanSet()
  {
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    MPANCore mpanToIgnore = new MPANCore("1111111111111");
    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(mpanToIgnore);
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);
    newAfmsMpan1.setCalculatedStandard1(1111L);
    newAfmsMpan1.setCalculatedStandard2(2222L);
    newAfmsMpan1.setCalculatedStandard3(333.333F);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush(); // simulate real changes to a db within a tx

    // attempt to delete
    Set<MPANCore> emptyMpansToIgnore = new HashSet<MPANCore>();
    Boolean isDeleted = mSp04FromAFMSMpanDao.delete(emptyMpansToIgnore);
    assertEquals(Boolean.TRUE, isDeleted);

    // check record with mpan 1111111111111 does not exists
    Sp04FromAFMSMpan existingMpan = mSp04FromAFMSMpanDao.getMpan(mpanToIgnore);
    assertNull(existingMpan);
  }

  /**
   * Delete all record but ignoring mpans provided.
   */
  @Test
  public void testDeleteSp04FromAFMSMpanTable()
  {
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(new MPANCore("1111111111111"));
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);
    newAfmsMpan1.setCalculatedStandard1(1111L);
    newAfmsMpan1.setCalculatedStandard2(2222L);
    newAfmsMpan1.setCalculatedStandard3(333.333F);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);

    Sp04FromAFMSMpan newAfmsMpan2 = new Sp04FromAFMSMpan();
    newAfmsMpan2.setMpan(new MPANCore("2222222222222"));
    newAfmsMpan2.setMpanFk(222222L);
    newAfmsMpan2.setSupplierFk(supplier.getPk());
    newAfmsMpan2.setMeterId("222222");
    newAfmsMpan2.setMeterRegisterFks(new Long(2L).toString());
    newAfmsMpan2.setDataCollector(dcName1);
    newAfmsMpan2.setDataCollectorFk(2L);
    newAfmsMpan2.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan2.setD0268SettlementDate(new DateTime());
    newAfmsMpan2.setMeterRegisterReading1(10F);
    newAfmsMpan2.setMeterRegisterReading2(20F);
    newAfmsMpan2.setMeterRegisterReading3(30F);
    newAfmsMpan2.setCalculatedStandard1(11110L);
    newAfmsMpan2.setCalculatedStandard2(22220L);
    newAfmsMpan2.setCalculatedStandard3(444.444F);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan2);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush(); // simulate real changes to a db within a tx

    // attempt to delete
    Set<MPANCore> mpansToIgnore = new HashSet<MPANCore>();
    MPANCore mpanToIgnore = new MPANCore("1111111111111");
    mpansToIgnore.add(mpanToIgnore);
    Boolean isDeleted = mSp04FromAFMSMpanDao.delete(mpansToIgnore);
    assertEquals(Boolean.TRUE, isDeleted);

    // check record with mpan 1111111111111 still exists
    Sp04FromAFMSMpan existingMpan = mSp04FromAFMSMpanDao.getMpan(mpanToIgnore);
    assertNotNull(existingMpan);
    assertEquals(mpanToIgnore, existingMpan.getMpan());

    // check record with mpan 2222222222222 does not exist
    MPANCore expDeletedMpan = new MPANCore("2222222222222");
    Sp04FromAFMSMpan doesntExistMpan = mSp04FromAFMSMpanDao.getMpan(expDeletedMpan);
    assertNull(doesntExistMpan);
  }

  /**
   * Try to delete all records but ignoring mpans provided.
   */
  @Test
  public void testDeleteNoneFoundInSp04FromAFMSMpanTableToDelete()
  {
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(new MPANCore("1111111111111"));
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);
    newAfmsMpan1.setCalculatedStandard1(1111L);
    newAfmsMpan1.setCalculatedStandard2(2222L);
    newAfmsMpan1.setCalculatedStandard3(333.333F);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush(); // simulate real changes to a db within a tx

    // attempt to delete
    Set<MPANCore> mpansToIgnore = new HashSet<MPANCore>();
    MPANCore mpanToIgnore = new MPANCore("1111111111111");
    mpansToIgnore.add(mpanToIgnore);
    Boolean isDeleted = mSp04FromAFMSMpanDao.delete(mpansToIgnore);
    assertEquals(Boolean.FALSE, isDeleted);

    // check record with mpan 1111111111111 still exists
    Sp04FromAFMSMpan existingMpan = mSp04FromAFMSMpanDao.getMpan(mpanToIgnore);
    assertNotNull(existingMpan);
    assertEquals(mpanToIgnore, existingMpan.getMpan());
  }

  /**
   * Persist Sp04FromAfmsMpan record with calculated standards 1, 2, 3.
   */
  @Test
  public void testSaveSp04FromAfmsMpanWithStandards1_2_3()
  {
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(new MPANCore("1111111111111"));
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);
    newAfmsMpan1.setCalculatedStandard1(1111L);
    newAfmsMpan1.setCalculatedStandard2(2222L);
    newAfmsMpan1.setCalculatedStandard3(333.333F);

    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);

    assertNotNull(newAfmsMpan1.getPk());
    assertNotNull(newAfmsMpan1.getCalculatedStandard1());
    assertEquals(1111L, newAfmsMpan1.getCalculatedStandard1().longValue());
    assertEquals(2222L, newAfmsMpan1.getCalculatedStandard2().longValue());
    assertEquals(new Float(333.333F), newAfmsMpan1.getCalculatedStandard3());
  }

  /**
   * Get records with the given supplierpk and data collector name.
   */
  @Test
  public void testGetByDataCollector()
  {
    Supplier supplier = new Supplier("EMEB");
    supplier.setLastUpdated(new DateTime());
    String dcName1 = "EMEB";
    String dcName2 = "FROG";
    mSp04FromAFMSMpanDao.getHibernateTemplate().persist(supplier);
    mSp04FromAFMSMpanDao.getHibernateTemplate().flush();

    Sp04FromAFMSMpan newAfmsMpan1 = new Sp04FromAFMSMpan();
    newAfmsMpan1.setMpan(new MPANCore("1111111111111"));
    newAfmsMpan1.setMpanFk(111111L);
    newAfmsMpan1.setSupplierFk(supplier.getPk());
    newAfmsMpan1.setMeterId("111111");
    newAfmsMpan1.setMeterRegisterFks(new Long(1L).toString());
    newAfmsMpan1.setDataCollector(dcName1);
    newAfmsMpan1.setDataCollectorFk(1L);
    newAfmsMpan1.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan1.setD0268SettlementDate(new DateTime());
    newAfmsMpan1.setMeterRegisterReading1(1F);
    newAfmsMpan1.setMeterRegisterReading2(2F);
    newAfmsMpan1.setMeterRegisterReading3(3F);

    Sp04FromAFMSMpan newAfmsMpan2 = new Sp04FromAFMSMpan();
    newAfmsMpan2.setMpan(new MPANCore("2222222222222"));
    newAfmsMpan2.setMpanFk(111111L);
    newAfmsMpan2.setSupplierFk(supplier.getPk());
    newAfmsMpan2.setMeterId("222222");
    newAfmsMpan2.setMeterRegisterFks(new Long(2L).toString());
    newAfmsMpan2.setDataCollector(dcName2);
    newAfmsMpan2.setDataCollectorFk(2L);
    newAfmsMpan2.setCalculatedMeterInstallationDeadline(new DateTime());
    newAfmsMpan2.setD0268SettlementDate(new DateTime());
    newAfmsMpan2.setMeterRegisterReading1(1F);
    newAfmsMpan2.setMeterRegisterReading2(2F);
    newAfmsMpan2.setMeterRegisterReading3(3F);

    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan1);
    mSp04FromAFMSMpanDao.makePersistent(newAfmsMpan2);

    assertNotNull(newAfmsMpan1.getPk());
    assertNotNull(newAfmsMpan2.getPk());

    IterableMap<String, Sp04FromAFMSMpan> afmsMpans =  mSp04FromAFMSMpanDao.getByDataCollector(supplier.getPk(), dcName2);

    assertNotNull(afmsMpans);
    assertEquals(1, afmsMpans.size());
    assertNotNull(afmsMpans.get("2222222222222"));
    assertEquals(dcName2, afmsMpans.get("2222222222222").getDataCollector());
  }

  /**
   * GetAll returns an empty list.
   */
  @Test
  public void testGetNullSp04FromAFMSMpan()
  {
    assertNotNull(mSp04FromAFMSMpanDao);
    assertTrue(mSp04FromAFMSMpanDao.getAll().isEmpty());
  }
}

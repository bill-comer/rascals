package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.hibernate.Query;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;

import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028UploadError;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04FaultReasonType;


/**
 *
 */
@TransactionConfiguration(defaultRollback = true, transactionManager = "test.parms.transactionManager")
@Transactional(rollbackFor = RuntimeException.class)
@SuppressWarnings("unchecked")
public class P0028FileDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDaoHibernate mP0028FileDao;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028DataDao")
  private P0028DataDao mP0028DataDao;

  /**
   * Test P0028File.p0028Data collection mapping.
   */
  @Test
  public void testP0028FileP0028DataCollection()
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);
    P0028File p28 = new P0028File("x_filename", "dc",  supplier, "phil", new DateTime(),
                                  new ParmsReportingPeriod(new DateTime().toDateMidnight()));

    List<P0028Data> p28Datas = new ArrayList<P0028Data>();
    MPANCore mpan = new MPANCore("1000008880003");
    P0028Data p28Data01 = new P0028Data(123L, "1111", mpan, new DateTime(), p28);
    p28Data01.setDcAgentName("bert");
    P0028Data p28Data02 = new P0028Data(124L, "2222", mpan, new DateTime().plusDays(1), p28);
    p28Data02.setDcAgentName("fred");
    P0028Data p28Data03 = new P0028Data(125L, "3333", mpan, new DateTime().plusDays(10), p28);
    p28Data03.setDcAgentName("ernie");
    p28Datas.add(p28Data01);
    p28Datas.add(p28Data02);
    p28Datas.add(p28Data03);
    p28.setP0028Data(p28Datas);
    Long p28Pk = mP0028FileDao.makePersistent(p28).getPk();
    mP0028FileDao.getHibernateTemplate().flush();

    P0028File savedP28 = mP0028FileDao.getById(p28Pk);
    assertNotNull(savedP28);
    assertEquals(p28Pk, savedP28.getPk());

  }

  /**
   * Lookup P0028Files with P0028UploadError(s).
   */
  @Test
  public void testCustomHqlGetP0028FilesWithErrrorsCount()
  {
    createTestData();
    String hasErrors = "in";
    String hqlJoin = "select count(distinct pf) from P0028File pf join pf.p0028Data pd where pd.size > 0 "
      + "and pd.p0028File.pk " + hasErrors + " (select distinct pd2.p0028File.pk from P0028Data pd2 "
      + "where pd2.p0028UploadError.size > 0)";
    List results = mP0028FileDao.getHibernateTemplate().find(hqlJoin);
    assertTrue("expected a Long", results.get(0).getClass().equals(Long.class));
    assertEquals(2, ((Long) results.get(0)).longValue());
  }

  /**
   * Lookup P0028Files without P0028UploadError(s).
   */
  @Test
  public void testCustomHqlGetP0028FilesWithoutErrrorsCount()
  {
    createTestData();
    String hasNoErrors = "not in";
    String hqlJoin = "select count(distinct pf) from P0028File pf join pf.p0028Data pd where pd.size > 0 "
      + "and pd.p0028File.pk " + hasNoErrors + " (select distinct pd2.p0028File.pk from P0028Data pd2 "
      + "where pd2.p0028UploadError.size > 0)";
    List results = mP0028FileDao.getHibernateTemplate().find(hqlJoin);
    assertTrue("expected a Long", results.get(0).getClass().equals(Long.class));
    assertEquals(1, ((Long) results.get(0)).longValue());
  }

  /**
   * Retrieve only P0028File(s) containing P0028File.p0028Data.p0028UploadError(s).
   */
  @Test
  public void testCustomHqlGetP0028FilesWithErrors()
  {
    createTestData();
    String hasErrors = "in";
    String hqlJoin = "select distinct pf from P0028File pf join pf.p0028Data pd where pd.size > 0 and pd.p0028File.pk "
      + hasErrors + " (select distinct pd2.p0028File.pk from P0028Data pd2 where pd2.p0028UploadError.size > 0)";
    List results = mP0028FileDao.getHibernateTemplate().find(hqlJoin);
    assertEquals(2, results.size());
  }

  /**
   * Lookup P0028Files without P0028File.p0028Data.p0028UploadError(s).
   */
  @Test
  public void testCustomHqlGetP0028FilesWithoutErrors()
  {
    createTestData();
    String hasNoErrors = "not in";
    String hqlJoin = "select distinct pf from P0028File pf join pf.p0028Data pd where pd.size > 0 and pd.p0028File.pk "
      + hasNoErrors + " (select distinct pd2.p0028File.pk from P0028Data pd2 where pd2.p0028UploadError.size > 0)";
    List results = mP0028FileDao.getHibernateTemplate().find(hqlJoin);
    assertEquals(1, results.size());
  }

  /**
   * Lookup P0028Files without P0028File.p0028Data.p0028UploadError(s).
   */
  @Test
  public void testCustomHqlGetP0028FilesWithoutErrors2()
  {
    createTestData();
    boolean isErrored = false;
    String hql = "select distinct pf from P0028File pf where pf.errored = :id0";
    Query hqlQuery = mP0028FileDao.getSessionFactory().getCurrentSession().createQuery(hql);
    hqlQuery.setBoolean("id0", isErrored);
    List results = hqlQuery.list();
    assertEquals(1, results.size());
  }

  /**
   * Lookup P0028Files with P0028File.p0028Data.p0028UploadError(s).
   */
  @Test
  public void testCustomHqlGetP0028FilesWithErrors2()
  {
    createTestData();
    boolean isErrored = true;
    String hql = "select distinct pf from P0028File pf where pf.errored = :id0";
    Query hqlQuery = mP0028FileDao.getSessionFactory().getCurrentSession().createQuery(hql);
    hqlQuery.setBoolean("id0", isErrored);
    List results = hqlQuery.list();
    assertEquals(2, results.size());
  }

  /**
   * Check not null ConstraintViolationException is checked on P0028File class variables.
   */
  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testP0028FileIsNullConstraintsValidation()
  {
    P0028File file = new P0028File();
    mP0028FileDao.makePersistent(file);
  }

  /**
   * Retrieve all P0028Files.
   *
   * @throws Exception
   */
  @Test
  public void getAll() throws Exception
  {
    List<P0028File> all = mP0028FileDao.getAll();

    //test method
    assertEquals(0, all.size());
  }

  /**
   * Create and save P0028File.
   *
   * @throws Exception
   */
  @Test
  public void createAndSaveP0028File() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028File = new P0028File("a_filename", "dc",  supplier, "bert", receiptDate, period);

    //test method
    mP0028FileDao.makePersistent(p0028File);

    assertTrue(p0028File.getPk() > 0);

    P0028File fetchedFile = mP0028FileDao.getById(p0028File.getPk());
    assertNotNull(fetchedFile);
    assertEquals(p0028File.getPk(), fetchedFile.getPk());
    assertEquals("a_filename", fetchedFile.getFilename());
    assertEquals("bert", fetchedFile.getDcAgentName());
    assertEquals(supplier, fetchedFile.getSupplier());
    assertEquals(receiptDate, fetchedFile.getReceiptDate());
    assertEquals(period, fetchedFile.getReportingPeriod());
  }

  /**
   * Check if there are any more recent P0028Files already in the database.
   *
   * @throws Exception
   */
  @Test
  public void areThereAnyUploadsNewerThanThis_none_toFind() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    //test method
    assertFalse(mP0028FileDao.areThereAnyUploadsNewerThanThis(supplier, "FRED", new DateTime()));
  }


  /**
   * @throws Exception
   */
  @Test
  public void areThereAnyUploadsNewerThanThis_none_newer() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    P0028File p0028File = new P0028File("a_filename", "dc", supplier, "FRED",
                                        new DateTime().minusMonths(1), new ParmsReportingPeriod(11, 2010));
    mP0028FileDao.makePersistent(p0028File);

    //test method
    assertFalse(mP0028FileDao.areThereAnyUploadsNewerThanThis(supplier, "FRED", new DateTime()));
  }


  /**
   * @throws Exception
   */
  @Test
  public void areThereAnyUploadsNewerThanThis_one_newer() throws Exception
  {
    Supplier supplier = new Supplier("FRED");
    mSupplierDao.makePersistent(supplier);

    P0028File p0028File = new P0028File("a_filename", "dc", supplier, "FRED",
                                        new DateTime().plusMonths(1), new ParmsReportingPeriod(11, 2010));
    mP0028FileDao.makePersistent(p0028File);

    //test method
    assertTrue(mP0028FileDao.areThereAnyUploadsNewerThanThis(supplier, "FRED", new DateTime()));
  }

  private void createTestData()
  {
    DateTime readingDate = new DateTime();
    Supplier supplier = new Supplier("PHIL");
    supplier.setLastUpdated(new DateTime());

    List<Supplier> suppliers = new ArrayList<Supplier>();
    suppliers.add(supplier);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);

    P0028File p0028FileWithAllErrorData = new P0028File("x_filename", "dc",  supplier, "phil", receiptDate, period);
    p0028FileWithAllErrorData.setLastUpdated(new DateTime());
    p0028FileWithAllErrorData.setErrored(true);

    P0028File p0028FileWithoutErrorData = new P0028File("y_filename", "mop",  supplier, "phil", receiptDate, period);
    p0028FileWithoutErrorData.setLastUpdated(new DateTime());
    p0028FileWithoutErrorData.setErrored(false);

    P0028File p0028FileWithPartialErrorData = new P0028File("z_filename", "dc",  supplier, "phil", receiptDate, period);
    p0028FileWithPartialErrorData.setLastUpdated(new DateTime());
    p0028FileWithPartialErrorData.setErrored(true);

    List<P0028Data> datas1 = new ArrayList<P0028Data>();
    MPANCore mpan1 = new MPANCore("1000000000003");
    MPANCore mpan1a = new MPANCore("1200000000003");

    List<P0028Data> datas2 = new ArrayList<P0028Data>();
    MPANCore mpan2 = new MPANCore("1000000000004");

    List<P0028Data> datas3 = new ArrayList<P0028Data>();
    MPANCore mpan3 = new MPANCore("1000000000005");
    MPANCore mpan3a = new MPANCore("1000000000006");

    P0028Data data1 = new P0028Data(123L, "45678", mpan1, readingDate, p0028FileWithAllErrorData);
    data1.setLastUpdated(new DateTime());
    List<P0028UploadError> errors1 = new ArrayList<P0028UploadError>();
    P0028UploadError error1 = new P0028UploadError(data1, Sp04FaultReasonType.NO_AFMS_METER);
    error1.setLastUpdated(new DateTime());
    errors1.add(error1);
    data1.setP0028UploadError(errors1);
    datas1.add(data1);

    P0028Data data1a = new P0028Data(124L, "55679", mpan1a, readingDate, p0028FileWithAllErrorData);
    data1a.setLastUpdated(new DateTime());
    List<P0028UploadError> errors1a = new ArrayList<P0028UploadError>();
    P0028UploadError error1a = new P0028UploadError(data1a, Sp04FaultReasonType.NO_AFMS_METER);
    error1a.setLastUpdated(new DateTime());
    errors1a.add(error1a);
    data1a.setP0028UploadError(errors1a);
    datas1.add(data1a);
    p0028FileWithAllErrorData.setP0028Data(datas1);


    P0028Data data2 = new P0028Data(1235L, "45676", mpan2, readingDate, p0028FileWithoutErrorData);
    data2.setLastUpdated(new DateTime());
    datas2.add(data2);
    p0028FileWithoutErrorData.setP0028Data(datas2);

    P0028Data data3 = new P0028Data(3335L, "45679", mpan3, readingDate, p0028FileWithPartialErrorData);
    data3.setLastUpdated(new DateTime());
    datas3.add(data3);
    P0028Data data3a = new P0028Data(3339L, "4009", mpan3a, readingDate, p0028FileWithPartialErrorData);
    data3a.setLastUpdated(new DateTime());
    List<P0028UploadError> errors3a = new ArrayList<P0028UploadError>();
    P0028UploadError error3a = new P0028UploadError(data3a, Sp04FaultReasonType.NO_AFMS_METER);
    error3a.setLastUpdated(new DateTime());
    errors3a.add(error3a);
    data3a.setP0028UploadError(errors3a);
    datas3.add(data3a);
    p0028FileWithPartialErrorData.setP0028Data(datas3);

    List<P0028File> p0028Files = new ArrayList<P0028File>();
    p0028Files.add(p0028FileWithAllErrorData);
    p0028Files.add(p0028FileWithoutErrorData);
    p0028Files.add(p0028FileWithPartialErrorData);

    mSupplierDao.batchMakePersistent(suppliers);
    mP0028FileDao.batchMakePersistent(p0028Files);
    mP0028DataDao.batchMakePersistent(datas1);
    mP0028DataDao.batchMakePersistent(datas2);
    mP0028DataDao.batchMakePersistent(datas3);

    mP0028FileDao.getHibernateTemplate().flush();
  }
}

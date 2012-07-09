package uk.co.utilisoft.parms.file.p0028;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.IterableMap;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.P0028FileDaoHibernate;
import uk.co.utilisoft.parms.dao.P0028FileDataDaoHibernate;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.util.FileUtil;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/config/test-parms.xml" })
@TransactionConfiguration(defaultRollback = true, transactionManager = "test.parms.transactionManager")
@Transactional(rollbackFor = RuntimeException.class)
@SuppressWarnings("unchecked")
public class P0028ImportingIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required = true)
  @Qualifier("parms.p0028Importer")
  private P0028Importer mP0028Importer;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDao")
  private P0028FileDaoHibernate mP0028FileDao;

  @Autowired(required = true)
  @Qualifier("parms.p0028FileDataDao")
  private P0028FileDataDaoHibernate mP0028FileDataDao;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  /**
   *
   *
   * @throws Exception
   */
  @Test
  public void testP0028ImportedWithErrors() throws Exception
  {
    Supplier supplier = new Supplier("SUPP");
    mSupplierDao.makePersistent(supplier);
    String fileName = "uploadForSupplier.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" + fileName;
    ByteArrayInputStream bais = new ByteArrayInputStream(FileUtil.getBytesFromFile(new File(filePath)));
    DateTime now = new DateTime(2010, 10, 5, 11, 11, 11, 0);
    freezeTime(now);
    Map<UploadStatus, Map<String, IterableMap<String, P0028Active>>> statusP0028ActivesInfo
      = mP0028Importer.importer(supplier, fileName, bais, now.minusMonths(1));
    UploadStatus status = !statusP0028ActivesInfo.isEmpty() ? statusP0028ActivesInfo.keySet().iterator().next() : null;
    assertNotNull(status);
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, status);

    // check P0028File.errored has been set for errored file
    String hql = "from P0028File pf where pf.filename = :id0";
    Query query = mP0028FileDao.getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
    query.setString("id0", fileName);
    List<P0028File> p0028Files = query.list();
    assertEquals(p0028Files.size(), 1);
    assertEquals(true, p0028Files.get(0).isErrored());
  }

  /**
   * @throws Exception
   */
  @Test
  public void uploadForSupplier() throws Exception
  {
    assertNotNull("P0028FileDaoHibernate not injected into test", mP0028FileDao);


    assertEquals(0, mP0028FileDao.getAll().size());

    DateTime now = new DateTime(2010, 10, 5, 11, 11, 11, 0);
    freezeTime(now);

    //expected PRP is month 8 as day of month is < 7, and receipt date is now.minusMonths(1)
    ParmsReportingPeriod expectedPrp = new ParmsReportingPeriod(8, 2010);

    Supplier supplier = new Supplier("SUPP");
    mSupplierDao.makePersistent(supplier);

    String fileName = "uploadForSupplier.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +fileName;

    // changed FileInputStream to ByteArrayInputStream because that is what is actually passed into the P0028Importer.importer() method in the application
//    FileInputStream fis = new FileInputStream(filePath);
    ByteArrayInputStream bais = new ByteArrayInputStream(FileUtil.getBytesFromFile(new File(filePath)));

    //10:22:33 11/2/2010
    DateTime expectedDateTime = new DateTime(2010,2,11, 0,0, 0, 0);
    //11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010,3,12, 0 ,0, 0, 0);

    //test method
    Map<UploadStatus, Map<String, IterableMap<String, P0028Active>>> statusP0028ActivesInfo
      = mP0028Importer.importer(supplier, fileName, bais, now.minusMonths(1));
    UploadStatus status = !statusP0028ActivesInfo.isEmpty() ? statusP0028ActivesInfo.keySet().iterator().next() : null;
    assertNotNull(status);
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, status);

    List<P0028File> p0028FileList = mP0028FileDao.getAll();
    assertNotNull(p0028FileList);
    assertEquals(1, p0028FileList.size());

    P0028File p0028File = p0028FileList.get(0);
    assertNotNull(p0028File);

    assertNotNull("a P0028File shopuld have been created", p0028File);
    assertEquals("uploadForSupplier.csv", p0028File.getFilename());
    assertEquals(supplier, p0028File.getSupplier());
    assertTrue(p0028File.getP0028Data() != null);
    assertTrue("should be two rows", p0028File.getP0028Data().size() == 2);
    assertEquals(now.minusMonths(1), p0028File.getReceiptDate());

    //test the contents of the rows
    P0028Data first = p0028File.getP0028Data().get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterSerialId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(expectedDateTime, first.getReadingDate());

    //second row
    P0028Data second = p0028File.getP0028Data().get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterSerialId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());

    assertEquals("FRED", p0028File.getDcAgentName());
    assertEquals(now.minusMonths(1), p0028File.getReceiptDate());
    assertEquals(now, p0028File.getDateImported());

    assertEquals("should have been the previous month to now", expectedPrp, p0028File.getReportingPeriod());

    String hqlQuery = "from P0028UploadError";
    Session session = mP0028FileDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
    Query query = session.createQuery(hqlQuery);
    List results = query.list();
    assertTrue(results.size() == 2);

    // test P0028FileData clob has been saved
    P0028FileData p0028FileDataclob = mP0028FileDataDao.getByP0028FileId(p0028File.getPk());
    assertNotNull(p0028FileDataclob);
    assertTrue("expected P0028FileData.data to contain some clob data for file[" + fileName + "]",
               p0028FileDataclob.getData().length() > 0);
  }


  /**
   * @throws Exception
   */
  @Test
  public void uploadForSupplier_additionalBlankLines() throws Exception
  {
    assertNotNull("P0028FileDaoHibernate not injected into test", mP0028FileDao);


    assertEquals(0, mP0028FileDao.getAll().size());

    DateTime now = new DateTime(2010, 10, 5, 11, 11, 11, 0);
    freezeTime(now);

    //expected PRP is month 8 as day of month is < 7, and receipt date is now.minusMonths(1)
    ParmsReportingPeriod expectedPrp = new ParmsReportingPeriod(8, 2010);

    Supplier supplier = new Supplier("SUPP");
    mSupplierDao.makePersistent(supplier);

    String fileName = "uploadForSupplier_additionalBlankLines.csv";
    String filePath = "test/src/uk/co/utilisoft/parms/file/p0028/" +fileName;

    // changed FileInputStream to ByteArrayInputStream because that is what is actually passed into the P0028Importer.importer() method in the application
//    FileInputStream fis = new FileInputStream(filePath);
    ByteArrayInputStream bais = new ByteArrayInputStream(FileUtil.getBytesFromFile(new File(filePath)));

    //10:22:33 11/2/2010
    DateTime expectedDateTime = new DateTime(2010,2,11, 0,0, 0, 0);
    //11:23:44 12/3/2010
    DateTime secExpectedDateTime = new DateTime(2010,3,12, 0 ,0, 0, 0);

    //test method
    Map<UploadStatus, Map<String, IterableMap<String, P0028Active>>> statusP0028ActivesInfo
      = mP0028Importer.importer(supplier, fileName, bais, now.minusMonths(1));
    UploadStatus status = !statusP0028ActivesInfo.isEmpty() ? statusP0028ActivesInfo.keySet().iterator().next() : null;
    assertNotNull(status);
    assertEquals(UploadStatus.UPLOADED_WITH_ROW_ERRORS, status);

    List<P0028File> p0028FileList = mP0028FileDao.getAll();
    assertNotNull(p0028FileList);
    assertEquals(1, p0028FileList.size());

    P0028File p0028File = p0028FileList.get(0);
    assertNotNull(p0028File);

    assertNotNull("a P0028File shopuld have been created", p0028File);
    assertEquals("uploadForSupplier_additionalBlankLines.csv", p0028File.getFilename());
    assertEquals(supplier, p0028File.getSupplier());
    assertTrue(p0028File.getP0028Data() != null);
    assertTrue("should be two rows", p0028File.getP0028Data().size() == 2);
    assertEquals(now.minusMonths(1), p0028File.getReceiptDate());

    //test the contents of the rows
    P0028Data first = p0028File.getP0028Data().get(0);
    assertEquals("FRED", first.getDcAgentName());
    assertEquals(new Long(100), first.getMaxDemand());
    assertEquals("12345", first.getMeterSerialId());
    assertEquals("1000000000003", first.getMpan().getValue());
    assertEquals(expectedDateTime, first.getReadingDate());

    //second row
    P0028Data second = p0028File.getP0028Data().get(1);
    assertEquals("FRED", second.getDcAgentName());
    assertEquals(new Long(200), second.getMaxDemand());
    assertEquals("54321", second.getMeterSerialId());
    assertEquals("1000000000004", second.getMpan().getValue());
    assertEquals(secExpectedDateTime, second.getReadingDate());

    assertEquals("FRED", p0028File.getDcAgentName());
    assertEquals(now.minusMonths(1), p0028File.getReceiptDate());
    assertEquals(now, p0028File.getDateImported());

    assertEquals("should have been the previous month to now", expectedPrp, p0028File.getReportingPeriod());

    String hqlQuery = "from P0028UploadError";
    Session session = mP0028FileDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
    Query query = session.createQuery(hqlQuery);
    List results = query.list();
    assertTrue(results.size() == 2);

    // test P0028FileData clob has been saved
    P0028FileData p0028FileDataclob = mP0028FileDataDao.getByP0028FileId(p0028File.getPk());
    assertNotNull(p0028FileDataclob);
    assertTrue("expected P0028FileData.data to contain some clob data for file[" + fileName + "]",
               p0028FileDataclob.getData().length() > 0);
  }


}

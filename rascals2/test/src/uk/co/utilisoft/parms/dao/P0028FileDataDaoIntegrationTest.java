package uk.co.utilisoft.parms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.P0028FileData;
import uk.co.utilisoft.parms.domain.Supplier;

import static org.junit.Assert.*;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback = true, transactionManager = "test.parms.transactionManager")
@Transactional(rollbackFor = { RuntimeException.class })
@SuppressWarnings("unchecked")
public class P0028FileDataDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required = true)
  @Qualifier("parms.p0028FileDataDao")
  private P0028FileDataDaoHibernate mP0028FileDataDao;

  /**
   * Get the latest P0028FileData for a given P0028 File with the latest import date for the given P0028 File name.
   */
  @Test
  public void testGetLatestByP0028FileNameSuccess()
  {
    String p28Filename = "testp0028filename01.csv";
    P0028FileData p28FileData = mP0028FileDataDao.getLatestByP0028FileName(p28Filename);

    assertNotNull(p28FileData);
  }

  /**
   * Get the latest P0028FileData for a given P0028 File with the latest import date for an unknown P0028 File name.
   */
  @Test
  public void testGetLatestByP0028FileNameFail()
  {
    String p28Filename = "testp0028filename_unknown.csv";
    P0028FileData p28FileData = mP0028FileDataDao.getLatestByP0028FileName(p28Filename);

    assertNull(p28FileData);
  }

  /**
   * Check not null ConstraintViolationException is checked on P0028FileData instance variables.
   */
  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testP0028FileDataIsNullConstraintsValidation()
  {
    P0028FileData data = new P0028FileData();
    mP0028FileDataDao.makePersistent(data);
  }

  /**
   * Test returns null P0028FileData record.
   */
  @Test
  public void getByP0028FileIdFail()
  {
    P0028FileData data = mP0028FileDataDao.getById(0L);
    assertNull(data);
  }

  /**
   * Test return a single P0028FileData record.
   */
  @Test
  public void getByP0028FileDataSuccess()
  {
    P0028FileData data = mP0028FileDataDao.getByP0028FileId((Long) getPersistedObjectPks().get(P0028File.class)
                                                            .get(0));
    assertNotNull(data);
  }

  /**
   * Insert test data.
   */
  @BeforeTransaction
  public void init()
  {
    Supplier supplier = new Supplier("FRED");
    supplier.setLastUpdated(new DateTime());
    List<Supplier> suppliers = new ArrayList<Supplier>();
    suppliers.add(supplier);
    insertTestData(suppliers, mP0028FileDataDao, Supplier.class);

    DateTime receiptDate = new DateTime();
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    P0028File p0028File1 = new P0028File("test_p0028file_filename", "dc",  supplier, "bert", receiptDate, period);
    p0028File1.setLastUpdated(new DateTime());
    List<P0028File> p0028Files = new ArrayList<P0028File>();
    p0028Files.add(p0028File1);
    insertTestData(p0028Files, mP0028FileDataDao, P0028File.class);

    P0028FileData data = new P0028FileData();
    data.setData(new String("test_p0028filedata_clob_data"));
    data.setP0028File(p0028File1);
    data.setLastUpdated(new DateTime());
    List<P0028FileData> datas = new ArrayList<P0028FileData>();
    datas.add(data);
    insertTestData(datas, mP0028FileDataDao, P0028FileData.class);

    // for test getByP0028FileName
    String p28FilenameA = "testp0028filename01.csv";
    DateTime latestDateImported = new DateTime(2011, 5, 1, 0, 0, 0, 0);
    DateTime earlierDateImported = new DateTime(2011, 4, 24, 0, 0, 0, 0);

    Supplier supplierA = new Supplier("BART");
    supplierA.setLastUpdated(new DateTime());
    List<Supplier> suppliersA = new ArrayList<Supplier>();
    suppliersA.add(supplierA);
    insertTestData(suppliersA, mP0028FileDataDao, Supplier.class);

    P0028File p28File1 = new P0028File(p28FilenameA, "BART", supplier, "BART", new DateTime(2011, 4, 23, 0, 0, 0, 0),
                                       new ParmsReportingPeriod(new DateMidnight(2011, 3, 3)));
    p28File1.setLastUpdated(earlierDateImported.plusDays(2));
    p28File1.setErrored(false);
    p28File1.setReceiptDate(earlierDateImported.plusDays(1));
    p28File1.setDateImported(earlierDateImported);

    P0028File p28File2 = new P0028File(p28FilenameA, "BART", supplier, "BART", new DateTime(2011, 4, 29, 0, 0, 0, 0),
                                       new ParmsReportingPeriod(new DateMidnight(2011, 3, 3)));
    p28File2.setLastUpdated(latestDateImported.plusDays(2));
    p28File2.setErrored(false);
    p28File2.setReceiptDate(latestDateImported.plusDays(1));
    p28File2.setDateImported(latestDateImported);

    List<P0028File> p0028FilesA = new ArrayList<P0028File>();
    p0028FilesA.add(p28File1);
    p0028FilesA.add(p28File2);
    insertTestData(p0028FilesA, mP0028FileDataDao, P0028File.class);

    P0028FileData p28FileData1 = new P0028FileData();
    p28FileData1.setP0028File(p28File1);
    p28FileData1.setLastUpdated(earlierDateImported.plusDays(2));
    p28FileData1.setData("test_p0028_file_data_01");

    P0028FileData p28FileData2 = new P0028FileData();
    p28FileData2.setP0028File(p28File2);
    p28FileData2.setLastUpdated(latestDateImported.plusDays(2));
    p28FileData2.setData("test_p0028_file_data_02");

    List<P0028FileData> p0028FileDatasA = new ArrayList<P0028FileData>();
    p0028FileDatasA.add(p28FileData1);
    p0028FileDatasA.add(p28FileData2);
    insertTestData(p0028FileDatasA, mP0028FileDataDao, P0028FileData.class);
  }

  /**
   * Clean up test data.
   */
  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mP0028FileDataDao, P0028FileData.class);
    deleteTestData(mP0028FileDataDao, P0028File.class);
    deleteTestData(mP0028FileDataDao, Supplier.class);
  }
}

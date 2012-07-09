package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class DpiFileDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testDpiFileSupplierIsNullConstraintsValidation()
  {
    DpiFile dpiFile = new DpiFile();
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    dpiFile.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    mDpiFileDao.makePersistent(dpiFile);
  }

  @Test
  public void testDpiFileCreation()
  {
    assertNotNull(mDpiFileDao);
    assertNotNull("getHibernateTemplate is null", mDpiFileDao.getHibernateTemplate());

    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile();
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    dpiFile.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    dpiFile.setSupplier(supplier);

    mDpiFileDao.makePersistent(dpiFile);
    assertNotNull(dpiFile.getPk());

    DpiFile anoDpiFile = mDpiFileDao.getById(dpiFile.getPk());
    assertNotNull(anoDpiFile);
    assertEquals(dpiFile.getPk(), anoDpiFile.getPk());
    assertEquals(supplier.getSupplierId(), anoDpiFile.getSupplier().getSupplierId());
  }

  @Test
  public void testGetDpiFilesForaSupplier() throws Exception
  {
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);

    Supplier bert = new Supplier("bert");
    mSupplierDao.makePersistent(bert);

    DpiFile dpiFile = new DpiFile();
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName1");
    dpiFile.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    dpiFile.setSupplier(supplier);
    mDpiFileDao.makePersistent(dpiFile);

    DpiFile dpiFile2 = new DpiFile();
    dpiFile2.setLastUpdated(new DateTime());
    dpiFile2.setFileName("aTestFileName2");
    dpiFile2.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    dpiFile2.setSupplier(supplier);
    mDpiFileDao.makePersistent(dpiFile2);

    DpiFile dpiFile3 = new DpiFile();
    dpiFile3.setLastUpdated(new DateTime());
    dpiFile3.setFileName("aTestFileName3");
    dpiFile3.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    dpiFile3.setSupplier(bert);
    mDpiFileDao.makePersistent(dpiFile3);

    assertEquals(2, mDpiFileDao.getDpiFilesForSupplier(supplier).size());
  }
}

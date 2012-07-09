package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.validation.ConstraintViolationException;

import org.hibernate.StatelessSession;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 *
 */
@TransactionConfiguration(defaultRollback = true, transactionManager = "test.parms.transactionManager")
@Transactional(rollbackFor = RuntimeException.class)
@SuppressWarnings("rawtypes")
public class SupplierDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  /**
   * Check that can only insert suppliers with unique names.
   */
  @Test
  @ExpectedException(InvalidDataAccessResourceUsageException.class)
  public void testUniqueSupplierId()
  {
    String supplierId = "JACK";
    Supplier supplier1 = new Supplier(supplierId);
    mSupplierDao.makePersistent(supplier1);
    Supplier supplier2 = new Supplier(supplierId);
    mSupplierDao.makePersistent(supplier2);
  }

  /**
   * Check creating supplier with empty supplier id throws exception.
   */
  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testSupplierIdIsNullConstraintsValidation()
  {
    Supplier supplier = new Supplier(null);
    mSupplierDao.makePersistent(supplier);
  }

  /**
   *
   * @throws Exception
   */
  @Test
  @NotTransactional
  public void testCreateSupplier() throws Exception
  {
    DateTime timeToFreezeAt = new DateTime(2005, 3, 26, 11, 10, 50, 00);
    freezeTime(timeToFreezeAt);

    assertNotNull(mSupplierDao);
    assertNotNull("getHibernateTemplate is null", mSupplierDao.getHibernateTemplate());

    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    Supplier anoSupplier = mSupplierDao.getById(supplier.getPk());
    assertEquals(supplier.getSupplierId(), anoSupplier.getSupplierId());
    assertEquals(timeToFreezeAt, anoSupplier.getLastUpdated());
    assertEquals(timeToFreezeAt, supplier.getLastUpdated());

    // clean up because test is non transactional
    StatelessSession session = mSupplierDao.getHibernateTemplate().getSessionFactory().openStatelessSession();
    session.delete(supplier);
    session.close();
  }
}
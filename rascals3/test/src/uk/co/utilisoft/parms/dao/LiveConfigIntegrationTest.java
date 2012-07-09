package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/parms-main.xml"})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional(rollbackFor=RuntimeException.class)
public class LiveConfigIntegrationTest 
{
  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;
  
  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  @Test
  public void testCreateSupplier() throws Exception
  { 
    assertNotNull("SupplierDaoHibernate not injected", mSupplierDao);
    assertNotNull("DpiFileDaoHibernate not injected", mDpiFileDao);
  }
}

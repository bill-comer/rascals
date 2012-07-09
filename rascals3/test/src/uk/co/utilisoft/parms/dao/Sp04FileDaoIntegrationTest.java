package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Sp04Data;
import uk.co.utilisoft.parms.domain.Sp04File;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.utils.Freeze;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class Sp04FileDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.sp04FileDao")
  private Sp04FileDao mSp04FileDao;

  @Test
  public void createSimpleFileAndSave() throws Exception
  { 
    Freeze.freeze(11, 3, 2010);
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);
    
    ParmsReportingPeriod prp = new ParmsReportingPeriod(10, 2010);
    Sp04File file = new Sp04File("file", supplier, prp);
    MPANCore mpan1 = new MPANCore("1000000000001");
    MPANCore mpan2 = new MPANCore("1000000000002");
    Sp04Data data1 = new Sp04Data("g1", mpan1, 11L, 22L, 33.44F, file);
    Sp04Data data2 = new Sp04Data("g2", mpan2, 11L, 22L, 33.44F, file);
    
    file.getSp04Data().add(data1);
    file.getSp04Data().add(data2);
    
    //test method
    mSp04FileDao.makePersistent(file);
    
    Sp04File retreivedFile = mSp04FileDao.getById(file.getPk());
    assertNotNull(retreivedFile);
    assertEquals(file.getPk(), retreivedFile.getPk());
    assertEquals("fred", retreivedFile.getSupplier().getSupplierId());
    assertEquals(prp, retreivedFile.getReportingPeriod());
    assertEquals(2, retreivedFile.getSp04Data().size());
    
    boolean foundG1 = false, foundG2 = false;
    for (Sp04Data sp04 : retreivedFile.getSp04Data())
    {
      if (sp04.getGspGroupId().equals("g1"))
      {
        foundG1 = true;
        assertEquals("1000000000001", sp04.getMpanCore().getValue());
      }
      if (sp04.getGspGroupId().equals("g2"))
      {
        foundG2 = true;
        assertEquals("1000000000002", sp04.getMpanCore().getValue());
      }
    }
    assertTrue("not found SP04 for G1", foundG1);
    assertTrue("not found SP04 for G2", foundG2);
  }
  

  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;
}

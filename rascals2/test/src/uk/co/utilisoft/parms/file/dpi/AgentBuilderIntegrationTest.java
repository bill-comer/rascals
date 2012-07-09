package uk.co.utilisoft.parms.file.dpi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.AgentDao;
import uk.co.utilisoft.parms.dao.DpiFileDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;


@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class AgentBuilderIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{

  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDao mDpiFileDao;

  @Autowired(required=true)
  @Qualifier("parms.agentDao")
  private AgentDao mAgentDao;

  @Autowired(required=true)
  @Qualifier("parms.agentBuilder")
  private AgentBuilder mBuilder;
  
  @Test
  public void persistingMOPToDB() throws Exception
  { 
    Supplier supplier = new Supplier("SUPP");
    mSupplierDao.makePersistent(supplier);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    
    DpiFile dpifile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpifile);
    
    MOP agentMop = new MOP("mop1", true, dpifile, false);
    agentMop.setMop(true);
    
    mBuilder.mMops.put("mop1", agentMop);
    
    //test method
    mBuilder.saveAllAgents();
    
    List<GenericAgent> agents = mAgentDao.getAllAgents(dpifile);

    
    assertNotNull(agents);
    assertEquals(1, agents.size());
    GenericAgent createdMOP = (GenericAgent)agents.get(0);
    
    MOP expectedMop = new MOP("mop1", true, dpifile, false);
    assertTrue(expectedMop.equals(createdMOP));

  }
  
/*
  @Test
  public void persistingDCToDB() throws Exception
  { 
    Supplier supplier = new Supplier("SUPP");
    mSupplierDao.makePersistent(supplier);
    ParmsReportingPeriod period = new ParmsReportingPeriod(11, 2010);
    
    DpiFile dpifile = new DpiFile(period, supplier);
    mDpiFileDao.makePersistent(dpifile);
    
    DataCollector agentDC = new DataCollector("dc1", true, dpifile);
    agentDC.setMop(false);
    
    mBuilder.mDataCollectors.put("dc1", agentDC);
    
    //test method
    mBuilder.saveAllAgents();
    
    List<GenericAgent> agents = mAgentDao.getAll();
    
    assertNotNull(agents);
    assertEquals(1, agents.size());
    GenericAgent createdDC = (GenericAgent)agents.get(0);
    
    DataCollector expectedDC = new DataCollector("dc1", true, dpifile);
    assertTrue(expectedDC.equals(createdDC));
    
  }*/
}

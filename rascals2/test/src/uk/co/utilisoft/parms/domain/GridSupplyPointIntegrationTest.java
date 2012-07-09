package uk.co.utilisoft.parms.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.AgentDaoHibernate;
import uk.co.utilisoft.parms.dao.DpiFileDaoHibernate;
import uk.co.utilisoft.parms.dao.GridSupplyPointDaoHibernate;
import uk.co.utilisoft.parms.dao.SupplierDaoHibernate;


@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class GridSupplyPointIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  @Autowired(required=true)
  @Qualifier("parms.gridSupplyPointDao")
  private GridSupplyPointDaoHibernate mGridSupplyPointDao;

  @Autowired(required=true)
  @Qualifier("parms.agentDao")
  private AgentDaoHibernate mAgentDao;

  private DpiFile mDpiFile;
  private ParmsReportingPeriod mPeriod = new ParmsReportingPeriod(11, 2010);

  /**
   * creates an agent with a GSP
   * @throws Exception
   */
  @Test
  public void GetAgent() throws Exception
  {
    List<GenericAgent> agents = mAgentDao.getAllAgents(mDpiFile);
    assertNotNull(agents);
    assertEquals(1, agents.size());
    GenericAgent agent = agents.iterator().next();
    assertNotNull(agent);
    assertEquals("MOP", agent.getName());
    assertTrue(agent.isMop());

    //get GSP from agent
    assertNotNull(agent.getGridSupplyPoints());
    assertEquals(1, agent.getGridSupplyPoints().size());
    GridSupplyPoint gspFromAgent = agent.getGridSupplyPoints().iterator().next();
    assertNotNull(gspFromAgent);
    assertEquals("GSP", gspFromAgent.getName());

    //get Agent from GSP
    List<GridSupplyPoint> gsps = mGridSupplyPointDao.getAllGSPsDpi(mDpiFile);
    assertNotNull(gsps);
    assertEquals(1, gsps.size());
    GridSupplyPoint gsp = gsps.iterator().next();
    assertNotNull(gsp);
    assertEquals("GSP", gsp.getName());

    assertNotNull(gsp.getAgent());
    GenericAgent agentFromGsp = gsp.getAgent();
    assertNotNull(agentFromGsp);
    assertEquals("MOP", agentFromGsp.getName());
    assertTrue(agentFromGsp.isMop());
  }

  @Before
  public void prepDatabase()
  {
    Supplier supplier = new Supplier("SUPP");
    supplier.setLastUpdated(new DateTime());
    //mSupplierDao.makePersistent(supplier);
    List<Supplier> suppliers = new ArrayList<Supplier>();
    suppliers.add(supplier);
    insertTestData(suppliers, mSupplierDao, Supplier.class);

    mDpiFile = new DpiFile(mPeriod, supplier);
    mDpiFile.setLastUpdated(new DateTime());
    //mDpiFileDao.makePersistent(mDpiFile);
    List<DpiFile> files = new ArrayList<DpiFile>();
    files.add(mDpiFile);
    insertTestData(files, mDpiFileDao, DpiFile.class);

    MOP mop = new MOP("MOP", true, mDpiFile, false);
    mop.setLastUpdated(new DateTime());
    //mop.getGridSupplyPoints().add(gsp);
    //mAgentDao.makePersistent(mop);
    List<MOP> mops = new ArrayList<MOP>();
    mops.add(mop);
    insertTestData(mops, mAgentDao, MOP.class);

    GridSupplyPoint gsp = new GridSupplyPoint("GSP", mDpiFile);
    gsp.setLastUpdated(new DateTime());
    gsp.setAgent(mop);

    List<GridSupplyPoint> gsps = new ArrayList<GridSupplyPoint>();

    gsps.add(gsp);
    insertTestData(gsps, mGridSupplyPointDao, GridSupplyPoint.class);
  }

  /**
   * Clean up test data.
   */
  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mGridSupplyPointDao, GridSupplyPoint.class);
    deleteTestData(mAgentDao, MOP.class);
    deleteTestData(mDpiFileDao, DpiFile.class);
    deleteTestData(mSupplierDao, Supplier.class);
  }
}
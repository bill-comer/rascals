package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GenericAgentInterface;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class AgentDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.agentDao")
  private AgentDaoHibernate mAgentDao;

  @Autowired(required=true)
  @Qualifier("parms.gridSupplyPointDao")
  private GridSupplyPointDao mGridSupplyPointDao;

  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;


  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  /**
   * Look up by custom hql query Agents for the given DpiFile, Supplier, and Agent Type mop.
   */
  @Test
  public void testCustomHqlReturnsAgentsForDpiFileIdSupplierIdIsMop()
  {
    // insert dummy agent data
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());
    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);
    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);
    MOP mopAgent = new MOP("fred", true, dpiFile, false);
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));
    gsp1.setAgent(mopAgent);
    gsp2.setAgent(mopAgent);
    List<GridSupplyPoint> gsps = new ArrayList<GridSupplyPoint>();
    gsps.add(gsp1);
    gsps.add(gsp2);
    mopAgent.setGridSupplyPoints(gsps);
    assertNotNull(mAgentDao);
    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());
    mAgentDao.makePersistent(mopAgent);
    assertNotNull(mopAgent.getPk());

    // custom hql for agents with dpiFile.pk and agent type mop
    String hqlQuery = "select distinct a from GenericAgent a where a.dpiFile.pk = ? and a.dpiFile.supplier.supplierId = 'fred' and a.mop = true";
    List result = mAgentDao.getHibernateTemplate().find(hqlQuery, dpiFile.getPk());
    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  /**
   * Look up by custom hql query Agents for the given DpiFile, and Agent Type mop.
   */
  @Test
  public void testCustomHqlReturnsAgentsForDpiFileAndIsMop()
  {
    // insert dummy agent data
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());
    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);
    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);
    MOP mopAgent = new MOP("fred", true, dpiFile, false);
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));
    gsp1.setAgent(mopAgent);
    gsp2.setAgent(mopAgent);
    List<GridSupplyPoint> gsps = new ArrayList<GridSupplyPoint>();
    gsps.add(gsp1);
    gsps.add(gsp2);
    mopAgent.setGridSupplyPoints(gsps);
    assertNotNull(mAgentDao);
    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());
    mAgentDao.makePersistent(mopAgent);
    assertNotNull(mopAgent.getPk());

    // custom hql for agents with dpiFile.pk and agent type mop
    String hqlQuery = "select distinct a from GenericAgent a where a.dpiFile.pk = ? and a.mop is true";
    List result = mAgentDao.getHibernateTemplate().find(hqlQuery, dpiFile.getPk());
    assertFalse(result.isEmpty());
    assertTrue(result.size() == 1);
    Object value = result.get(0);
    assertTrue(value instanceof MOP);
    assertTrue(((GenericAgent) value).isMop());
  }

  /**
   * Look up by custom hql query count of Agents for the given DpiFile.
   */
  @Test
  public void testCustomHqlReturnsCountOfAgentsForDpiFile()
  {
    // insert dummy agent data
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());
    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);
    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);
    MOP mopAgent = new MOP("fred", true, dpiFile, false);
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));
    gsp1.setAgent(mopAgent);
    gsp2.setAgent(mopAgent);
    List<GridSupplyPoint> gsps = new ArrayList<GridSupplyPoint>();
    gsps.add(gsp1);
    gsps.add(gsp2);
    mopAgent.setGridSupplyPoints(gsps);
    assertNotNull(mAgentDao);
    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());
    mAgentDao.makePersistent(mopAgent);
    assertNotNull(mopAgent.getPk());

    // custom hql for count of agents
    String hqlQuery = "select count(distinct a) from GenericAgent a where a.dpiFile.pk = ?";
    List result = mAgentDao.getHibernateTemplate().find(hqlQuery, dpiFile.getPk());
    assertFalse(result.isEmpty());
    assertTrue(result.size() == 1);
    Object value = result.get(0);
    assertTrue(value instanceof Long);
    assertTrue(((Long) value).longValue() == 1L);
  }

  /**
   * Look up by custom hql query Agents with GridSupplyPoints for the given DpiFile.
   */
  @Test
  public void testCustomHqlReturnsAgentsWithGspsForDpifile()
  {
    // insert dummy agent data
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());
    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);
    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);
    GridSupplyPoint gsp2 = new GridSupplyPoint("_B", dpiFile);
    MOP mopAgent = new MOP("fred", true, dpiFile, false);
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));
    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));
    gsp1.setAgent(mopAgent);
    gsp2.setAgent(mopAgent);
    List<GridSupplyPoint> gsps = new ArrayList<GridSupplyPoint>();
    gsps.add(gsp1);
    gsps.add(gsp2);
    mopAgent.setGridSupplyPoints(gsps);
    assertNotNull(mAgentDao);
    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());
    mAgentDao.makePersistent(mopAgent);
    assertNotNull(mopAgent.getPk());

    // custom hql for agent and gsps
    String hqlQuery = "select distinct a from GenericAgent a left join fetch a.gridSupplyPoints gsps"
      + " where a.dpiFile.pk = ? order by a.name asc, a.mop desc";
    List agents = mAgentDao.getHibernateTemplate().find(hqlQuery, dpiFile.getPk());
    assertFalse(agents.isEmpty());
    assertFalse(((GenericAgent) agents.get(0)).getGridSupplyPoints().isEmpty());
  }

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testDataCollectorDpiFileIsNullConstraintsValidation()
  {
    DataCollector dcAgent = new DataCollector("fred", true, null, true);
    mAgentDao.makePersistent(dcAgent);
  }

  @Test
  public void testSaveGridSupplyPoints()
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gspa = new GridSupplyPoint("_A", dpiFile);

    DataCollector dcAgent = new DataCollector("fred", true, dpiFile, false);

    gspa.setAgent(dcAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gspa));

    assertNotNull(mAgentDao);
    mAgentDao.makePersistent(dcAgent);
    Long dcAgentPk = dcAgent.getPk();
    assertNotNull(dcAgent.getPk());
    GenericAgent existDCAgent = mAgentDao.getById(dcAgentPk);
    assertNotNull(existDCAgent);
  }

  @Test
  public void testDataCollectorAgentCreation()
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gsp = new GridSupplyPoint("_A", dpiFile);

    DataCollector dcAgent = new DataCollector("fred", true, dpiFile, true);

    gsp.setAgent(dcAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp));

    assertNotNull(mAgentDao);

    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());

    mAgentDao.makePersistent(dcAgent);
    assertNotNull(dcAgent.getPk());

    GenericAgentInterface anoDCAgent = mAgentDao.getById(dcAgent.getPk());
    assertNotNull(anoDCAgent);
    assertEquals(dcAgent.getPk(), anoDCAgent.getPk());
    assertEquals(dcAgent.getName(), anoDCAgent.getName());
    assertEquals(dcAgent.getLastUpdated(), anoDCAgent.getLastUpdated());
    assertEquals(dcAgent.isHalfHourMpans2ndMonth(), anoDCAgent.isHalfHourMpans2ndMonth());
    assertTrue(anoDCAgent.isHalfHourMpans2ndMonth());
    assertEquals(dcAgent.isMop(), anoDCAgent.isMop());
    assertFalse("DCs can not be a MOP", anoDCAgent.isMop());
  }

  @Test
  public void testMOPAgentCreation_isHH_monthTMinus1()
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gsp = new GridSupplyPoint("_A", dpiFile);

    MOP mopAgent = new MOP("fred", true, dpiFile, false);

    gsp.setAgent(mopAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp));

    assertNotNull(mAgentDao);

    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());

    mAgentDao.makePersistent(mopAgent);
    assertNotNull(mopAgent.getPk());

    GenericAgentInterface anoMOPAgent = mAgentDao.getById(mopAgent.getPk());
    assertNotNull(anoMOPAgent);
    assertEquals(mopAgent.getPk(), anoMOPAgent.getPk());
    assertEquals(mopAgent.getName(), anoMOPAgent.getName());
    assertEquals(mopAgent.getLastUpdated(), anoMOPAgent.getLastUpdated());
    assertEquals(mopAgent.isHalfHourMpans2ndMonth(), anoMOPAgent.isHalfHourMpans2ndMonth());
    assertEquals(mopAgent.isHalfHourMpansFirstMonth(), anoMOPAgent.isHalfHourMpansFirstMonth());
    assertTrue(anoMOPAgent.isHalfHourMpansFirstMonth());
    assertFalse(anoMOPAgent.isHalfHourMpans2ndMonth());
    assertEquals(mopAgent.isMop(), anoMOPAgent.isMop());
    assertTrue("MOPs must be true", anoMOPAgent.isMop());
  }

  @Test
  public void testMOPAgentCreation_isHH_monthT()
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gsp = new GridSupplyPoint("_A", dpiFile);

    MOP mopAgent = new MOP("fred", true, dpiFile, true);

    gsp.setAgent(mopAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp));

    assertNotNull(mAgentDao);

    assertNotNull("getHibernateTemplate is null", mAgentDao.getHibernateTemplate());

    mAgentDao.makePersistent(mopAgent);
    assertNotNull(mopAgent.getPk());

    GenericAgentInterface anoMOPAgent = mAgentDao.getById(mopAgent.getPk());
    assertNotNull(anoMOPAgent);
    assertEquals(mopAgent.getPk(), anoMOPAgent.getPk());
    assertEquals(mopAgent.getName(), anoMOPAgent.getName());
    assertEquals(mopAgent.getLastUpdated(), anoMOPAgent.getLastUpdated());
    assertEquals(mopAgent.isHalfHourMpans2ndMonth(), anoMOPAgent.isHalfHourMpans2ndMonth());
    assertEquals(mopAgent.isHalfHourMpansFirstMonth(), anoMOPAgent.isHalfHourMpansFirstMonth());
    assertFalse(anoMOPAgent.isHalfHourMpansFirstMonth());
    assertTrue(anoMOPAgent.isHalfHourMpans2ndMonth());
    assertEquals(mopAgent.isMop(), anoMOPAgent.isMop());
    assertTrue("MOPs must be true", anoMOPAgent.isMop());
  }

  @Test
  public void testInheritance() throws Exception
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);

    MOP mopAgent = new MOP("fred", true, dpiFile, true);
    gsp1.setAgent(mopAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));

    mAgentDao.makePersistent(mopAgent);

    GridSupplyPoint gsp2 = new GridSupplyPoint("_A", dpiFile);

    DataCollector dcAgent = new DataCollector("fred", true, dpiFile, true);
    gsp2.setAgent(dcAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));

    mAgentDao.makePersistent(dcAgent);

    List<GenericAgentInterface> dcs = mAgentDao.getHibernateTemplate().loadAll(DataCollector.class);
    assertEquals("should just be 1 extender",1,  dcs.size());
    assertEquals(dcAgent.getName(), dcs.get(0).getName());
  }

  @Test
  public void testGetDataCollector() throws Exception
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);

    MOP mopAgent = new MOP("fred", true, dpiFile, false);
    gsp1.setAgent(mopAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));

    mAgentDao.makePersistent(mopAgent);

    GridSupplyPoint gsp2 = new GridSupplyPoint("_A", dpiFile);

    DataCollector dcAgent = new DataCollector("fred", false, dpiFile, false);
    gsp2.setAgent(dcAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));

    mAgentDao.makePersistent(dcAgent);

    GenericAgent dc = mAgentDao.getById(dcAgent.getPk());

    assertNotNull(dc);
    assertFalse(dc.isMop());
    assertEquals(dcAgent.getName(), dc.getName());
    assertFalse(dc.isHalfHourMpans2ndMonth());
  }

  @Test
  public void testGetMOP() throws Exception
  {
    Supplier supplier = new Supplier("fred");

    mSupplierDao.makePersistent(supplier);
    assertNotNull(supplier.getPk());

    DpiFile dpiFile = new DpiFile(new ParmsReportingPeriod(new DateMidnight()), supplier);
    dpiFile.setLastUpdated(new DateTime());
    dpiFile.setFileName("aTestFileName");
    mDpiFileDao.makePersistent(dpiFile);

    GridSupplyPoint gsp1 = new GridSupplyPoint("_A", dpiFile);

    MOP mopAgent = new MOP("fred", true, dpiFile, true);
    gsp1.setAgent(mopAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp1));

    mAgentDao.makePersistent(mopAgent);

    GridSupplyPoint gsp2 = new GridSupplyPoint("_A", dpiFile);

    DataCollector dcAgent = new DataCollector("fred", true, dpiFile, false);
    gsp2.setAgent(dcAgent);

    assertNotNull(mGridSupplyPointDao.makePersistent(gsp2));

    mAgentDao.makePersistent(dcAgent);

    //test method
    GenericAgent mop = mAgentDao.getById(mopAgent.getPk());

    assertNotNull(mop);
    assertTrue(mop.isMop());
    assertEquals(dcAgent.getName(), mop.getName());
    assertTrue(mop.isHalfHourMpans2ndMonth());
    assertFalse(mop.isHalfHourMpansFirstMonth());

    assertFalse(dcAgent.isHalfHourMpans2ndMonth());
    assertTrue(dcAgent.isHalfHourMpansFirstMonth());
  }
}

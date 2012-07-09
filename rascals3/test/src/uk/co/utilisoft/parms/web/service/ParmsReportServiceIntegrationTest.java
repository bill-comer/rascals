package uk.co.utilisoft.parms.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.dao.AgentDaoHibernate;
import uk.co.utilisoft.parms.dao.DpiFileDaoHibernate;
import uk.co.utilisoft.parms.dao.GridSupplyPointDaoHibernate;
import uk.co.utilisoft.parms.dao.SupplierDaoHibernate;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.utils.Freeze;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class ParmsReportServiceIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.dpiReportService")
  private DpiReportServiceImpl mParmsReportServiceImpl;
  
  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;
  
  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  @Autowired(required=true)
  @Qualifier("parms.agentDao")
  private AgentDaoHibernate mAgentDao;
  
  @Autowired(required=true)
  @Qualifier("parms.gridSupplyPointDao")
  private GridSupplyPointDaoHibernate mGridSupplyPointDao;
  
  @Test
  public void replicate_with_1MOP_1DC_1GSP_each_monthT() throws Exception
  { 
    boolean isMonthT = true;
    Freeze.freeze(new DateTime(100000000));
    //test data
    Supplier supplier = new Supplier("test");
    mSupplierDao.makePersistent(supplier);
    
    DpiFile srcDpiFile = new DpiFile(new ParmsReportingPeriod(11, 2011), supplier);
    mDpiFileDao.makePersistent(srcDpiFile);
    
    GridSupplyPoint srcGridSupplyPointMop = new GridSupplyPoint("gsp1", srcDpiFile);
    GridSupplyPoint srcGridSupplyPointDC = new GridSupplyPoint("gsp2", srcDpiFile);
    
    MOP srcMop1 = new MOP("mop1", true, srcDpiFile, isMonthT);
    srcMop1.getGridSupplyPoints().add(srcGridSupplyPointMop);
    //srcGridSupplyPointMop.setAgent(srcMop1);
    mAgentDao.makePersistent(srcMop1);

    DataCollector srcDataCollector1 = new DataCollector("dc1", false, srcDpiFile, isMonthT);
    srcDataCollector1.getGridSupplyPoints().add(srcGridSupplyPointDC);
    mAgentDao.makePersistent(srcDataCollector1);
    

    Freeze.thaw();
    //test method
    DpiFile replicatedDpiFile = mParmsReportServiceImpl.replicateDpiFile(srcDpiFile);
    
    //test the Replicated Dpi file
    assertTrue(srcDpiFile.getPk() != replicatedDpiFile.getPk());
    assertNotNull(replicatedDpiFile);
    assertEquals(srcDpiFile.getReportingPeriod(), replicatedDpiFile.getReportingPeriod());
    assertEquals(srcDpiFile.getSupplier(), replicatedDpiFile.getSupplier());
    
    List<GenericAgent> replicatedAgents 
      = mAgentDao.getAllAgents(replicatedDpiFile);
    
    assertEquals(2, replicatedAgents.size());
    
    //get the replicated Mop Agent
    GenericAgent repMopAgent = null;
    for (GenericAgent genericAgent : replicatedAgents)
    {
      if (genericAgent.isMop())
      {
        repMopAgent = genericAgent;
      }
    }
    assertNotNull("ooops - no replicated MOP agent found", repMopAgent);
    assertTrue(srcMop1.getPk() != repMopAgent.getPk());
    assertEquals(srcMop1.getName(), repMopAgent.getName());
    assertTrue(repMopAgent.isMop());
    assertEquals(srcMop1.isHalfHourMpans2ndMonth(), repMopAgent.isHalfHourMpans2ndMonth());
    
    //get the replicated GSP for the MOP
    assertTrue(repMopAgent.getGridSupplyPoints().size() == 1);
    GridSupplyPoint repMopGSP = repMopAgent.getGridSupplyPoints().iterator().next();
    assertTrue(srcGridSupplyPointMop.getPk() != repMopGSP.getPk());
    assertEquals(srcGridSupplyPointMop.getName(), repMopGSP.getName());
    assertEquals(replicatedDpiFile.getPk(), repMopGSP.getDpiFile().getPk());
    
    // get the replicated DC
    GenericAgent repDCAgent = null;
    for (GenericAgent genericAgent : replicatedAgents)
    {
      if (!genericAgent.isMop())
      {
        repDCAgent = genericAgent;
      }
    }
    assertNotNull("ooops - no replicated DC agent found", repDCAgent);

    assertTrue(srcDataCollector1.getPk() != repDCAgent.getPk());
    assertEquals(srcDataCollector1.getName(), repDCAgent.getName());
    assertFalse("replicaed DC should be a DC", repDCAgent.isMop());
    assertEquals(srcDataCollector1.isHalfHourMpans2ndMonth(), repDCAgent.isHalfHourMpans2ndMonth());
    

    //get the replicated GSP for the DC
    assertTrue(repDCAgent.getGridSupplyPoints().size() == 1);
    GridSupplyPoint repDcGSP = repDCAgent.getGridSupplyPoints().iterator().next();
    assertTrue(srcGridSupplyPointDC.getPk() != repDcGSP.getPk());
    assertEquals(srcGridSupplyPointDC.getName(), repDcGSP.getName());
    assertEquals(replicatedDpiFile.getPk(), repDcGSP.getDpiFile().getPk());
    
    
    
  }
  

  /**
   * 2 mops, 2 Dcs, each with 1 & the same GSp
   * IE there should only be 2 new replicated GSPs at the end
   * @throws Exception
   */
  @Test
  public void replicate_with_2MOP_2DC_1GSP_each_monthT() throws Exception
  {
    boolean isMonthT = true;

    Freeze.freeze(new DateTime(100000000));
    //test data
    Supplier supplier = new Supplier("test");
    mSupplierDao.makePersistent(supplier);
    
    DpiFile srcDpiFile = new DpiFile(new ParmsReportingPeriod(11, 2011), supplier);
    mDpiFileDao.makePersistent(srcDpiFile);
    
    GridSupplyPoint srcGridSupplyPointMop1 = new GridSupplyPoint("gspm1", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointMop1);
    GridSupplyPoint srcGridSupplyPointMop2 = new GridSupplyPoint("gspm2", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointMop2);

    GridSupplyPoint srcGridSupplyPointDC1 = new GridSupplyPoint("gspdc1", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointDC1);
    GridSupplyPoint srcGridSupplyPointDC2 = new GridSupplyPoint("gspdc2", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointDC2);
    
    MOP srcMop1 = new MOP("mop1", true, srcDpiFile, isMonthT);
    srcMop1.getGridSupplyPoints().add(srcGridSupplyPointMop1);
    mAgentDao.makePersistent(srcMop1);
    
    MOP srcMop2 = new MOP("mop2", true, srcDpiFile, isMonthT);
    srcMop2.getGridSupplyPoints().add(srcGridSupplyPointMop2);
    mAgentDao.makePersistent(srcMop2);

    DataCollector srcDataCollector1 = new DataCollector("dc1", false, srcDpiFile, isMonthT);
    srcDataCollector1.getGridSupplyPoints().add(srcGridSupplyPointDC1);
    mAgentDao.makePersistent(srcDataCollector1);
    
    DataCollector srcDataCollector2 = new DataCollector("dc2", false, srcDpiFile, isMonthT);
    srcDataCollector2.getGridSupplyPoints().add(srcGridSupplyPointDC2);
    mAgentDao.makePersistent(srcDataCollector2);
    
    Freeze.thaw();
    //test method
    DpiFile replicatedDpiFile = mParmsReportServiceImpl.replicateDpiFile(srcDpiFile);
    
    //test the Replicated Dpi file
    assertTrue(srcDpiFile.getPk() != replicatedDpiFile.getPk());
    assertNotNull(replicatedDpiFile);
    assertEquals(srcDpiFile.getReportingPeriod(), replicatedDpiFile.getReportingPeriod());
    assertEquals(srcDpiFile.getSupplier(), replicatedDpiFile.getSupplier());
    
    List<GenericAgent> replicatedAgents 
      = mAgentDao.getAllAgents(replicatedDpiFile);
    
    assertEquals("should be 4 rep agents",4,  replicatedAgents.size());
    
    //get the replicated Mop Agents
    boolean foundMop1 = false, foundMop2 = false;
    GenericAgent repMopAgent = null;
    for (GenericAgent genericAgent : replicatedAgents)
    {
      if (genericAgent.isMop())
      {
        repMopAgent = genericAgent;

        assertEquals(srcMop1.isHalfHourMpans2ndMonth(), repMopAgent.isHalfHourMpans2ndMonth());
        
        if (repMopAgent.getName().equals("mop1")){
          foundMop1 = true;
          assertTrue(srcMop1.getPk() != repMopAgent.getPk());
        } else if (repMopAgent.getName().equals("mop2")){
          foundMop2 = true;
          assertTrue(srcMop2.getPk() != repMopAgent.getPk());
        }
      }
    }
    assertTrue("not found mop1", foundMop1);
    assertTrue("not found mop2", foundMop2);
    
    //get the replicated GSP for the MOP
    assertTrue(repMopAgent.getGridSupplyPoints().size() == 1);
    GridSupplyPoint repMopGSP = repMopAgent.getGridSupplyPoints().iterator().next();
    
    //assertTrue(srcGridSupplyPointMop.getPk() != repMopGSP.getPk());
    //assertEquals(srcGridSupplyPointMop.getName(), repMopGSP.getName());
    assertEquals(replicatedDpiFile.getPk(), repMopGSP.getDpiFile().getPk());
    
    // get the replicated DC
    boolean foundDc1 = false, foundDc2 = false;
    GenericAgent repDCAgent = null;
    for (GenericAgent genericAgent : replicatedAgents)
    {
      if (!genericAgent.isMop())
      {
        repDCAgent = genericAgent;

        assertEquals(srcDataCollector1.isHalfHourMpans2ndMonth(), repDCAgent.isHalfHourMpans2ndMonth());
        
        if (repDCAgent.getName().equals("dc1")){
          foundDc1 = true;
          assertTrue(srcDataCollector1.getPk() != repDCAgent.getPk());
        } else if (repDCAgent.getName().equals("dc2")){
          foundDc2 = true;
          assertTrue(srcDataCollector2.getPk() != repDCAgent.getPk());
        }
      }
    }
    assertTrue("not found dc1", foundDc1);
    assertTrue("not found dc2", foundDc2);

    //get the replicated GSP for the DC
    assertTrue(repDCAgent.getGridSupplyPoints().size() == 1);
    GridSupplyPoint repDcGSP = repDCAgent.getGridSupplyPoints().iterator().next();
    //assertTrue(srcGridSupplyPointDC.getPk() != repDcGSP.getPk());
    //assertEquals(srcGridSupplyPointDC.getName(), repDcGSP.getName());
    assertEquals(replicatedDpiFile.getPk(), repDcGSP.getDpiFile().getPk());
    
    assertEquals("shud be only 4 GSps for src DPi file", 
        4, mGridSupplyPointDao.getAllGSPsDpi(srcDpiFile).size());
    
    assertEquals("shud be only 4 GSps for replicated DPi file", 
        4, mGridSupplyPointDao.getAllGSPsDpi(replicatedDpiFile).size());
    
  }
  

  @Test
  public void replicate_with_1MOP_1DC_1GSP_Dc_2GSPs_for_mop_monthT() throws Exception
  { 
    boolean isMonthT = true;
    
    Freeze.freeze(new DateTime(100000000));
    //test data
    Supplier supplier = new Supplier("test");
    mSupplierDao.makePersistent(supplier);
    
    DpiFile srcDpiFile = new DpiFile(new ParmsReportingPeriod(11, 2011), supplier);
    mDpiFileDao.makePersistent(srcDpiFile);
    
    GridSupplyPoint srcGridSupplyPointMop1 = new GridSupplyPoint("gsp1_mop1", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointMop1);
    
    GridSupplyPoint srcGridSupplyPointMop2 = new GridSupplyPoint("gsp2_mop1", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointMop2);

    GridSupplyPoint srcGridSupplyPointDC = new GridSupplyPoint("gsp2_dc", srcDpiFile);
    mGridSupplyPointDao.makePersistent(srcGridSupplyPointDC);
    
    MOP srcMop1 = new MOP("mop1", true, srcDpiFile, isMonthT);
    srcMop1.getGridSupplyPoints().add(srcGridSupplyPointMop1);
    srcMop1.getGridSupplyPoints().add(srcGridSupplyPointMop2);
    mAgentDao.makePersistent(srcMop1);

    DataCollector srcDataCollector1 = new DataCollector("dc1", false, srcDpiFile, isMonthT);
    srcDataCollector1.getGridSupplyPoints().add(srcGridSupplyPointDC);
    mAgentDao.makePersistent(srcDataCollector1);
    
    Freeze.thaw();
    //test method
    DpiFile replicatedDpiFile = mParmsReportServiceImpl.replicateDpiFile(srcDpiFile);
    
    //test the Replicated Dpi file
    assertTrue(srcDpiFile.getPk() != replicatedDpiFile.getPk());
    assertNotNull(replicatedDpiFile);
    assertEquals(srcDpiFile.getReportingPeriod(), replicatedDpiFile.getReportingPeriod());
    assertEquals(srcDpiFile.getSupplier(), replicatedDpiFile.getSupplier());
    
    List<GenericAgent> replicatedAgents 
      = mAgentDao.getAllAgents(replicatedDpiFile);
    
    assertEquals(2, replicatedAgents.size());
    
    List<GridSupplyPoint> listOfSrcGSPs =  mGridSupplyPointDao.getAllGSPsDpi(srcDpiFile);
    assertTrue("should be 3 src GSP files", listOfSrcGSPs.size() == 3);
    
    // get the replicated DC
    GenericAgent repDCAgent = null;
    for (GenericAgent genericAgent : replicatedAgents)
    {
      if (!genericAgent.isMop())
      {
        repDCAgent = genericAgent;
      }
    }
    assertNotNull("ooops - no replicated DC agent found", repDCAgent);
    assertTrue(srcDataCollector1.getPk() != repDCAgent.getPk());
    assertFalse(repDCAgent.isMop());
    assertEquals(srcDataCollector1.isHalfHourMpans2ndMonth(), repDCAgent.isHalfHourMpans2ndMonth());
    assertEquals(srcDataCollector1.getName(), repDCAgent.getName());
    

    //get the replicated GSP for the DC
    assertTrue(repDCAgent.getGridSupplyPoints().size() == 1);
    GridSupplyPoint repDcGSP = repDCAgent.getGridSupplyPoints().iterator().next();
    assertTrue(srcGridSupplyPointDC.getPk() != repDcGSP.getPk());
    assertEquals(srcGridSupplyPointDC.getName(), repDcGSP.getName());
    assertEquals(replicatedDpiFile.getPk(), repDcGSP.getDpiFile().getPk());
    
  //get the replicated Mop Agent
    GenericAgent repMopAgent = null;
    for (GenericAgent genericAgent : replicatedAgents)
    {
      if (genericAgent.isMop())
      {
        repMopAgent = genericAgent;
      }
    }
    assertNotNull("ooops - no replicated MOP agent found", repMopAgent);
    assertTrue(srcMop1.getPk() != repMopAgent.getPk());
    assertTrue(repMopAgent.isMop());
    assertEquals(srcMop1.isHalfHourMpans2ndMonth(), repMopAgent.isHalfHourMpans2ndMonth());
    assertEquals(srcMop1.getName(), repMopAgent.getName());
    
    //get the replicated GSP for the MOP
    assertTrue("should be 2 GSPs for replicated MOP", repMopAgent.getGridSupplyPoints().size() == 2);
    GridSupplyPoint repMopGSP = repMopAgent.getGridSupplyPoints().iterator().next();
    assertTrue(srcGridSupplyPointMop1.getPk() != repMopGSP.getPk());
    assertEquals(srcGridSupplyPointMop1.getName(), repMopGSP.getName());
    assertEquals(replicatedDpiFile.getPk(), repMopGSP.getDpiFile().getPk());
    
    List<GridSupplyPoint> allGSPs = mGridSupplyPointDao.getAll();
    assertEquals(new Integer(6), new Integer(allGSPs.size()));
    
    List<GridSupplyPoint> listOfReplicatedGSPs =  mGridSupplyPointDao.getAllGSPsDpi(replicatedDpiFile);
    assertEquals("BUG - this will fail until n:n Agents:GSPs is sorted. Should be 3 replicated GSP files", 
        new Integer(3), new Integer(listOfReplicatedGSPs.size()));
    
  }

}

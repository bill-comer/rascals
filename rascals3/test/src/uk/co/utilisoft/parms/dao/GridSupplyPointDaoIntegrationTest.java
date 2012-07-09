package uk.co.utilisoft.parms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GridSupplyPoint;
import uk.co.utilisoft.parms.domain.MOP;
import uk.co.utilisoft.parms.domain.Supplier;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("unchecked")
public class GridSupplyPointDaoIntegrationTest extends BaseDaoIntegrationTest
{
  @Autowired(required=true)
  @Qualifier("parms.gridSupplyPointDao")
  private GridSupplyPointDaoHibernate mGridSupplyPointDao;

  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.dpiFileDao")
  private DpiFileDaoHibernate mDpiFileDao;

  @Autowired(required=true)
  @Qualifier("parms.agentDao")
  private AgentDaoHibernate agentDao;

  DpiFile mDpiFile;
  DpiFile mDpiFile2;

  @Before
  public void prep()
  {
    Supplier supplier = new Supplier("fred");
    mSupplierDao.makePersistent(supplier);

    Supplier bert = new Supplier("bert");
    mSupplierDao.makePersistent(bert);

    mDpiFile = new DpiFile();
    mDpiFile.setLastUpdated(new DateTime());
    mDpiFile.setFileName("aTestFileName1");
    mDpiFile.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    mDpiFile.setSupplier(supplier);
    mDpiFileDao.makePersistent(mDpiFile);

    mDpiFile2 = new DpiFile();
    mDpiFile2.setLastUpdated(new DateTime());
    mDpiFile2.setFileName("aTestFileName2");
    mDpiFile2.setReportingPeriod(new ParmsReportingPeriod(new DateMidnight()));
    mDpiFile2.setSupplier(bert);
    mDpiFileDao.makePersistent(mDpiFile2);
  }

  @Test
  @ExpectedException(ConstraintViolationException.class)
  public void testGridSupplyPointNameIsNullConstraintsValidation()
  {
    GridSupplyPoint gsp = new GridSupplyPoint(null, null);
    mGridSupplyPointDao.makePersistent(gsp);
  }

  @Test
  public void testGetNOAllGSPsDpi() throws Exception
  {
    assertNotNull(mGridSupplyPointDao);

    List<GridSupplyPoint> listGSPs = mGridSupplyPointDao.getAllGSPsDpi(mDpiFile);
    assertNotNull(listGSPs);
    assertEquals(0, listGSPs.size());
  }


  @Test
  public void testGetAllGSPsDpi() throws Exception
  {
    MOP mop1 = new MOP("mop1",true, mDpiFile, true);
    agentDao.makePersistent(mop1);

    MOP mop2 = new MOP("mop2",true, mDpiFile, true);
    agentDao.makePersistent(mop2);

    GridSupplyPoint gsp1 = new GridSupplyPoint("1_", mDpiFile);
    gsp1.setHalfHourMpans2ndMonth(true);
    gsp1.setAgent(mop1);
    mGridSupplyPointDao.makePersistent(gsp1);

    GridSupplyPoint gsp2 = new GridSupplyPoint("2_", mDpiFile);
    gsp2.setHalfHourMpans2ndMonth(true);
    gsp2.setAgent(mop1);
    mGridSupplyPointDao.makePersistent(gsp2);

    GridSupplyPoint gsp3 = new GridSupplyPoint("3_", mDpiFile);
    gsp3.setHalfHourMpans2ndMonth(true);
    gsp3.setAgent(mop1);
    mGridSupplyPointDao.makePersistent(gsp3);

    GridSupplyPoint gsp4 = new GridSupplyPoint("1_", mDpiFile2);
    gsp4.setHalfHourMpans2ndMonth(true);
    gsp4.setAgent(mop2);
    mGridSupplyPointDao.makePersistent(gsp4);

    List<GridSupplyPoint> listGSPs = mGridSupplyPointDao.getAllGSPsDpi(mDpiFile);
    assertNotNull(listGSPs);
    assertEquals(3, listGSPs.size());
  }
}

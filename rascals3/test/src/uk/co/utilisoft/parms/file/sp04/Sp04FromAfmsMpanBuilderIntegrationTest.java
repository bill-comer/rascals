package uk.co.utilisoft.parms.file.sp04;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.dao.AFMSMeterDaoHibernate;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDaoHibernate;
import uk.co.utilisoft.parms.dao.SupplierDaoHibernate;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.utils.Freeze;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class Sp04FromAfmsMpanBuilderIntegrationTest  extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{

  @Autowired(required=true)
  @Qualifier("parms.sp04FromAFMSMpanDao")
  private Sp04FromAFMSMpanDaoHibernate mSp04FromAFMSMpanDao;


  @Autowired(required=true)
  @Qualifier("parms.sp04FromAfmsMpanBuilder")
  private Sp04FromAfmsMpanBuilder mSp04FromAfmsMpanBuilder;

  @Test
  public void exists()
  {
    insertMpans(9, "777777777777", 1L, "msid1");

    MPANCore mpan1 = new MPANCore("1777777777788");
    assertFalse(mSp04FromAFMSMpanDao.exists(mpan1));


    MPANCore mpan2 = new MPANCore("1777777777777");
    assertTrue(mSp04FromAFMSMpanDao.exists(mpan2));
  }

  @Test
  public void getAll_noneToget() throws Exception
  {
    assertNotNull(mSp04FromAFMSMpanDao.getAll(1L));
    assertEquals(0, mSp04FromAFMSMpanDao.getAll(1L).size());
  }


  @Test
  public void getAll_ThreeToGet() throws Exception
  {
    insertMpans(3, "777777777777", 1L, "msid1");
    assertNotNull(mSp04FromAFMSMpanDao.getAll(1L));
    assertEquals(3, mSp04FromAFMSMpanDao.getAll(1L).size());
  }


  @Test
  public void getAll_ThreeToGet_withExtraSupplier() throws Exception
  {
    insertMpans(3, "777777777777", 1L, "msid1");
    insertMpans(3, "777777777778", 2L, "msid2");
    assertNotNull(mSp04FromAFMSMpanDao.getAll(1L));
    assertEquals(3, mSp04FromAFMSMpanDao.getAll(1L).size());
  }



  /**
   * Insert test data.
   */
  @BeforeTransaction
  public void init()
  {
    Freeze.freeze(10, 8, 2000);
    assertNotNull(mSp04FromAfmsMpanBuilder);
  }

  /**
   * Cleanup test data.
   */
  @AfterTransaction
  public void cleanUp()
  {
    deleteTestData(mSp04FromAFMSMpanDao, Sp04FromAFMSMpan.class);
    Freeze.thaw();
  }

  @After
  public void thaw()
  {
    Freeze.thaw();
  }

  private void insertMpans(int aNumberMpans, String startNumber, Long aSupplierFk, String aMeterId)
  {
    List<Sp04FromAFMSMpan> mpansToSave = new ArrayList<Sp04FromAFMSMpan>();
    for (int i = 0; i <= (aNumberMpans - 1); i++)
    {
      Sp04FromAFMSMpan sp04FromAFMSMpan = new Sp04FromAFMSMpan();
      sp04FromAFMSMpan.setLastUpdated(new DateTime());
      MPANCore mpan = new MPANCore(i + startNumber);
      sp04FromAFMSMpan.setMpan(mpan);
      sp04FromAFMSMpan.setSupplierFk(aSupplierFk);
      sp04FromAFMSMpan.setMeterId(aMeterId);
      sp04FromAFMSMpan.setMpanFk(1L);
      sp04FromAFMSMpan.setCalculatedMeterInstallationDeadline(new DateTime());
      sp04FromAFMSMpan.setDataCollectorFk(1L);
      sp04FromAFMSMpan.setMeterRegisterFks(new Long(1L).toString());
      sp04FromAFMSMpan.setDataCollector("dc");
      mpansToSave.add(sp04FromAFMSMpan);
    }
    insertTestData(mpansToSave, mSp04FromAFMSMpanDao, Sp04FromAFMSMpan.class);
  }

  //test utility Daos
  @Autowired(required=true)
  @Qualifier("parms.supplierDao")
  private SupplierDaoHibernate mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  private AFMSMeterDaoHibernate mAFMSMeterDao;
}

package uk.co.utilisoft.afms.dao;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IAnswer;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;
import uk.co.utilisoft.parms.domain.Supplier;
import uk.co.utilisoft.parms.file.sp04.Sp04FromAfmsMpanBuilder;
import uk.co.utilisoft.parms.web.service.sp04.Sp04ServiceImpl;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class Sp04FromAfmsMpanBuilderLiveDbIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDaoHibernate mAFMSMPanDao;

  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required=true)
  @Qualifier("parms.sp04FromAfmsMpanBuilder")
  private Sp04FromAfmsMpanBuilder mSp04FromAfmsMpanBuilder;

  private AFMSMpanDao mMockAFMSMpanDao = createMock(AFMSMpanDao.class);
  private SupplierDao mMockSupplierDao = createMock(SupplierDao.class);
  private Sp04FromAFMSMpanDao mMockSp04FromAFMSMpanDao = createMock(Sp04FromAFMSMpanDao.class);

  private static final String SUPPLIER_ID = "EBES";

  /**
   * Expect Sp04FromAfmsMpanBuilder.mpan=1100025513718 mpan_uniq_id=177251
   * to be written to PARMS_SP04_FROM_AFMS_MPANS table.
   */
  @Test
  public void getAfmsMpansForSp04Inclusion1()
  {
    Long uniqId = 177251L;
    String mpan = "1100025513718";
    runGetAfmsMpansForSp04Inclusion(uniqId, mpan);
  }

  /**
   * Expect Sp04FromAfmsMpanBuilder.mpan=1100039362147 mpan_uniq_id=177849
   * to be written to PARMS_SP04_FROM_AFMS_MPANS table.
   */
  @Test
  public void getAfmsMpansForSp04Inclusion2()
  {
    Long uniqId = 177849L;
    String mpan = "1100039362147";
    runGetAfmsMpansForSp04Inclusion(uniqId, mpan);
  }

  /**
   * Expect Sp04FromAfmsMpanBuilder.mpan=1100770751195 mpan_uniq_id=181470
   * to be written to PARMS_SP04_FROM_AFMS_MPANS table.
   */
  @Test
  public void getAfmsMpansForSp04Inclusion3()
  {
    Long uniqId = 181470L;
    String mpan = "1100770751195";
    runGetAfmsMpansForSp04Inclusion(uniqId, mpan);
  }

  /**
   * Expect Sp04FromAfmsMpanBuilder.mpan=1200010259024 mpan_uniq_id=177392
   * to be written to PARMS_SP04_FROM_AFMS_MPANS table.
   */
  @Test
  public void getAfmsMpansForSp04Inclusion4()
  {
    Long uniqId = 177392L;
    String mpan = "1200010259024";
    runGetAfmsMpansForSp04Inclusion(uniqId, mpan);
  }

  /**
   * Expect Sp04FromAfmsMpanBuilder.mpan=1200052522015 mpan_uniq_id=177523
   * to be written to PARMS_SP04_FROM_AFMS_MPANS table.
   */
  @Test
  public void getAfmsMpansForSp04Inclusion5()
  {
    Long uniqId = 177523L;
    String mpan = "1200052522015";
    runGetAfmsMpansForSp04Inclusion(uniqId, mpan);
  }

  /**
   * Expect Sp04FromAfmsMpanBuilder.mpan=1200060197571 mpan_uniq_id=177591
   * to be written to PARMS_SP04_FROM_AFMS_MPANS table.
   */
  @Test
  public void getAfmsMpansForSp04Inclusion6()
  {
    Long uniqId = 177591L;
    String mpan = "1200060197571";
    AFMSMpan afmsMpan = mAFMSMPanDao.getById(uniqId);

    if (afmsMpan != null)
    {
      Supplier supplier = mSupplierDao.getSupplier(SUPPLIER_ID);

      if (supplier != null)
      {
        DateTime validEFD = new DateTime(2011, 9, 1, 0, 0, 0, 0);
        DateTime validETD = new DateTime(2010, 9, 1, 0, 0, 0, 0);

        List<AFMSMpan> activeMpans = new ArrayList<AFMSMpan>();
        activeMpans.add(afmsMpan);

        expect(mMockSupplierDao.getById(supplier.getPk())).andReturn(supplier).once();
        expect(mMockAFMSMpanDao.getActiveMpansForLast12Months(supplier, validEFD, validETD, 3))
          .andReturn(activeMpans).once();
        expect(mMockSp04FromAFMSMpanDao.getMpan(new MPANCore(mpan))).andReturn(null).once();

        replay(mMockSupplierDao, mMockAFMSMpanDao, mMockSp04FromAFMSMpanDao);

        // call method
        mSp04FromAfmsMpanBuilder.getAfmsMpansForSp04Inclusion(supplier.getPk());

        verify(mMockSupplierDao, mMockAFMSMpanDao, mMockSp04FromAFMSMpanDao);
      }
      else
      {
        fail("Supplier:" + SUPPLIER_ID + " not found in live database");
      }
    }
    else
    {
      fail("AFMSMpan with uniq_id:" + uniqId + " and J0003:" + afmsMpan + " not found in live database");
    }
  }

  @SuppressWarnings("unchecked")
  private void runGetAfmsMpansForSp04Inclusion(Long aMpanUniqId, String aAfmsMpan)
  {
    AFMSMpan afmsMpan = mAFMSMPanDao.getById(aMpanUniqId);

    if (aAfmsMpan != null)
    {
      Supplier supplier = mSupplierDao.getSupplier(SUPPLIER_ID);

      if (supplier != null)
      {
        DateTime validEFD = new DateTime(2011, 9, 1, 0, 0, 0, 0);
        DateTime validETD = new DateTime(2010, 9, 1, 0, 0, 0, 0);

        List<AFMSMpan> activeMpans = new ArrayList<AFMSMpan>();
        activeMpans.add(afmsMpan);

        expect(mMockSupplierDao.getById(supplier.getPk())).andReturn(supplier).once();
        expect(mMockAFMSMpanDao.getActiveMpansForLast12Months(supplier, validEFD, validETD, 3))
          .andReturn(activeMpans).once();
        expect(mMockSp04FromAFMSMpanDao.exists(new MPANCore(aAfmsMpan))).andReturn(Boolean.FALSE).once();
        mMockSp04FromAFMSMpanDao.makePersistent(isA(Sp04FromAFMSMpan.class));
        expectLastCall().andAnswer(new IAnswer()
        {
          @Override
          public Object answer() throws Throwable
          {
            return null;
          }
        });


        replay(mMockSupplierDao, mMockAFMSMpanDao, mMockSp04FromAFMSMpanDao);

        // call method
        mSp04FromAfmsMpanBuilder.getAfmsMpansForSp04Inclusion(supplier.getPk());

        verify(mMockSupplierDao, mMockAFMSMpanDao, mMockSp04FromAFMSMpanDao);
      }
      else
      {
        fail("Supplier:" + SUPPLIER_ID + " not found in live database");
      }
    }
    else
    {
      fail("AFMSMpan with uniq_id:" + aMpanUniqId + " and J0003:" + aAfmsMpan + " not found in live database");
    }
  }

  /**
   *
   */
  @BeforeTransaction
  public void init()
  {
    assertNotNull(mSupplierDao);
    String supplierId = SUPPLIER_ID;
    assertNotNull(mSupplierDao.getSupplier(supplierId));

    assertNotNull(mAFMSMPanDao);

    Number afmsMpanCount = (Number) mAFMSMPanDao.getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSMpan.class).setProjection(Projections.rowCount()).uniqueResult();
      }
    });

    assertNotNull(afmsMpanCount);

    if (afmsMpanCount.longValue() < 1)
    {
      fail("Failed to find any AFMSMpan records in the live database. Check this test class is configured to run against a live AFMS database");
    }
    else
    {
      // NOTE: Tests should not write to the live sample database.
      // Need to create mockServices and mockDaos inside mSp04FromAfmsMpanBuilder
      mSp04FromAfmsMpanBuilder.setAFMSMPanDao(mMockAFMSMpanDao);
      mSp04FromAfmsMpanBuilder.setSupplierDao(mMockSupplierDao);
      mSp04FromAfmsMpanBuilder.setSp04FromAFMSMpanDao(mMockSp04FromAFMSMpanDao);
      Object sp04ServiceObj = mSp04FromAfmsMpanBuilder.getSp04Service();
      assertEquals(Boolean.TRUE, sp04ServiceObj instanceof Sp04ServiceImpl);
      ((Sp04ServiceImpl) sp04ServiceObj).setSp04FromAFMSMpanDao(mMockSp04FromAFMSMpanDao);
    }
  }
}

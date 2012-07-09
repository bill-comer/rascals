package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.utils.Freeze;

@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class AFMSMeterDaoIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{

  @Autowired(required=true)
  @Qualifier("parms.afmsMeterDao")
  private AFMSMeterDaoHibernate mAFMSMeterDao;

  @Test
  public void GetLatestOfThree() throws Exception
  {
    Freeze.freeze(new DateTime());

    insertMpanWithEffectiveFromDateOneMeter("177777777777");

    //test method
    AFMSMeter meter = mAFMSMeterDao.getLatestMeterForMeterSerialId("aMeterId");

    assertNotNull(meter);
    assertEquals("should have been meter2", new DateTime().plusYears(1), meter.getLastUpdated());

  }


  @Test
  public void GetReturnsNullIfNoneExists() throws Exception
  {
    Freeze.freeze(new DateTime());

    insertMpanWithEffectiveFromDateOneMeter("177777777777");

    //test method
    AFMSMeter meter = mAFMSMeterDao.getLatestMeterForMeterSerialId("bananas");

    assertNull(meter);


    Freeze.thaw();
  }

  private Long insertMpanWithEffectiveFromDateOneMeter(String mpanCore)
  {
    DateTime efd = new DateTime();
    AFMSMpan mpan = new AFMSMpan();
    mpan.setEffectiveFromDate(efd);
    mpan.setMpanCore(mpanCore);
    mpan.setLastUpdated(new DateTime());
    mpan.setSupplierId("fred");
    efd = efd.plusDays(1);

    AFMSMeter meter1 = new AFMSMeter();
    meter1.setLastUpdated(new DateTime());
    meter1.setMpan(mpan);
    meter1.setMeterSerialId("aMeterId");
    mpan.getMeters().add(meter1);
    meter1.setMpanLinkId(111111L);

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setLastUpdated(new DateTime().plusYears(1));
    meter2.setMpan(mpan);
    meter2.setMeterSerialId("aMeterId");
    mpan.getMeters().add(meter2);
    meter2.setMpanLinkId(111111L);

    AFMSMeter meter3 = new AFMSMeter();
    meter3.setLastUpdated(new DateTime().minusYears(1));
    meter3.setMpan(mpan);
    meter3.setMeterSerialId("aMeterId");
    mpan.getMeters().add(meter3);
    meter3.setMpanLinkId(111111L);

    mAFMSMeterDao.getHibernateTemplate().persist(mpan);

    return mpan.getPk();

  }
}

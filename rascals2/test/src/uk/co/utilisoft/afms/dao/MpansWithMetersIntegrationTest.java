package uk.co.utilisoft.afms.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.BaseDaoIntegrationTest;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.utils.Freeze;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class MpansWithMetersIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  private DateMidnight mFirstDateMidnight = new DateMidnight(2021, 12, 1);

  @Autowired(required=true)
  @Qualifier("parms.afmsMpanDao")
  private AFMSMpanDaoHibernate mAFMSMPanDao;

  /**
   * Find an mpan record.
   */
  @Test
  public void getMPanWithMeterAndMeterRegister()
  {
    Freeze.freeze(new DateTime());
    Long createdPk = insertMpanWithEffectiveFromDateOneMeter("177777777777", mFirstDateMidnight.toDateTime(), true);

    AFMSMpan mpan = (AFMSMpan) mAFMSMPanDao.getHibernateTemplate().get(AFMSMpan.class, createdPk);
    assertNotNull(mpan);
    assertNotNull(mpan.getPk());
    assertNotNull(mpan.getMpanCore());
    mpan.getMpanCore().matches("177777777777");

    Collection<AFMSMeter> meters = mpan.getMeters();
    assertNotNull(meters);
    assertTrue(meters.size() == 1);
    AFMSMeter meter = meters.iterator().next();
    assertNotNull(meter);
    assertEquals("aMeterId", meter.getMeterSerialId());
    assertEquals(new DateTime(), meter.getSettlementDate());

    AFMSMeterRegister meterReg = meter.getMeterRegisters().iterator().next();
    assertNotNull(meterReg);
    assertEquals("fred", meterReg.getMeterRegisterId());
    assertEquals("QQ", meterReg.getMeasurementQuantityId());
    assertEquals("C", meterReg.getMeterRegType());

    assertNotNull(meterReg.getMeter());
    assertNotNull(meterReg.getMeter().getMpan());
    assertEquals("177777777777", meterReg.getMeter().getMpan().getMpanCore());

    assertNotNull(meterReg.getMeterRegReadings());
    assertEquals(2, meterReg.getMeterRegReadings().size());

    AFMSMeterRegReading regReading = meterReg.getMeterRegReadings().iterator().next();
    assertNotNull(regReading.getMeterRegister());
    assertEquals("fred", regReading.getMeterRegister().getMeterRegisterId());

  }

  /**
   * Find an mpan record.
   */
  @Test
  public void getMPanWith2Meters()
  {
    Long createdPk = insertMpanWithEffectiveFromDateTwoMeters("177777777778", mFirstDateMidnight.toDateTime());

    AFMSMpan mpan = (AFMSMpan) mAFMSMPanDao.getHibernateTemplate().get(AFMSMpan.class, createdPk);
    assertNotNull(mpan);
    assertNotNull(mpan.getPk());
    assertNotNull(mpan.getMpanCore());
    mpan.getMpanCore().matches("177777777778");

    Collection<AFMSMeter> meters = mpan.getMeters();
    assertNotNull(meters);
    assertEquals(2, meters.size());
    AFMSMeter meter = meters.iterator().next();
    assertNotNull(meter);
    assertEquals("aMeterId", meter.getMeterSerialId());
  }



  private Long insertMpanWithEffectiveFromDateOneMeter(String mpanCore, DateTime aEfd, boolean aHasMeterWithRegisters)
  {
    DateTime efd = aEfd;
    AFMSMpan mpan = new AFMSMpan();
    mpan.setEffectiveFromDate(efd);
    mpan.setMpanCore(mpanCore);
    mpan.setLastUpdated(new DateTime());
    mpan.setSupplierId("fred");
    efd = efd.plusDays(1);

    AFMSMeter meter = new AFMSMeter();
    meter.setLastUpdated(new DateTime());
    meter.setMpan(mpan);
    meter.setMeterSerialId("aMeterId");
    meter.setMpanLinkId(111111L);
    mpan.getMeters().add(meter);

    AFMSMeterRegister meterRegister = new AFMSMeterRegister();
    meterRegister.setEffectiveFromDate(new DateTime());
    meterRegister.setEffectiveToDate(new DateTime());
    meterRegister.setLastUpdated(new DateTime());
    meterRegister.setMeterRegType("C");
    meterRegister.setMeterRegisterId("fred");
    meterRegister.setMeasurementQuantityId("QQ");
    meterRegister.setMeter(meter);

    meter.getMeterRegisters().add(meterRegister);
    meter.setSettlementDate(new DateTime());

    AFMSMeterRegReading regReading = new AFMSMeterRegReading();
    regReading.setLastUpdated(new DateTime());
    regReading.setDateReceived(new DateTime());
    regReading.setMeterReadingDate(new DateTime());
    regReading.setMeterRegister(meterRegister);
    regReading.setRegisterReading(new Float(200.10));
    regReading.setFlowReceived("aFlow");

    AFMSMeterRegReading regReading2 = new AFMSMeterRegReading();
    regReading2.setLastUpdated(new DateTime());
    regReading2.setDateReceived(new DateTime());
    regReading2.setMeterReadingDate(new DateTime());
    regReading2.setMeterRegister(meterRegister);
    regReading2.setRegisterReading(new Float(300.10));
    regReading2.setFlowReceived("2Flow");

    meterRegister.getMeterRegReadings().add(regReading);
    meterRegister.getMeterRegReadings().add(regReading2);

    mAFMSMPanDao.getHibernateTemplate().persist(mpan);

    return mpan.getPk();

  }


  private Long insertMpanWithEffectiveFromDateTwoMeters(String mpanCore, DateTime aEfd)
  {
    DateTime efd = aEfd;
    AFMSMpan mpan = new AFMSMpan();
    mpan.setEffectiveFromDate(efd);
    mpan.setMpanCore(mpanCore);
    mpan.setLastUpdated(new DateTime());
    mpan.setSupplierId("fred");
    efd = efd.plusDays(1);

    AFMSMeter meter = new AFMSMeter();
    meter.setLastUpdated(new DateTime());
    meter.setMpan(mpan);
    meter.setMeterSerialId("aMeterId");
    meter.setMpanLinkId(111111L);

    AFMSMeter meter2 = new AFMSMeter();
    meter2.setLastUpdated(new DateTime());
    meter2.setMpan(mpan);
    meter2.setMeterSerialId("aMeter2Id");
    meter2.setMpanLinkId(111111L);

    mpan.getMeters().add(meter);
    mpan.getMeters().add(meter2);
    mAFMSMPanDao.getHibernateTemplate().persist(mpan);

    return mpan.getPk();

  }
}

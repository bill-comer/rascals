package uk.co.utilisoft.afms.dao;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static uk.co.utilisoft.afms.domain.AFMSMeterRegReading.READING_TYPE;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMeterRegReading;
import uk.co.utilisoft.afms.domain.AFMSMeterRegister;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.dao.SupplierDao;
import uk.co.utilisoft.parms.file.sp04.AFMSMeterRegReadingSortByReadingReverse;
import uk.co.utilisoft.parms.file.sp04.ThreeHighestReadingsAndAverage;

/**
 * @author Philip Lau
 * @version 1.0
 */
@TransactionConfiguration(defaultRollback=true, transactionManager="test.parms.transactionManager")
@Transactional(rollbackFor=RuntimeException.class)
@SuppressWarnings("rawtypes")
public class AFMSMeterLiveDbIntegrationTest extends BaseDaoIntegrationTest<UtilisoftGenericDaoHibernate>
{
  @Autowired(required = true)
  @Qualifier("parms.supplierDao")
  private SupplierDao mSupplierDao;

  @Autowired(required = true)
  @Qualifier("parms.afmsMeterDao")
  private AFMSMeterDaoHibernate mAFMSMeterDao;

  private static final String SUPPLIER_ID = "EBES";

  /**
   *  bug#6375 - get last 12 months meter readings for mpan from old NHH meter registers for a change of NHH Meter
   *  Requires oracle database Y:\DocumentLibrary\SystemDocuments\PARMS\Testing\PHASE 3\1.4.29\22082011\ENHANCEMENT
   */
  @Test
  @SuppressWarnings("unchecked")
  public void getLast12MonthsMeterReadingsForChangeOfNHHMeter()
  {
    final String mpan = "2366510081011";

    List<AFMSMeterRegReading> validReadings = (List<AFMSMeterRegReading>) mAFMSMeterDao.getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        List<AFMSMpan> afmsMpans = (List<AFMSMpan>) aSession.createCriteria(AFMSMpan.class).add(Restrictions.eq("mpanCore", mpan)).list();
        assertTrue(afmsMpans != null && afmsMpans.size() == 1);

        AFMSMpan afmsMpan = afmsMpans.get(0);
        assertNotNull(afmsMpan);

        // afms meters
        List<AFMSMeter> afmsMeters = (List<AFMSMeter>) aSession.createCriteria(AFMSMeter.class)
          .add(Restrictions.eq("mpan.pk", afmsMpan.getPk()))
          .add(Restrictions.or(Restrictions.isNull("effectiveToDateMSID"),
            Restrictions.and(Restrictions.gt("effectiveToDateMSID", new DateTime(2010, 7, 31, 0, 0, 0, 0)),
                             Restrictions.lt("effectiveToDateMSID", new DateTime(2011, 8, 1, 0, 0, 0, 0)))))
          .add(Restrictions.or(Restrictions.in("meterType", Arrays.asList(new String[] {"NCAMR", "RCAMY", "RCAMR"})),
                               Restrictions.isNull("outstationId")))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        assertTrue(afmsMeters != null && afmsMeters.size() == 3);

        List<Long> afmsMeterPks = extract(afmsMeters, on(AFMSMeter.class).getPk());

        // meter_register_type.J0474=M,3, and meter_register.J0103=KW, and meter_register.J0010=MD
        List<AFMSMeterRegister> afmsMeterRegisters = aSession.createCriteria(AFMSMeterRegister.class)
          .createAlias("meter", "afmsMeter")
          .createAlias("afmsMeter.mpan", "afmsMpan")
          .add(Restrictions.eq("afmsMpan.mpanCore", mpan))
          .add(Restrictions.eq("meterRegisterId", "MD"))
          .add(Restrictions.in("meterRegType", Arrays.asList(new String[] {"M", "3"})))
          .add(Restrictions.eq("measurementQuantityId", "KW"))
          .add(Restrictions.in("afmsMeter.pk", afmsMeterPks))
          .add(Restrictions.or(Restrictions.isNull("effectiveToDate"), Restrictions.or(Restrictions
            .gt("effectiveToDate", new DateTime(2010, 7, 31, 0, 0, 0, 0)),
            Restrictions.lt("effectiveToDate", new DateTime(2011, 8, 1, 0, 0, 0, 0)))))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .list();
        assertTrue(afmsMeterRegisters != null && afmsMeterRegisters.size() == 2);

        // afms meter readings
        List<Long> afmsMeterRegisterPks = extract(afmsMeterRegisters, on(AFMSMeterRegister.class).getPk());
        List<AFMSMeterRegReading> readings = aSession.createCriteria(AFMSMeterRegReading.class)
          .createAlias("meterRegister", "afmsMeterRegister")
          .add(Restrictions.in("afmsMeterRegister.meterRegPk", afmsMeterRegisterPks))
          .add(Restrictions.eq("BSCValidationStatus", AFMSMeterRegReading.BSC_VALIDATION_STATUS.V.getValue()))
          .add(Restrictions.and(Restrictions.gt("dateReceived", new DateTime(2010, 7, 31, 0, 0, 0, 0)),
                                Restrictions.lt("dateReceived", new DateTime(2011, 8, 01, 0, 0, 0, 0))))
          .add(Restrictions.not(Restrictions.eq("readingType", READING_TYPE.W.getValue()))) // remove withdrawn readings
          .addOrder(Order.desc("meterReadingDate"))
          .addOrder(Order.desc("registerReading"))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .list();
        assertTrue(readings != null && readings.size() == 18);

        // sort readings by highest reading
        Collections.sort(readings, new AFMSMeterRegReadingSortByReadingReverse());

        // mock calculations
        ThreeHighestReadingsAndAverage results = new ThreeHighestReadingsAndAverage();
        Float total = 0.0F;
        int numFound = 0;
        int minNumberReadings = 3;
        if (readings.size() >= minNumberReadings)
        {
          for (AFMSMeterRegReading value : readings)
          {
            numFound++;
            total+= value.getRegisterReading();
            results.threeHighestReadings.add(value);

            if (numFound >= minNumberReadings)
            {
              break;
            }
          }
        }
        return results.threeHighestReadings;
      }
    });

    assertNotNull(validReadings);
    assertTrue(validReadings != null && validReadings.size() == 3);
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

    assertNotNull(mAFMSMeterDao);

    Number afmsMeterCount = (Number) mAFMSMeterDao.getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSMeter.class).setProjection(Projections.rowCount()).uniqueResult();
      }
    });

    assertNotNull(afmsMeterCount);

    if (afmsMeterCount.longValue() < 1)
    {
      fail("Failed to find any AFMSMeter records in the live database. Check this test class is configured to run "
           + "against a live AFMS database in configuration file test-db.properties");
    }
  }
}
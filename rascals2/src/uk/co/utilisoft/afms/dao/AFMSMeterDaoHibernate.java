package uk.co.utilisoft.afms.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateMidnight;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.afms.domain.AFMSMeter;
import uk.co.utilisoft.afms.domain.AFMSMpan;

@Repository("parms.afmsMeterDao")
public class AFMSMeterDaoHibernate
   extends UtilisoftGenericDaoHibernate<AFMSMeter, Long> implements AFMSMeterDao
{
  @Override
  public AFMSMeter getLatestMeterForMpanUniqId(final Long aMpanUniqId)
  {
    return (AFMSMeter) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public AFMSMeter doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        AFMSMeter meter = null;

        List<AFMSMeter> meters =  aSession.createCriteria(AFMSMeter.class)
          .add(Restrictions.eq("mpan.pk", aMpanUniqId))
          .addOrder(Order.desc("lastUpdated"))
          .list();

        if (meters != null && meters.size() > 0)
        {
          meter = meters.get(0);
        }
        return meter;
      }
    });
  }

  @Override
  public AFMSMeter getLatestMeterForMeterSerialId(final String aMeterSerialId)
  {
    return (AFMSMeter) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public AFMSMeter doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        AFMSMeter meter = null;

        List<AFMSMeter> meters =  aSession.createCriteria(AFMSMeter.class)
          .add(Restrictions.eq("meterSerialId", aMeterSerialId))
          .addOrder(Order.desc("lastUpdated"))
          .list();

        if (meters != null && meters.size() > 0)
        {
          meter = meters.get(0);
        }
        return meter;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMeterDao#getLatestMeterForMeterSerialIdAndMpanUniqId(java.lang.String,
   * java.lang.Long)
   */
  @Override
  @SuppressWarnings("unchecked")
  public AFMSMeter getLatestMeterForMeterSerialIdAndMpanUniqId(final String aMeterSerialId, final Long aMpanUniqId)
  {
    return (AFMSMeter) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        AFMSMeter meter = null;

        List<AFMSMeter> meters = aSession.createCriteria(AFMSMeter.class)
          .add(Restrictions.eq("meterSerialId", aMeterSerialId))
          .add(Restrictions.eq("mpan.pk", aMpanUniqId))
          .addOrder(Order.desc("lastUpdated"))
          .list();

        if (meters != null && meters.size() > 0)
        {
          meter = meters.get(0);
        }

        return meter;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMeterDao#getByMpanUniqueId(java.lang.Long)
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<AFMSMeter> getByMpanUniqueId(final Long aMpanUniqueId)
  {
    return (Collection<AFMSMeter>) getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSMeter.class)
          .add(Restrictions.eq("mpan.pk", aMpanUniqueId))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }

  @Override
  public Collection<AFMSMeter> getNonHalfHourlyMetersForPeriod(final AFMSMpan aAfmsMpan, final DateMidnight endOfValidMeterDateRange)
  {
    Collection<AFMSMeter> allNonHHMeters = new ArrayList<AFMSMeter>();

    Collection<AFMSMeter> allMeters =  getMetersForPeriod(aAfmsMpan, endOfValidMeterDateRange);
    for (AFMSMeter afmsMeter : allMeters)
    {
      // remove HalfHourly meters
      if (afmsMeter.isNonHalfHourlyMeter())
      {
        allNonHHMeters.add(afmsMeter);
      }
    }
    return allNonHHMeters;
  }



  @SuppressWarnings("unchecked")
  public Collection<AFMSMeter> getMetersForPeriod(final AFMSMpan aAfmsMpan, final DateMidnight endOfValidMeterDateRange)
  {
    return (Collection<AFMSMeter>) getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSMeter.class)
          .add(Restrictions.eq("mpan.pk", aAfmsMpan.getPk()))
          .add(Restrictions.or(Restrictions.isNull("effectiveToDateMSID"),
                      Restrictions.ge("effectiveToDateMSID", endOfValidMeterDateRange.toDateTime())))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }
}

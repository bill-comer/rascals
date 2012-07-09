package uk.co.utilisoft.afms.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.afms.domain.AFMSMpan;
import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 *
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.afmsMpanDao")
public class AFMSMpanDaoHibernate extends UtilisoftGenericDaoHibernate<AFMSMpan, Long> implements AFMSMpanDao
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMpanDao#getSupplierIdsForTwoMonths(uk.co.utilisoft.parms.ParmsReportingPeriod)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<String> getSupplierIdsForTwoMonths(final ParmsReportingPeriod aTimeperiod)
  {
    List<String> results = (List<String>) getHibernateTemplate().executeFind(
        new HibernateCallback()
        {
          /**
           * @see org.springframework.orm.hibernate3.HibernateCallback
           *      #doInHibernate(org.hibernate.Session)
           */
          public List<DpiFile> doInHibernate(Session aSession)
                                 throws HibernateException, SQLException
          {
            Query query = aSession.getNamedQuery("getSupplierIDsForParmsMonth");
            //query.setDate("start",aTimeperiod.getStartOfFirstMonthInPeriod().toDate());
            query.setDate("end",aTimeperiod.getNextReportingPeriod().getNextReportingPeriod().getStartOfFirstMonthInPeriod().toDate());

            return (List<DpiFile>)query.list();
          }
        });

    return results;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMpanDao#getActiveMpans(org.joda.time.DateTime)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<AFMSMpan> getActiveMpans(final ParmsReportingPeriod aYearMonthTimePeriod,
      final Supplier aSupplier, final boolean isMonthT)
  {
    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        return aSession.createCriteria(AFMSMpan.class)
          .add(Restrictions.or(Restrictions.isNull("effectiveToDate"),
              Restrictions.ge("effectiveToDate", aYearMonthTimePeriod.getStartOfMonth(isMonthT).toDateTime())))
          .add(Restrictions.lt("effectiveFromDate", aYearMonthTimePeriod.getEndOfMonth(isMonthT).toDateTime()))
          .add(Restrictions.or(Restrictions.isNull("regiStatus"), Restrictions.ne("regiStatus", 2L) ))
          .add(Restrictions.or(Restrictions.isNull("regiStatus"), Restrictions.ne("regiStatus", 4L) ))
          .add(Restrictions.eq("supplierId", aSupplier.getSupplierId()))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMpanDao#getActiveMpansForLast12Months(uk.co.utilisoft.parms.domain.Supplier,
   * org.joda.time.DateTime, org.joda.time.DateTime, java.lang.Integer)
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<AFMSMpan> getActiveMpansForLast12Months(final Supplier aSupplier,
                                                      final DateTime aValidEffectiveFromDate,
                                                      final DateTime aValidStartOf12MonthMonitoringDate,
                                                      final Integer aMinimumNoOfMonthsInSupply)
  {
    return getHibernateTemplate().executeFind(new HibernateCallback()
    {

      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        // bug#5817 the EFD=J0049 can be < 12 months, but mpan needs to have been in supply for at least 3 months
        int maxMonitorPeriodMonths = 12;
        DateTime minimumStartOfMonitoringPeriod = getMinimumStartOfMonitoringPeriod(aValidStartOf12MonthMonitoringDate,
                                                                                    maxMonitorPeriodMonths,
                                                                                    aMinimumNoOfMonthsInSupply);

        return aSession.createCriteria(AFMSMpan.class)
          .add(Restrictions.or(Restrictions.isNull("effectiveToDate"),
              Restrictions.ge("effectiveToDate", aValidStartOf12MonthMonitoringDate)))
          .add(Restrictions.lt("effectiveFromDate", minimumStartOfMonitoringPeriod))
          .add(Restrictions.or(Restrictions.isNull("regiStatus"), Restrictions.ne("regiStatus", 2L) ))  // 2 == Registration terminated - Rejected by MPAS
          .add(Restrictions.or(Restrictions.isNull("regiStatus"), Restrictions.ne("regiStatus", 4L) ))  // 4 == Registration terminated - Objection upheld
          .add(Restrictions.eq("supplierId", aSupplier.getSupplierId()))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }

  /**
   * @param aValidStartOfMonitoringPeriod the point when an mpan is monitored
   * @param aNoMonthsInMonitoringPeriod the maximum number of months an mpan is monitored
   * @param aMinimumNoOfMonthsMpanInSupply  the number of months an mpan needs to be in supply to allow for monitoring
   * @return
   */
  DateTime getMinimumStartOfMonitoringPeriod(DateTime aValidStartOfMonitoringPeriod, Integer aNoMonthsInMonitoringPeriod,
                                             Integer aMinimumNoOfMonthsMpanInSupply)
  {
    // bug#5817 the EFD=J0049 can be < 12 months, but mpan needs to have been in supply for at least 3 months
    DateTime minimumStartOfMonitoringPeriod = null;

    if (aNoMonthsInMonitoringPeriod != null)
    {
      Integer startOfMonitoringPeriodMonthOffset = aNoMonthsInMonitoringPeriod - aMinimumNoOfMonthsMpanInSupply;

      if (startOfMonitoringPeriodMonthOffset > 0)
      {
        minimumStartOfMonitoringPeriod = aMinimumNoOfMonthsMpanInSupply != null
          ? aValidStartOfMonitoringPeriod.plusMonths(startOfMonitoringPeriodMonthOffset)
          : aValidStartOfMonitoringPeriod;
      }
      else
      {
        minimumStartOfMonitoringPeriod = aValidStartOfMonitoringPeriod;
      }
    }

    return minimumStartOfMonitoringPeriod;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMpanDao#getAfmsMpan(uk.co.utilisoft.parms.MPANCore, uk.co.utilisoft.parms.domain.Supplier,
   * uk.co.utilisoft.parms.ParmsReportingPeriod)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AFMSMpan getAfmsMpan(final MPANCore mpanCore, final Supplier supplier,
      final ParmsReportingPeriod aYearMonthTimePeriod)
  {
    return (AFMSMpan) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public AFMSMpan doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        DateTime efdRef = aYearMonthTimePeriod.getStartOfMonth(false).plusMonths(1);
        DateTime etdRef = aYearMonthTimePeriod.getStartOfMonth(false).plusMonths(1);

        List<AFMSMpan> mpans = aSession.createCriteria(AFMSMpan.class)
        /*
         * ETD & EFD are a bit arbitary at the moment.
         */
          .add(Restrictions.lt("effectiveFromDate", efdRef))
          .add(Restrictions.or(Restrictions.isNull("effectiveToDate"),
              Restrictions.ge("effectiveToDate", etdRef)))
          .add(Restrictions.eq("supplierId", supplier.getSupplierId()))
          .add(Restrictions.eq("mpanCore", mpanCore.getValue()))
          .addOrder(Order.desc("lastUpdated"))

          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

        if (mpans != null && mpans.size() > 0)
        {
          return mpans.get(0);
        }

        return null;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMpanDao#getAfmsMpan(uk.co.utilisoft.parms.MPANCore,
   * uk.co.utilisoft.parms.domain.Supplier, org.joda.time.DateTime)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AFMSMpan getAfmsMpan(final MPANCore aMpanCore, final Supplier aSupplier,
                              final DateTime aCurrentTime)
  {
    return (AFMSMpan) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public AFMSMpan doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<AFMSMpan> mpans = aSession.createCriteria(AFMSMpan.class)
          .add(Restrictions.lt("effectiveFromDate", aCurrentTime))
          .add(Restrictions.or(Restrictions.isNull("effectiveToDate"),
              Restrictions.ge("effectiveToDate", aCurrentTime)))
          .add(Restrictions.eq("supplierId", aSupplier.getSupplierId()))
          .add(Restrictions.eq("mpanCore", aMpanCore.getValue()))
          .addOrder(Order.desc("lastUpdated"))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

        if (mpans != null && mpans.size() > 0)
        {
          return mpans.get(0);
        }

        return null;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSMpanDao#getAfmsMpan(uk.co.utilisoft.parms.MPANCore,
   * uk.co.utilisoft.parms.domain.Supplier)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AFMSMpan getAfmsMpan(final MPANCore aMpanCore, final Supplier aSupplier)
  {
    return (AFMSMpan) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public AFMSMpan doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<AFMSMpan> mpans = aSession.createCriteria(AFMSMpan.class)
          .add(Restrictions.eq("supplierId", aSupplier.getSupplierId()))
          .add(Restrictions.eq("mpanCore", aMpanCore.getValue()))
          .addOrder(Order.desc("lastUpdated"))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

        if (mpans != null && mpans.size() > 0)
        {
          return mpans.get(0);
        }

        return null;
      }
    });
  }

  /**
   * Method not supported
   *
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#batchMakePersistent(java.util.List)
   */
  @Override
  public List<AFMSMpan> batchMakePersistent(List<AFMSMpan> aEntities)
  {
    throw new UnsupportedOperationException("AFMSMPanDaoHibernate.batchMakePersistent() method not supported");
  }

  /**
   * Method not supported
   *
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#makePersistent(uk.co.formfill.hibernateutils.domain.DomainObject)
   */
  @Override
  public AFMSMpan makePersistent(AFMSMpan aEntity)
  {
    throw new UnsupportedOperationException("AFMSMPanDaoHibernate.makePersistent() method not supported");
  }

  /**
   * Method not supported
   *
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#makeTransient(uk.co.formfill.hibernateutils.domain.DomainObject)
   */
  @Override
  public void makeTransient(AFMSMpan aEntity)
  {
    throw new UnsupportedOperationException("AFMSMPanDaoHibernate.makeTransient() method not supported");
  }
}
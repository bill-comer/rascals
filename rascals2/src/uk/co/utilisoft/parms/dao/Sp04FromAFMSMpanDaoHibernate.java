package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.map.HashedMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Sp04FromAFMSMpan;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.sp04FromAFMSMpanDao")
public class Sp04FromAFMSMpanDaoHibernate extends ParmsGenericDao<Sp04FromAFMSMpan, Long>
    implements Sp04FromAFMSMpanDao
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao#getInReportingPeriod(java.lang.Long, org.joda.time.DateTime)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<Sp04FromAFMSMpan> getInReportingPeriod(final Long aSupplierPk,
                                                     final DateTime aCurrentTime)
  {
    final List<Sp04FromAFMSMpan> resultsIncLostSupply = new ArrayList<Sp04FromAFMSMpan>();

    if (aCurrentTime != null)
    {
      return (List<Sp04FromAFMSMpan>) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
         */
        @Override
        public List<Sp04FromAFMSMpan> doInHibernate(Session aSession) throws HibernateException, SQLException
        {
          //PRP is always for the previous month
          ParmsReportingPeriod currentReportTime = new ParmsReportingPeriod(aCurrentTime.toDateMidnight());
          DateTime reportEfd = currentReportTime.getStartOfMonth(false);
          DateTime reportEtd = currentReportTime.getStartOfMonth(false);

          List<Sp04FromAFMSMpan> allSp04s = aSession.createCriteria(Sp04FromAFMSMpan.class)
            .add(Restrictions.eq("supplierFk", aSupplierPk))
            .add(Restrictions.lt("effectiveFromDate", reportEfd))
            .add(Restrictions.or(Restrictions.isNull("effectiveToDate"), Restrictions.ge("effectiveToDate", reportEtd)))
            .list();

          if (!allSp04s.isEmpty())
          {
            resultsIncLostSupply.addAll(allSp04s);
          }

          return resultsIncLostSupply;
        }
      });
    }

    return resultsIncLostSupply;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao#getByDataCollector(java.lang.Long, java.lang.String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public IterableMap<String, Sp04FromAFMSMpan> getByDataCollector(final Long aSupplierPk,
                                                                  final String aDataCollectorName)
  {
    return (IterableMap<String, Sp04FromAFMSMpan>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public IterableMap<String, Sp04FromAFMSMpan> doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<Sp04FromAFMSMpan> afmsMpans = aSession.createCriteria(Sp04FromAFMSMpan.class)
          .add(Restrictions.eq("supplierFk", aSupplierPk))
          .add(Restrictions.eq("dataCollector", aDataCollectorName))
          .list();

        IterableMap<String, Sp04FromAFMSMpan> afmsMpansMap = new HashedMap<String, Sp04FromAFMSMpan>();
        if (!afmsMpans.isEmpty())
        {
          for (Sp04FromAFMSMpan afmsMpan : afmsMpans)
          {
            afmsMpansMap.put(afmsMpan.getMpan().getValue(), afmsMpan);
          }
        }

        return afmsMpansMap;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao#exists(uk.co.utilisoft.parms.MPANCore)
   */
  @Override
  public boolean exists(MPANCore aMpan)
  {
    Sp04FromAFMSMpan found = getMpan(aMpan);

    if (found != null)
    {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.AgentDao#getAgentById(java.lang.Long)
   */
  @Override
  public Sp04FromAFMSMpan getMpan(final MPANCore mpan)
  {
    return (Sp04FromAFMSMpan) getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(Sp04FromAFMSMpan.class)
        .add(Restrictions.eq("mpan", mpan))
        .uniqueResult();
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao#get(java.lang.Long, org.joda.time.DateTime)
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Sp04FromAFMSMpan> get(final Long aSupplierPk, final DateTime aCurrentDate)
  {
    if (aCurrentDate != null)
    {
      return (List<Sp04FromAFMSMpan>) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
         */
        @Override
        public List<Sp04FromAFMSMpan> doInHibernate(Session aSession) throws HibernateException, SQLException
        {
          //PRP is always for the previous month
          ParmsReportingPeriod currentReportTime = new ParmsReportingPeriod(aCurrentDate.toDateMidnight());
          DateTime reportMid = currentReportTime.getStartOfMonth(false).toDateTime();
          DateTime reportEfd = currentReportTime.getStartOfMonth(false).plusMonths(1);
          DateTime reportEtd = currentReportTime.getStartOfMonth(false).plusMonths(1);

          List <Sp04FromAFMSMpan> results =  aSession.createCriteria(Sp04FromAFMSMpan.class)
          .add(Restrictions.eq("supplierFk", aSupplierPk))
          .add(Restrictions.le("calculatedMeterInstallationDeadline", aCurrentDate))
          .add(Restrictions.lt("calculatedMeterInstallationDeadline", reportMid))
          .add(Restrictions.lt("effectiveFromDate", reportEfd))
          .add(Restrictions.or(Restrictions.isNull("effectiveToDate"), Restrictions.ge("effectiveToDate", reportEtd)))
          .list();

          return results;
        }
      });
    }

    return getAll(aSupplierPk);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao#getAll(java.lang.Long)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<Sp04FromAFMSMpan> getAll(final Long aSupplierPk)
  {
    return (List<Sp04FromAFMSMpan>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public List<Sp04FromAFMSMpan> doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List <Sp04FromAFMSMpan> all =  aSession.createCriteria(Sp04FromAFMSMpan.class)
          .add(Restrictions.eq("supplierFk", aSupplierPk))
          .list();

        return all;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.Sp04FromAFMSMpanDao#delete(java.util.Set)
   */
  @Override
  public Boolean delete(final Set<MPANCore> aMpanToExcludeFromDeletion)
  {
    return (Boolean)getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        Long count = 0L;
        String countHql = "select count(distinct pk) from Sp04FromAFMSMpan";
        String whereHql = "";
        Query countQry = null;

        if (!aMpanToExcludeFromDeletion.isEmpty())
        {
          whereHql = " where mpan not in (:mpanCores)";
          countQry = aSession.createQuery(countHql + whereHql).setParameterList("mpanCores", aMpanToExcludeFromDeletion);
        }
        else
        {
          countQry = aSession.createQuery(countHql);
        }

        count = (Long) countQry.uniqueResult();

        if (count != null)
        {
          if (count > 0)
          {
            String deleteHql = "delete from Sp04FromAFMSMpan";
            Query deleteQry = null;

            if (!aMpanToExcludeFromDeletion.isEmpty())
            {
              deleteQry = aSession.createQuery(deleteHql + whereHql).setParameterList("mpanCores", aMpanToExcludeFromDeletion);
            }
            else
            {
              deleteQry = aSession.createQuery(deleteHql);
            }

            int numberRowsDeleted = deleteQry.executeUpdate();
            return numberRowsDeleted > 0 ? Boolean.TRUE : Boolean.FALSE;
          }

          return Boolean.FALSE;
        }

        return Boolean.FALSE;
      }
    });
  }
}
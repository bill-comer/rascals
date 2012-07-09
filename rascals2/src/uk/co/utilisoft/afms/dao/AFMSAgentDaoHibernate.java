package uk.co.utilisoft.afms.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.parms.ParmsReportingPeriod;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.afmsAgentDao")
public class AFMSAgentDaoHibernate extends AfmsGenericDao<AFMSAgent, Long> implements AFMSAgentDao
{

  @Override
  @SuppressWarnings("unchecked")
  public MultiHashMap<String, AFMSAgent> getDataCollectorAgents(
      final ParmsReportingPeriod reportingPeriod, final boolean isMonthT)
  {
    
    return (MultiHashMap<String, AFMSAgent>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public MultiHashMap<String, AFMSAgent> doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<AFMSAgent> agents = aSession.createCriteria(AFMSAgent.class)
          .add(Restrictions.or(Restrictions.isNull("DCEffectiveToDate"),
              Restrictions.ge("DCEffectiveToDate", reportingPeriod.getStartOfMonth(isMonthT).toDateTime())))
          .add(Restrictions.lt("DCEffectiveFromDate", reportingPeriod.getEndOfMonth(isMonthT).toDateTime()))
          .add(Restrictions.isNotNull("dataCollector"))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .list();

        MultiHashMap<String, AFMSAgent> agentMap = new MultiHashMap<String, AFMSAgent>();
        for (AFMSAgent afmsAgent : agents)
        {
          agentMap.put(afmsAgent.getMpan().getMpanCore(), afmsAgent);
        }
        return agentMap;
      }
    });
  }
  

  @Override
  @SuppressWarnings("unchecked")
  public MultiHashMap<String, AFMSAgent> getMOPAgents(
      final ParmsReportingPeriod reportingPeriod, final boolean isMonthT)
  {
    return (MultiHashMap<String, AFMSAgent>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public MultiHashMap<String, AFMSAgent> doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<AFMSAgent> agents = aSession.createCriteria(AFMSAgent.class)
          //.add(Restrictions.ge("MOEffectiveFromDate", reportingPeriod.getStartOfMonth(isMonthT).toDateTime()))
          .add(Restrictions.or(Restrictions.isNull("MOEffectiveToDate"),
              Restrictions.ge("MOEffectiveToDate", reportingPeriod.getStartOfMonth(isMonthT).toDateTime())))
          .add(Restrictions.lt("MOEffectiveFromDate", reportingPeriod.getEndOfMonth(isMonthT).toDateTime()))
          .add(Restrictions.isNotNull("meterOperator"))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .list();

        MultiHashMap<String, AFMSAgent> agentMap = new MultiHashMap<String, AFMSAgent>();
        for (AFMSAgent afmsAgent : agents)
        {
          agentMap.put(afmsAgent.getMpan().getMpanCore(), afmsAgent);
        }
        return agentMap;
      }
    });
  }

  /**
   * Method not supported
   *
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#batchMakePersistent(java.util.List)
   */
  @Override
  public List<AFMSAgent> batchMakePersistent(List<AFMSAgent> aEntities)
  {
    throw new UnsupportedOperationException("AFMSAgentDaoHibernate.batchMakePersistent() method not supported");
  }

  /**
   * Method not supported
   *
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#makePersistent(uk.co.formfill.hibernateutils.domain.DomainObject)
   */
  @Override
  public AFMSAgent makePersistent(AFMSAgent aEntity)
  {
    throw new UnsupportedOperationException("AFMSAgentDaoHibernate.makePersistent() method not supported");
  }

  /**
   * Method not supported
   *
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#makeTransient(uk.co.formfill.hibernateutils.domain.DomainObject)
   */
  @Override
  public void makeTransient(AFMSAgent aEntity)
  {
    throw new UnsupportedOperationException("AFMSAgentDaoHibernate.makeTransient() method not supported");
  }

}

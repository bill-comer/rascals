package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.agentDao")
public class AgentDaoHibernate extends ParmsGenericDao<GenericAgent, Long> implements AgentDao
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.AgentDao#getAgentById(java.lang.Long)
   */
  @Override
  public GenericAgent getAgentById(final Long aAgentId)
  {
    return (GenericAgent) getHibernateTemplate().execute(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(GenericAgent.class)
        .add(Restrictions.eq("pk", aAgentId))
        .setFetchMode("gridSupplyPoints", FetchMode.JOIN)
        .uniqueResult();
      }
    });
  }

  /**
   * @see uk.co.utilisoft.parms.dao.AgentDao#getAllAgents(uk.co.utilisoft.parms.domain.DpiFile)
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<GenericAgent> getAllAgents(final DpiFile aDpiFile)
  {
    return (List<GenericAgent>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public List<GenericAgent> doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<GenericAgent> agents =  aSession.createCriteria(GenericAgent.class)
          .add(Restrictions.eq("dpiFile", aDpiFile))
          .setFetchMode("gridSupplyPoints", FetchMode.JOIN)
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .addOrder(Order.asc("name"))
          .addOrder(Order.desc("mop"))
          .list();

        return agents;
      }
    });
  }



}

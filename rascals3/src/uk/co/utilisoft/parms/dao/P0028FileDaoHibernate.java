package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Supplier;


/**
 *
 */
@Repository("parms.p0028FileDao")
public class P0028FileDaoHibernate extends ParmsGenericDao<P0028File, Long> implements P0028FileDao
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.P0028FileDao#areThereAnyUploadsNewerThanThis(uk.co.utilisoft.parms.domain.Supplier,
   * java.lang.String, org.joda.time.DateTime)
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean areThereAnyUploadsNewerThanThis(final Supplier aSupplier,
      final String aDcAgentName, final DateTime aP0028Received)
  {
    return (Boolean) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Boolean doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<P0028File> p0028File =  aSession.createCriteria(P0028File.class)
          .add(Restrictions.eq("supplier", aSupplier))
          .add(Restrictions.eq("dcAgentName", aDcAgentName))
          .add(Restrictions.gt("receiptDate", aP0028Received))
          .list();

        if (p0028File.size() > 0)
        {
          return true;
        }
        return false;
      }
    });
  }


}

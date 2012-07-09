package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.P0028FileData;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.p0028FileDataDao")
public class P0028FileDataDaoHibernate extends ParmsGenericDao<P0028FileData, Long> implements P0028FileDataDao
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.P0028FileDataDao#getLatestByP0028FileName(java.lang.String)
   */
  @Override
  public P0028FileData getLatestByP0028FileName(final String aP0028FileName)
  {
    return (P0028FileData) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        Criteria crit = aSession.createCriteria(P0028FileData.class, "pfd1")
          .createAlias("p0028File", "pfd1_pf")
          .add(Restrictions.eq("pfd1_pf.filename", aP0028FileName));

        DetachedCriteria subSelect = DetachedCriteria.forClass(P0028FileData.class, "pfd2")
          .createAlias("p0028File", "pfd2_pf")
          .add(Restrictions.eq("pfd2_pf.filename", aP0028FileName))
          .setProjection(Projections.max("pfd2_pf.dateImported"));

        return crit.add(Property.forName("pfd1_pf.dateImported").in(subSelect)).uniqueResult();
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.P0028FileDataDao#getByP0028FileId(java.lang.Long)
   */
  @Override
  public P0028FileData getByP0028FileId(final Long aP0028FileId)
  {
    return (P0028FileData) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        return aSession.createCriteria(P0028FileData.class)
          .add(Restrictions.eq("p0028File.pk", aP0028FileId)).uniqueResult();
      }
    });
  }
}

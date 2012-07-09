package uk.co.utilisoft.afms.dao;

import java.sql.SQLException;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.afms.domain.AFMSAregiProcess;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.afmsAregiProcessDao")
public class AFMSAregiProcessDaoHibernate extends UtilisoftGenericDaoHibernate<AFMSAregiProcess, Long>
    implements AFMSAregiProcessDao
{
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.afms.dao.AFMSAregiProcessDao#getByMpanUniqueId(java.lang.String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<AFMSAregiProcess> getByMpanUniqueId(final Long aMpanUniqueId)
  {
    return (Collection<AFMSAregiProcess>) getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(AFMSAregiProcess.class)
          .add(Restrictions.eq("mpan.pk", aMpanUniqueId))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }
}

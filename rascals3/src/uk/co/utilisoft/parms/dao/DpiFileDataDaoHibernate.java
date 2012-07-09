package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.DpiFileData;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.dpiFileDataDao")
public class DpiFileDataDaoHibernate extends ParmsGenericDao<DpiFileData, Long> implements DpiFileDataDao
{
  /**
   * @see uk.co.utilisoft.parms.dao.DpiFileDataDao#getByDpiFilePk(java.lang.Integer)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<DpiFileData> getByDpiFilePk(final Long aDpiFilePk)
  {
    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        return aSession.createCriteria(DpiFileData.class)
          .add(Restrictions.eq("dpiFile.pk", aDpiFilePk)).list();
      }
    });
  }
}

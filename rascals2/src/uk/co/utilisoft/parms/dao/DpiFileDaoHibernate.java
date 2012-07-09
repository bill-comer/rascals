package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.dpiFileDao")
public class DpiFileDaoHibernate extends ParmsGenericDao<DpiFile, Long> implements DpiFileDao
{
  @Override
  @SuppressWarnings("unchecked")
  public List<DpiFile> getDpiFilesForSupplier(final Supplier aSupplier)
  {
    List<DpiFile> results = (List<DpiFile>) getHibernateTemplate().executeFind(
        new HibernateCallback()
        {
          /**
           * @see org.springframework.orm.hibernate3.HibernateCallback
           *      #doInHibernate(org.hibernate.Session)
           */
          public List<DpiFile> doInHibernate(Session aSession)
                                 throws HibernateException, SQLException
          {
            Query query = aSession.getNamedQuery("getDpiFilesForSupplier");
            query.setLong("supplier",aSupplier.getPk());

            return (List<DpiFile>)query.list();
          }
        });

    return results;
  }
}

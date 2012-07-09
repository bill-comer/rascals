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
import uk.co.utilisoft.parms.domain.GridSupplyPoint;

@Repository("parms.gridSupplyPointDao")
public class GridSupplyPointDaoHibernate  extends ParmsGenericDao<GridSupplyPoint, Long> implements GridSupplyPointDao
{

  @Override
  @SuppressWarnings("unchecked")
  public List<GridSupplyPoint> getAllGSPsDpi(final DpiFile aDpiFile)
  {
    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        return aSession.createCriteria(GridSupplyPoint.class)
          .setFetchMode("agents", FetchMode.JOIN)
          .add(Restrictions.eq("dpiFile.pk", aDpiFile.getPk()))
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .addOrder(Order.asc("name")).list();
      }
    });
  }


}

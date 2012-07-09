package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.domain.SerialConfiguration;

@Repository("parms.serialConfigDao")
public class SerialConfigDaoHibernate extends ParmsGenericDao<SerialConfiguration, Long> implements SerialConfigDao
{

  @Override
  public List<SerialConfiguration> getAllSupplierSerials()
  {
    return getAllEnabledSupplierSerials(false, false, false, true);
  }

  @Override
  public List<SerialConfiguration> getAllHHMopSerials(boolean isForMonthT)
  {
    boolean isMOP = true;
    boolean isHH = true;

    return getAllEnabledMOPSerials(isMOP, !isMOP, isHH, isForMonthT);
  }

  @Override
  public List<SerialConfiguration> getAllNonHHMopSerials(boolean isForMonthT)
  {
    boolean isMOP = true;
    boolean isHH = false;

    return getAllEnabledMOPSerials(isMOP, !isMOP, isHH, isForMonthT);
  }

  @Override
  public List<SerialConfiguration> getAllHHDCSerials(boolean isForMonthT)
  {
    boolean isMOP = false;
    boolean isHH = true;

    return getAllEnabledDCSerials(isMOP, !isMOP, isHH, isForMonthT);
  }

  @Override
  public List<SerialConfiguration> getAllNonHHDCSerials(boolean isForMonthT)
  {
    boolean isMOP = false;
    boolean isHH = false;

    return getAllEnabledDCSerials(isMOP, !isMOP, isHH, isForMonthT);
  }

  private List<SerialConfiguration> getAllEnabledDCSerials(final boolean isMOP,
      final boolean isDC, final boolean isHH, final boolean isForMonthT)
  {

    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(SerialConfiguration.class)
            .add(Restrictions.eq("enabled", true))
            .add(Restrictions.eq("halfHourly", isHH))
            .add(Restrictions.eq("dataCollector", isDC))
            .add(Restrictions.eq("monthT", isForMonthT))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }

  private List<SerialConfiguration> getAllEnabledMOPSerials(final boolean isMOP, final boolean isDC, final boolean isHH,
                                                         final boolean isForMonthT)
  {

    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        return aSession.createCriteria(SerialConfiguration.class)
            .add(Restrictions.eq("enabled", true))
            .add(Restrictions.eq("halfHourly", isHH))
            .add(Restrictions.eq("mop", isMOP))
            .add(Restrictions.eq("monthT", isForMonthT))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }

  private List<SerialConfiguration> getAllEnabledSupplierSerials(
      final boolean isMOP, final boolean isDC, final boolean isHH,
      final boolean isForMonthT)
  {

    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        return aSession.createCriteria(SerialConfiguration.class)
            .add(Restrictions.eq("enabled", true))
            .add(Restrictions.eq("halfHourly", isHH))
            .add(Restrictions.eq("mop", isMOP))
            .add(Restrictions.eq("dataCollector", isDC))
            .add(Restrictions.eq("monthT", isForMonthT))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      }
    });
  }

}

package uk.co.utilisoft.parmsmop.dao;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parmsmop.domain.ParmsMopReport;
import uk.co.utilisoft.parmsmop.domain.ParmsMopSourceData;


@Repository("parms.mopSourceDataDao")
public class ParmsMopSourceDataDaoImpl extends MOPGenericDao<ParmsMopSourceData, BigInteger> implements ParmsMopSourceDataDao
{

  @Override
  public List<ParmsMopSourceData> getMopReportSourceData(final String aSerial, final DateTime aStartDate, final DateTime aEndDate,
              ParmsMopReport aParmsMopReport)
  {
    return getHibernateTemplate().executeFind(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public Object doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        Criterion startDateBetween = Restrictions.and(Restrictions.ge("startDate", aStartDate), Restrictions.le("startDate", aEndDate));

        Criterion completedDateBetween = Restrictions.and(Restrictions.ge("completedDate", aStartDate), Restrictions.le("completedDate", aEndDate));
        Criterion completedDateNullStartDateBefore = Restrictions.and(Restrictions.isNull("completedDate"), Restrictions.le("startDate", aStartDate));
        Criterion completedDateCriterion = Restrictions.or(completedDateBetween, completedDateNullStartDateBefore);
        
        return aSession.createCriteria(ParmsMopSourceData.class)
          .add(Restrictions.eq("serialType", aSerial))
          .add(Restrictions.or(startDateBetween, completedDateCriterion))
          .list();
      }
    });
  }




}

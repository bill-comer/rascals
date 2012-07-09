package uk.co.utilisoft.parms.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.map.HashedMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.utilisoft.parms.MPANCore;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.DataCollector;
import uk.co.utilisoft.parms.domain.P0028Active;
import uk.co.utilisoft.parms.domain.P0028Data;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 *
 */
@Repository("parms.p0028ActiveDao")
public class P0028ActiveDaoHibernate extends ParmsGenericDao<P0028Active, Long> implements P0028ActiveDao
{

  @Override
  public void storeNewP0028Active(P0028Data aP0028Data, DataCollector aDataCollector, String aDcAgentName)
  {
    P0028Active newP0028Active =
      new P0028Active(aP0028Data.getP0028File().getSupplier(), aDataCollector,  aDcAgentName,
          aP0028Data, aP0028Data.getMaxDemand(), aP0028Data.getP0028File().getReceiptDate(),
          aP0028Data.getMeterSerialId(), aP0028Data.getMpan(), aP0028Data.getReadingDate());

    P0028Active existingP0028Active = getForSupplierAndMpan(aP0028Data.getP0028File().getSupplier(), aP0028Data.getMpan());

    if (existingP0028Active == null)
    {
      makePersistent(newP0028Active);
    }
    else
    {
      // only replace old with new if date is before new date
      if (isNewMeterReadingdateBeforeExisting(aP0028Data, existingP0028Active))
      {
        //delete old one
        makeTransient(existingP0028Active);
        getHibernateTemplate().flush();

        makePersistent(newP0028Active);
      }
    }
  }

  private boolean isNewMeterReadingdateBeforeExisting(P0028Data aP0028Data,
      P0028Active existingP0028Active)
  {
    return aP0028Data.getReadingDate().isBefore(existingP0028Active.getMeterReadingDate());
  }


  /**
   * gets a P0028Active for an Mpan & Supplier
   * @param aSupplier the supplier
   * @param aMpan the mpan
   * @return P0028 Active record
   */
  P0028Active getForSupplierAndMpan(final Supplier aSupplier, final MPANCore aMpan)
  {
    return (P0028Active) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @SuppressWarnings("unchecked")
      @Override
      public P0028Active doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        List<P0028Active> p0028ActiveList =  aSession.createCriteria(P0028Active.class)
          .add(Restrictions.eq("supplier", aSupplier))
          .add(Restrictions.eq("mpanCore", aMpan))
          .list();

        if (p0028ActiveList.size() > 0)
        {
          return p0028ActiveList.get(0);
        }

        return null;
      }
    });
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.P0028ActiveDao#get(uk.co.utilisoft.parms.domain.Supplier, org.joda.time.DateTime)
   */
  @SuppressWarnings("unchecked")
  @Override
  public IterableMap<String, P0028Active> get(final  Supplier aSupplier,
                                              final DateTime aCurrentDate)
  {
    if (aCurrentDate != null)
    {
      return (IterableMap<String, P0028Active>) getHibernateTemplate().execute(new HibernateCallback()
      {
        /**
         * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
         */
        @Override
        public IterableMap<String, P0028Active> doInHibernate(Session aSession) throws HibernateException, SQLException
        {
          //PRP is always for the previous month
          ParmsReportingPeriod prpEnd = new ParmsReportingPeriod(new DateMidnight());

          List<P0028Active> p0028ActiveList =  aSession.createCriteria(P0028Active.class)
            .add(Restrictions.eq("supplier", aSupplier))
            .createAlias("latestP0028Data", "p28data")
            .createAlias("p28data.p0028File", "p28file")
            .add(Restrictions.lt("p28file.reportingPeriod", prpEnd))
            .add(Restrictions.le("p28file.receiptDate", aCurrentDate.minusMonths(3))) // the MID = p0028file.receiptDate plus 3 months, so need to offset now-3months
            .list();

          return mapTransformer(p0028ActiveList);
        }
      });
    }

    return getAllForSupplier(aSupplier);
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.P0028ActiveDao#getAllForSupplier(uk.co.utilisoft.parms.domain.Supplier)
   */
  @SuppressWarnings("unchecked")
  @Override
  public IterableMap<String, P0028Active> getAllForSupplier(final Supplier aSupplier)
  {
    return (IterableMap<String, P0028Active>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public IterableMap<String, P0028Active> doInHibernate(Session aSession) throws HibernateException, SQLException
      {
        //PRP is always for the previous month
        ParmsReportingPeriod prpEnd = new ParmsReportingPeriod(new DateMidnight());

        List<P0028Active> p0028ActiveList =  aSession.createCriteria(P0028Active.class)
          .add(Restrictions.eq("supplier", aSupplier))
          .createAlias("latestP0028Data", "p28data")
          .createAlias("p28data.p0028File", "p28file")
          .add(Restrictions.lt("p28file.reportingPeriod", prpEnd))
          .list();

        return mapTransformer(p0028ActiveList);
      }
    });
  }

  private IterableMap<String, P0028Active> mapTransformer(List<P0028Active> aP0028Actives)
  {
    IterableMap<String, P0028Active> mapOfP0028s = new HashedMap<String, P0028Active>();
    for (P0028Active p0028Active : aP0028Actives)
    {
      mapOfP0028s.put(p0028Active.getMpanCore().getValue(), p0028Active);
    }

    return mapOfP0028s;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.dao.P0028ActiveDao#getForSupplierWithinP28UploadAndMID(
   * uk.co.utilisoft.parms.domain.Supplier)
   */
  @Override
  public IterableMap<String, P0028Active> getForSupplierWithinP28UploadAndMID(final Supplier aSupplier)
  {
    IterableMap<String, P0028Active> newP0028Actives = new HashedMap<String, P0028Active>();
    IterableMap<String, P0028Active> currentP0028Actives = getAllForSupplier(aSupplier);
    MapIterator<String, P0028Active> p0028ActivesIter = currentP0028Actives.mapIterator();
    DateTime startToday = new DateMidnight().toDateTime();

    while (p0028ActivesIter.hasNext())
    {
      p0028ActivesIter.next();
      P0028Active p0028Active = p0028ActivesIter.getValue();
      DateTime receiptTime = p0028Active.getLatestP0028Data().getP0028File().getReceiptDate();
      DateTime midTime = p0028Active.getMeterInstallationDeadline();

      // check within p28 upload and MID
      if (startToday.isAfter(receiptTime.toDateMidnight().minusDays(1).toDateTime())
          && startToday.isBefore(midTime.toDateMidnight().plusDays(1).toDateTime()))
      {
        newP0028Actives.put(p0028Active.getMpanCore().getValue(), p0028Active);
      }
    }

    return newP0028Actives;
  }
}
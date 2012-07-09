package uk.co.utilisoft.afms.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
@Repository("parms.afmsAgentHistoryDao")
public class AFMSAgentHistoryDaoHibernate extends UtilisoftGenericDaoHibernate<AFMSAgentHistory, Long> implements
    AFMSAgentHistoryDao
{

  /**
   * Method not supported
   * 
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#batchMakePersistent(java.util.List)
   */
  @Override
  public List<AFMSAgentHistory> batchMakePersistent(List<AFMSAgentHistory> aEntities)
  {
    throw new UnsupportedOperationException("AFMSAgentHistoryDaoHibernate.batchMakePersistent() method not supported");
  }

  /**
   * Method not supported
   * 
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#makePersistent(uk.co.formfill.hibernateutils.domain.DomainObject)
   */
  @Override
  public AFMSAgentHistory makePersistent(AFMSAgentHistory aEntity)
  {
    throw new UnsupportedOperationException("AFMSAgentHistoryDaoHibernate.makePersistent() method not supported");
  }

  /**
   * Method not supported
   * 
   * @see uk.co.formfill.hibernateutils.dao.UtilisoftGenericDaoHibernate#makeTransient(uk.co.formfill.hibernateutils.domain.DomainObject)
   */
  @Override
  public void makeTransient(AFMSAgentHistory aEntity)
  {
    throw new UnsupportedOperationException("AFMSAgentHistoryDaoHibernate.makeTransient() method not supported");
  }


  @Override
  @SuppressWarnings("unchecked")
  public MultiHashMap<String, AFMSAgentHistory> getAllAgentHistoryScrollableResults(
                                                                                    final ParmsReportingPeriod aYearMonthTimePeriod,
                                                                                    final Supplier aSupplier, 
                                                                                    final boolean isMonthT)
  {

    return (MultiHashMap<String, AFMSAgentHistory>) getHibernateTemplate().execute(new HibernateCallback()
    {
      /**
       * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
       */
      @Override
      public MultiHashMap<String, AFMSAgentHistory> doInHibernate(Session aSession) throws HibernateException,
          SQLException
      {
        String sql = "SELECT AH.* FROM AGENT_HISTORY AH, MPAN WHERE " 
          + " AH.AGENT_ETD > :startDate"
          + " and ah.REASON_FOR_CHANGE != 'OS' and ah.REASON_FOR_CHANGE != 'OB'"
          + " and AH.AGENT_EFD <= :endDate and mpan.supplier_id = :supplierId"
          + " and MPAN.uniq_id = AH.mpan_lnk"
          ;

        SQLQuery query = aSession.createSQLQuery(sql);
        query.addEntity(AFMSAgentHistory.class);
        query.setDate("startDate", aYearMonthTimePeriod.getStartOfMonth(isMonthT).toDate());
        query.setDate("endDate", aYearMonthTimePeriod.getEndOfMonth(isMonthT).toDate());
        query.setString("supplierId", aSupplier.getSupplierId());

        ScrollableResults itemCursor = query.scroll();

        MultiHashMap<String, AFMSAgentHistory> agentMap = new MultiHashMap<String, AFMSAgentHistory>();
        int count = 0;
        while(itemCursor.next())
        {
          AFMSAgentHistory agentHist = (AFMSAgentHistory) itemCursor.get(0);
          agentMap.put(agentHist.getMpan().getMpanCore(), agentHist);

          if (++count % 100 == 0)
          {
            System.out.println("flushing count[" + count + "]");
            aSession.flush();
            aSession.clear();
          }
        }

        return agentMap;
      }
    });
  }

}

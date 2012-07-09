package uk.co.utilisoft.afms.dao;

import org.apache.commons.collections15.multimap.MultiHashMap;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.afms.domain.AFMSAgentHistory;
import uk.co.utilisoft.parms.ParmsReportingPeriod;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface AFMSAgentHistoryDao extends UtilisoftGenericDao<AFMSAgentHistory, Long>
{
  MultiHashMap<String, AFMSAgentHistory> getAllAgentHistoryScrollableResults (
      final ParmsReportingPeriod aYearMonthTimePeriod, 
      final Supplier aSupplier, 
      final boolean isMonthT);

}

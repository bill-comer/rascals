package uk.co.utilisoft.afms.dao;

import org.apache.commons.collections15.multimap.MultiHashMap;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.afms.domain.AFMSAgent;
import uk.co.utilisoft.parms.ParmsReportingPeriod;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface AFMSAgentDao extends UtilisoftGenericDao<AFMSAgent, Long>
{

  /**
   * get a map of all DC agents for a month
   * @param reportingPeriod
   * @param isMonthT TODO
   * @return
   */
  MultiHashMap<String, AFMSAgent> getDataCollectorAgents(ParmsReportingPeriod reportingPeriod, boolean isMonthT);
  

  /**
   * get a map of all DC agents for a month
   * @param reportingPeriod
   * @param isMonthT TODO
   * @return
   */
  MultiHashMap<String, AFMSAgent> getMOPAgents(ParmsReportingPeriod reportingPeriod, boolean isMonthT);
}

package uk.co.utilisoft.parms.dao;

import java.util.List;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.DpiFile;
import uk.co.utilisoft.parms.domain.GenericAgent;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface AgentDao extends UtilisoftGenericDao<GenericAgent, Long>
{
  /**
   * Look up an Agent by identifier.
   *
   * @param aAgentId the Agent identifier
   * @return the Agent
   */
  GenericAgent getAgentById(final Long aAgentId);

  /**
   * Look up all Agents for the given DpiFile.
   *
   * @param aDpiFile the Dpi File
   * @return the agents
   */
  List<GenericAgent> getAllAgents(DpiFile aDpiFile);
}

package uk.co.utilisoft.parms.dao;

import org.joda.time.DateTime;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.P0028File;
import uk.co.utilisoft.parms.domain.Supplier;

/**
 *
 */
public interface P0028FileDao extends UtilisoftGenericDao<P0028File, Long>
{
  /**
   * Returns true if there are already uploads for this Supplier & Dc that are newer than this new one
   *
   * @param supplier the supplier
   * @param dcAgentName the data collector agent name
   * @param aP0028Received the time when the P0028File was received
   * @return true if there are any uploaded records more recent than for the given supplier, data collector,
   * and time received; otherwise, false
   */
  boolean areThereAnyUploadsNewerThanThis(Supplier supplier, String dcAgentName,
      DateTime aP0028Received);
}

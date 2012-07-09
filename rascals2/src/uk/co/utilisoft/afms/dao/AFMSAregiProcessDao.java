package uk.co.utilisoft.afms.dao;

import java.util.Collection;

import uk.co.utilisoft.afms.domain.AFMSAregiProcess;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface AFMSAregiProcessDao
{
  /**
   * @param aMpanUniqueId the Mpan link id
   * @return the AFMSAregiProcess records for an Mpan link id
   */
  Collection<AFMSAregiProcess> getByMpanUniqueId(final Long aMpanUniqueId);
}
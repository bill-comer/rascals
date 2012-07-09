package uk.co.utilisoft.parms.dao;

import uk.co.formfill.hibernateutils.dao.UtilisoftGenericDao;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;

/**
 * @author Philip Lau
 * @version 1.0
 */
public interface ConfigurationParameterDao extends UtilisoftGenericDao<ConfigurationParameter, Long>
{
  /**
   * @param aName the name of the parameter
   * @return the Configuration Parameter
   */
  ConfigurationParameter getByName(ConfigurationParameter.NAME aName);

  /**
   * @return the location to save the uploaded P0028File errors file.
   */
  String getP0028UploadErrorLocation();
}

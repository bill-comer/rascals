package uk.co.utilisoft.parms.web.service;

import java.util.List;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;

/**
 * @author Gareth Morris
 * @version 1.0
 */
public interface ConfigurationParameterService
{
  /**
   * Returns configuration parameters found.
   * @return List<Configuration Parameter> or error as null
   */
  List<ConfigurationParameter> getAllConfigurationParameters();

  /**
   * Persist the configuration parameter.
   * @param aConfigurationParameter the Configuration Parameter
   * @return null or error message.
   */
  String update(ConfigurationParameter aConfigurationParameter);

  /**
   * Delete the configuration parameter object.
   * @param aConfigurationParameter the Configuration Parameter
   * @return null or error message.
   */
  String delete(ConfigurationParameter aConfigurationParameter);

  /**
   * Gets a specific config parameter
   * @param configParamName
   * @return
   */
  ConfigurationParameter getConfigParameterByPk(String configParamName);
}

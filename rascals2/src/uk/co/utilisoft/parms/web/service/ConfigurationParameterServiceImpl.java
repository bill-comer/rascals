package uk.co.utilisoft.parms.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import uk.co.utilisoft.parms.audit.annotation.ParmsAudit;
import uk.co.utilisoft.parms.dao.ConfigurationParameterDao;
import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.web.controller.AdminListDTO;

import static uk.co.utilisoft.parms.domain.Audit.TYPE;

/**
 * @author Gareth Morris
 * @version 1.0
 */
@Service("parms.configurationParameterService")
public class ConfigurationParameterServiceImpl implements ConfigurationParameterService, AdminService
{
  @Autowired(required=true)
  @Qualifier("parms.configurationParameterDao")
  private ConfigurationParameterDao mConfigurationParameterDao;

  /**
   * @see uk.co.utilisoft.parms.web.service.ConfigurationParameterService#getAllConfigurationParameters()
   */
  @Override
  public List<ConfigurationParameter> getAllConfigurationParameters()
  {
    try
    {
      return mConfigurationParameterDao.getAll();
    }
    catch (DataAccessException dae)
    {
      return null;
    }
  }

  /**
   * @see uk.co.utilisoft.parms.web.service.ConfigurationParameterService#update(uk.co.utilisoft.parms.domain.ConfigurationParameter)
   */
  @Override
  @ParmsAudit(auditType = TYPE.ADMIN_PARAM_CHANGE)
  public String update(ConfigurationParameter aConfigurationParameter)
  {
    try
    {
      mConfigurationParameterDao.makePersistent(aConfigurationParameter);
    }
    catch (DataAccessException dae)
    {
      return dae.getCause().getMessage();
    }
    return null;
  }

  /**
   * @see uk.co.utilisoft.parms.web.service.ConfigurationParameterService#delete(uk.co.utilisoft.parms.domain.ConfigurationParameter)
   */
  @Override
  public String delete(ConfigurationParameter aConfigurationParameter)
  {
    try
    {
      mConfigurationParameterDao.makeTransient(aConfigurationParameter);
    }
    catch (DataAccessException dae)
    {
      return dae.getCause().getMessage();
    }
    return null;
  }

  @Override
  public ConfigurationParameter getConfigParameterByPk(String configParamName)
  {
    ConfigurationParameter configParameter = mConfigurationParameterDao.getById(new Long((configParamName)));
    if (configParameter == null || configParameter.getValue().trim().equals(""))
    {
      throw new RuntimeException("The ConfigurationParameter by name [" + configParamName + "] could not be found.");
    }
    
    return configParameter;
  }

  @Override
  public List<AdminListDTO> getAllSortedRecords()
  {
    List<AdminListDTO> listItems = new ArrayList<AdminListDTO>();
    
    List<ConfigurationParameter> params = getAllConfigurationParameters();
    for (ConfigurationParameter configParameter : params)
    {
      List<Object> list = new ArrayList<Object>();
      List<Object> currentObjectList = new ArrayList<Object>();
      currentObjectList.add(configParameter.getName());
      currentObjectList.add(configParameter.getDescription());
      currentObjectList.add(configParameter.getValue());
      
      list.add(configParameter.getPk());
      listItems.add(new AdminListDTO(list, currentObjectList));
    }
    
    return listItems;
  }

}

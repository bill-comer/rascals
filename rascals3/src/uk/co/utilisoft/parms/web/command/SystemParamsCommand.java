package uk.co.utilisoft.parms.web.command;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;

public class SystemParamsCommand
{

  private Boolean mIsNew;
  
  private ConfigurationParameter mConfigurationParameter;
  
  private String mAdminRequestedAction;



  /**
   * @return the isNew.
   */
  public Boolean getIsNew()
  {
    return mIsNew;
  }
  /**
   * @param aIsNew the isNew to set
   */
  public void setIsNew(Boolean aIsNew)
  {
    mIsNew = aIsNew;
  }
  
  
  public ConfigurationParameter getConfigurationParameter()
  {
    return mConfigurationParameter;
  }
  public void setConfigurationParameter(
      ConfigurationParameter aConfigurationParameter)
  {
    this.mConfigurationParameter = aConfigurationParameter;
  }
  

  /**
   * @return the adminRequestedAction.
   */
  public String getAdminRequestedAction()
  {
    return mAdminRequestedAction;
  }
  /**
   * @param aAdminRequestedAction the adminRequestedAction to set
   */
  public void setAdminRequestedAction(String aAdminRequestedAction)
  {
    mAdminRequestedAction = aAdminRequestedAction;
  }
  
  
  
}

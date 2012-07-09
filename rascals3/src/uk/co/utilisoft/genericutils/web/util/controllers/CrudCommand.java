package uk.co.utilisoft.genericutils.web.util.controllers;

import java.util.List;

/**
 * @author comerb
 * @version 1.0
 */
public abstract class CrudCommand
{
  private Boolean mCanCreateNew;

  private Boolean mIsNew;
  
  private List<Object> mIdentifier;

  private String mRequestedAction;
  

  /**
   * Default constructor.
   */
  public CrudCommand()
  { }


  /**
   * @return the canCreateNew
   */
  public Boolean getCanCreateNew()
  {
    return mCanCreateNew;
  }

  /**
   * @param aCanCreateNew the canCreateNew to set
   */
  public void setCanCreateNew(Boolean aCanCreateNew)
  {
    mCanCreateNew = aCanCreateNew;
  }


  /**
   * @return the identifier.
   */
  public List<Object> getIdentifier()
  {
    return mIdentifier;
  }


  /**
   * @param aIdentifier the identifier to set
   */
  public void setIdentifier(List<Object> aIdentifier)
  {
    mIdentifier = aIdentifier;
  }
  

  /**
   * @return the adminRequestedAction.
   */
  public String getRequestedAction()
  {
    return mRequestedAction;
  }
  /**
   * @param aAdminRequestedAction the adminRequestedAction to set
   */
  public void setRequestedAction(String aAdminRequestedAction)
  {
    mRequestedAction = aAdminRequestedAction;
  }

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
  

}

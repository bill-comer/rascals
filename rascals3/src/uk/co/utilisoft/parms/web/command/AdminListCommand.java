package uk.co.utilisoft.parms.web.command;

import java.util.List;

/**
 * @author winstanleyd
 * @version 1.0
 */
public class AdminListCommand
{
  private Boolean mCanCreateNew;
  private List<Object> mIdentifier;
  
 

  /**
   * Default constructor.
   */
  public AdminListCommand()
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
  

}

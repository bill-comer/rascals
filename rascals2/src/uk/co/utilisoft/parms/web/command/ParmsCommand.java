package uk.co.utilisoft.parms.web.command;

import java.io.Serializable;
import java.util.List;

public abstract class ParmsCommand implements Serializable
{
  private static final long serialVersionUID = 4443387717241898667L;

  private List<Object> mIdentifier;
  private String mRequestedAction;
  private String mErrorMessage;


  public List<Object> getIdentifier()
  {
    return mIdentifier;
  }
  public void setIdentifier(List<Object> aIdentifier)
  {
    mIdentifier = aIdentifier;
  }

  public String getRequestedAction()
  {
    return mRequestedAction;
  }
  public void setRequestedAction(String aRequestedAction)
  {
    this.mRequestedAction = aRequestedAction;
  }


  public String getErrorMessage()
  {
    return mErrorMessage;
  }
  public void setErrorMessage(String aErrorMessage)
  {
    mErrorMessage = aErrorMessage;
  }

  /**
   * @return the serialversionuid
   */
  public static long getSerialversionuid()
  {
    return serialVersionUID;
  }

}

package uk.co.utilisoft.parms.web.security;

import java.util.Calendar;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
public class UserRecord
{
  private String mUserName;
  private Calendar mLastActivityTime;

  /**
   * @param aUserName The name of the user.
   * @param aLastActivity When they were last active.
   */
  public UserRecord(String aUserName, Calendar aLastActivity)
  {
    mUserName = aUserName;
    mLastActivityTime = aLastActivity;
  }

  /**
   * Return mUserName.
   * @return .
   */
  public String getUserName()
  {
    return mUserName;
  }

  /**
   * Set a value to parameter mUserName.
   * @param aUserName aUserSesssionsCount.
   */
  public void setUserName(String aUserName)
  {
    this.mUserName = aUserName;
  }

  /**
   * @return the lastActivity.
   */
  public Calendar getLastActivityTime()
  {
    return mLastActivityTime;
  }

  /**
   * @param aLastActivity the lastActivity to set
   */
  public void setLastActivityTime(Calendar aLastActivity)
  {
    mLastActivityTime = aLastActivity;
  }
}
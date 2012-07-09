package uk.co.utilisoft.parms.web.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.ui.logout.LogoutHandler;

import static uk.co.utilisoft.parms.web.controller.WebConstants.LOGGED_ON_USERS_RECORDS;


/**
 * @author Alison Verkroost
 * @version 1.0
 */
public class ParmsLogoutHandler implements LogoutHandler
{

  /**
   * @param aRequest the HTTP request
   * @param aResponse the HTTP resonse
   * @param aAuthentication the current principal details
   *
   * @see org.ParmsLogoutHandler.ui.logout.LogoutHandler
   *      #logout(javax.servlet.http.HttpServletRequest,
   *              javax.servlet.http.HttpServletResponse,
   *              org.acegisecurity.Authentication)
   */
  @SuppressWarnings("unchecked")
  public void logout(HttpServletRequest aRequest,
                     HttpServletResponse aResponse,
                     Authentication aAuthentication)
  {
    ServletContext servletContext
      = aRequest.getSession(true).getServletContext();

    Map<String, UserRecord> loggedOnUsers
      = (HashMap<String, UserRecord>) servletContext
                  .getAttribute(LOGGED_ON_USERS_RECORDS);
    //servletContext.getAttributeNames();
    String logoffName = null;
    if (aAuthentication != null)
    {
      logoffName = aAuthentication.getName();
      if (logoffName != null && loggedOnUsers != null)
      {
        loggedOnUsers.remove(logoffName);
      }
    }
  }

}

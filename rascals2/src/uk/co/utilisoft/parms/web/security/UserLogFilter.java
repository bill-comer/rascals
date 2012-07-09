package uk.co.utilisoft.parms.web.security;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.SpringSecurityFilter;

import static uk.co.utilisoft.parms.web.controller.WebConstants.LOGGED_ON_USERS_RECORDS;


/**
 * @author Alison Verkroost
 * @version 1.0
 */
public class UserLogFilter extends SpringSecurityFilter
{
  private int mOrder;

  /**
   * @param aOrder the order to set
   */
  public void setOrder(int aOrder)
  {
    mOrder = aOrder;
  }

  /**
   * @see org.springframework.core.Ordered#getOrder()
   */
  public int getOrder()
  {
    return mOrder;
  }

  /**
   * @see org.springframework.security.ui.SpringSecurityFilter
   *      #doFilterHttp(javax.servlet.http.HttpServletRequest,
   *                    javax.servlet.http.HttpServletResponse,
   *                    javax.servlet.FilterChain)
   */
  @Override
  @SuppressWarnings("unchecked")
  protected void doFilterHttp(HttpServletRequest aRequest,
                              HttpServletResponse aResponse, FilterChain aChain)
      throws IOException, ServletException
  {
    Authentication authentication
      = SecurityContextHolder.getContext().getAuthentication();
    ServletContext servletContext
      = aRequest.getSession(true).getServletContext();

    // if session has timed out there is no authentication object
    if (authentication != null)
    {
      String thisUser = authentication.getName();

      Map<String, UserRecord> allLoggedOnUsers
        = (HashMap<String, UserRecord>)
          servletContext.getAttribute(LOGGED_ON_USERS_RECORDS);

      if (allLoggedOnUsers == null)
      {
        allLoggedOnUsers = new HashMap<String, UserRecord>();
      }

      UserRecord userRecord = allLoggedOnUsers.get(thisUser);
      Calendar sysdate = Calendar.getInstance();
      if (userRecord == null)
      {
        userRecord = new UserRecord(thisUser, sysdate);
        allLoggedOnUsers.put(thisUser, userRecord);
      }
      else
      {
        userRecord.setLastActivityTime(sysdate);
      }

      servletContext.setAttribute(
        LOGGED_ON_USERS_RECORDS, allLoggedOnUsers);
    }

    aChain.doFilter(aRequest, aResponse);

  }
}

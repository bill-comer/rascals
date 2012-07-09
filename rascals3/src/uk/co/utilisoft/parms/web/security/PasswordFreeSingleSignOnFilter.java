package uk.co.utilisoft.parms.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.Ordered;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
public class PasswordFreeSingleSignOnFilter implements Filter, Ordered
{
  private boolean mUsingPasswordFreeSingleSignOn;
  private int mOrder;

  /**
   * @param aUsingPasswordFreeSingleSignOn
   *        True if no passwords are being used, false otherwise.
   */
  public PasswordFreeSingleSignOnFilter(boolean aUsingPasswordFreeSingleSignOn)
  {
    mUsingPasswordFreeSingleSignOn = aUsingPasswordFreeSingleSignOn;
  }

  /**
   * @param aOrder the order to set
   */
  public void setOrder(int aOrder)
  {
    mOrder = aOrder;
  }

  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig aFilterConfig) throws ServletException
  {
    // No initialisation required.
  }

  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   *                                    javax.servlet.ServletResponse,
   *                                    javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest aServletRequest,
                       ServletResponse aServletResponse,
                       FilterChain aFilterChain)
    throws IOException, ServletException
  {
    aServletRequest.setAttribute("USING_PASSWORD_FREE_SSO",
                                 mUsingPasswordFreeSingleSignOn);
    aFilterChain.doFilter(aServletRequest, aServletResponse);
  }

  /**
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy()
  {
    // No destroy action required
  }

  /**
   * @see org.springframework.core.Ordered#getOrder()
   */
  public int getOrder()
  {
    return mOrder;
  }

}

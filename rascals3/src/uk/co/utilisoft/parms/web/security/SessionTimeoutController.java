package uk.co.utilisoft.parms.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author Alison Verkroost
 * @version 1.0
 */
@Controller("sessionTimeoutController")
public class SessionTimeoutController extends AbstractController
{
  /**
   * @param aRequest The current HttpServletRequest.
   * @param aResponse The current HttpServletResponse.
   * @return The required model and view.
   * @see org.springframework.web.servlet.mvc.AbstractController
   *      #handleRequestInternal(javax.servlet.http.HttpServletRequest,
   *                             javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest aRequest,
                                               HttpServletResponse aResponse)
      throws Exception
  {
    return new ModelAndView("sessionTimeout");
  }
}

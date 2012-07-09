package uk.co.utilisoft.parms.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles the processing for logout.
 *
 * @author Alison Verkroost
 * @version 1.0
 */
@Controller("parms.logoutController")
public class LogoutController 
{
  /**
   * Handles the request to logout by setting the Authentication object
   * to null, invalidating the session and returning the user to the
   * login screen.
   * @param aRequest HttpServletRequest
   * @param aResponse HttpServletResponse
   * @throws Exception if problem occurs
   * @return Login view and a null model
   */
  @RequestMapping(value = "/logout.htm")
  protected ModelAndView logoutRequest(HttpServletRequest aRequest,
      HttpServletResponse aResponse) throws Exception
  {
    /*if (getUseSingleSignon())
    {
      return new ModelAndView("logout");
    }
    else if (getUseNtlmSingleSignon())
    {
      return new ModelAndView("close");
    }
    else*/
    
    {
      return new ModelAndView("parms.login");
    }
  }

}

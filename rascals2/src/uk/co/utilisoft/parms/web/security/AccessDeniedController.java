package uk.co.utilisoft.parms.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alison Verkroost
 * @version 1.0
 */

@Controller("parms.accessDeniedController")
public class AccessDeniedController
{
  /**
   * @param aRequest The current http servlet request.
   * @param aResponse The current http servlet response.
   * @return The desired ModelAndView object.
   * @see org.springframework.web.servlet.mvc.AbstractController
   *      #handleRequestInternal(javax.servlet.http.HttpServletRequest,
   *                             javax.servlet.http.HttpServletResponse)
   * @inheritDoc
   */
  @RequestMapping(value = "/accessDenied.htm")
  protected ModelAndView loginRequest(HttpServletRequest aRequest,
      HttpServletResponse aResponse) throws Exception
  {
    return new ModelAndView("parms.accessDenied");
  }

}

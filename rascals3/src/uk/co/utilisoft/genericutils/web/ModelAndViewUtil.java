package uk.co.utilisoft.genericutils.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author Philip Lau
 * @version 1.0
 */
public final class ModelAndViewUtil
{
  private ModelAndViewUtil()
  { }

  /**
   * Redirect request and load any response message data as a session scope attribute message code with arguments.
   * Messages configured here can be picked up by other pages in the same session.
   *
   * @param aViewName the view name
   * @param aRequest the HttpServletRequest
   * @param aAttributeName the attribute name
   * @param aResponseMessageArguments the response message code arguments
   * @return the ModelAndView
   */
  public static ModelAndView redirectResponseMessage(String aViewName, HttpServletRequest aRequest,
                                                     String aAttributeName, String[] aResponseMessageArguments)
  {
    ModelAndView mav = new ModelAndView("redirect:" + aViewName);
    if (aAttributeName != null && aAttributeName.length() > 0)
    {
      String[] arguments = aResponseMessageArguments != null && aResponseMessageArguments.length > 0
        ? aResponseMessageArguments : new String[] {};

      aRequest.getSession(true).setAttribute(aAttributeName, arguments);
    }
    return mav;
  }
}

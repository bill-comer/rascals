package uk.co.utilisoft.parms.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * Handles the processing for login.
 *
 * @author Alison Verkroost
 * @version 1.0
 */
@Controller("parms.loginController")
public class LoginController
{
  private DateTime jodaTestTime;


  public void setJodaTestTime(String aJodaTestTime) {
    if (StringUtils.isNotEmpty(aJodaTestTime))
    {
      try
      {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime theDate = fmt.parseDateTime(aJodaTestTime);
        jodaTestTime = theDate;
        DateTimeUtils.setCurrentMillisFixed(jodaTestTime.getMillis());
      }
      catch(Exception e) {}
    }
  }


  /**
   * The processing of login is handled by j_acegi_security_check.  This
   * controller simply returns the login page with a null model
   * @param aRequest HttpServletRequest
   * @param aResponse HttpServletResponse
   * @throws Exception if problem occurs
   * @return Login view and a null model
   */
  @RequestMapping(value = "/login.htm")
  protected ModelAndView loginRequest(HttpServletRequest aRequest,
      HttpServletResponse aResponse) throws Exception
  {
    /*if (getUseSingleSignon() || getUseNtlmSingleSignon())
    {
      aResponse.sendRedirect("dpiList.htm");
    }*/

    return new ModelAndView("parms.login");
  }

}


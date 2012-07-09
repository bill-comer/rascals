package uk.co.utilisoft.parms.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import uk.co.utilisoft.parms.domain.ConfigurationParameter;
import uk.co.utilisoft.parms.web.command.SystemParamsCommand;
import uk.co.utilisoft.parms.web.service.ConfigurationParameterService;

/**
 * @author Bill Comer
 * @version 1.0
 */
@Controller("parms.systemParamsEditController")
@SessionAttributes(WebConstants.SYSTEM_PARAMS_COMMAND)
public class SystemParamsEditController 
{
  private static final String mCommandName = WebConstants.SYSTEM_PARAMS_COMMAND;
  
  @Autowired(required=true)
  @Qualifier("parms.configurationParameterService")
  private ConfigurationParameterService mConfigurationParameterService;

  private String mViewName;

  protected MessageSource mMessageSource;

  /**
   * HTTP Get method.  Displays New/Blank or existing systemParams record.
   * @param aRequest HttpServletRequest
   * @return ModelAndView
   * @throws Exception
   */
  @RequestMapping(method = RequestMethod.GET)
  protected ModelAndView formBackingObject(HttpServletRequest aRequest)
    throws Exception
  {
    SystemParamsCommand command = new SystemParamsCommand();
    ModelAndView mav = new ModelAndView(getViewName());
    
    /* if ID is empty, display new/blank system param form. */
    aRequest.getParameterMap();
    if (aRequest.getParameter("id_0").isEmpty())
    {
      command.setIsNew(true);
      mav.addObject(mCommandName, command);
      return mav;
    }
    else /* Display existing record. */
    {
      String configParamName = aRequest.getParameter("id_0");
      command.setIsNew(false);
      ConfigurationParameter configParams = mConfigurationParameterService.getConfigParameterByPk(configParamName);

      command.setConfigurationParameter(configParams);

      mav.addObject(mCommandName, command);
      return mav;
    }
  }

  /**
   * HTTP Post with Save parameter.
   * @param aRequest HttpServletRequest
   * @param aCommand SystemParamsCommand
   * @param aErrors
   * @return ModelAndView
   */
  @RequestMapping(params = "adminRequestedAction" + "=save", method = RequestMethod.POST)
  public ModelAndView saveDetail(
                                 HttpServletRequest aRequest,
                                 @ModelAttribute(mCommandName)
                                 SystemParamsCommand aCommand,
                                 Errors aErrors)
  {
    ConfigurationParameter systemParams = aCommand.getConfigurationParameter();

    mConfigurationParameterService.update(systemParams);

    aCommand.setIsNew(false);

    ModelAndView mav = new ModelAndView(new RedirectView("systemConfigurationList.htm"));
    mav.addObject(mCommandName, aCommand);
    return mav;
  }

  /**
   * Cancel Post Method.
   * Returns the user back to the System Parameter List.
   * @return ModelAndView
   */
  @RequestMapping(params = "adminRequestedAction" + "=cancel", method = RequestMethod.POST)
  public ModelAndView cancel()
  {
    return new ModelAndView(new RedirectView("systemConfigurationList.htm"));
  }
  
  /**
   * HTTP Post with Delete parameter.
   * 
   * @param aRequest HttpServletRequest
   * @param aCommand SystemParamsCommand
   * @param aErrors
   * @return ModelAndView
   */
  @RequestMapping(params = "adminRequestedAction" + "=delete", method = RequestMethod.POST)
  public ModelAndView delete(
                             HttpServletRequest aRequest,
                             @ModelAttribute(mCommandName) SystemParamsCommand aCommand,
                             Errors aErrors)
  {
    ConfigurationParameter object = aCommand.getConfigurationParameter();

    if (object != null)
    {
      mConfigurationParameterService.delete(object);
    }
    ModelAndView mav =
        new ModelAndView(new RedirectView("systemConfigurationList.htm"));
    mav.addObject(mCommandName, aCommand);
    return mav;
  }


  /**
   * @param aMessageSource the messageSource to set
   */
  public void setMessageSource(MessageSource aMessageSource)
  {
    mMessageSource = aMessageSource;
  }

  /**
   * @param aViewName the viewName to set
   */
  public void setViewName(String aViewName)
  {
    mViewName = aViewName;
  }

  /**
   *
   * @return
   */
  public String getViewName()
  {
    return mViewName;
  }

  /**
   * @param aSystemParamsService the systemParamsService to set
  public void setSystemParamsService(ConfigurationParameterService aSystemParamsService)
  {
    mConfigurationParameterService = aSystemParamsService;
  }
   */
}

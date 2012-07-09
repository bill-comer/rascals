package uk.co.utilisoft.parms.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.web.command.ParmsCommand;


/**
 *
 */
public abstract class AbstractParmsController implements ParmsController
{
  private Logger mLogger = Logger.getLogger(getClass());

  private String mControllerName;

  /**
   * @return the controller name
   */
  public String getControllerName()
  {
    return mControllerName;
  }
  /**
   * @param aControllerName the controllerName to set
   */
  public void setControllerName(String aControllerName)
  {
    mControllerName = aControllerName;
  }

  private int mNumberOfIdentifiers;

  /**
   * @return the number of identifiers
   */
  public int getNumberOfIdentifiers()
  {
    return mNumberOfIdentifiers;
  }
  /**
   * @param aNumberOfIdentifiers the numberOfIdentifiers to set
   */
  public void setNumberOfIdentifiers(int aNumberOfIdentifiers)
  {
    mNumberOfIdentifiers = aNumberOfIdentifiers;
  }


  private String mViewName;
  /**
   * @param aViewName the viewName to set
   */
  public void setViewName(String aViewName)
  {
    mViewName = aViewName;
  }

  /**
   *
   * @return the view name
   */
  public String getViewName()
  {
    return mViewName;
  }

  protected MessageSource mMessageSource;
  /**
   * @param aMessageSource the messageSource to set
   */
  public void setMessageSource(MessageSource aMessageSource)
  {
    mMessageSource = aMessageSource;
  }

  private String mObjectName;

  public String getObjectName()
  {
    return mObjectName;
  }
  public void setObjectName(String aObjectName)
  {
    this.mObjectName = aObjectName;
  }


  private List validParamPatterns;
  public void setValidParamPatterns(List aValidParamPatterns)
  {
    this.validParamPatterns = aValidParamPatterns;
  }
  public List getValidParamPatterns()
  {
    return validParamPatterns;
  }

  private String mOtherReportName;
  public String getOtherReportName()
  {
    return mOtherReportName;
  }
  public void setOtherReportName(String aOtherReportName)
  {
    this.mOtherReportName = aOtherReportName;
  }


  private List<String> mColumnMessageCodes;
  @ModelAttribute("columnMessageCodes")
  public List<String> columnMessageCodes()
  {
    return mColumnMessageCodes;
  }
  /**
   * @param aColumnMessageCodes the columnMessageCodes to set
   */
  public void setColumnMessageCodes(List<String> aColumnMessageCodes)
  {
    mColumnMessageCodes = aColumnMessageCodes;
  }

  private List<Integer> mColumnWidths;
  @ModelAttribute("columnWidths")
  public List<Integer> columnWidths()
  {
    return mColumnWidths;
  }
  /**
   * @param aColumnWidths the columnWidths to set
   */
  public void setColumnWidths(List<Integer> aColumnWidths)
  {
    mColumnWidths = aColumnWidths;
  }


  /**
   * Form Backing Object Method (get).
   *
   * @param aRequest HttpServletRequest object.
   * @return ModelAndView for the initial load of the page.
   * @throws Exception Exception thrown if an error occurs.
   */
  @RequestMapping(method = RequestMethod.GET)
  public Object formBackingObject(HttpServletRequest aRequest) throws Exception
  {
    aRequest.setAttribute("title", getObjectName() + " List");
    ParmsCommand command = getNewCommand();

    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < getNumberOfIdentifiers(); i++)
    {
      list.add(null);
    }
    command.setIdentifier(list);

    ModelAndView mav = new ModelAndView(getViewName());
    mav.addObject(command);
    return mav;
  }

  public ParmsCommand getNewCommand()
  {
    throw new UnsupportedOperationException("This method must be overwritten");
  }

  /**
   *
   * @return String
   */
  public String getReportActionUrl(String aRequestedAction)
  {
    return getObjectName() + aRequestedAction + ".htm";
  }


  /**
   *
   * @return boolean
   */
  private boolean allCapitals()
  {
    int charCount = mControllerName.length();
    for (int i = 0; i < charCount; i++)
    {
      if (Character.isLowerCase(mControllerName.charAt(i)))
      {
        return false;
      }
    }
    return true;
  }

  public ModelAndView abstractOnSubmit(HttpServletRequest aRequest,
      ParmsCommand aCommand)
  {
    mLogger.debug("Entered onSubmit() for: " + getControllerName());
    aRequest.setAttribute("title", getObjectName() + " List");

    ModelAndView mav = new ModelAndView(new RedirectViewIdParametersOnly(getReportActionUrl(aCommand.getRequestedAction()), getValidParamPatterns()));
    ParmsCommand command = (ParmsCommand) aCommand;
    for (int i = 0; i < command.getIdentifier().size(); i++)
    {
      mav.addObject("id_" + i, command.getIdentifier().get(i));
    }
    return mav;
  }

  public BindException getError(ParmsCommand aCommand, String aCommandName, String aErrorMsgCode,
                                String ... aErrorMsgArgs)
  {
    BindException error = null;

    error = new BindException(aCommand, aCommandName);
    Object[] errMsgArgs = aErrorMsgArgs != null && aErrorMsgArgs.length > 0 ? aErrorMsgArgs : null;
    error.reject(aErrorMsgCode, errMsgArgs, null);

    return error;
  }

  public ModelAndView nextPage(ParmsCommand aCommand, BindingResult aErrors)
  {
    ModelAndView mav = new ModelAndView();

    if (aErrors != null && aErrors.hasErrors())
    {
      mav.addAllObjects(aErrors.getModel());
    }

    mav.setViewName(getViewName());

    if (aCommand != null)
    {
      mav.addObject(getCommandName(), aCommand);
    }

    return mav;
  }

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
  public ModelAndView redirectResponseMessage(String aViewName, HttpServletRequest aRequest, String aAttributeName,
                                              String[] aResponseMessageArguments)
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

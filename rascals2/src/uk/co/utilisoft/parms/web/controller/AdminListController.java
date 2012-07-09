package uk.co.utilisoft.parms.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.parms.web.command.AdminListCommand;
import uk.co.utilisoft.parms.web.service.AdminService;

/**
 * Generic Administration List Controller.
 *
 * @author Bill Comer
 * @version 1.0
 */
@Controller("parms.adminListController")
@SessionAttributes(WebConstants.ADMIN_COMMAND)
public class AdminListController
{
  private static final String mCommandName = WebConstants.ADMIN_COMMAND;

  private String mControllerName, mObjectName;

  @Autowired(required=true)
  @Qualifier("parms.configurationParameterService")
  private AdminService mAdminService;

  private List<String> mColumnMessageCodes;
  private List<Integer> mColumnWidths;
  private Boolean mCanCreateNew;
  private String mViewName;
  private int mNumberOfIdentifiers;

  private List validParamPatterns;
  public void setValidParamPatterns(List aValidParamPatterns)
  {
    this.validParamPatterns = aValidParamPatterns;
  }
  public List getValidParamPatterns()
  {
    return validParamPatterns;
  }

  /**
   * @param aNumberOfIdentifiers the numberOfIdentifiers to set
   */
  public void setNumberOfIdentifiers(int aNumberOfIdentifiers)
  {
    mNumberOfIdentifiers = aNumberOfIdentifiers;
  }

  /**
   * @param aControllerName the controllerName to set
   */
  public void setControllerName(String aControllerName)
  {
    mControllerName = aControllerName;
  }

  /**
   * @param aObjectName the objectName to set
   */
  public void setObjectName(String aObjectName)
  {
    mObjectName = aObjectName;
  }

  /**
   * @param aAdminService the adminService to set
   */
  public void setAdminService(AdminService aAdminService)
  {
    mAdminService = aAdminService;
  }



  public AdminService getAdminService()
  {
    return mAdminService;
  }

  /**
   * @param aColumnMessageCodes the columnMessageCodes to set
   */
  public void setColumnMessageCodes(List<String> aColumnMessageCodes)
  {
    mColumnMessageCodes = aColumnMessageCodes;
  }

  /**
   * @param aColumnWidths the columnWidths to set
   */
  public void setColumnWidths(List<Integer> aColumnWidths)
  {
    mColumnWidths = aColumnWidths;
  }

  /**
   * @param aCanCreateNew the canCreateNew to set
   */
  public void setCanCreateNew(Boolean aCanCreateNew)
  {
    mCanCreateNew = aCanCreateNew;
  }

  /**
   * @param aViewName the viewName to set
   */
  public void setViewName(String aViewName)
  {
    mViewName = aViewName;
  }

  private Logger mLogger = Logger.getLogger(getClass());

  /**
   * @see org.springframework.web.servlet.mvc.BaseCommandController
   *      #initBinder(javax.servlet.http.HttpServletRequest,
   *      org.springframework.web.bind.ServletRequestDataBinder) {@inheritDoc}
   */
  @InitBinder
  public void initBinder(ServletRequestDataBinder aBinder) throws Exception
  {
    CustomNumberEditor numEditor = new CustomNumberEditor(Long.class, true);
    aBinder.registerCustomEditor(Long.class, numEditor);

    CustomBooleanEditor booleanEditor = new CustomBooleanEditor(true);
    aBinder.registerCustomEditor(Boolean.class, booleanEditor);
  }

  /**
   * Gets the model attribute "items".
   *
   * @return List of AdminListDTO objects.
   */
  @ModelAttribute("items")
  public List<AdminListDTO> getAllSortedRecords()
  {
    return mAdminService.getAllSortedRecords();
  }

  /**
   * Gets the model attribute "columnMessageCodes".
   *
   * @return List of String objects.
   */
  @ModelAttribute("columnMessageCodes")
  public List<String> columnMessageCodes()
  {
    return mColumnMessageCodes;
  }

  /**
   * Gets the model attribute "columnWidths".
   *
   * @return List of Integer objects.
   */
  @ModelAttribute("columnWidths")
  public List<Integer> columnWidths()
  {
    return mColumnWidths;
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
    aRequest.setAttribute("title", mObjectName + " List");
    AdminListCommand command = new AdminListCommand();

    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < mNumberOfIdentifiers; i++)
    {
      list.add(null);
    }
    command.setIdentifier(list);

    command.setCanCreateNew(mCanCreateNew);

    ModelAndView mav = new ModelAndView(getViewName());
    mav.addObject(command);
    return mav;
  }

  /**
   * On Submit Method (post).
   *
   * @param aRequest HttpServletRequest object.
   * @param aCommand Command object.
   * @return ModelAndView for the redirected page.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView onSubmit(HttpServletRequest aRequest,
                               @ModelAttribute(mCommandName) AdminListCommand aCommand)
  {
    mLogger.debug("Entered onSubmit() for: " + mControllerName);
    aRequest.setAttribute("title", mObjectName + " List");

    ModelAndView mav = new ModelAndView(new RedirectViewIdParametersOnly(getEditUrl(), getValidParamPatterns()));
    AdminListCommand command = (AdminListCommand) aCommand;
    for (int i = 0; i < command.getIdentifier().size(); i++)
    {
      mav.addObject("id_" + i, command.getIdentifier().get(i));
    }
    return mav;
  }

  /**
   *
   * @param aStringToFormat String
   * @return String
   */
  @SuppressWarnings("unused")
  private String removeSpaces(String aStringToFormat)
  {
    StringTokenizer tokenizedString = new StringTokenizer(aStringToFormat, " ", false);
    String result = "";
    while (tokenizedString.hasMoreElements())
    {
      result += tokenizedString.nextElement();
    }
    return result;
  }

  /**
   *
   * @return String
   */
  private String getEditUrl()
  {
    String formattedControllerName;
    if (allCapitals())
    {
      formattedControllerName = mControllerName.toLowerCase();
    }
    else
    {
      char firstChar = mControllerName.charAt(0);
      firstChar = Character.toLowerCase(firstChar);
      formattedControllerName = firstChar + mControllerName.substring(1);
    }
    return formattedControllerName + "Edit.htm";
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

  /**
   * @return the view name.
   */
  private String getViewName()
  {
    return mViewName;
  }

}

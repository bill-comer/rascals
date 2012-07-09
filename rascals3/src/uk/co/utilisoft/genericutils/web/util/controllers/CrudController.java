package uk.co.utilisoft.genericutils.web.util.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.table.model.JodaTimePropertyEditor;

public abstract class CrudController
{
  private String mControllerName, mObjectName;
  
  private String mViewName;
  
  private List<String> mColumnMessageCodes;

  private List<Integer> mColumnWidths;
  private Boolean mCanCreateNew = true;
  private int mNumberOfIdentifiers = 1;
  
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

    CustomBooleanEditor booleanEditor = new CustomBooleanEditor( false);
    aBinder.registerCustomEditor(Boolean.class, booleanEditor);
    
    JodaTimePropertyEditor jodaDateEditor = new JodaTimePropertyEditor(
         DateTimeFormat.forPattern("dd/MM/yyyy"));

    aBinder.registerCustomEditor(DateTime.class, jodaDateEditor);
  }

  public int getNumberOfIdentifiers()
  {
    return mNumberOfIdentifiers;
  }
  public void setNumberOfIdentifiers(int aNumberOfIdentifiers)
  {
    this.mNumberOfIdentifiers = aNumberOfIdentifiers;
  }

  private List<String> validParamPatterns;
  public void setValidParamPatterns(List<String> aValidParamPatterns)
  {
    this.validParamPatterns = aValidParamPatterns;
  }
  public List<String> getValidParamPatterns()
  {
    return validParamPatterns;
  }
  

  /**
   * @param aCanCreateNew the canCreateNew to set
   */
  public void setCanCreateNew(Boolean aCanCreateNew)
  {
    mCanCreateNew = aCanCreateNew;
  }

  public Boolean canCreateNew()
  {
    return mCanCreateNew;
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
      formattedControllerName = getControllerName().toLowerCase();
    }
    else
    {
      char firstChar = getControllerName().charAt(0);
      firstChar = Character.toLowerCase(firstChar);
      formattedControllerName = firstChar + getControllerName().substring(1);
    }
    return formattedControllerName + "Edit.htm";
  }

  /**
   *
   * @return boolean
   */
  private boolean allCapitals()
  {
    int charCount = getControllerName().length();
    for (int i = 0; i < charCount; i++)
    {
      if (Character.isLowerCase(getControllerName().charAt(i)))
      {
        return false;
      }
    }
    return true;
  }

  public String getControllerName()
  {
    Assert.notNull(mControllerName, "mControllerName MUST be set up in the super class constructor");
    return mControllerName;
  }
  public void setControllerName(String aControllerName)
  {
    mControllerName = aControllerName;
  }
  

  /**
   * @return the view name.
   */
  public String getViewName()
  {

    Assert.notNull(mViewName, "mViewName MUST be set up in the super class constructor");
    return mViewName;
  }
  public void setViewName(String aViewName)
  {
    mViewName = aViewName;
  }
  
  public String getObjectName()
  {

    Assert.notNull(mObjectName, "mObjectName MUST be set up in the super class constructor");
    return mObjectName;
  }

  public void setObjectName(String aObjectName)
  {
    this.mObjectName = aObjectName;
  }



  /**
   * Form Backing Object Method (get).
   *
   * @param aRequest HttpServletRequest object.
   * @return ModelAndView for the initial load of the page.
   * @throws Exception Exception thrown if an error occurs.
   */
  @RequestMapping(method = RequestMethod.GET)
  public Object defaultGet(HttpServletRequest aRequest) throws Exception
  {
    aRequest.setAttribute("title", getObjectName());
    CrudCommand command = getCommand();

    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < getNumberOfIdentifiers(); i++)
    {
      list.add(null);
    }
    command.setIdentifier(list);

    command.setCanCreateNew(canCreateNew());

    ModelAndView mav = new ModelAndView(getViewName());
    mav.addObject(getCommandName(), command);
    return mav;
  }

  @RequestMapping(method = RequestMethod.GET, params = "create")
  public Object createNew(HttpServletRequest aRequest) throws Exception
  {
    CrudCommand command = getCommand();
    command.setIsNew(true);
    
    ModelAndView mav = new ModelAndView("project.createUser");
    mav.addObject(getCommandName(), command);
    return mav;
  }


  public abstract CrudCommand getCommand();
  public abstract String getCommandName();

}

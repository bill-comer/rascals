package uk.co.utilisoft.rascals.web.Swimmers;

import static uk.co.utilisoft.genericutils.web.ModelAndViewUtil.redirectResponseMessage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.genericutils.web.util.controllers.CrudCommand;
import uk.co.utilisoft.genericutils.web.util.controllers.CrudController;
import uk.co.utilisoft.rascals.domain.Swimmer;
import config.GenericWebConstants;

@Controller("project.swimmersController")
@SessionAttributes("swimmersCommand")
public class SwimmersController extends CrudController
{
  private static final String mCommandName = "swimmersCommand";


  @Autowired(required=true)
  @Qualifier("project.swimmerService")
  private SwimmerServiceImpl swimmerService;

  public SwimmersController()
  {
    super();
    setControllerName("SwimmersController");
    setObjectName("List Swimmers");
    setViewName("parms.createSwimmer");
    
    /*
     * set up column names for list fields 
     */
    List<String> columnMessageCodes = new ArrayList<String>();
    columnMessageCodes.add("field.admin.swimmer.firstname");
    columnMessageCodes.add("field.admin.swimmer.surname");
    setColumnMessageCodes(columnMessageCodes);
    
    
    
    /*
     * set up column widths for list fields
     */
    List<Integer> columnWidths = new ArrayList<Integer>();
    columnWidths.add(250);
    columnWidths.add(250);
    columnWidths.add(250);
    columnWidths.add(50);
    setColumnWidths(columnWidths);
    
    /*
     * set up ?
     */
    List<String> validParamPatterns = new ArrayList<String>();
    validParamPatterns.add("id");
    setValidParamPatterns(validParamPatterns);
    
  }


  /**
   * Gets the model attribute "items".
   *
   * @return List of AdminListDTO objects.
   */
  @ModelAttribute("items")
  public List<SwimmerListDTO> getAllSortedRecords()
  {
    return swimmerService.getAllSortedRecords();
  }

  @RequestMapping(method = RequestMethod.GET, value="/editSwimmer.htm", params = "id_0")
  public Object editSwimmer(HttpServletRequest aRequest, @RequestParam("id_0") String aSwimmerId) throws Exception
  {
    Long id = new Long(aSwimmerId);
    Swimmer swimmer = swimmerService.getForId(id);
    
    aRequest.setAttribute("title", getObjectName());
    SwimmerCommand command = (SwimmerCommand) getCommand();
    command.setSwimmer(swimmer);
    command.setMale(swimmer.isMale());

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
  

  /**
   * On Submit Method (post).
   *
   * @param aRequest HttpServletRequest object.
   * @param aCommand Command object.
   * @return ModelAndView for the redirected page.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView createEditSwimmer(HttpServletRequest aRequest,
                               @ModelAttribute(mCommandName) SwimmerCommand aCommand) throws Exception
  {
    Swimmer swimmer = aCommand.getSwimmer();
    try {
      
      //swimmer.setDateOfBirth(new DateTime());
      swimmer.setMale(isMale(aCommand.getMale()));
      
      swimmerService.createNewSwimmer(swimmer);
    } catch (Exception e) {
      
      e.printStackTrace();

      ModelAndView mav = new ModelAndView("parms.createSwimmer");
     
      mav.addObject(aCommand);
      mav = redirectResponseMessage("/listBoySwimmers.htm?create", aRequest, GenericWebConstants.USERS_CREATE_FAILURE,
                  new String[] {e.getMessage()});
      return mav;
    }

    String listUrl = "listGirlSwimmers.htm";
    if (swimmer.isMale())
    {
      listUrl = "listBoySwimmers.htm";
    }
    
    ModelAndView mav = new ModelAndView("redirect:" + listUrl);
    
    return mav;
  }


  @Override
  public CrudCommand getCommand()
  {
    return new SwimmerCommand();
  }
  

  @Override
  public String getCommandName()
  {
    return mCommandName;
  }

  private boolean isMale(String aMaleOrFemale)
  {
    return aMaleOrFemale.equalsIgnoreCase("male");
  } 
  
}
package uk.co.utilisoft.rascals.web.Galas;

import static uk.co.utilisoft.genericutils.web.ModelAndViewUtil.redirectResponseMessage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import uk.co.utilisoft.rascals.domain.Gala;
import config.GenericWebConstants;

@Controller("project.galasController")
@SessionAttributes("galasCommand")
public class GalasController extends CrudController
{
  private static final String mCommandName = "galasCommand";


  @Autowired(required=true)
  @Qualifier("project.galaService")
  private GalaServiceImpl galaService;

  public GalasController()
  {
    super();
    setControllerName("GalasController");
    setObjectName("List Galas");
    //setViewName("project.listGalas");
    setViewName("parms.createGala");
    
    /*
     * set up column names for list fields 
     */
    List<String> columnMessageCodes = new ArrayList<String>();
    columnMessageCodes.add("field.admin.gala.name");
    columnMessageCodes.add("field.admin.gala.league");
    columnMessageCodes.add("field.admin.gala.eventDate");
    columnMessageCodes.add("field.admin.gala.eventDateOfBirthDate");
    columnMessageCodes.add("field.admin.gala.postcode");
    columnMessageCodes.add("field.admin.gala.home.or.away");
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
  public List<GalaListDTO> getAllSortedRecords()
  {
    return galaService.getAllSortedRecords();
  }

  @RequestMapping(method = RequestMethod.GET, value="/editGala.htm", params = "id_0")
  public Object editGala(HttpServletRequest aRequest, @RequestParam("id_0") String aGalaId) throws Exception
  {
    Long id = new Long(aGalaId);
    Gala gala = galaService.getForId(id);
    
    aRequest.setAttribute("title", getObjectName());
    GalaCommand command = (GalaCommand) getCommand();
    command.setGala(gala);
    
    command.setHomeAway(gala.isAtHome());
    

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
  public ModelAndView createEditGala(HttpServletRequest aRequest,
                               @ModelAttribute(mCommandName) GalaCommand aCommand) throws Exception
  {
    try {
      
      Gala gala = aCommand.getGala();
      gala.setAtHome(isHome(aCommand.getHomeAway()));
      
      galaService.createNewGala(gala);
    } catch (Exception e) {
      
      e.printStackTrace();

      ModelAndView mav = new ModelAndView("parms.createGala");
     
      mav.addObject(aCommand);
      mav = redirectResponseMessage("/listGalas.htm?create", aRequest, GenericWebConstants.USERS_CREATE_FAILURE,
                  new String[] {e.getMessage()});
      return mav;
    }

    
    ModelAndView mav = new ModelAndView("redirect:" + "listGalas.htm");
    
    return mav;
  }


  @Override
  public CrudCommand getCommand()
  {
    return new GalaCommand();
  }
  

  @Override
  public String getCommandName()
  {
    return mCommandName;
  }
  
  private boolean isHome(String aHomeAway)
  {
    return aHomeAway.equalsIgnoreCase("home");
  }
}
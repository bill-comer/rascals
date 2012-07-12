package uk.co.utilisoft.rascals.web.listGalaRaces;


import static config.GenericWebConstants.COMMAND_NAME_SEARCH;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.genericutils.web.searchfilter.CellDTO;
import uk.co.utilisoft.genericutils.web.searchfilter.ResultsDaoHibernate;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterCommand;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterController;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterControllerInterface;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterValidator;
import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;
import uk.co.utilisoft.rascals.domain.Gala;
import uk.co.utilisoft.rascals.domain.Race;
import uk.co.utilisoft.table.TableConstants;
import uk.co.utilisoft.table.model.Field;


@Controller("project.listGalaRacesController")
public class ListGalaRacesController extends SearchFilterController<ListGalaRacesDataSearchWrapper, Long, ListGalaRacesDataCommand , Gala>
    implements SearchFilterControllerInterface<ListGalaRacesDataCommand>
{
  public static String DISPLAY_MESSAGE = "DISPLAY_MESSAGE";

  public static final String PK = "pk";

  @Autowired(required = true)
  @Qualifier("project.listGalaRacesSearchDao")
  private ResultsDaoHibernate<ListGalaRacesDataSearchWrapper, Long> mListDataSearchDao;

  @Autowired(required = true)
  @Qualifier("generic.SearchFilterValidator")
  private SearchFilterValidator mSearchFilterValidator;
  protected SearchFilterValidator getSearchFilterValidator()
  {
    return mSearchFilterValidator;
  }
  public void setSearchFilterValidator(SearchFilterValidator aValidator)
  {
    mSearchFilterValidator = aValidator;
  }



  public ListGalaRacesController()
  {
    super();
    setViewName("parms.listGalaRaces");
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getResultsDao()
   */
  @Override
  public ResultsDaoHibernate<ListGalaRacesDataSearchWrapper, Long> getResultsDao()
  {
    return mListDataSearchDao;
  }

  @Override
  protected boolean includeFixedCriteria()
  {
    return true;
  }

  
  @RequestMapping(value = "/listGalaRaces.htm", method = RequestMethod.GET, params = PK)
  public ModelAndView listTestResultsOrdered(HttpServletRequest aRequest, @RequestParam(PK) String galaPk) throws Exception
  {
    return formSearchBackingObject(aRequest);
  }
  

  @RequestMapping(value = "/listGalaRaces.htm", params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_SORT }, method = RequestMethod.POST)
  public ModelAndView listTestResultsOrdered(HttpServletRequest aRequest,
              @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommand aCommand) throws Exception
  {
    return viewProcessing(aRequest, getResultsView(), aCommand.getSortList(), (ListGalaRacesDataCommand) aCommand );
  }



  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getFields()
   */
  @Override
  protected List<Field<?>> getFields()
  {
    List<Field<?>> fields = new ArrayList<Field<?>>();
    fields.add(new Field<String>("PK", getResultsDao().getAlias() + ".pk", false, String.class, true));
    
    fields.add(new Field<String>("Race Type", getResultsDao().getAlias() + ".raceType", false, String.class, true));

    fields.add(new Field<String>("Distance", getResultsDao().getAlias() + ".distance", false, String.class, true));
    fields.add(new Field<String>("Stroke", getResultsDao().getAlias() + ".stroke", false, String.class, true));

    fields.add(new Field<String>("Age Date", getResultsDao().getAlias() + ".age", false, String.class, true));

    fields.add(new Field<String>("Male/Female", getResultsDao().getAlias() + ".male", false, String.class, true));

    return fields;
  }


  /*@Override
  protected BaseVersionedDomainObject<Long, Long, DateTime> transferDomainToRow(
              ListReportsDataSearchWrapper aSearchWrapperObj, List<CellDTO> cells)
  {
    // TODO Auto-generated method stub
    return null;
  }
*/
  public BaseVersionedDomainObject transferDomainToRow(
              ListGalaRacesDataSearchWrapper aSearchWrapperObj, List<CellDTO> cells)
  {
    DateTimeFormatter mmmYYYYFmt = DateTimeFormat.forPattern("MMM yyyy");
    DateTimeFormatter ddMMFmt = DateTimeFormat.forPattern("dd MMM");
    DateTimeFormatter ddMMyyyyHHmiSSFmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    
    //get domain object
    Race report = aSearchWrapperObj.getRace();
    
    // add a CellDto for each column
    cells.add(new CellDTO(report.getPk()));
    
    cells.add(new CellDTO(report.getRaceType()));
    cells.add(new CellDTO(report.getDistance()));
    cells.add(new CellDTO(report.getStroke()));
    cells.add(new CellDTO(report.getAge()));
    cells.add(new CellDTO(report.isMale()));
    
    return report;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getResultsView()
   */
  @Override
  protected String getResultsView()
  {
    // /pages/generic/searchFilter/results.jsp
    return "generic/searchFilter/results";
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getFilterView()
   */
  @Override
  protected String getFilterView()
  {
    return "generic/searchFilter/filter";
  }

  
  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#transferExtraDomainToRow(uk.co.formfill
   * .hibernateutils.domain.DomainObject, uk.co.utilisoft.parms.web.dto.RowDTO, javax.servlet.http.HttpServletRequest)
   */
  /*@Override
  protected void transferExtraDomainToRow(ListReportsDataSearchWrapper aTestResultsDataSearchWrapperObj,
                                          RowDTO aRow,
                                          HttpServletRequest aRequest)
  {
    List<CellDTO> cells = new ArrayList<CellDTO>();

    TestResults testResults = (TestResults)aTestResultsDataSearchWrapperObj.getFileObject();
    cells.add(new CellDTO(testResults.getName()));

    DateTimeFormatter mmmYYYYFmt = DateTimeFormat.forPattern("MMMM yyyy");
    String fmtReportingPeriod = mmmYYYYFmt.print(testResults.getLastUpdated());
    cells.add(new CellDTO(fmtReportingPeriod));

    cells.add(new CellDTO(testResults.isAllTestsPassed()));

    aRow.setCells(cells);
  }
*/
  protected String getInitialSortColumn()
  {
    return  getResultsDao().getAlias() + ".age";
  }
 


}
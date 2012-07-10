package uk.co.utilisoft.rascals.web.listSwimmers;


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
import org.springframework.web.servlet.ModelAndView;

import uk.co.utilisoft.genericutils.web.searchfilter.CellDTO;
import uk.co.utilisoft.genericutils.web.searchfilter.QueryStringBindValueCombo;
import uk.co.utilisoft.genericutils.web.searchfilter.ResultsDaoHibernate;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterCommand;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterController;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterControllerInterface;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterValidator;
import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;
import uk.co.utilisoft.rascals.domain.Swimmer;
import uk.co.utilisoft.table.TableConstants;
import uk.co.utilisoft.table.model.Field;
/**
 * Lists Tests in the configurable directory defined by the bean project.test.suite.directory.
 *
 * @author Bill COmer
 * @version 1.0
 */

@Controller("project.listSwimmersController")
public class ListSwimmersController extends SearchFilterController<ListSwimmersDataSearchWrapper, Long, ListSwimmersDataCommand , Swimmer>
    implements SearchFilterControllerInterface<ListSwimmersDataCommand>
{
  public static String DISPLAY_MESSAGE = "DISPLAY_MESSAGE";

  @Autowired(required = true)
  @Qualifier("project.listSwimmersSearchDao")
  private ResultsDaoHibernate<ListSwimmersDataSearchWrapper, Long> mListDataSearchDao;

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



  public ListSwimmersController()
  {
    super();
    setViewName("parms.listSwimmers");
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getResultsDao()
   */
  @Override
  public ResultsDaoHibernate<ListSwimmersDataSearchWrapper, Long> getResultsDao()
  {
    return mListDataSearchDao;
  }

  @Override
  protected boolean includeFixedCriteria()
  {
    return true;
  }


  
  @RequestMapping(value = "/listBoySwimmers.htm", method = RequestMethod.GET)
  public ModelAndView listBoySwimmersOrdered(HttpServletRequest aRequest) throws Exception
  {
    ((ListSwimmersDataSearchDaoHibernate)mListDataSearchDao).setMale(true);
    
    return formSearchBackingObject(aRequest);
  }

  
  @RequestMapping(value = "/listGirlSwimmers.htm", method = RequestMethod.GET)
  public ModelAndView listGirkSwimmersOrdered(HttpServletRequest aRequest) throws Exception
  {
    ((ListSwimmersDataSearchDaoHibernate)mListDataSearchDao).setMale(false);
    
    return formSearchBackingObject(aRequest);
  }
  

  @RequestMapping(value = "/listBoySwimmers.htm", params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_SORT }, method = RequestMethod.POST)
  public ModelAndView listBoySwimmersOrdered(HttpServletRequest aRequest,
              @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommand aCommand) throws Exception
  {
    return viewProcessing(aRequest, getResultsView(), aCommand.getSortList(), (ListSwimmersDataCommand) aCommand );
  }


  @RequestMapping(value = "/listSwimmers.htm", params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_SORT }, method = RequestMethod.POST)
  public ModelAndView listGirlSwimmersOrdered(HttpServletRequest aRequest,
              @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommand aCommand) throws Exception
  {
    return viewProcessing(aRequest, getResultsView(), aCommand.getSortList(), (ListSwimmersDataCommand) aCommand );
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
    

    fields.add(new Field<String>("First Name", getResultsDao().getAlias() + ".firstname", false, String.class, true));
    fields.add(new Field<String>("Surname", getResultsDao().getAlias() + ".surname", false, String.class, true));
    fields.add(new Field<String>("Date of birth", getResultsDao().getAlias() + ".dateOfBirth", false, String.class, true));


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
              ListSwimmersDataSearchWrapper aSearchWrapperObj, List<CellDTO> cells)
  {
    DateTimeFormatter mmmYYYYFmt = DateTimeFormat.forPattern("MMM yyyy");
    DateTimeFormatter ddMMyyyyFmt = DateTimeFormat.forPattern("dd/MM/yyyy");
    DateTimeFormatter ddMMyyyyHHmiSSFmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    
    //get domain object
    Swimmer report = aSearchWrapperObj.getSwimmer();
    
    // add a CellDto for each column
    cells.add(new CellDTO(report.getPk()));
    
    cells.add(new CellDTO(report.getFirstname()));
    cells.add(new CellDTO(report.getSurname()));
    cells.add(new CellDTO(ddMMyyyyFmt.print(report.getDateOfBirth())));
    
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
    return  getResultsDao().getAlias() + ".surname";
  }


}
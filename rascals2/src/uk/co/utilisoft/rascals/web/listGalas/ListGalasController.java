package uk.co.utilisoft.rascals.web.listGalas;


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
import uk.co.utilisoft.genericutils.web.searchfilter.ResultsDaoHibernate;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterCommand;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterController;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterControllerInterface;
import uk.co.utilisoft.genericutils.web.searchfilter.SearchFilterValidator;
import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;
import uk.co.utilisoft.rascals.domain.Gala;
import uk.co.utilisoft.table.TableConstants;
import uk.co.utilisoft.table.model.Field;
/**
 * Lists Tests in the configurable directory defined by the bean project.test.suite.directory.
 *
 * @author Bill COmer
 * @version 1.0
 */

@Controller("project.listGalasController")
public class ListGalasController extends SearchFilterController<ListGalasDataSearchWrapper, Long, ListGalasDataCommand , Gala>
    implements SearchFilterControllerInterface<ListGalasDataCommand>
{
  public static String DISPLAY_MESSAGE = "DISPLAY_MESSAGE";

  @Autowired(required = true)
  @Qualifier("project.listGalasSearchDao")
  private ResultsDaoHibernate<ListGalasDataSearchWrapper, Long> mListDataSearchDao;

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



  public ListGalasController()
  {
    super();
    setViewName("parms.listGalas");
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getResultsDao()
   */
  @Override
  public ResultsDaoHibernate<ListGalasDataSearchWrapper, Long> getResultsDao()
  {
    return mListDataSearchDao;
  }

  @Override
  protected boolean includeFixedCriteria()
  {
    return true;
  }

  
  @RequestMapping(value = "/listGalas.htm", method = RequestMethod.GET)
  public ModelAndView listTestResultsOrdered(HttpServletRequest aRequest) throws Exception
  {
    return formSearchBackingObject(aRequest);
  }
  

  @RequestMapping(value = "/listGalas.htm", params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_SORT }, method = RequestMethod.POST)
  public ModelAndView listTestResultsOrdered(HttpServletRequest aRequest,
              @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommand aCommand) throws Exception
  {
    return viewProcessing(aRequest, getResultsView(), aCommand.getSortList(), (ListGalasDataCommand) aCommand );
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
    
    fields.add(new Field<String>("Event Date", getResultsDao().getAlias() + ".eventDate", false, String.class, true));

    fields.add(new Field<String>("Name", getResultsDao().getAlias() + ".name", false, String.class, true));
    fields.add(new Field<String>("League", getResultsDao().getAlias() + ".league", false, String.class, true));

    fields.add(new Field<String>("Age Rollover Date", getResultsDao().getAlias() + ".eventDateOfBirthDate", false, String.class, true));

    fields.add(new Field<String>("Postcode", getResultsDao().getAlias() + ".postcode", false, String.class, true));
    
    fields.add(new Field<String>("Home/Away", getResultsDao().getAlias() + ".atHome", false, String.class, true));

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
              ListGalasDataSearchWrapper aSearchWrapperObj, List<CellDTO> cells)
  {
    DateTimeFormatter mmmYYYYFmt = DateTimeFormat.forPattern("MMM yyyy");
    DateTimeFormatter ddMMFmt = DateTimeFormat.forPattern("dd MMM");
    DateTimeFormatter ddMMyyyyHHmiSSFmt = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    
    //get domain object
    Gala report = aSearchWrapperObj.getGala();
    
    // add a CellDto for each column
    cells.add(new CellDTO(report.getPk()));
    
    cells.add(new CellDTO(ddMMyyyyHHmiSSFmt.print(report.getEventDate())));
    cells.add(new CellDTO(report.getName()));
    cells.add(new CellDTO(report.getLeague()));
    cells.add(new CellDTO(ddMMFmt.print(report.getEventDateOfBirthDate())));
    cells.add(new CellDTO(report.getPostcode()));
    cells.add(new CellDTO(report.isAtHome()));
    
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
    return  getResultsDao().getAlias() + ".eventDate";
  }
 


}
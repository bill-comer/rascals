package uk.co.utilisoft.genericutils.web.searchfilter;

import static config.GenericWebConstants.COMMAND_NAME_SEARCH;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.co.formfill.hibernateutils.domain.DomainObject;
import uk.co.utilisoft.genericutils.web.util.JodaTimePropertyEditor;
import uk.co.utilisoft.parms.domain.BaseVersionedDomainObject;
import uk.co.utilisoft.table.TableConstants;
import uk.co.utilisoft.table.model.Field;


/**
 * @author Kirk Hawksworth, Daniel Winstanley, Bill Comer
 * @version 1.0
 *
 * @param <T> Object Type.
 * @param <S> Identifier Type.
 * @param <SearchFilterCommandType> the subclass of SearchFilterCommand
 * @param <DomainType> the domain object type of InnerDataDTO for nested row data
 */
@SessionAttributes(COMMAND_NAME_SEARCH)
public abstract class SearchFilterController<T extends DomainObject<?, ?>, S extends Serializable,
    SearchFilterCommandType extends SearchFilterCommand,
    DomainType extends Serializable> implements SearchFilterControllerInterface<SearchFilterCommandType>,
    PagingControllerInterface<SearchFilterCommandType>
{

  public static final String REQUESTED_ACTION = "requestedAction";
  
  private DateTimeFormatter mDateFormatter = DateTimeFormat.forPattern(TableConstants.SCREEN_DATE_FORMAT);
  private DateTimeFormatter mDateTimeFormatter = DateTimeFormat.forPattern(TableConstants.SCREEN_DATE_TIME_FORMAT);

  protected List<SearchCriteriaDTO<?>> mInitialFilters = new ArrayList<SearchCriteriaDTO<?>>();
  private Class<SearchFilterCommandType> mSearchFilterCommandType;

  private String mViewName;

  /**
   * Set Validator and SearchFilterCommand object types
   */
  protected SearchFilterController()
  {
    setSearchFilterCommandType();
  }


  /**
   * @param aRequest the request
   * @param aFileId the File identifier
   * @return the ModelAndView
   * @throws Exception
   */
  @RequestMapping(method = RequestMethod.GET, params = "id_0")
  public ModelAndView formBackingObject(HttpServletRequest aRequest, @RequestParam("id_0") String aFileId)
      throws Exception
  {
    return formBackingObject(aRequest, aFileId, getRecordIdModelName());
  }

  /**
   * Initial Binder.
   *
   * @param aRequest HttpServletRequest.
   * @param aBinder ServletRequestDataBinder.
   * @throws Exception thrown.
   */
  @InitBinder
  protected void initBinder(HttpServletRequest aRequest,
                            ServletRequestDataBinder aBinder) throws Exception
  {
    DateTimeFormatter df = DateTimeFormat.forPattern(TableConstants.SCREEN_DATE_FORMAT);
    JodaTimePropertyEditor je = new JodaTimePropertyEditor(df);
    CustomBooleanEditor booleanEditor = new CustomBooleanEditor(false);

    aBinder.registerCustomEditor(DateTime.class, je);
    aBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    aBinder.registerCustomEditor(Boolean.class, booleanEditor);
    aBinder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#formBackingObject(javax.servlet.http
   * .HttpServletRequest, java.lang.String, java.lang.String)
   */
  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView formBackingObject(HttpServletRequest aRequest, String aRecordId, String aRecordIdModelName)
      throws Exception
  {
    aRequest.getSession().setAttribute(COMMAND_NAME_SEARCH, null);
    SearchFilterCommandType command = newSearchFilterCommand();
    ModelAndView mav = new ModelAndView();
    mav.setViewName(getDefaultFormView());
    onBindOnNewForm(aRequest, command, false, aRecordId, aRecordIdModelName);
    mav.addObject(COMMAND_NAME_SEARCH, command);
    return mav;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#updateCount(javax.servlet.http
   * .HttpServletRequest, uk.co.utilisoft.parms.web.command.SearchFilterCommand, org.springframework.validation.Errors,
   * java.lang.String)
   */
  @Override
  @RequestMapping(params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_COUNT }, method = RequestMethod.POST)
  public ModelAndView updateCount(HttpServletRequest aRequest,
                                  @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommandType aCommand,
                                  Errors aErrors)
  {
    getSearchFilterValidator().validate(aCommand, aErrors);

    if (!aErrors.hasErrors())
    {
      // remove search criteria with empty search values
      List<SearchCriteriaDTO<?>> currentSearchCriteria = removeEmptySearchCriteria(aCommand);

      // add default record id criteria
      SearchCriteriaDTO<?> defaultCriteria = getDefaultSearchCriteria(aRequest.getParameter(getRecordIdFieldName()),
                                                                      getRecordIdModelName());
      if (defaultCriteria != null)
      {
        currentSearchCriteria.add(defaultCriteria);
      }

      // customize search criteria
      customiseFilterSearchCriteria(currentSearchCriteria);

      Long count = getResultsDao().getCount(currentSearchCriteria,
                                       mInitialFilters,
                                       includeFixedCriteria(),
                                       includeDynamicCriteria());
      aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, count);
    }
    addFiltersToRequest(aRequest);
    return new ModelAndView(getFilterView());
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#doPaging(javax.servlet.http
   * .HttpServletRequest, uk.co.utilisoft.parms.web.command.SearchFilterCommand, org.springframework.validation.Errors)
   */
  @Override
  @RequestMapping(params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_PAGE }, method = RequestMethod.POST)
  public ModelAndView doPaging(HttpServletRequest aRequest,
                               @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommandType aCommand,
                               Errors aErrors)
  {
    getSearchFilterValidator().validate(aCommand, aErrors);

    if (!aErrors.hasErrors())
    {
      SearchFilterCommand command = (SearchFilterCommand) aCommand;

      // remove search criteria with empty values
      List<SearchCriteriaDTO< ? >> currentSearchCriteria = removeEmptySearchCriteria(aCommand);

      // add default record id criteria
      SearchCriteriaDTO<?> defaultCriteria = getDefaultSearchCriteria(aRequest.getParameter(getRecordIdFieldName()),
                                                                      getRecordIdModelName());
      if (defaultCriteria != null)
      {
        currentSearchCriteria.add(defaultCriteria);
      }

      // customize search criteria
      customiseFilterSearchCriteria(currentSearchCriteria);

      int firstResult = ((command.getCurrentPage() - 1) * command.getRecordsPerPage());

      if (aCommand.getSortList() == null || aCommand.getSortList().equals(""))
      {
        aCommand.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());
      }

      Collection<T> results = getResultsDao().getData(currentSearchCriteria,
                                                      mInitialFilters,
                                                      aCommand.getSortList(),
                                                      firstResult,
                                                      aCommand.getRecordsPerPage(),
                                                      includeFixedCriteria(),
                                                      includeDynamicCriteria(),
                                                      false,
                                                      getJoinSql());
      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS,
                            transformResultsForDisplay(results, aRequest));

      Long count = getResultsDao().getCount(currentSearchCriteria,
                                            mInitialFilters,
                                            includeFixedCriteria(),
                                            includeDynamicCriteria(),
                                            false,
                                            getJoinSql(),
                                            true);
      aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, count);

      aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, command.getCurrentPage());
      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, command.getRecordsPerPage());
      addSortingToRequest(aRequest, aCommand);
      aRequest.setAttribute(TableConstants.RECORDS_PER_PAGE_LIST, Filters.RECORDS_PER_PAGE.getComparators());
      aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());
      addFiltersToRequest(aRequest);

      // extra table results and headers
      aRequest.setAttribute("EXTRA_HEADERS", getExtraTableFields());
      aRequest.setAttribute("EXTRA_RESULTS", transformExtraResultsForDisplay(results, aRequest));

      // add custom attributes and parameters
      addExtraAttributes(aRequest, results);

      return new ModelAndView(getResultsView());
    }
    return new ModelAndView(getFilterView());
  }

  /**
   * For setting custom attributes or parameters.
   *
   * @param aRequest the request
   * @param aResultObjects the results
   */
  protected void addExtraAttributes(HttpServletRequest aRequest, Collection<T> aResultObjects)
  {
    // here so it can be overwiddled
  }


  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#changeRecordsPerPage(javax.servlet.http
   * .HttpServletRequest, uk.co.utilisoft.parms.web.command.SearchFilterCommand, org.springframework.validation.Errors)
   */
  @Override
  @RequestMapping(params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_CHANGE_PER_PAGE },
                  method = RequestMethod.POST)
  public ModelAndView changeRecordsPerPage(HttpServletRequest aRequest,
                                           @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommandType aCommand,
                                           Errors aErrors)
  {
    getSearchFilterValidator().validate(aCommand, aErrors);

    if (!aErrors.hasErrors())
    {
      SearchFilterCommandType command = (SearchFilterCommandType) aCommand;
      List<SearchCriteriaDTO<?>> currentSearchCriteria = removeEmptySearchCriteria(command);

      // add default record id criteria
      SearchCriteriaDTO<?> defaultCriteria = getDefaultSearchCriteria(aRequest.getParameter(getRecordIdFieldName()),
                                                                      getRecordIdModelName());
      if (defaultCriteria != null)
      {
        currentSearchCriteria.add(defaultCriteria);
      }

      // customize search criteria
      customiseFilterSearchCriteria(currentSearchCriteria);

      if (aCommand.getSortList() == null || aCommand.getSortList().equals(""))
      {
        aCommand.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());
      }

      Collection<T> results = getResultsDao().getData(currentSearchCriteria,
                                                      mInitialFilters,
                                                      aCommand.getSortList(),
                                                      0,
                                                      aCommand.getRecordsPerPage(),
                                                      includeFixedCriteria(),
                                                      includeDynamicCriteria(),
                                                      false,
                                                      getJoinSql());
      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS,
                            transformResultsForDisplay(results, aRequest));

      Long count = getResultsDao().getCount(currentSearchCriteria,
                                            mInitialFilters,
                                            includeFixedCriteria(),
                                            includeDynamicCriteria(),
                                            false,
                                            getJoinSql(),
                                            true);
      aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, count);

      aRequest.setAttribute(TableConstants.RECORDS_PER_PAGE_LIST, Filters.RECORDS_PER_PAGE.getComparators());
      aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, 1);
      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, aCommand.getRecordsPerPage());
      addSortingToRequest(aRequest, aCommand);
      addFiltersToRequest(aRequest);
      aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());

      // extra table results and headers
      aRequest.setAttribute("EXTRA_HEADERS", getExtraTableFields());
      aRequest.setAttribute("EXTRA_RESULTS", transformExtraResultsForDisplay(results, aRequest));

      // add custom attributes and parameters
      addExtraAttributes(aRequest, results);

      return new ModelAndView(getResultsView());
    }

    return new ModelAndView(getFilterView());
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#applyFilter(javax.servlet.http
   * .HttpServletRequest, uk.co.utilisoft.parms.web.command.SearchFilterCommand, org.springframework.validation.Errors
   * , java.lang.String)
   */
  @RequestMapping(params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_APPLY },
                  method = RequestMethod.POST)
  public ModelAndView applyFilter(HttpServletRequest aRequest,
                                  @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommandType aCommand,
                                  Errors aErrors)
  {
    getSearchFilterValidator().validate(aCommand, aErrors);
    SearchFilterCommandType command = (SearchFilterCommandType) aCommand;
    command.setCurrentPage(1);
    aRequest.setAttribute(TableConstants.RECORDS_PER_PAGE_LIST, Filters.RECORDS_PER_PAGE.getComparators());

    if (!aErrors.hasErrors())
    {
      // customize search criteria
      customiseFilterSearchCriteria(command.getSearchCriteriaDTOs());
      // remove search criteria containing empty search values
      List<SearchCriteriaDTO< ? >> currentSearchCriteria = removeEmptySearchCriteria(command);

      // add default record id criteria. Returns null if record id is not enabled.
      // For example on list screens, records shown are not filtered using a record id
      SearchCriteriaDTO<?> defaultCriteria = getDefaultSearchCriteria(aRequest.getParameter(getRecordIdFieldName()),
                               getRecordIdModelName());

      if (defaultCriteria != null)
      {
        currentSearchCriteria.add(defaultCriteria);
      }


      if (command.getSortList() == null  || command.getSortList().equals(""))
      {
        command.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());
      }

      Collection<T> results = getResultsDao().getData(currentSearchCriteria,
                                                      mInitialFilters,
                                                      aCommand.getSortList(),
                                                      0,
                                                      aCommand.getRecordsPerPage(),
                                                      includeFixedCriteria(),
                                                      includeDynamicCriteria(),
                                                      false,
                                                      getJoinSql());

      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(results,
                                                                                        aRequest));
      Long count = getResultsDao().getCount(currentSearchCriteria,
                                            mInitialFilters,
                                            includeFixedCriteria(),
                                            includeDynamicCriteria(),
                                            false,
                                            getJoinSql(),
                                            true);
      aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, count);

      aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());
      aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, aCommand.getCurrentPage());
      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, aCommand.getRecordsPerPage());
      addSortingToRequest(aRequest, aCommand);

      // extra table results and headers
      aRequest.setAttribute("EXTRA_HEADERS", getExtraTableFields());
      aRequest.setAttribute("EXTRA_RESULTS", transformExtraResultsForDisplay(results, aRequest));

      // add custom attributes and parameters
      addExtraAttributes(aRequest, results);

      return new ModelAndView(getResultsView());
    }

    addFiltersToRequest(aRequest);
    return new ModelAndView(getFilterView());
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#clearFilter(javax.servlet.http
   * .HttpServletRequest, uk.co.utilisoft.parms.web.command.SearchFilterCommand)
   */
  @RequestMapping(params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_CLEAR },
                  method = RequestMethod.POST)
  @SuppressWarnings("unchecked")
  public ModelAndView clearFilter(HttpServletRequest aRequest,
                                  @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommandType aCommand)
  {
    List<SearchCriteriaDTO<?>> currentSearchCriteria = aCommand.getSearchCriteriaDTOs();

    SearchCriteriaDTO<Boolean> currentBoolean;
    for (SearchCriteriaDTO<?> current : currentSearchCriteria)
    {
      if (current.getType().equals(SearchCriteriaDTO.TYPE_BOOLEAN))
      {
        currentBoolean = (SearchCriteriaDTO<Boolean>) current;
        currentBoolean.setSearchValue(Boolean.TRUE);
      }
      else if (current.getType().equals(SearchCriteriaDTO.TYPE_CUSTOM_SELECT))
      {
        SearchCriteriaDTO<OptionDTO> currentCustomSelect = (SearchCriteriaDTO<OptionDTO>) current;
        currentCustomSelect.setComparator(null); // for a join the comparator is used as the value e.g. >|< 0
        currentCustomSelect.setSearchValue(null);
      }
      else
      {
        current.setSearchValue(null);
      }
    }

    Collection<T> results = new ArrayList<T>();
    Long count = new Long(0);

    addFiltersToRequest(aRequest);
    aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());
    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(results, aRequest));
    aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, count);

    aRequest.getSession().setAttribute(COMMAND_NAME_SEARCH, null);
    return new ModelAndView(getFilterView());
  }
  
  public ModelAndView viewProcessing(HttpServletRequest aRequest, String view, String sortList, SearchFilterCommand aCommand) throws Exception
  {
    aRequest.getSession().setAttribute(COMMAND_NAME_SEARCH, null);
    ModelAndView mav = new ModelAndView(view);
    aCommand.setSearchCriteriaDTOs(getSearchCriteria());

    List<T> results = getResultsDao().getData(new ArrayList<SearchCriteriaDTO<?>>(),
                mInitialFilters, sortList, 0, aCommand.getRecordsPerPage(), false);
    
    if (sortList.toLowerCase().contains("asc"))
    {
      //Collections.reverse(results);
    }
    
    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(results, aRequest));
    aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, getResultsDao().getCount(new ArrayList<SearchCriteriaDTO<?>>(),
                mInitialFilters,
                false,
                includeDynamicCriteria()));
    
    aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, 1);
    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, aCommand.getRecordsPerPage());
    aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());
    
    if (aRequest.getMethod().toUpperCase().equals("GET"))
    {
      aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_ORDER, getInitialSortOrder());
      aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_COLUMN, getInitialSortColumn());
    }
    else
    {
      addSortingToRequest(aRequest, aCommand);
    }
    
    addFiltersToRequest(aRequest);
    aCommand.setRecordsPerPage(new Integer(aCommand.getRecordsPerPage()));
    aCommand.setSortList(sortList);
    mav.addObject(COMMAND_NAME_SEARCH, aCommand);

    return mav;
  }

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterControllerInterface#sort(javax.servlet.http
   * .HttpServletRequest, uk.co.utilisoft.parms.web.command.SearchFilterCommand, org.springframework
   * .validation.Errors, java.lang.String)
   */
  @Override
  @RequestMapping(params = { REQUESTED_ACTION + "=" + TableConstants.ACTION_SORT }, method = RequestMethod.POST)
  public ModelAndView sort(HttpServletRequest aRequest,
                           @ModelAttribute(COMMAND_NAME_SEARCH) SearchFilterCommandType aCommand,
                           Errors aErrors)
  {
    aRequest.getSession().setAttribute(COMMAND_NAME_SEARCH, null);
    SearchFilterCommandType command = getSearchFilterCommand();

    ModelAndView mav = new ModelAndView(getDefaultFormView());
    command.setSearchCriteriaDTOs(getSearchCriteria());

    Collection<T> results = getResultsDao().getData(new ArrayList<SearchCriteriaDTO<?>>(),
      mInitialFilters, getInitialSortColumn() + " " + getInitialSortOrder(), 0, 10, false);

    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(results, aRequest));
    aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, getResultsDao().getCount(new ArrayList<SearchCriteriaDTO<?>>(),
                                                                                  mInitialFilters,
                                                                                  false,
                                                                                  includeDynamicCriteria()));
    aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, 1);
    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, TableConstants.DEFAULT_RECORDS_PER_PAGE);
    aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());
    aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_ORDER, getInitialSortOrder());
    aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_COLUMN, getInitialSortColumn());
    addFiltersToRequest(aRequest);

    command.setRecordsPerPage(new Integer(TableConstants.DEFAULT_RECORDS_PER_PAGE));
    command.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());
    mav.addObject(COMMAND_NAME_SEARCH, command);

    return mav;
  }

  /**
   * @param aViewName the viewName to set
   */
  public void setViewName(String aViewName)
  {
    mViewName = aViewName;
  }

  /**
   * @return the viewName
   */
  public String getViewName()
  {
    return mViewName;
  }


  /**
   * Obtain existing command from a model; Otherwise create a new command.
   *
   * @param aModelAndView the model and view
   * @return the command object
   */
  @SuppressWarnings("unchecked")
  protected SearchFilterCommandType getCommandFromModel(ModelAndView aModelAndView)
  {
    Object commandObject = aModelAndView.getModel().get(COMMAND_NAME_SEARCH);
    SearchFilterCommandType command = null;

    if (commandObject != null)
    {
      command = (SearchFilterCommandType) commandObject;
    }
    else
    {
      command = newSearchFilterCommand();
      aModelAndView.addObject(COMMAND_NAME_SEARCH, command);
    }

    return command;
  }

  /**
   * Override to customise filter criteria.
   *
   * @param aFilterSearchCriterias the current filter criteria used for the database query
   */
  protected void customiseFilterSearchCriteria(List<SearchCriteriaDTO<?>> aFilterSearchCriterias)
  {

  }

  /**
   * Override to customise filter's sort column model details.
   *
   * @param aSortList the command sort list
   * @return the customised sort list
   */
  protected String customiseSortList(String aSortList)
  {
    return aSortList;
  }

  /**
   * Override this method to customise initial search criteria.
   *
   * @param aRecordId the record id
   * @param aRecordIdModelName the model name of the record id
   * @return the default SearchCriteriaDTO
   */
  protected SearchCriteriaDTO<?> getDefaultSearchCriteria(Object aRecordId, String aRecordIdModelName)
  {
    if (aRecordId != null)
    {
      if (StringUtils.isNotBlank((String) aRecordId))
      {
        return getSearchCriteriaById(Long.valueOf((String) aRecordId), aRecordIdModelName);
      }
    }
    return null;
  }

  /**
   * Remove Search Criteria containing empty search values.
   * @param aCommand the command object
   * @return the list of SearchCriteriaDTO
   */
  protected List<SearchCriteriaDTO<?>> removeEmptySearchCriteria(SearchFilterCommandType aCommand)
  {
    List<SearchCriteriaDTO<?>> currentSearchCriteria = new ArrayList<SearchCriteriaDTO<?>>();
    currentSearchCriteria.addAll(aCommand.getSearchCriteriaDTOs());

    List<SearchCriteriaDTO<?>> includeSearchCriteria = new ArrayList<SearchCriteriaDTO<?>>();

    if (currentSearchCriteria != null)
    {
      for (SearchCriteriaDTO<?> sc : currentSearchCriteria)
      {
        if (sc.getSearchValue() != null && !sc.getSearchValue().toString().trim().equals(""))
        {
          includeSearchCriteria.add(sc);
        }
      }
    }

    return includeSearchCriteria;
  }

  /**
   * Add Sorting To Request.
   *
   * @param aRequest HttpServletRequest.
   * @param aCommand TabbedTableCommand.
   */
  protected void addSortingToRequest(HttpServletRequest aRequest,
                                     SearchFilterCommand aCommand)
  {
    if (aCommand.getSortList() == null || aCommand.getSortList().equals(""))
    {
      aCommand.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());
    }

    String[] sorting = aCommand.getSortList().split(" ");

    if (sorting.length < 2)
    {
      aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_ORDER, getInitialSortOrder());
      aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_COLUMN, getInitialSortColumn());
    }
    else
    {
      aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_ORDER, sorting[1]);
      aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_COLUMN, sorting[0]);
    }
  }

  /**
   * @return true if include fixed criteria; otherwise, false
   */
  protected boolean includeFixedCriteria()
  {
    return false;
  }

  /**
   * @return true if include dynamic criteria; otherwise, false
   */
  protected boolean includeDynamicCriteria()
  {
    return false;
  }

  /**
   * Load empty Filters by default. Override to set a default initial view for filter.
   *
   * @return List< SearchCriteriaDTO<?>> SearchCriteria.
   */
  protected List<SearchCriteriaDTO<?>> loadDefaultFilters()
  {
    return new ArrayList<SearchCriteriaDTO<?>>();
  }

  /**
   * @return the error view name
   */
  protected String getErrorView()
  {
    return TableConstants.VIEW_ERROR;
  }

  /**
   * Override in subclass to give a different filter view for anything non standard
   * @return the filter view name
   */
  abstract protected String getFilterView();

  /**
   * @return a validator
   */
  abstract protected SearchFilterValidator getSearchFilterValidator();
  

  /**
   * @param aValidator the validator
   */
  abstract public void setSearchFilterValidator(SearchFilterValidator aValidator);

  /**
   * @return the search filter command object
   */
  protected SearchFilterCommandType getSearchFilterCommand()
  {
    return newSearchFilterCommand();
  }

  /**
   * Method gets original, unfiltered data and unfiltered count if there is no
   * filter in properties file. (which it looks for in reguest param).
   * @param aRequest the request
   * @param aCommand the command
   * @param aRecordId the record identifier
   * @param aErrorsFound true if errors found; otherwise, false
   * @param aRecordIdModelName the model name
   * @see org.springframework.web.servlet.mvc.AbstractFormController
   */
  protected void onBindOnNewForm(HttpServletRequest aRequest,
                                 SearchFilterCommandType aCommand,
                                 boolean aErrorsFound, String aRecordId,
                                 String aRecordIdModelName) throws Exception
  {
    mInitialFilters = loadDefaultFilters();

    SearchCriteriaDTO<?> defaultSearchCriteria = getDefaultSearchCriteria(aRecordId, aRecordIdModelName);

    if (aCommand.getSearchCriteriaDTOs().size() == 0 && defaultSearchCriteria == null)
    {
      aCommand.setCurrentPage(1);
      aCommand.setSearchCriteriaDTOs(getSearchCriteria());

      // extra results
      aRequest.setAttribute("EXTRA_RESULTS", transformExtraResultsForDisplay(new ArrayList<T>(), aRequest));

      aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(new ArrayList<T>(), aRequest));
      aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, 0);
    }
    else
    {
      if (aErrorsFound)
      {
        // extra results
        aRequest.setAttribute("EXTRA_RESULTS", transformExtraResultsForDisplay(new ArrayList<T>(), aRequest));

        aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS,
                              transformResultsForDisplay(new ArrayList<T>(), aRequest));
        aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, 0);
      }
      else
      {
        List<SearchCriteriaDTO<?>> currentSearchCriteria = new ArrayList<SearchCriteriaDTO<?>>();

        if (aCommand.getSearchCriteriaDTOs().isEmpty())
        {
          // add search options to filter
          aCommand.setSearchCriteriaDTOs(getSearchCriteria());
        }

        if (defaultSearchCriteria != null)
        {
          currentSearchCriteria.add(defaultSearchCriteria);
        }

        // customize search criteria
        customiseFilterSearchCriteria(currentSearchCriteria);

        Collection<T> results = getResultsDao().getData(currentSearchCriteria,
                                          mInitialFilters,
                                          getInitialSortColumn() + " " + getInitialSortOrder(),
                                          0,
                                          10,
                                          includeFixedCriteria(),
                                          includeDynamicCriteria(),
                                          false,
                                          getJoinSql());

        // extra results
        aRequest.setAttribute("EXTRA_RESULTS", transformExtraResultsForDisplay(results, aRequest));

        aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(results, aRequest));
        aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, getResultsDao().getCount(currentSearchCriteria,
                                                                                      mInitialFilters,
                                                                                      includeFixedCriteria(),
                                                                                      includeDynamicCriteria(),
                                                                                      false,
                                                                                      getJoinSql(),
                                                                                      true));
        // add custom attributes and parameters
        addExtraAttributes(aRequest, results);
      }
    }

    aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, 1);
    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, TableConstants.DEFAULT_RECORDS_PER_PAGE);
    aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());

    aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_ORDER, getInitialSortOrder());
    aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_COLUMN, getInitialSortColumn());

    // add extra headers table
    aRequest.setAttribute("EXTRA_HEADERS", getExtraTableFields());

    addFiltersToRequest(aRequest);

    aCommand.setRecordsPerPage(new Integer(TableConstants.DEFAULT_RECORDS_PER_PAGE));
    aCommand.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());

  }

  /**
   * Override to add a join sql to query. Default is null.
   *
   * @return the join sql
   */
  protected String getJoinSql()
  {
    return null;
  }

  /**
   * Given a Collection of results from the DB, this method tranforms them into something the front end can read;
   * A list of rows
   * @param aResults collection of domain objects
   * @param aRequest the request
   * @return Row for display, property editors added
   */
  protected List<RowDTO> transformResultsForDisplay(Collection<T> aResults,
                                                    HttpServletRequest aRequest)
  {
    List<RowDTO> rows = new ArrayList<RowDTO>();
    for (T dom : aResults)
    {
      if (includeDomain(dom))
      {
        RowDTO r = new RowDTO(new ArrayList<CellDTO>());
        transferDomainToRow(dom, r, aRequest);
        rows.add(r);
      }
    }
    return rows;
  }

  /**
   * @param aResults the additional results
   * @param aRequest the request
   * @return the list of rowDTO
   */
  protected List<RowDTO> transformExtraResultsForDisplay(Collection<T> aResults, HttpServletRequest aRequest)
  {
    List<RowDTO> rows = new ArrayList<RowDTO>();
    for (T dom : aResults)
    {
      RowDTO r = new RowDTO(new ArrayList<CellDTO>());
      transferExtraDomainToRow(dom, r, aRequest);
      rows.add(r);
    }
    return rows;
  }

  /**
   * @param aDate the date
   * @return the date as text
   */
  protected String convertDate(DateTime aDate)
  {
    return aDate == null ? null : mDateFormatter.print(aDate.getMillis());
  }

  /**
   * @param aDate the date
   * @return the date as text
   */
  protected String convertDateTime(DateTime aDate)
  {
    return aDate == null ? null : mDateTimeFormatter.print(aDate.getMillis());
  }

  /**
   * Get Initial Sort Column.
   *
   * @return String.
   */
  protected String getInitialSortColumn()
  {
    return  getResultsDao().getAlias() + ".lastUpdated";
  }


  /**
   * Override to exclude domains from the view.
   *
   * @param aDom the domain object to process
   * @return true if including domain in display
   */
  protected boolean includeDomain(T aDom)
  {
    return true;
  }

  /**
   * Empty data is inserted by default. Override to add extra data into a row.
   *
   * @param aDom a domain wrapper object
   * @param aRow a row object representing the domain object in a wrapper
   * @param aRequest the request
   */
  protected void transferExtraDomainToRow(T aDom, RowDTO aRow, HttpServletRequest aRequest)
  {
    aRow.setCells(new ArrayList<CellDTO>());
  }

  /**
   * Defaults to providing empty data. Override to insert headers and inner row data.
   *
   * @param aDomainObject the domain object to extract data from
   * @return the InnerDataDTO for a DomainObject's extracted data
   */
  protected InnerDataDTO<DomainType> transformInnerResultsForDisplay(Object aDomainObject)
  {
    return new InnerDataDTO<DomainType>();
  }

 /**
   * Add a list of fields, for the Table header, Filter, Model and type.
   * MUST be added in the same order as transferDomainToRow(T dom, RowDTO row)
   * @return List of fields
   */
  protected abstract List<Field<?>> getFields();

  /**
   * @return the additional table field headers
   */
  protected List<Field<?>> getExtraTableFields()
  {
    return new ArrayList<Field<?>>();
  }

  /**
   * AJAX workaround for validation errors. Spring returns getFormView() on validation errors.
   * So when validation errors, need to change the default form view and then reset it back
   * to this when no validation errors.
   * @return the form view name
   */

  /**
   * {@inheritDoc}
   * @see uk.co.utilisoft.parms.web.controller.SearchFilterController#getDefaultFormView()
   */
  protected String getDefaultFormView()
  {
    return getViewName();
  }

  /**
   * Dao to use
   * @return the resultsDao.
   */
  protected abstract ResultsDaoHibernate<T, S> getResultsDao();

  /**
   * @return the List of SearchCriteriaDTO
   */
  protected List<SearchCriteriaDTO<?>> getSearchCriteria()
  {
    List<SearchCriteriaDTO<?>> listCriteria = new ArrayList<SearchCriteriaDTO<?>>();
    
/*    
 * Example
 * SearchCriteriaDTO<String> dcAgentNameCriteria = new SearchCriteriaDTO<String>(String.class, "Data Collector",
        "pf.dcAgentName", SearchCriteriaDTO.TYPE_TEXT, true, SearchCriteriaDTO.COMPARATOR_EQUALS);
    p0028ListCriteria.add(dcAgentNameCriteria);
*/

    return listCriteria;
  }

  /**
   * Override in subclass to give a different results view for anything non standard.
   * @return the result view name
   */
  protected abstract String getResultsView();


  /**
   * Retrieve response message data from an existing session.
   * Only response messages in session for the given response message keys are retrieved from the session.
   *
   * @param aRequest the HttpServletRequest
   * @param aResponseMessageKeys the response message keys
   */
//  protected void showResponseMessages(HttpServletRequest aRequest, String ... aResponseMessageKeys)
//  {
//    ResponseMessageLoader messageLoader = new ResponseMessageLoader();
//    aRequest.setAttribute(PARMS_RESPONSE_MESSAGE_DATA,
//                          messageLoader.appendResponseMessageData(aRequest, aResponseMessageKeys));
//  }

  private SearchCriteriaDTO<?> getSearchCriteriaById(Long aId, String aIdModelName)
  {
    SearchCriteriaDTO<Long> criteriaById = new SearchCriteriaDTO<Long>(Long.class, "", aIdModelName,
        SearchCriteriaDTO.TYPE_TEXT);
    criteriaById.setSearchValue(aId);
    criteriaById.setEnabled(true);
    criteriaById.setComparator(SearchCriteriaDTO.COMPARATOR_EQUALS);
    return criteriaById;
  }

  /**
   * @param aRequest the request
   */
  protected void addFiltersToRequest(HttpServletRequest aRequest)
  {
    aRequest.setAttribute(TableConstants.FILTER_OPTIONS, getFields());
    aRequest.setAttribute(TableConstants.NUM_COMPARATOR, Filters.NUM.getComparators());
    aRequest.setAttribute(TableConstants.DATE_COMPARATOR, Filters.DATE.getComparators());
    aRequest.setAttribute(TableConstants.TEXT_COMPARATOR, Filters.TEXT.getComparators());
    aRequest.setAttribute(TableConstants.BOOL_COMPARATOR, Filters.BOOL.getComparators());
    aRequest.setAttribute(TableConstants.RECORDS_PER_PAGE_LIST, Filters.RECORDS_PER_PAGE.getComparators());
  }

  /**
   * Load any response message data as a session scope attribute message code with arguments.
   * Messages configured here can be picked up by other pages in the same session.
   *
   * @param aRequest the HttpServletRequest
   * @param aAttributeName the attribute name
   * @param aResponseMessageArguments the response message code arguments
   */
  protected void populateResponseMessage(HttpServletRequest aRequest, String aAttributeName,
                                         String[] aResponseMessageArguments)
  {
    if (aAttributeName != null && aAttributeName.length() > 0)
    {
      String[] arguments = aResponseMessageArguments != null && aResponseMessageArguments.length > 0
        ? aResponseMessageArguments : new String[] {};

      aRequest.getSession(true).setAttribute(aAttributeName, arguments);
    }
  }

  /*
   * Create instance of SearchFilterCommandType.
   * @return the SearchFilterCommand class type
   * @throws RuntimeException message on error
   */
  private SearchFilterCommandType newSearchFilterCommand()
  {
    try
    {
      return mSearchFilterCommandType.newInstance();
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException("Could not create an instance of class type "
                                         + SearchFilterCommand.class.getName());
    }
  }

  /*
   * Set SearchFilterCommand class type by reflection.
   * @throws RuntimeException message on error
   */
  @SuppressWarnings("unchecked")
  private void setSearchFilterCommandType()
  {
    try
    {
      mSearchFilterCommandType = ((Class<SearchFilterCommandType>)((ParameterizedType) getClass()
          .getGenericSuperclass()).getActualTypeArguments()[2]);
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException("Subclass of " + getClass().getName()
        + " requires a generic parameter which extends type " + SearchFilterCommand.class.getName() + " at position 4");
    }
  }
  

  

  
  protected abstract BaseVersionedDomainObject transferDomainToRow(T aSearchWrapperObj, List<CellDTO> cells);
  

  

  /**
   * Form Backing Object Method (get).
   *
   * @param aRequest HttpServletRequest object.
   * @return ModelAndView for the initial load of the page.
   * @throws Exception Exception thrown if an error occurs.
   */
  public ModelAndView formSearchBackingObject(HttpServletRequest aRequest) throws Exception
  {
    aRequest.getSession().setAttribute(COMMAND_NAME_SEARCH, null);
    SearchFilterCommandType command = getSearchFilterCommand();

    ModelAndView mav = new ModelAndView(getDefaultFormView());
    command.setSearchCriteriaDTOs(getSearchCriteria());

    Collection<T> results = getResultsDao().getData(new ArrayList<SearchCriteriaDTO<?>>(),
      mInitialFilters, getInitialSortColumn() + " " + getInitialSortOrder(), 0, 10, false);

    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS, transformResultsForDisplay(results, aRequest));
    aRequest.setAttribute(TableConstants.REQ_ATTR_COUNT, getResultsDao().getCount(new ArrayList<SearchCriteriaDTO<?>>(),
                                                                                  mInitialFilters,
                                                                                  false,
                                                                                  includeDynamicCriteria()));
    aRequest.setAttribute(TableConstants.REQ_ATTR_CURRENT_PAGE, 1);
    aRequest.setAttribute(TableConstants.REQ_ATTR_RESULTS_PER_PAGE, TableConstants.DEFAULT_RECORDS_PER_PAGE);
    aRequest.setAttribute(TableConstants.REQ_ATTR_HEADERS, getFields());
    aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_ORDER, getInitialSortOrder());
    aRequest.setAttribute(TableConstants.REQ_ATTR_SORT_COLUMN, getInitialSortColumn());
    addFiltersToRequest(aRequest);

    command.setRecordsPerPage(new Integer(TableConstants.DEFAULT_RECORDS_PER_PAGE));
    command.setSortList(getInitialSortColumn() + " " + getInitialSortOrder());
    mav.addObject(COMMAND_NAME_SEARCH, command);

    return mav;
  }
  

  
  
  //methods you are likely to override
  /**
   * 
   */
  protected String getInitialSortOrder()
  {
    return "desc";
  }
  

  /**
   */
  protected String getRecordIdFieldName()
  {
    return "id_0";
  }
  
  /**
   * 
   */
  public void transferDomainToRow(T aSearchWrapperObj, RowDTO aRow,
                                     HttpServletRequest aRequest)
  {
    List<CellDTO> cells = new ArrayList<CellDTO>();
    BaseVersionedDomainObject<Long, Long, DateTime> report = transferDomainToRow(aSearchWrapperObj, cells);
   
    aRow.setCells(cells);
    String thisId = "thisId";
    aRow.getRowMap().put(thisId, Long.toString(report.getPk()));
    aRow.getRowMap().put("onClickFunction", "clickedRow('" + thisId + "_" + Long.toString(report.getPk())
                         + "','" + thisId + "_'" + ",'" + getRecordIdFieldName() + "');");
  }
  

  /**
   * The RecordIdModelName is disabled by default. Override to provide an expected record id model name.
   *
   * @return the row record identifier model name
   */
  protected String getRecordIdModelName()
  {
    return "";
  }
}